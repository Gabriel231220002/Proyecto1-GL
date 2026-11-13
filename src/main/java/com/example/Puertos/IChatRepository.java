package com.example.Puertos;

import com.example.Dominio.Chat;
import java.util.List;

public interface IChatRepository {
    void guardar(Chat chat) throws Exception;
    List<Chat> listarConversacion(int usuario1, int usuario2) throws Exception;
    List<Chat> listarMensajesUsuario(int usuarioId) throws Exception;
    void marcarComoLeido(int chatId) throws Exception;
}
