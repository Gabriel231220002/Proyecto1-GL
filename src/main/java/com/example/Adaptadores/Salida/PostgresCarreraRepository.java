package com.example.Adaptadores.Salida;

import com.example.Dominio.Carrera;
import com.example.Puertos.ICarreraRepository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostgresCarreraRepository implements ICarreraRepository {

    @Override
    public void guardar(Carrera c) throws Exception {
        String sql = "INSERT INTO carrera (codigo, nombre, sigla) VALUES (?, ?, ?) RETURNING id;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getCodigo());
            ps.setString(2, c.getNombre());
            ps.setString(3, c.getSigla());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    c.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public void actualizar(Carrera c) throws Exception {
        String sql = "UPDATE carrera SET codigo = ?, nombre = ?, sigla = ? WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getCodigo());
            ps.setString(2, c.getNombre());
            ps.setString(3, c.getSigla());
            ps.setInt(4, c.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int id) throws Exception {
        String sql = "DELETE FROM carrera WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public Carrera obtenerPorId(int id) throws Exception {
        String sql = "SELECT * FROM carrera WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Carrera(rs.getInt("id"), rs.getString("codigo"), rs.getString("nombre"), rs.getString("sigla"));
                }
            }
        }
        return null;
    }

    @Override
    public List<Carrera> listar() throws Exception {
        List<Carrera> lista = new ArrayList<>();
        String sql = "SELECT * FROM carrera ORDER BY id ASC;";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Carrera(rs.getInt("id"), rs.getString("codigo"), rs.getString("nombre"), rs.getString("sigla")));
            }
        }
        return lista;
    }

    @Override
    public void asignarUsuarioCarrera(int usuarioId, int carreraId) throws Exception {
        String sql = "INSERT INTO usuario_carrera (usuario_id, carrera_id) VALUES (?, ?) ON CONFLICT DO NOTHING;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            ps.setInt(2, carreraId);
            ps.executeUpdate();
        }
    }

    @Override
    public void asociarMateria(int carreraId, int materiaId, int nivelAcademico) throws Exception {
        String sql = "INSERT INTO carrera_materia (carrera_id, materia_id, nivel_academico) VALUES (?, ?, ?) "
                + "ON CONFLICT (carrera_id, materia_id) DO UPDATE SET nivel_academico = EXCLUDED.nivel_academico;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, carreraId);
            ps.setInt(2, materiaId);
            ps.setInt(3, nivelAcademico);
            ps.executeUpdate();
        }
    }
}
