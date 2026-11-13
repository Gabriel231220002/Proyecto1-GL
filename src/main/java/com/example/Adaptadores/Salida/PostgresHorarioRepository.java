package com.example.Adaptadores.Salida;

import com.example.Dominio.Horario;
import com.example.Puertos.IHorarioRepository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostgresHorarioRepository implements IHorarioRepository {

    @Override
    public void guardar(Horario h) throws Exception {
        String sql = "INSERT INTO horario (dia, hora_inicio, hora_fin, labarotario_id) VALUES (?, ?, ?, ?) RETURNING id;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, h.getDia());
            ps.setTime(2, h.getHoraInicio());
            ps.setTime(3, h.getHoraFin());
            if (h.getLaboratorioId() != null && h.getLaboratorioId() > 0) {
                ps.setInt(4, h.getLaboratorioId());
            } else {
                ps.setNull(4, Types.INTEGER);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    h.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public void actualizar(Horario h) throws Exception {
        String sql = "UPDATE horario SET dia = ?, hora_inicio = ?, hora_fin = ?, labarotario_id = ? WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, h.getDia());
            ps.setTime(2, h.getHoraInicio());
            ps.setTime(3, h.getHoraFin());
            if (h.getLaboratorioId() != null && h.getLaboratorioId() > 0) {
                ps.setInt(4, h.getLaboratorioId());
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            ps.setInt(5, h.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int id) throws Exception {
        String sql = "DELETE FROM horario WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public Horario obtenerPorId(int id) throws Exception {
        String sql = "SELECT * FROM horario WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearHorario(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Horario> listar() throws Exception {
        List<Horario> lista = new ArrayList<>();
        String sql = "SELECT * FROM horario ORDER BY dia ASC, hora_inicio ASC;";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapearHorario(rs));
            }
        }
        return lista;
    }

    private Horario mapearHorario(ResultSet rs) throws SQLException {
        Horario h = new Horario(
            rs.getInt("id"),
            rs.getString("dia"),
            rs.getTime("hora_inicio"),
            rs.getTime("hora_fin")
        );
        int labId = rs.getInt("labarotario_id");
        if (!rs.wasNull()) {
            h.setLaboratorioId(labId);
        }
        return h;
    }
}
