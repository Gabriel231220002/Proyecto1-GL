package com.example.Adaptadores.Salida;

import com.example.Dominio.Periodo;
import com.example.Puertos.IPeriodoRepository;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PostgresPeriodoRepository implements IPeriodoRepository {

    @Override
    public void guardar(Periodo p) throws Exception {
        String sql = "INSERT INTO periodo (gestion, semestre, aprobado, fecha_apertura, fecha_finalizacion, estado) "
                + "VALUES (?, ?, ?, ?, ?, ?) RETURNING id;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, p.getGestion());
            ps.setInt(2, p.getSemestre());
            ps.setBoolean(3, p.isAprobado());

            Date inicio = p.getFechaApertura() != null ? p.getFechaApertura() : Date.valueOf(LocalDate.of(p.getGestion(), p.getSemestre() == 1 ? 2 : 8, 1));
            Date fin = p.getFechaFinalizacion() != null ? p.getFechaFinalizacion() : Date.valueOf(LocalDate.of(p.getGestion(), p.getSemestre() == 1 ? 6 : 12, 20));
            ps.setDate(4, inicio);
            ps.setDate(5, fin);
            ps.setBoolean(6, p.isEstado());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    p.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public void actualizar(Periodo p) throws Exception {
        String sql = "UPDATE periodo SET gestion = ?, semestre = ?, aprobado = ?, fecha_apertura = ?, fecha_finalizacion = ?, estado = ? WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, p.getGestion());
            ps.setInt(2, p.getSemestre());
            ps.setBoolean(3, p.isAprobado());

            Date inicio = p.getFechaApertura() != null ? p.getFechaApertura() : Date.valueOf(LocalDate.of(p.getGestion(), p.getSemestre() == 1 ? 2 : 8, 1));
            Date fin = p.getFechaFinalizacion() != null ? p.getFechaFinalizacion() : Date.valueOf(LocalDate.of(p.getGestion(), p.getSemestre() == 1 ? 6 : 12, 20));
            ps.setDate(4, inicio);
            ps.setDate(5, fin);
            ps.setBoolean(6, p.isEstado());
            ps.setInt(7, p.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int id) throws Exception {
        String sql = "DELETE FROM periodo WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public Periodo obtenerPorId(int id) throws Exception {
        String sql = "SELECT * FROM periodo WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearPeriodo(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Periodo> listar() throws Exception {
        List<Periodo> lista = new ArrayList<>();
        String sql = "SELECT * FROM periodo ORDER BY gestion DESC, semestre DESC;";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapearPeriodo(rs));
            }
        }
        return lista;
    }

    private Periodo mapearPeriodo(ResultSet rs) throws SQLException {
        return new Periodo(
            rs.getInt("id"),
            rs.getInt("gestion"),
            rs.getInt("semestre"),
            rs.getBoolean("aprobado"),
            rs.getDate("fecha_apertura"),
            rs.getDate("fecha_finalizacion"),
            rs.getBoolean("estado")
        );
    }
}
