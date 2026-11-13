package com.example;

import com.example.Adaptadores.Entrada.EmailCommandParser;
import com.example.Dominio.ComandoDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EmailCommandParserTest {

    private EmailCommandParser parser;

    @BeforeEach
    public void setup() {
        parser = new EmailCommandParser();
    }

    private String crearCorreo(String remitente, String subject) {
        return "Return-Path: <" + remitente + ">\n"
             + "Subject: " + subject + "\n"
             + "\n"
             + "Cuerpo del mensaje de prueba";
    }

    @Test
    public void testParsearHelp() {
        String raw = crearCorreo("auxiliar@uagrm.edu.bo", "HELP");
        ComandoDTO cmd = parser.parsearCuerpo(raw);
        assertNotNull(cmd);
        assertEquals("HELP", cmd.accion);
        assertEquals("auxiliar@uagrm.edu.bo", cmd.emailRemitente);
    }

    @Test
    public void testParsearListarLaboratorios() {
        String raw = crearCorreo("director@uagrm.edu.bo", "LISTARLABORATORIOS[*]");
        ComandoDTO cmd = parser.parsearCuerpo(raw);
        assertNotNull(cmd);
        assertEquals("LISTAR", cmd.accion);
        assertEquals("LABORATORIOS", cmd.entidadObjetivo);
    }

    @Test
    public void testParsearCreateReserva() {
        String raw = crearCorreo("docente@uagrm.edu.bo", "CREATERESERVAS[4, 1, 1, 2026-10-25, Examen Practico]");
        ComandoDTO cmd = parser.parsearCuerpo(raw);
        assertNotNull(cmd);
        assertEquals("CREATE", cmd.accion);
        assertEquals("RESERVAS", cmd.entidadObjetivo);
        assertEquals("4, 1, 1, 2026-10-25, Examen Practico", cmd.parametros.get("raw"));
    }

    @Test
    public void testParsearComandoDirectoDisponibilidad() {
        String raw = crearCorreo("estudiante@uagrm.edu.bo", "DISPONIBILIDAD[1, LUNES]");
        ComandoDTO cmd = parser.parsearCuerpo(raw);
        assertNotNull(cmd);
        assertEquals("DISPONIBILIDAD", cmd.accion);
        assertEquals("1, LUNES", cmd.parametros.get("raw"));
    }

    @Test
    public void testParsearReporteAsistencia() {
        String raw = crearCorreo("director@uagrm.edu.bo", "REPORTEASISTENCIA[*]");
        ComandoDTO cmd = parser.parsearCuerpo(raw);
        assertNotNull(cmd);
        assertEquals("REPORTE", cmd.accion);
        assertEquals("ASISTENCIA", cmd.entidadObjetivo);
    }
}
