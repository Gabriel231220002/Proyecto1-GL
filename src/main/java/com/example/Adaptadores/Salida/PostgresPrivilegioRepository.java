package com.example.Adaptadores.Salida;

import com.example.Dominio.Privilegio;
import com.example.Dominio.UsuarioPrivilegio;
import com.example.Puertos.IPrivilegioRepository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostgresPrivilegioRepository implements IPrivilegioRepository {

    @Override
    public void guardar(Privilegio p) throws Exception {
        String sql = "INSERT INTO privilegio (codigo, nombre, descripcion) VALUES (?, ?, ?) RETURNING id;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getCodigo());
            ps.setString(2, p.getNombre());
            ps.setString(3, p.getDescripcion());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    p.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public void actualizar(Privilegio p) throws Exception {
        String sql = "UPDATE privilegio SET codigo = ?, nombre = ?, descripcion = ? WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getCodigo());
            ps.setString(2, p.getNombre());
            ps.setString(3, p.getDescripcion());
            ps.setInt(4, p.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int id) throws Exception {
        String sql = "DELETE FROM privilegio WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public Privilegio obtenerPorId(int id) throws Exception {
        String sql = "SELECT * FROM privilegio WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Privilegio(rs.getInt("id"), rs.getString("codigo"), rs.getString("nombre"), rs.getString("descripcion"));
                }
            }
        }
        return null;
    }

    @Override
    public List<Privilegio> listar() throws Exception {
        List<Privilegio> lista = new ArrayList<>();
        String sql = "SELECT * FROM privilegio ORDER BY id ASC;";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Privilegio(rs.getInt("id"), rs.getString("codigo"), rs.getString("nombre"), rs.getString("descripcion")));
            }
        }
        return lista;
    }

    @Override
    public void asignarPrivilegio(int usuarioId, int privilegioId) throws Exception {
        String sql = "INSERT INTO rol_privilegio (rol_id, privilegio_id) "
                + "SELECT ur.rol_id, ? FROM usuario_rol ur WHERE ur.usuario_id = ? "
                + "ON CONFLICT (rol_id, privilegio_id) DO NOTHING;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, privilegioId);
            ps.setInt(2, usuarioId);
            ps.executeUpdate();
        }
    }

    @Override
    public void revocarPrivilegio(int usuarioId, int privilegioId) throws Exception {
        String sql = "DELETE FROM rol_privilegio rp WHERE rp.privilegio_id = ? "
                + "AND rp.rol_id IN (SELECT ur.rol_id FROM usuario_rol ur WHERE ur.usuario_id = ?);";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, privilegioId);
            ps.setInt(2, usuarioId);
            ps.executeUpdate();
        }
    }

    @Override
    public List<UsuarioPrivilegio> listarPrivilegiosDeUsuario(int usuarioId) throws Exception {
        List<UsuarioPrivilegio> lista = new ArrayList<>();
        String sql = "SELECT DISTINCT rp.privilegio_id "
                + "FROM rol_privilegio rp "
                + "JOIN usuario_rol ur ON rp.rol_id = ur.rol_id "
                + "WHERE ur.usuario_id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new UsuarioPrivilegio(usuarioId, rs.getInt("privilegio_id"),
                            new Date(System.currentTimeMillis()), true));
                }
            }
        }
        return lista;
    }
}
