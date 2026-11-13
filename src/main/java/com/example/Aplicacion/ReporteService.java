package com.example.Aplicacion;

import com.example.Adaptadores.Salida.DatabaseConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ReporteService {

    public List<Object[]> reporteAsistenciaAuxiliares() throws Exception {
        List<Object[]> filas = new ArrayList<>();
        String sql = "SELECT u.id, u.nombres || ' ' || u.apellido_p as auxiliar, "
                + "COUNT(a.id) as total_registros, "
                + "SUM(CASE WHEN UPPER(a.estado_asistencia) = 'PRESENTE' THEN 1 ELSE 0 END) as presentes, "
                + "SUM(CASE WHEN UPPER(a.estado_asistencia) = 'FALTA' THEN 1 ELSE 0 END) as faltas, "
                + "SUM(CASE WHEN UPPER(a.estado_asistencia) = 'LICENCIA' THEN 1 ELSE 0 END) as licencias, "
                + "ROUND(100.0 * SUM(CASE WHEN UPPER(a.estado_asistencia) = 'PRESENTE' THEN 1 ELSE 0 END) / NULLIF(COUNT(a.id), 0), 1) || '%' as porcentaje_cumplimiento "
                + "FROM usuario u "
                + "JOIN usuario_rol ur ON u.id = ur.usuario_id "
                + "JOIN rol r ON ur.rol_id = r.id AND UPPER(r.nombre) = 'AUXILIAR' "
                + "LEFT JOIN asistencia a ON u.id = a.usuario_id "
                + "GROUP BY u.id, u.nombres, u.apellido_p "
                + "ORDER BY total_registros DESC, u.apellido_p ASC;";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                filas.add(new Object[]{
                    rs.getInt("id"),
                    rs.getString("auxiliar"),
                    rs.getInt("total_registros"),
                    rs.getInt("presentes"),
                    rs.getInt("faltas"),
                    rs.getInt("licencias"),
                    rs.getString("porcentaje_cumplimiento") != null ? rs.getString("porcentaje_cumplimiento") : "0.0%"
                });
            }
        }
        return filas;
    }

    public List<Object[]> reporteOcupacionLaboratorios() throws Exception {
        List<Object[]> filas = new ArrayList<>();
        String sql = "SELECT l.id, l.nombre, 'Horario: ' || l.hora_apertura || ' - ' || l.hora_cierre as ubicacion, "
                + "COUNT(DISTINCT gh.horario_id) as clases_fijas_semana, "
                + "COUNT(DISTINCT rh.reserva_id) as reservas_registradas "
                + "FROM labarotario l "
                + "LEFT JOIN horario h ON l.id = h.labarotario_id "
                + "LEFT JOIN grupo_horario gh ON h.id = gh.horario_id AND gh.estado = TRUE "
                + "LEFT JOIN reserva_horario rh ON h.id = rh.horario_id AND rh.estado NOT IN ('RECHAZADA', 'CANCELADA') "
                + "GROUP BY l.id, l.nombre, l.hora_apertura, l.hora_cierre "
                + "ORDER BY l.id ASC;";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                filas.add(new Object[]{
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("ubicacion"),
                    rs.getInt("clases_fijas_semana"),
                    rs.getInt("reservas_registradas")
                });
            }
        }
        return filas;
    }

    public List<Object[]> reporteIncidenciasInventario() throws Exception {
        List<Object[]> filas = new ArrayList<>();
        String sql = "SELECT l.nombre as laboratorio, "
                + "COUNT(ti.id) as total_incidencias, "
                + "SUM(CASE WHEN ti.fecha_solucion IS NULL AND ti.usuario_atiende_id IS NULL THEN 1 ELSE 0 END) as abiertas, "
                + "SUM(CASE WHEN ti.fecha_solucion IS NULL AND ti.usuario_atiende_id IS NOT NULL THEN 1 ELSE 0 END) as en_proceso, "
                + "SUM(CASE WHEN ti.fecha_solucion IS NOT NULL OR ti.solucion_aplicada IS NOT NULL THEN 1 ELSE 0 END) as resueltas "
                + "FROM labarotario l "
                + "LEFT JOIN item_inventario ii ON l.id = ii.labarotario_id "
                + "LEFT JOIN ticket_incidencia ti ON ii.id = ti.item_inventario_id "
                + "GROUP BY l.id, l.nombre "
                + "ORDER BY total_incidencias DESC, l.nombre ASC;";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                filas.add(new Object[]{
                    rs.getString("laboratorio"),
                    rs.getInt("total_incidencias"),
                    rs.getInt("abiertas"),
                    rs.getInt("en_proceso"),
                    rs.getInt("resueltas")
                });
            }
        }
        return filas;
    }

    public List<Object[]> reporteEstadoInventario() throws Exception {
        List<Object[]> filas = new ArrayList<>();
        String sql = "SELECT estado_fisico, COUNT(*) as cantidad "
                + "FROM item_inventario "
                + "GROUP BY estado_fisico "
                + "ORDER BY cantidad DESC;";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                filas.add(new Object[]{
                    rs.getString("estado_fisico"),
                    rs.getInt("cantidad")
                });
            }
        }
        return filas;
    }

    public List<Object[]> reporteObjetosPerdidos() throws Exception {
        List<Object[]> filas = new ArrayList<>();
        String sql = "SELECT CASE WHEN estado_devolucion = TRUE THEN 'ENTREGADO' ELSE 'REPORTADO' END as estado, "
                + "COUNT(*) as cantidad "
                + "FROM objeto_perdido "
                + "GROUP BY estado_devolucion "
                + "ORDER BY cantidad DESC;";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                filas.add(new Object[]{
                    rs.getString("estado"),
                    rs.getInt("cantidad")
                });
            }
        }
        return filas;
    }
}
