package com.example.Adaptadores.Salida;

import com.example.Dominio.Materia;
import com.example.Puertos.IMateriaRepository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostgresMateriaRepository implements IMateriaRepository {

    @Override
    public void guardar(Materia m) throws Exception {
        String sql = "INSERT INTO materia (nombre, sigla) VALUES (?, ?) RETURNING id;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getNombre());
            ps.setString(2, m.getSigla());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    m.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public void actualizar(Materia m) throws Exception {
        String sql = "UPDATE materia SET nombre = ?, sigla = ? WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getNombre());
            ps.setString(2, m.getSigla());
            ps.setInt(3, m.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int id) throws Exception {
        String sql = "DELETE FROM materia WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public Materia obtenerPorId(int id) throws Exception {
        String sql = "SELECT id, nombre, sigla FROM materia WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Materia(rs.getInt("id"), rs.getString("nombre"), rs.getString("sigla"));
                }
            }
        }
        return null;
    }

    @Override
    public List<Materia> listar() throws Exception {
        List<Materia> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, sigla FROM materia ORDER BY id ASC;";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Materia(rs.getInt("id"), rs.getString("nombre"), rs.getString("sigla")));
            }
        }
        return lista;
    }
}
