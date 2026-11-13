package com.example.Adaptadores.Salida;

import com.example.Aplicacion.TerminalUI;
import com.example.Puertos.IEmailResponderPort;

public class SmtpEmailResponderAdapter implements IEmailResponderPort {

    private final ClienteSMTP clienteSMTP;
    private final ClienteSMTPGoogle clienteSMTPGoogle;
    private final boolean useGoogle;

    public SmtpEmailResponderAdapter(boolean useGoogle) {
        this.clienteSMTP = new ClienteSMTP();
        ClienteSMTPGoogle tempGoogle = null;
        boolean googleSuccess = false;

        if (useGoogle) {
            try {
                tempGoogle = ClienteSMTPGoogle.fromConfig();
                TerminalUI.ok("SMTP Google habilitado para respuestas.");
                googleSuccess = true;
            } catch (Exception e) {
                TerminalUI.warn("SMTP Google no configurado, se forzará SMTP por defecto. Detalle: " + e.getMessage());
            }
        }

        this.clienteSMTPGoogle = tempGoogle;
        this.useGoogle = googleSuccess;
    }

    @Override
    public void enviarRespuesta(String emailDestino, String resultado) {
        if (useGoogle && clienteSMTPGoogle != null) {
            TerminalUI.protocolo("Enviando respuesta vía Google SMTP a: " + emailDestino);
            clienteSMTPGoogle.enviarCorreo(emailDestino, "Resultado de la Consulta", resultado);
        } else {
            TerminalUI.protocolo("Enviando respuesta vía Tecnoweb SMTP a: " + emailDestino);
            clienteSMTP.enviarCorreo(emailDestino, "Resultado de la Consulta", resultado);
        }
    }
}