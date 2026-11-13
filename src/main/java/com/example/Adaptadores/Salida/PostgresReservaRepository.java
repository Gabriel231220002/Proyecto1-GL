package com.example.Adaptadores.Salida;

import com.example.Dominio.Reserva;
import com.example.Dominio.ReservaHorario;
import com.example.Puertos.IReservaRepository;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PostgresReservaRepository implements IReservaRepository {

    @Override
    public void guardar(Reserva r) throws Exception {
        String sql = "INSERT INTO reserva (usuario_id, usuario_registro_id, fecha_solicitud) "
                + "VALUES (?, ?, COALESCE(?, CURRENT_DATE)) RETURNING id;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, r.getUsuarioId());
            if (r.getUsuarioRegistroId() != null) {
                ps.setInt(2, r.getUsuarioRegistroId());
            } else {
                ps.setNull(2, Types.INTEGER);
            }
            ps.setDate(3, r.getFechaSolicitud());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    r.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public void eliminar(int id) throws Exception {
        String sql = "DELETE FROM reserva WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public Reserva obtenerPorId(int id) throws Exception {
        String sql = "SELECT * FROM reserva WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Integer uReg = rs.getObject("usuario_registro_id") != null ? rs.getInt("usuario_registro_id") : null;
                    return new Reserva(rs.getInt("id"), rs.getInt("usuario_id"), uReg, rs.getDate("fecha_solicitud"));
                }
            }
        }
        return null;
    }

    @Override
    public List<Reserva> listar() throws Exception {
        List<Reserva> lista = new ArrayList<>();
        String sql = "SELECT * FROM reserva ORDER BY id DESC;";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Integer uReg = rs.getObject("usuario_registro_id") != null ? rs.getInt("usuario_registro_id") : null;
                lista.add(new Reserva(rs.getInt("id"), rs.getInt("usuario_id"), uReg, rs.getDate("fecha_solicitud")));
            }
        }
        return lista;
    }

    @Override
    public boolean verificarConflicto(int laboratorioId, int horarioId, Date fechaUso) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // 1. Conflicto con otra reserva no cancelada/rechazada
            String sqlReserva = "SELECT COUNT(*) FROM reserva_horario rh "
                    + "JOIN horario h ON rh.horario_id = h.id "
                    + "WHERE (h.labarotario_id = ? OR ? = 0) AND rh.horario_id = ? "
                    + "AND rh.fecha_uso = ? AND rh.estado NOT IN ('RECHAZADA', 'CANCELADA');";
            try (PreparedStatement ps = conn.prepareStatement(sqlReserva)) {
                ps.setInt(1, laboratorioId);
                ps.setInt(2, laboratorioId);
                ps.setInt(3, horarioId);
                ps.setDate(4, fechaUso);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        return true;
                    }
                }
            }

            // 2. Conflicto con carga horaria regular fija de grupos
            LocalDate ld = fechaUso.toLocalDate();
            int diaSemana = ld.getDayOfWeek().getValue(); // 1=Lunes .. 7=Domingo
            String[] diasEs = {"LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES", "SABADO", "DOMINGO"};
            String diaEs = diasEs[diaSemana - 1];

            String sqlCarga = "SELECT COUNT(*) FROM grupo_horario gh "
                    + "JOIN horario h ON gh.horario_id = h.id "
                    + "WHERE (h.labarotario_id = ? OR ? = 0) AND gh.horario_id = ? "
                    + "AND gh.estado = TRUE AND (UPPER(h.dia) = ? OR UPPER(h.dia) LIKE ? OR h.dia = ?);";
            try (PreparedStatement ps = conn.prepareStatement(sqlCarga)) {
                ps.setInt(1, laboratorioId);
                ps.setInt(2, laboratorioId);
                ps.setInt(3, horarioId);
                ps.setString(4, diaEs);
                ps.setString(5, diaEs.substring(0, 3) + "%");
                ps.setString(6, String.valueOf(diaSemana));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public int crearReservaConHorario(int usuarioId, int laboratorioId, int horarioId, Date fechaUso, String motivo) throws Exception {
        if (verificarConflicto(laboratorioId, horarioId, fechaUso)) {
            throw new IllegalArgumentException("Conflicto de horario: El laboratorio " + laboratorioId
                    + " ya se encuentra ocupado o reservado para la fecha " + fechaUso + " en el horario " + horarioId + ".");
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int reservaId;
                String sqlReserva = "INSERT INTO reserva (usuario_id, fecha_solicitud) VALUES (?, CURRENT_DATE) RETURNING id;";
                try (PreparedStatement ps = conn.prepareStatement(sqlReserva)) {
                    ps.setInt(1, usuarioId);
                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        reservaId = rs.getInt(1);
                    }
                }

                String sqlRH = "INSERT INTO reserva_horario (reserva_id, horario_id, fecha_uso, estado, motivo) "
                        + "VALUES (?, ?, ?, 'APROBADA', ?) "
                        + "ON CONFLICT (reserva_id, horario_id) DO UPDATE SET fecha_uso = EXCLUDED.fecha_uso, estado = EXCLUDED.estado, motivo = EXCLUDED.motivo;";
                try (PreparedStatement ps = conn.prepareStatement(sqlRH)) {
                    ps.setInt(1, reservaId);
                    ps.setInt(2, horarioId);
                    ps.setDate(3, fechaUso);
                    ps.setString(4, motivo != null ? motivo : "Reserva académica");
                    ps.executeUpdate();
                }

                conn.commit();
                return reservaId;
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    @Override
    public void actualizarEstadoReservaHorario(int reservaId, String nuevoEstado) throws Exception {
        String sql = "UPDATE reserva_horario SET estado = ? WHERE reserva_id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, reservaId);
            ps.executeUpdate();
        }
    }

    @Override
    public List<ReservaHorario> listarReservasHorario() throws Exception {
        List<ReservaHorario> lista = new ArrayList<>();
        String sql = "SELECT rh.*, COALESCE(h.labarotario_id, 0) as laboratorio_id "
                + "FROM reserva_horario rh "
                + "LEFT JOIN horario h ON rh.horario_id = h.id "
                + "ORDER BY rh.fecha_uso DESC;";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new ReservaHorario(rs.getInt("reserva_id"), rs.getInt("reserva_id"), rs.getInt("horario_id"),
                        rs.getInt("laboratorio_id"), rs.getDate("fecha_uso"), rs.getString("estado"), rs.getString("motivo")));
            }
        }
        return lista;
    }

    @Override
    public List<Object[]> listarReservasDetalladas() throws Exception {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT rh.reserva_id, u.nombres || ' ' || u.apellido_p as solicitante, "
                + "COALESCE(l.nombre, 'Lab ' || h.labarotario_id) as laboratorio, "
                + "h.dia, h.hora_inicio || ' - ' || h.hora_fin as horario, rh.fecha_uso, rh.estado, rh.motivo "
                + "FROM reserva_horario rh "
                + "JOIN reserva r ON rh.reserva_id = r.id "
                + "JOIN usuario u ON r.usuario_id = u.id "
                + "JOIN horario h ON rh.horario_id = h.id "
                + "LEFT JOIN labarotario l ON h.labarotario_id = l.id "
                + "ORDER BY rh.fecha_uso DESC, rh.reserva_id DESC;";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getInt("reserva_id"),
                    rs.getString("solicitante"),
                    rs.getString("laboratorio"),
                    rs.getString("dia"),
                    rs.getString("horario"),
                    rs.getDate("fecha_uso"),
                    rs.getString("estado"),
                    rs.getString("motivo")
                });
            }
        }
        return lista;
    }
}
