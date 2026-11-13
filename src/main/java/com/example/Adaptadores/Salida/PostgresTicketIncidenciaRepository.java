package com.example.Adaptadores.Salida;

import com.example.Dominio.TicketIncidencia;
import com.example.Puertos.ITicketIncidenciaRepository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostgresTicketIncidenciaRepository implements ITicketIncidenciaRepository {

    @Override
    public void guardar(TicketIncidencia t) throws Exception {
        String sql = "INSERT INTO ticket_incidencia (item_inventario_id, usuario_reporta_id, usuario_atiende_id, asunto, descripcion, "
                + "solucion_aplicada, fecha_incidencia, fecha_solucion) VALUES (?, ?, ?, ?, ?, ?, CURRENT_DATE, ?) RETURNING id;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, t.getItemInventarioId());
            ps.setInt(2, t.getUsuarioReportaId());
            if (t.getUsuarioAtiendeId() != null) ps.setInt(3, t.getUsuarioAtiendeId()); else ps.setNull(3, Types.INTEGER);
            ps.setString(4, t.getAsunto());
            ps.setString(5, t.getDescripcion());
            ps.setString(6, t.getSolucionAplicada());
            ps.setDate(7, t.getFechaSolucion());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    t.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public void actualizar(TicketIncidencia t) throws Exception {
        String sql = "UPDATE ticket_incidencia SET item_inventario_id = ?, usuario_reporta_id = ?, usuario_atiende_id = ?, "
                + "asunto = ?, descripcion = ?, solucion_aplicada = ?, fecha_solucion = ? WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, t.getItemInventarioId());
            ps.setInt(2, t.getUsuarioReportaId());
            if (t.getUsuarioAtiendeId() != null) ps.setInt(3, t.getUsuarioAtiendeId()); else ps.setNull(3, Types.INTEGER);
            ps.setString(4, t.getAsunto());
            ps.setString(5, t.getDescripcion());
            ps.setString(6, t.getSolucionAplicada());
            ps.setDate(7, t.getFechaSolucion());
            ps.setInt(8, t.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int id) throws Exception {
        String sql = "DELETE FROM ticket_incidencia WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public TicketIncidencia obtenerPorId(int id) throws Exception {
        String sql = "SELECT *, "
                + "CASE WHEN fecha_solucion IS NOT NULL OR solucion_aplicada IS NOT NULL THEN 'RESUELTO' "
                + "WHEN usuario_atiende_id IS NOT NULL THEN 'EN_PROCESO' ELSE 'ABIERTO' END AS estado "
                + "FROM ticket_incidencia WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearTicket(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<TicketIncidencia> listar() throws Exception {
        List<TicketIncidencia> lista = new ArrayList<>();
        String sql = "SELECT *, "
                + "CASE WHEN fecha_solucion IS NOT NULL OR solucion_aplicada IS NOT NULL THEN 'RESUELTO' "
                + "WHEN usuario_atiende_id IS NOT NULL THEN 'EN_PROCESO' ELSE 'ABIERTO' END AS estado "
                + "FROM ticket_incidencia ORDER BY fecha_incidencia DESC, id DESC;";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapearTicket(rs));
            }
        }
        return lista;
    }

    @Override
    public List<TicketIncidencia> listarPorEstado(String estado) throws Exception {
        List<TicketIncidencia> lista = new ArrayList<>();
        String sql = "SELECT * FROM ("
                + "SELECT *, CASE WHEN fecha_solucion IS NOT NULL OR solucion_aplicada IS NOT NULL THEN 'RESUELTO' "
                + "WHEN usuario_atiende_id IS NOT NULL THEN 'EN_PROCESO' ELSE 'ABIERTO' END AS estado "
                + "FROM ticket_incidencia) sub WHERE UPPER(sub.estado) = UPPER(?) ORDER BY fecha_incidencia DESC;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, estado);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearTicket(rs));
                }
            }
        }
        return lista;
    }

    private TicketIncidencia mapearTicket(ResultSet rs) throws SQLException {
        Integer atiendeId = rs.getObject("usuario_atiende_id") != null ? rs.getInt("usuario_atiende_id") : null;
        return new TicketIncidencia(
            rs.getInt("id"),
            rs.getInt("item_inventario_id"),
            rs.getInt("usuario_reporta_id"),
            atiendeId,
            rs.getString("asunto"),
            rs.getString("descripcion"),
            rs.getString("estado"),
            rs.getString("solucion_aplicada"),
            rs.getDate("fecha_incidencia"),
            rs.getDate("fecha_solucion")
        );
    }
}
