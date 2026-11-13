package com.example.Adaptadores.Salida;

import com.example.Dominio.PermutaTurno;
import com.example.Puertos.IPermutaTurnoRepository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostgresPermutaTurnoRepository implements IPermutaTurnoRepository {

    @Override
    public void guardar(PermutaTurno p, int horarioId) throws Exception {
        String sql = "INSERT INTO permuta_turno (usuario_solicitante_id, usuario_receptor_id, fecha_ausencia, motivo, estado, fecha_solicitud) "
                + "VALUES (?, ?, ?, ?, ?, CURRENT_DATE) RETURNING id;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, p.getUsuarioSolicitanteId());
            ps.setInt(2, p.getUsuarioReceptorId());
            ps.setDate(3, p.getFechaAusencia());
            ps.setString(4, p.getMotivo());
            ps.setString(5, p.getEstado() != null ? p.getEstado() : "PENDIENTE");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int pId = rs.getInt(1);
                    p.setId(pId);
                    if (horarioId > 0) {
                        try (PreparedStatement ps2 = conn.prepareStatement(
                                "INSERT INTO permuta_turno_horario (permuta_turno_id, horario_id) VALUES (?, ?) ON CONFLICT DO NOTHING;")) {
                            ps2.setInt(1, pId);
                            ps2.setInt(2, horarioId);
                            ps2.executeUpdate();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void actualizarEstado(int id, String estado) throws Exception {
        String sql = "UPDATE permuta_turno SET estado = ? WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int id) throws Exception {
        String sql = "DELETE FROM permuta_turno WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public PermutaTurno obtenerPorId(int id) throws Exception {
        String sql = "SELECT * FROM permuta_turno WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearPermuta(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<PermutaTurno> listar() throws Exception {
        List<PermutaTurno> lista = new ArrayList<>();
        String sql = "SELECT * FROM permuta_turno ORDER BY fecha_solicitud DESC, id DESC;";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapearPermuta(rs));
            }
        }
        return lista;
    }

    @Override
    public List<PermutaTurno> listarPorUsuario(int usuarioId) throws Exception {
        List<PermutaTurno> lista = new ArrayList<>();
        String sql = "SELECT * FROM permuta_turno WHERE usuario_solicitante_id = ? OR usuario_receptor_id = ? ORDER BY id DESC;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            ps.setInt(2, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearPermuta(rs));
                }
            }
        }
        return lista;
    }

    private PermutaTurno mapearPermuta(ResultSet rs) throws SQLException {
        return new PermutaTurno(
            rs.getInt("id"),
            rs.getInt("usuario_solicitante_id"),
            rs.getInt("usuario_receptor_id"),
            rs.getDate("fecha_ausencia"),
            rs.getString("motivo"),
            rs.getString("estado"),
            rs.getDate("fecha_solicitud")
        );
    }
}
