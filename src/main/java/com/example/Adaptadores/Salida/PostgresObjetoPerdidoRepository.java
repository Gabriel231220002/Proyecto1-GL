package com.example.Adaptadores.Salida;

import com.example.Dominio.ObjetoPerdido;
import com.example.Puertos.IObjetoPerdidoRepository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostgresObjetoPerdidoRepository implements IObjetoPerdidoRepository {

    @Override
    public void guardar(ObjetoPerdido o) throws Exception {
        String sql = "INSERT INTO objeto_perdido (labarotario_id, usuario_registra_id, usuario_reclama_id, nombre, "
                + "descripcion_objeto, estado_devolucion, fecha_hallazgo, fecha_entrega, foto) "
                + "VALUES (?, ?, ?, ?, ?, ?, CURRENT_DATE, ?, ?) RETURNING id;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, o.getLaboratorioId());
            ps.setInt(2, o.getUsuarioRegistraId());
            if (o.getUsuarioReclamaId() != null) ps.setInt(3, o.getUsuarioReclamaId()); else ps.setNull(3, Types.INTEGER);
            ps.setString(4, o.getNombre() != null ? o.getNombre() : "Objeto");
            ps.setString(5, o.getDescripcionObjeto());
            ps.setBoolean(6, o.isEstadoDevolucion());
            ps.setDate(7, o.getFechaEntrega());
            ps.setString(8, o.getFoto());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    o.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public void actualizar(ObjetoPerdido o) throws Exception {
        String sql = "UPDATE objeto_perdido SET labarotario_id = ?, usuario_registra_id = ?, usuario_reclama_id = ?, "
                + "nombre = ?, descripcion_objeto = ?, estado_devolucion = ?, fecha_entrega = ?, foto = ? WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, o.getLaboratorioId());
            ps.setInt(2, o.getUsuarioRegistraId());
            if (o.getUsuarioReclamaId() != null) ps.setInt(3, o.getUsuarioReclamaId()); else ps.setNull(3, Types.INTEGER);
            ps.setString(4, o.getNombre() != null ? o.getNombre() : "Objeto");
            ps.setString(5, o.getDescripcionObjeto());
            ps.setBoolean(6, o.isEstadoDevolucion());
            ps.setDate(7, o.getFechaEntrega());
            ps.setString(8, o.getFoto());
            ps.setInt(9, o.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int id) throws Exception {
        String sql = "DELETE FROM objeto_perdido WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public ObjetoPerdido obtenerPorId(int id) throws Exception {
        String sql = "SELECT * FROM objeto_perdido WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearObjeto(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<ObjetoPerdido> listar() throws Exception {
        List<ObjetoPerdido> lista = new ArrayList<>();
        String sql = "SELECT * FROM objeto_perdido ORDER BY fecha_hallazgo DESC, id DESC;";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapearObjeto(rs));
            }
        }
        return lista;
    }

    @Override
    public List<ObjetoPerdido> listarPorLaboratorio(int laboratorioId) throws Exception {
        List<ObjetoPerdido> lista = new ArrayList<>();
        String sql = "SELECT * FROM objeto_perdido WHERE labarotario_id = ? ORDER BY fecha_hallazgo DESC;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, laboratorioId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearObjeto(rs));
                }
            }
        }
        return lista;
    }

    private ObjetoPerdido mapearObjeto(ResultSet rs) throws SQLException {
        Integer recId = rs.getObject("usuario_reclama_id") != null ? rs.getInt("usuario_reclama_id") : null;
        return new ObjetoPerdido(
            rs.getInt("id"),
            rs.getString("nombre"),
            rs.getString("descripcion_objeto"),
            rs.getBoolean("estado_devolucion"),
            rs.getDate("fecha_hallazgo"),
            rs.getDate("fecha_entrega"),
            rs.getString("foto"),
            rs.getInt("usuario_registra_id"),
            recId,
            rs.getInt("labarotario_id")
        );
    }
}
