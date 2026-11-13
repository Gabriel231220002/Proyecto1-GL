package com.example.Adaptadores.Salida;

import com.example.Aplicacion.MensajesError;
import com.example.Aplicacion.TerminalUI;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Properties;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class ClienteSMTPGoogle {

    private static final String SERVIDOR = "smtp.gmail.com";
    private static final int PUERTO = 465;

    private final String emisor;
    private final String appPassword;

    public ClienteSMTPGoogle(String emisor, String appPassword) {
        this.emisor = emisor;
        this.appPassword = appPassword;
    }

    public static ClienteSMTPGoogle fromConfig() {
        Properties props = new Properties();
        String origen = null;

        try (InputStream input = ClienteSMTPGoogle.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                props.load(input);
                origen = "classpath:config.properties";
            }
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer config.properties del classpath: " + e.getMessage(), e);
        }

        if (origen == null) {
            Path[] rutasCandidatas = new Path[] {
                    Paths.get("config.properties"),
                    Paths.get("src/main/resources/config.properties"),
                    Paths.get("src/main/java/com/example/resources/config.properties"),
                    Paths.get("src/main/java/org/bebidas/resources/config.properties")
            };

            for (Path ruta : rutasCandidatas) {
                if (Files.exists(ruta)) {
                    try (InputStream input = new FileInputStream(ruta.toFile())) {
                        props.load(input);
                        origen = ruta.toAbsolutePath().toString();
                        break;
                    } catch (IOException e) {
                        throw new IllegalStateException("No se pudo leer " + ruta + ": " + e.getMessage(), e);
                    }
                }
            }
        }

        if (origen == null) {
            throw new IllegalStateException("No se encontró config.properties (classpath ni rutas locales).");
        }

        String user = props.getProperty("smtp.gmail.user", "").trim();
        String password = props.getProperty("smtp.gmail.appPassword", "").trim();

        if (user.isEmpty() || password.isEmpty()) {
            throw new IllegalStateException("Faltan smtp.gmail.user o smtp.gmail.appPassword en config.properties.");
        }

        TerminalUI.info("SMTP Google cargado desde: " + origen);

        return new ClienteSMTPGoogle(user, password);
    }

    public void enviarCorreo(String usuarioReceptor, String subject, String mensaje) {
        try (SSLSocket socket = (SSLSocket) SSLSocketFactory.getDefault().createSocket(SERVIDOR, PUERTO);
                BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                OutputStreamWriter salida = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8)) {

            leerYValidarRespuesta(entrada, "220", "Conexión inicial");

            enviarComando(salida, entrada, "EHLO localhost\r\n", "250", "EHLO");
            enviarComando(salida, entrada, "AUTH LOGIN\r\n", "334", "AUTH LOGIN");
            enviarComando(salida, entrada, base64(emisor) + "\r\n", "334", "Usuario SMTP");
            enviarComando(salida, entrada, base64(appPassword) + "\r\n", "235", "Password SMTP");

            enviarComando(salida, entrada, "MAIL FROM:<" + emisor + ">\r\n", "250", "MAIL FROM");
            enviarComando(salida, entrada, "RCPT TO:<" + usuarioReceptor + ">\r\n", "250", "RCPT TO");
            enviarComando(salida, entrada, "DATA\r\n", "354", "DATA");

            salida.write("From: " + emisor + "\r\n");
            salida.write("To: " + usuarioReceptor + "\r\n");
            salida.write("Subject: " + subject + "\r\n");
            salida.write("MIME-Version: 1.0\r\n");

            TerminalUI.debug("[ClienteSMTPGoogle] enviarCorreo a=" + usuarioReceptor + " subject=" + subject);
            TerminalUI.debug("[ClienteSMTPGoogle] mensaje tiene 'data:image/png;base64,' = " + mensaje.contains("data:image/png;base64,"));

            boolean tieneBase64 = mensaje != null && mensaje.contains("data:image/png;base64,");
            if (tieneBase64) {
                int startIndex = mensaje.indexOf("data:image/png;base64,") + "data:image/png;base64,".length();
                TerminalUI.debug("[ClienteSMTPGoogle] startIndex=" + startIndex + " (contexto previo: '...base64," + mensaje.substring(startIndex, Math.min(startIndex + 30, mensaje.length())) + "...')");
                // Buscar el fin del base64: tomar la PRIMERA comilla (simple o doble) que aparezca
                int endSingle = mensaje.indexOf("'", startIndex);
                int endDouble = mensaje.indexOf("\"", startIndex);
                int endIndex;
                if (endSingle >= 0 && endDouble >= 0) {
                    endIndex = Math.min(endSingle, endDouble);
                } else if (endSingle >= 0) {
                    endIndex = endSingle;
                } else {
                    endIndex = endDouble;
                }
                TerminalUI.debug("[ClienteSMTPGoogle] endIndex=" + endIndex + " (endSingle=" + endSingle + ", endDouble=" + endDouble + ")");
                if (endIndex > startIndex) {
                    String base64Image = mensaje.substring(startIndex, endIndex);
                    int base64Len = base64Image.length();
                    TerminalUI.debug("[ClienteSMTPGoogle] base64 extraído, length=" + base64Len + " primeros 50=" + base64Image.substring(0, Math.min(50, base64Len)));
                    String htmlConCid = mensaje.replace("data:image/png;base64," + base64Image, "cid:qrCodeImage");
                    String boundary = "----=_Part_0_" + System.currentTimeMillis();

                    salida.write("Content-Type: multipart/related; boundary=\"" + boundary + "\"\r\n");
                    salida.write("\r\n");
                    salida.write("--" + boundary + "\r\n");
                    salida.write("Content-Type: text/html; charset=utf-8\r\n");
                    salida.write("Content-Transfer-Encoding: 8bit\r\n");
                    salida.write("\r\n");
                    salida.write(htmlConCid.replace("\n", "\r\n") + "\r\n");
                    salida.write("--" + boundary + "\r\n");
                    salida.write("Content-Type: image/png; name=\"qr.png\"\r\n");
                    salida.write("Content-Transfer-Encoding: base64\r\n");
                    salida.write("Content-ID: <qrCodeImage>\r\n");
                    salida.write("Content-Disposition: inline; filename=\"qr.png\"\r\n");
                    salida.write("\r\n");
                    TerminalUI.debug("[ClienteSMTPGoogle] Enviando multipart/related con QR inline");
                    salida.write(base64Image + "\r\n");
                    salida.write("--" + boundary + "--\r\n");
                } else {
                    TerminalUI.warn("[ClienteSMTPGoogle] WARN: no se encontró el fin del base64 (endIndex=" + endIndex + ")");
                    tieneBase64 = false;
                }
            }

            if (!tieneBase64) {
                String mensajeFormateado = mensaje.replace("\n", "\r\n").replace("\r\n.", "\r\n..");
                if (mensaje.trim().toLowerCase().startsWith("<!doctype html>")) {
                    salida.write("Content-Type: text/html; charset=utf-8\r\n");
                } else {
                    salida.write("Content-Type: text/plain; charset=utf-8\r\n");
                }
                salida.write("\r\n");
                salida.write(mensajeFormateado + "\r\n");
            }

            salida.write(".\r\n");
            salida.flush();

            leerYValidarRespuesta(entrada, "250", "Envío del mensaje");
            enviarComando(salida, entrada, "QUIT\r\n", "221", "QUIT");

            TerminalUI.ok("Correo enviado por Gmail a " + usuarioReceptor);
        } catch (Exception e) {
            MensajesError.imprimirErrorTerminal("ERROR AL ENVIAR CORREO POR GMAIL (SMTP)", e);
        }
    }

    private static void enviarComando(OutputStreamWriter salida, BufferedReader entrada, String comando,
            String codigoEsperado, String etapa) throws IOException {
        // Nunca imprimir credenciales (usuario/password base64 del AUTH LOGIN)
        if ("Usuario SMTP".equals(etapa) || "Password SMTP".equals(etapa)) {
            TerminalUI.protocolo("SMTP → [credenciales ocultas]");
        } else {
            TerminalUI.protocolo("SMTP → " + comando.trim());
        }
        salida.write(comando);
        salida.flush();
        leerYValidarRespuesta(entrada, codigoEsperado, etapa);
    }

    private static void leerYValidarRespuesta(BufferedReader in, String codigoEsperado, String etapa)
            throws IOException {
        String respuesta = leerRespuesta(in);
        TerminalUI.protocolo("SMTP ← " + respuesta.trim());
        if (!respuesta.startsWith(codigoEsperado)) {
            throw new IOException("Fallo en " + etapa + ". Esperado " + codigoEsperado + " pero llegó: " + respuesta);
        }
    }

    private static String leerRespuesta(BufferedReader in) throws IOException {
        StringBuilder lines = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            lines.append(line).append("\r\n");
            if (line.length() > 3 && line.charAt(3) == ' ') {
                break;
            }
        }
        if (line == null) {
            throw new IOException("El servidor cerró la conexión inesperadamente.");
        }
        return lines.toString();
    }

    private static String base64(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }
}
