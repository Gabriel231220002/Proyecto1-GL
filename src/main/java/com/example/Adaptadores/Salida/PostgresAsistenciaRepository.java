package com.example.Adaptadores.Salida;

import com.example.Dominio.Asistencia;
import com.example.Puertos.IAsistenciaRepository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostgresAsistenciaRepository implements IAsistenciaRepository {

    @Override
    public void guardar(Asistencia a) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int horarioId = a.getHorarioId();
                if (horarioId <= 0) {
                    // Si viene con grupoId o sin horarioId, buscar el horario asociado al grupo o el primer horario disponible
                    horarioId = resolverHorarioId(conn, a.getUsuarioId(), a.getGrupoId());
                    a.setHorarioId(horarioId);
                }

                // Asegurar que el par (usuario_id, horario_id) exista en usuario_horario para respetar la FK
                String sqlUH = "INSERT INTO usuario_horario (usuario_id, horario_id, tipo_turno) "
                        + "VALUES (?, ?, 'TURNO') ON CONFLICT (usuario_id, horario_id) DO NOTHING;";
                try (PreparedStatement psUH = conn.prepareStatement(sqlUH)) {
                    psUH.setInt(1, a.getUsuarioId());
                    psUH.setInt(2, horarioId);
                    psUH.executeUpdate();
                }

                String sql = "INSERT INTO asistencia (usuario_id, horario_id, fecha, estado_asistencia, justificacion, es_falta_justificada) "
                        + "VALUES (?, ?, ?, ?, ?, ?) RETURNING id;";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, a.getUsuarioId());
                    ps.setInt(2, horarioId);
                    ps.setDate(3, a.getFecha() != null ? a.getFecha() : new Date(System.currentTimeMillis()));
                    ps.setString(4, a.getEstadoAsistencia());
                    ps.setString(5, a.getJustificacion());
                    ps.setBoolean(6, a.isEsFaltaJustificada());

                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            a.setId(rs.getInt(1));
                        }
                    }
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
    public void actualizar(Asistencia a) throws Exception {
        String sql = "UPDATE asistencia SET usuario_id = ?, horario_id = ?, fecha = ?, estado_asistencia = ?, "
                + "justificacion = ?, es_falta_justificada = ? WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, a.getUsuarioId());
            ps.setInt(2, a.getHorarioId());
            ps.setDate(3, a.getFecha());
            ps.setString(4, a.getEstadoAsistencia());
            ps.setString(5, a.getJustificacion());
            ps.setBoolean(6, a.isEsFaltaJustificada());
            ps.setInt(7, a.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int id) throws Exception {
        String sql = "DELETE FROM asistencia WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public Asistencia obtenerPorId(int id) throws Exception {
        String sql = "SELECT * FROM asistencia WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearAsistencia(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Asistencia> listar() throws Exception {
        List<Asistencia> lista = new ArrayList<>();
        String sql = "SELECT * FROM asistencia ORDER BY fecha DESC, id DESC;";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapearAsistencia(rs));
            }
        }
        return lista;
    }

    @Override
    public List<Asistencia> listarPorUsuario(int usuarioId) throws Exception {
        List<Asistencia> lista = new ArrayList<>();
        String sql = "SELECT * FROM asistencia WHERE usuario_id = ? ORDER BY fecha DESC;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearAsistencia(rs));
                }
            }
        }
        return lista;
    }

    @Override
    public List<Asistencia> listarPorGrupo(int grupoId) throws Exception {
        List<Asistencia> lista = new ArrayList<>();
        String sql = "SELECT a.* FROM asistencia a "
                + "JOIN grupo_horario gh ON a.horario_id = gh.horario_id "
                + "WHERE gh.grupo_id = ? ORDER BY a.fecha DESC;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, grupoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearAsistencia(rs));
                }
            }
        }
        return lista;
    }

    private int resolverHorarioId(Connection conn, int usuarioId, Integer grupoId) throws SQLException {
        if (grupoId != null && grupoId > 0) {
            String sqlG = "SELECT horario_id FROM grupo_horario WHERE grupo_id = ? LIMIT 1;";
            try (PreparedStatement ps = conn.prepareStatement(sqlG)) {
                ps.setInt(1, grupoId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getInt(1);
                }
            }
        }

        String sqlU = "SELECT horario_id FROM usuario_horario WHERE usuario_id = ? LIMIT 1;";
        try (PreparedStatement ps = conn.prepareStatement(sqlU)) {
            ps.setInt(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }

        String sqlAny = "SELECT id FROM horario ORDER BY id ASC LIMIT 1;";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sqlAny)) {
            if (rs.next()) return rs.getInt(1);
        }

        // Si no hay horarios, crear uno base
        String sqlInsertH = "INSERT INTO horario (dia, hora_inicio, hora_fin) VALUES ('LUNES', '07:00:00', '09:15:00') RETURNING id;";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sqlInsertH)) {
            if (rs.next()) return rs.getInt(1);
        }

        return 1;
    }

    private Asistencia mapearAsistencia(ResultSet rs) throws SQLException {
        return new Asistencia(
            rs.getInt("id"),
            rs.getInt("usuario_id"),
            rs.getInt("horario_id"),
            rs.getDate("fecha"),
            rs.getString("estado_asistencia"),
            rs.getString("justificacion"),
            rs.getBoolean("es_falta_justificada")
        );
    }
}
