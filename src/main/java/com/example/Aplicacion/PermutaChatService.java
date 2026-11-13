package com.example.Aplicacion;

import com.example.Dominio.Chat;
import com.example.Dominio.PermutaTurno;
import com.example.Puertos.IChatRepository;
import com.example.Puertos.IPermutaTurnoRepository;
import java.sql.Date;
import java.util.List;

public class PermutaChatService {

    private final IPermutaTurnoRepository permutaRepo;
    private final IChatRepository chatRepo;

    public PermutaChatService(IPermutaTurnoRepository permutaRepo, IChatRepository chatRepo) {
        this.permutaRepo = permutaRepo;
        this.chatRepo = chatRepo;
    }

    // --- Permutas ---
    public PermutaTurno solicitarPermuta(int solicitanteId, int receptorId, int horarioId,
                                         Date fechaAusencia, String motivo) throws Exception {
        if (solicitanteId == receptorId) {
            throw new IllegalArgumentException("No puedes solicitar una permuta de turno a ti mismo.");
        }
        if (fechaAusencia == null) {
            throw new IllegalArgumentException("Debe especificar la fecha de ausencia.");
        }
        PermutaTurno p = new PermutaTurno(0, solicitanteId, receptorId, fechaAusencia, motivo, "PENDIENTE", null);
        permutaRepo.guardar(p, horarioId);
        return p;
    }

    public void responderPermuta(int permutaId, String estado) throws Exception {
        permutaRepo.actualizarEstado(permutaId, estado.toUpperCase());
    }

    public List<PermutaTurno> listarPermutas() throws Exception {
        return permutaRepo.listar();
    }

    public List<PermutaTurno> listarPermutasUsuario(int usuarioId) throws Exception {
        return permutaRepo.listarPorUsuario(usuarioId);
    }

    // --- Chat Interno ---
    public Chat enviarMensaje(int remitenteId, int destinatarioId, String contenido) throws Exception {
        if (contenido == null || contenido.trim().isEmpty()) {
            throw new IllegalArgumentException("El contenido del mensaje no puede estar vacío.");
        }
        Chat c = new Chat(0, remitenteId, destinatarioId, contenido.trim(), null, false);
        chatRepo.guardar(c);
        return c;
    }

    public List<Chat> listarConversacion(int u1, int u2) throws Exception {
        return chatRepo.listarConversacion(u1, u2);
    }

    public List<Chat> listarMensajesUsuario(int usuarioId) throws Exception {
        return chatRepo.listarMensajesUsuario(usuarioId);
    }
}
