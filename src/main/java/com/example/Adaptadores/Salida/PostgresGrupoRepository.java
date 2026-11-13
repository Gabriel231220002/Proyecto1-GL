package com.example.Adaptadores.Salida;

import com.example.Dominio.Grupo;
import com.example.Dominio.GrupoHorario;
import com.example.Puertos.IGrupoRepository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostgresGrupoRepository implements IGrupoRepository {

    @Override
    public void guardar(Grupo g) throws Exception {
        String sql = "INSERT INTO grupo (nombre, materia_id, periodo_id, usuario_id) VALUES (?, ?, ?, ?) RETURNING id;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, g.getNombre());
            ps.setInt(2, g.getMateriaId());
            if (g.getPeriodoId() != null) ps.setInt(3, g.getPeriodoId()); else ps.setNull(3, Types.INTEGER);
            if (g.getUsuarioId() != null) ps.setInt(4, g.getUsuarioId()); else ps.setNull(4, Types.INTEGER);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    g.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public void actualizar(Grupo g) throws Exception {
        String sql = "UPDATE grupo SET nombre = ?, materia_id = ?, periodo_id = ?, usuario_id = ? WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, g.getNombre());
            ps.setInt(2, g.getMateriaId());
            if (g.getPeriodoId() != null) ps.setInt(3, g.getPeriodoId()); else ps.setNull(3, Types.INTEGER);
            if (g.getUsuarioId() != null) ps.setInt(4, g.getUsuarioId()); else ps.setNull(4, Types.INTEGER);
            ps.setInt(5, g.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int id) throws Exception {
        String sql = "DELETE FROM grupo WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public Grupo obtenerPorId(int id) throws Exception {
        String sql = "SELECT id, nombre, materia_id, usuario_id, periodo_id FROM grupo WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Integer uId = rs.getObject("usuario_id") != null ? rs.getInt("usuario_id") : null;
                    Integer pId = rs.getObject("periodo_id") != null ? rs.getInt("periodo_id") : null;
                    return new Grupo(rs.getInt("id"), rs.getString("nombre"), rs.getInt("materia_id"), uId, pId);
                }
            }
        }
        return null;
    }

    @Override
    public List<Grupo> listar() throws Exception {
        List<Grupo> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, materia_id, usuario_id, periodo_id FROM grupo ORDER BY id ASC;";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Integer uId = rs.getObject("usuario_id") != null ? rs.getInt("usuario_id") : null;
                Integer pId = rs.getObject("periodo_id") != null ? rs.getInt("periodo_id") : null;
                lista.add(new Grupo(rs.getInt("id"), rs.getString("nombre"), rs.getInt("materia_id"), uId, pId));
            }
        }
        return lista;
    }

    @Override
    public void asignarHorario(GrupoHorario gh) throws Exception {
        String sql = "INSERT INTO grupo_horario (grupo_id, horario_id, estado, es_parcial, fecha_parcial) "
                + "VALUES (?, ?, ?, ?, ?) "
                + "ON CONFLICT (grupo_id, horario_id) DO UPDATE SET "
                + "estado = EXCLUDED.estado, es_parcial = EXCLUDED.es_parcial, fecha_parcial = EXCLUDED.fecha_parcial;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, gh.getGrupoId());
            ps.setInt(2, gh.getHorarioId());
            ps.setBoolean(3, gh.isEstado());
            ps.setBoolean(4, gh.isEsParcial());
            ps.setDate(5, gh.getFechaParcial());
            ps.executeUpdate();
        }
    }

    @Override
    public List<GrupoHorario> listarHorariosPorGrupo(int grupoId) throws Exception {
        List<GrupoHorario> lista = new ArrayList<>();
        String sql = "SELECT gh.*, h.labarotario_id AS laboratorio_id "
                + "FROM grupo_horario gh "
                + "LEFT JOIN horario h ON gh.horario_id = h.id "
                + "WHERE gh.grupo_id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, grupoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Integer labId = rs.getObject("laboratorio_id") != null ? rs.getInt("laboratorio_id") : null;
                    lista.add(new GrupoHorario(0, rs.getInt("grupo_id"), rs.getInt("horario_id"),
                            labId, rs.getBoolean("estado"), rs.getBoolean("es_parcial"), rs.getDate("fecha_parcial")));
                }
            }
        }
        return lista;
    }

    @Override
    public List<GrupoHorario> listarTodosGrupoHorarios() throws Exception {
        List<GrupoHorario> lista = new ArrayList<>();
        String sql = "SELECT gh.*, h.labarotario_id AS laboratorio_id "
                + "FROM grupo_horario gh "
                + "LEFT JOIN horario h ON gh.horario_id = h.id "
                + "ORDER BY gh.grupo_id ASC, gh.horario_id ASC;";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Integer labId = rs.getObject("laboratorio_id") != null ? rs.getInt("laboratorio_id") : null;
                lista.add(new GrupoHorario(0, rs.getInt("grupo_id"), rs.getInt("horario_id"),
                        labId, rs.getBoolean("estado"), rs.getBoolean("es_parcial"), rs.getDate("fecha_parcial")));
            }
        }
        return lista;
    }
}
