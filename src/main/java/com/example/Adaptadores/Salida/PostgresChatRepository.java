package com.example.Adaptadores.Salida;

import com.example.Dominio.Chat;
import com.example.Puertos.IChatRepository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostgresChatRepository implements IChatRepository {

    @Override
    public void guardar(Chat c) throws Exception {
        String sql = "INSERT INTO chat (remitente_id, destinatario_id, contenido, fecha_envio, estado_lectura) "
                + "VALUES (?, ?, ?, CURRENT_TIMESTAMP, ?) RETURNING id, fecha_envio;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, c.getRemitenteId());
            ps.setInt(2, c.getDestinatarioId());
            ps.setString(3, c.getContenido());
            ps.setBoolean(4, c.isEstadoLectura());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    c.setId(rs.getInt(1));
                    c.setFechaEnvio(rs.getTimestamp(2));
                }
            }
        }
    }

    @Override
    public List<Chat> listarConversacion(int u1, int u2) throws Exception {
        List<Chat> lista = new ArrayList<>();
        String sql = "SELECT * FROM chat WHERE (remitente_id = ? AND destinatario_id = ?) "
                + "OR (remitente_id = ? AND destinatario_id = ?) ORDER BY fecha_envio ASC;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, u1);
            ps.setInt(2, u2);
            ps.setInt(3, u2);
            ps.setInt(4, u1);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Chat(rs.getInt("id"), rs.getInt("remitente_id"), rs.getInt("destinatario_id"),
                            rs.getString("contenido"), rs.getTimestamp("fecha_envio"), rs.getBoolean("estado_lectura")));
                }
            }
        }
        return lista;
    }

    @Override
    public List<Chat> listarMensajesUsuario(int usuarioId) throws Exception {
        List<Chat> lista = new ArrayList<>();
        String sql = "SELECT * FROM chat WHERE destinatario_id = ? OR remitente_id = ? ORDER BY fecha_envio DESC LIMIT 50;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            ps.setInt(2, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Chat(rs.getInt("id"), rs.getInt("remitente_id"), rs.getInt("destinatario_id"),
                            rs.getString("contenido"), rs.getTimestamp("fecha_envio"), rs.getBoolean("estado_lectura")));
                }
            }
        }
        return lista;
    }

    @Override
    public void marcarComoLeido(int chatId) throws Exception {
        String sql = "UPDATE chat SET estado_lectura = TRUE WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, chatId);
            ps.executeUpdate();
        }
    }
}
