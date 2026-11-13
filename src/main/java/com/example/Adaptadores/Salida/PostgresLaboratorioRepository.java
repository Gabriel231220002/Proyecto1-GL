package com.example.Adaptadores.Salida;

import com.example.Dominio.Laboratorio;
import com.example.Puertos.ILaboratorioRepository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostgresLaboratorioRepository implements ILaboratorioRepository {

    @Override
    public void guardar(Laboratorio lab) throws Exception {
        String sql = "INSERT INTO labarotario (nombre, hora_apertura, hora_cierre) VALUES (?, ?, ?) RETURNING id;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, lab.getNombre());
            ps.setTime(2, lab.getHoraApertura() != null ? lab.getHoraApertura() : Time.valueOf("07:00:00"));
            ps.setTime(3, lab.getHoraCierre() != null ? lab.getHoraCierre() : Time.valueOf("21:30:00"));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    lab.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public void actualizar(Laboratorio lab) throws Exception {
        String sql = "UPDATE labarotario SET nombre = ?, hora_apertura = ?, hora_cierre = ? WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, lab.getNombre());
            ps.setTime(2, lab.getHoraApertura() != null ? lab.getHoraApertura() : Time.valueOf("07:00:00"));
            ps.setTime(3, lab.getHoraCierre() != null ? lab.getHoraCierre() : Time.valueOf("21:30:00"));
            ps.setInt(4, lab.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int id) throws Exception {
        String sql = "DELETE FROM labarotario WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public Laboratorio obtenerPorId(int id) throws Exception {
        String sql = "SELECT * FROM labarotario WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearLaboratorio(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Laboratorio> listar() throws Exception {
        List<Laboratorio> lista = new ArrayList<>();
        String sql = "SELECT * FROM labarotario ORDER BY id ASC;";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapearLaboratorio(rs));
            }
        }
        return lista;
    }

    private Laboratorio mapearLaboratorio(ResultSet rs) throws SQLException {
        Laboratorio lab = new Laboratorio(
            rs.getInt("id"),
            rs.getString("nombre"),
            rs.getTime("hora_apertura"),
            rs.getTime("hora_cierre")
        );
        lab.setAula(lab.getId()); // ID como fallback para retrocompatibilidad
        return lab;
    }
}
