package com.example.Adaptadores.Salida;

import com.example.Dominio.Usuario;
import com.example.Puertos.IUsuarioRepository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostgresUsuarioRepository implements IUsuarioRepository {

    @Override
    public void guardar(Usuario u) throws Exception {
        String sqlUsuario = "INSERT INTO usuario (nombres, apellido_p, apellido_m, genero, foto, ci, "
                + "fecha_nacimiento, telefono, email, password_hash, registro_institucional, estado) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id;";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conn.prepareStatement(sqlUsuario)) {
                    ps.setString(1, u.getNombres());
                    ps.setString(2, u.getApellidoP());
                    ps.setString(3, u.getApellidoM());
                    ps.setString(4, u.getGenero() != null ? u.getGenero() : "M");
                    ps.setString(5, u.getFoto());
                    ps.setInt(6, u.getCi());
                    ps.setDate(7, u.getFechaNacimiento());
                    ps.setInt(8, u.getTelefono());
                    ps.setString(9, u.getEmail());
                    ps.setString(10, u.getPasswordHash());
                    ps.setInt(11, u.getRegistroInstitucional());
                    ps.setBoolean(12, u.isEstado());

                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            u.setId(rs.getInt(1));
                        }
                    }
                }

                // Asignar rol si viene especificado
                if (u.getRol() != null && !u.getRol().trim().isEmpty()) {
                    asignarRolAUsuario(conn, u.getId(), u.getRol().trim(), true);
                }

                conn.commit();
            } catch (Exception ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    @Override
    public void actualizar(Usuario u) throws Exception {
        String sql = "UPDATE usuario SET nombres = ?, apellido_p = ?, apellido_m = ?, genero = ?, foto = ?, "
                + "ci = ?, fecha_nacimiento = ?, telefono = ?, email = ?, password_hash = ?, "
                + "registro_institucional = ?, estado = ? WHERE id = ?;";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, u.getNombres());
                    ps.setString(2, u.getApellidoP());
                    ps.setString(3, u.getApellidoM());
                    ps.setString(4, u.getGenero() != null ? u.getGenero() : "M");
                    ps.setString(5, u.getFoto());
                    ps.setInt(6, u.getCi());
                    ps.setDate(7, u.getFechaNacimiento());
                    ps.setInt(8, u.getTelefono());
                    ps.setString(9, u.getEmail());
                    ps.setString(10, u.getPasswordHash());
                    ps.setInt(11, u.getRegistroInstitucional());
                    ps.setBoolean(12, u.isEstado());
                    ps.setInt(13, u.getId());
                    ps.executeUpdate();
                }

                if (u.getRol() != null && !u.getRol().trim().isEmpty()) {
                    asignarRolAUsuario(conn, u.getId(), u.getRol().trim(), true);
                }

                conn.commit();
            } catch (Exception ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    @Override
    public void eliminar(int id) throws Exception {
        String sql = "DELETE FROM usuario WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public Usuario obtenerPorId(int id) throws Exception {
        String sql = "SELECT u.*, COALESCE(r.nombre, '') AS rol_nombre "
                + "FROM usuario u "
                + "LEFT JOIN usuario_rol ur ON u.id = ur.usuario_id AND ur.es_principal = TRUE "
                + "LEFT JOIN rol r ON ur.rol_id = r.id "
                + "WHERE u.id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearUsuario(rs);
                }
            }
        }
        return null;
    }

    @Override
    public Usuario obtenerPorEmail(String email) throws Exception {
        String sql = "SELECT u.*, COALESCE(r.nombre, '') AS rol_nombre "
                + "FROM usuario u "
                + "LEFT JOIN usuario_rol ur ON u.id = ur.usuario_id AND ur.es_principal = TRUE "
                + "LEFT JOIN rol r ON ur.rol_id = r.id "
                + "WHERE LOWER(u.email) = LOWER(?);";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearUsuario(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Usuario> listar() throws Exception {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT u.*, COALESCE(r.nombre, '') AS rol_nombre "
                + "FROM usuario u "
                + "LEFT JOIN usuario_rol ur ON u.id = ur.usuario_id AND ur.es_principal = TRUE "
                + "LEFT JOIN rol r ON ur.rol_id = r.id "
                + "ORDER BY u.id ASC;";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapearUsuario(rs));
            }
        }
        return lista;
    }

    private void asignarRolAUsuario(Connection conn, int usuarioId, String nombreRol, boolean esPrincipal) throws SQLException {
        int rolId = obtenerOCrearRol(conn, nombreRol);
        String sqlUpsert = "INSERT INTO usuario_rol (usuario_id, rol_id, es_principal) "
                + "VALUES (?, ?, ?) "
                + "ON CONFLICT (usuario_id, rol_id) DO UPDATE SET es_principal = EXCLUDED.es_principal;";
        try (PreparedStatement ps = conn.prepareStatement(sqlUpsert)) {
            ps.setInt(1, usuarioId);
            ps.setInt(2, rolId);
            ps.setBoolean(3, esPrincipal);
            ps.executeUpdate();
        }
    }

    private int obtenerOCrearRol(Connection conn, String nombreRol) throws SQLException {
        String sqlSelect = "SELECT id FROM rol WHERE LOWER(nombre) = LOWER(?);";
        try (PreparedStatement ps = conn.prepareStatement(sqlSelect)) {
            ps.setString(1, nombreRol);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        String sqlInsert = "INSERT INTO rol (nombre) VALUES (?) RETURNING id;";
        try (PreparedStatement ps = conn.prepareStatement(sqlInsert)) {
            ps.setString(1, nombreRol);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("No se pudo obtener ni crear el rol: " + nombreRol);
    }

    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        return new Usuario(
            rs.getInt("id"),
            rs.getString("nombres"),
            rs.getString("apellido_p"),
            rs.getString("apellido_m"),
            rs.getString("genero"),
            rs.getString("foto"),
            rs.getInt("ci"),
            rs.getDate("fecha_nacimiento"),
            rs.getInt("telefono"),
            rs.getString("rol_nombre"),
            rs.getString("email"),
            rs.getString("password_hash"),
            rs.getInt("registro_institucional"),
            rs.getBoolean("estado")
        );
    }
}
