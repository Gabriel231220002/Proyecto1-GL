package com.example.Adaptadores.Salida;

import com.example.Aplicacion.MensajesError;
import com.example.Aplicacion.TerminalUI;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class ClienteSMTP {

    private String servidor;
    private int puerto;
    private String emisor;

    public ClienteSMTP() {
        cargarConfiguracion();
    }

    public ClienteSMTP(String servidor, int puerto, String emisor) {
        this.servidor = servidor;
        this.puerto = puerto;
        this.emisor = emisor;
    }

    private void cargarConfiguracion() {
        Properties props = new Properties();
        String origen = null;

        try (InputStream input = ClienteSMTP.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                props.load(input);
                origen = "classpath:config.properties";
            }
        } catch (Exception ignored) {}

        if (origen == null) {
            Path[] rutas = new Path[] {
                Paths.get("config.properties"),
                Paths.get("src/main/resources/config.properties")
            };
            for (Path ruta : rutas) {
                if (Files.exists(ruta)) {
                    try (InputStream input = new FileInputStream(ruta.toFile())) {
                        props.load(input);
                        origen = ruta.toAbsolutePath().toString();
                        break;
                    } catch (Exception ignored) {}
                }
            }
        }

        this.servidor = props.getProperty("smtp.host", "mail.tecnoweb.org.bo").trim().replace("\"", "").replace("'", "");
        String portStr = props.getProperty("smtp.port", "25").trim().replace("\"", "").replace("'", "");
        try {
            this.puerto = Integer.parseInt(portStr);
        } catch (NumberFormatException e) {
            this.puerto = 25;
        }
        this.emisor = props.getProperty("smtp.sender", props.getProperty("smtp.emisor", "grupo03sc@tecnoweb.org.bo"))
                .trim().replace("\"", "").replace("'", "");

        TerminalUI.info("Cliente SMTP TecnoWeb configurado con emisor: " + this.emisor + " en " + this.servidor + ":" + this.puerto
                + " (Origen: " + (origen != null ? origen : "default") + ")");
    }

    private void enviarComando(OutputStreamWriter salida, BufferedReader entrada, String comando)
            throws IOException {
        salida.write(comando);
        salida.flush();
        String respuesta = leerRespuesta(entrada);

        int codigoRespuesta = Integer.parseInt(respuesta.substring(0, 3));
        if (codigoRespuesta >= 400) {
            throw new IOException(
                    "No se pudo enviar el correo, error durante el comando: " + comando + ".\nError: " + respuesta);
        }
    }

    static protected String leerRespuesta(BufferedReader in) throws IOException {
        StringBuilder lines = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            lines.append(line).append("\r\n");
            if (line.length() > 3 && line.charAt(3) == ' ')
                break;
        }
        if (line == null) {
            throw new IOException("El servidor cerró inesperadamente la conexión SMTP.");
        }
        return lines.toString();
    }

    public void enviarCorreo(String usuarioReceptor, String subject, String mensaje) {
        try (Socket socket = new Socket(servidor, puerto);
             BufferedReader entrada = new BufferedReader(
                     new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             OutputStreamWriter salida = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8)) {

            String linea = entrada.readLine();
            TerminalUI.protocolo("SMTP: " + linea);

            enviarComando(salida, entrada, "HELO " + servidor + "\r\n");
            enviarComando(salida, entrada, "MAIL FROM:<" + emisor + ">\r\n");
            enviarComando(salida, entrada, "RCPT TO:<" + usuarioReceptor + ">\r\n");
            enviarComando(salida, entrada, "DATA\r\n");

            salida.write("Subject: " + subject + "\r\n");
            salida.write("MIME-Version: 1.0\r\n");

            String mensajeFormateado = mensaje != null ? mensaje.replace("\n", "\r\n") : "";
            if (mensajeFormateado.trim().toLowerCase().startsWith("<!doctype html>")) {
                salida.write("Content-Type: text/html; charset=utf-8\r\n");
            } else {
                salida.write("Content-Type: text/plain; charset=utf-8\r\n");
            }
            salida.write("\r\n");
            salida.write(mensajeFormateado + "\r\n");

            salida.write(".\r\n");
            salida.flush();
            leerRespuesta(entrada);

            enviarComando(salida, entrada, "QUIT\r\n");
            TerminalUI.ok("Correo SMTP enviado exitosamente a " + usuarioReceptor);

        } catch (Exception e) {
            MensajesError.imprimirErrorTerminal("ERROR AL ENVIAR CORREO (SMTP TECNOWEB)", e);
        }
    }
}
