package com.example.Adaptadores.Entrada;

import com.example.Aplicacion.TerminalUI;
import com.example.Dominio.ComandoDTO;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailCommandParser {

    private static final Pattern LISTAR_PATRON = Pattern.compile("^LISTAR([A-Z0-9_]+)\\[(.*)\\]$", Pattern.CASE_INSENSITIVE);
    private static final Pattern CREATE_PATRON = Pattern.compile("^CREATE([A-Z0-9_]+)\\[(.+)\\]$", Pattern.CASE_INSENSITIVE);
    private static final Pattern UPDATE_PATRON = Pattern.compile("^UPDATE([A-Z0-9_]+)\\[(.+)\\]$", Pattern.CASE_INSENSITIVE);
    private static final Pattern DELETE_PATRON = Pattern.compile("^DELETE([A-Z0-9_]+)\\[(\\d+)\\]$", Pattern.CASE_INSENSITIVE);
    private static final Pattern GET_PATRON = Pattern.compile("^GET([A-Z0-9_]+)\\[(\\d+)\\]$", Pattern.CASE_INSENSITIVE);
    private static final Pattern REPORTE_PATRON = Pattern.compile("^REPORTE([A-Z0-9_]+)\\[(.*)\\]$", Pattern.CASE_INSENSITIVE);
    private static final Pattern COMANDO_DIRECTO_PATRON = Pattern.compile("^([A-Z0-9_]+)\\[(.*)\\]$", Pattern.CASE_INSENSITIVE);

    public ComandoDTO parsearCuerpo(String rawEmail) {
        if (rawEmail == null || rawEmail.isEmpty()) {
            return null;
        }

        String subject = extraerSubject(rawEmail);
        String remitente = extraerRemitente(rawEmail);

        if (subject == null || remitente == null) {
            return null;
        }

        String decodedSubject = decodificarMIME(subject).trim();

        ComandoDTO cmd = new ComandoDTO();
        cmd.emailRemitente = remitente;
        cmd.parametros = new HashMap<>();

        Matcher matcher;
        if (decodedSubject.equalsIgnoreCase("HELP")) {
            cmd.accion = "HELP";
            cmd.entidadObjetivo = "";
        } else if ((matcher = LISTAR_PATRON.matcher(decodedSubject)).matches()) {
            cmd.accion = "LISTAR";
            cmd.entidadObjetivo = matcher.group(1).toUpperCase();
            cmd.parametros.put("raw", matcher.group(2).trim());
        } else if ((matcher = CREATE_PATRON.matcher(decodedSubject)).matches()) {
            cmd.accion = "CREATE";
            cmd.entidadObjetivo = matcher.group(1).toUpperCase();
            cmd.parametros.put("raw", matcher.group(2).trim());
        } else if ((matcher = UPDATE_PATRON.matcher(decodedSubject)).matches()) {
            cmd.accion = "UPDATE";
            cmd.entidadObjetivo = matcher.group(1).toUpperCase();
            cmd.parametros.put("raw", matcher.group(2).trim());
        } else if ((matcher = DELETE_PATRON.matcher(decodedSubject)).matches()) {
            cmd.accion = "DELETE";
            cmd.entidadObjetivo = matcher.group(1).toUpperCase();
            cmd.parametros.put("raw", matcher.group(2).trim());
        } else if ((matcher = GET_PATRON.matcher(decodedSubject)).matches()) {
            cmd.accion = "GET";
            cmd.entidadObjetivo = matcher.group(1).toUpperCase();
            cmd.parametros.put("raw", matcher.group(2).trim());
        } else if ((matcher = REPORTE_PATRON.matcher(decodedSubject)).matches()) {
            cmd.accion = "REPORTE";
            cmd.entidadObjetivo = matcher.group(1).toUpperCase();
            cmd.parametros.put("raw", matcher.group(2).trim());
        } else if ((matcher = COMANDO_DIRECTO_PATRON.matcher(decodedSubject)).matches()) {
            // Comandos semánticos especiales como DISPONIBILIDAD[...], CHAT[...], ASIGNARHORARIO[...]
            String operacionDirecta = matcher.group(1).toUpperCase();
            cmd.accion = operacionDirecta;
            cmd.entidadObjetivo = operacionDirecta;
            cmd.parametros.put("raw", matcher.group(2).trim());
        } else {
            cmd.accion = "DESCONOCIDO";
            cmd.entidadObjetivo = "";
            cmd.parametros.put("raw", decodedSubject);
        }

        // Registrar en terminal log estructurado de correo entrante
        TerminalUI.logLlegadaCorreo(remitente, decodedSubject, cmd);

        return cmd;
    }

    private String extraerSubject(String rawEmail) {
        StringBuilder subjectBuilder = new StringBuilder();
        boolean subjectFound = false;

        for (String line : rawEmail.split("\n")) {
            line = line.replace("\r", "");
            if (line.startsWith("Subject:")) {
                subjectBuilder.append(line.substring(8).trim());
                subjectFound = true;
            } else if (subjectFound && (line.startsWith(" ") || line.startsWith("\t"))) {
                subjectBuilder.append(" ").append(line.trim());
            } else if (subjectFound) {
                break;
            }
        }
        return subjectBuilder.length() > 0 ? subjectBuilder.toString() : null;
    }

    private String extraerRemitente(String rawEmail) {
        for (String line : rawEmail.split("\n")) {
            line = line.replace("\r", "");
            if (line.startsWith("Return-Path:")) {
                int start = line.indexOf('<') + 1;
                int end = line.indexOf('>');
                if (start > 0 && end > start) {
                    return line.substring(start, end).trim();
                }
            }
        }
        for (String line : rawEmail.split("\n")) {
            line = line.replace("\r", "");
            if (line.startsWith("From:")) {
                int start = line.indexOf('<') + 1;
                int end = line.indexOf('>');
                if (start > 0 && end > start) {
                    return line.substring(start, end).trim();
                } else {
                    String from = line.substring(5).trim();
                    if (from.contains(" ")) {
                        from = from.substring(from.lastIndexOf(" ") + 1);
                    }
                    return from;
                }
            }
        }
        return null;
    }

    private String decodificarMIME(String encoded) {
        if (encoded == null || encoded.isEmpty()) {
            return encoded;
        }

        Pattern pattern = Pattern.compile("=\\?([^?]+)\\?([BbQq])\\?([^?]*)\\?=", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(encoded);
        StringBuffer decoded = new StringBuffer();

        while (matcher.find()) {
            String charset = matcher.group(1);
            String encoding = matcher.group(2).toUpperCase();
            String text = matcher.group(3);

            String decodedText;
            try {
                if ("B".equals(encoding)) {
                    byte[] bytes = Base64.getDecoder().decode(text);
                    decodedText = new String(bytes, charset);
                } else if ("Q".equals(encoding)) {
                    decodedText = decodeQuotedPrintable(text, charset);
                } else {
                    decodedText = text;
                }
            } catch (Exception e) {
                decodedText = text;
            }

            matcher.appendReplacement(decoded, Matcher.quoteReplacement(decodedText));
        }
        matcher.appendTail(decoded);

        return decoded.toString().replaceAll("\\s+", " ").trim();
    }

    private String decodeQuotedPrintable(String text, String charset) throws UnsupportedEncodingException {
        text = text.replace("_", " ");
        StringBuilder result = new StringBuilder();
        byte[] bytes = new byte[text.length()];
        int byteIndex = 0;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '=' && i + 2 < text.length()) {
                try {
                    int hex = Integer.parseInt(text.substring(i + 1, i + 3), 16);
                    bytes[byteIndex++] = (byte) hex;
                    i += 2;
                } catch (NumberFormatException e) {
                    bytes[byteIndex++] = (byte) c;
                }
            } else {
                bytes[byteIndex++] = (byte) c;
            }
        }

        return new String(bytes, 0, byteIndex, charset);
    }
}