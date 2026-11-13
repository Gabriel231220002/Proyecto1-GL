package com.example.Adaptadores.Entrada;

import com.example.Aplicacion.MensajesError;
import com.example.Aplicacion.TerminalUI;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class ClientePOP {

    private String host;
    private int port;
    private String user;
    private String pass;

    private Socket socket;
    private BufferedReader entrada;
    private DataOutputStream salida;

    public ClientePOP() {
        cargarConfiguracion();
    }

    public ClientePOP(String host, int port, String user, String pass) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.pass = pass;
    }

    private void cargarConfiguracion() {
        Properties props = new Properties();
        String origen = null;

        try (InputStream input = ClientePOP.class.getClassLoader().getResourceAsStream("config.properties")) {
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

        this.host = props.getProperty("pop.host", "mail.tecnoweb.org.bo").trim().replace("\"", "").replace("'", "");
        String portStr = props.getProperty("pop.port", "110").trim().replace("\"", "").replace("'", "");
        try {
            this.port = Integer.parseInt(portStr);
        } catch (NumberFormatException e) {
            this.port = 110;
        }
        this.user = props.getProperty("pop.user", "grupo03sc").trim().replace("\"", "").replace("'", "");
        this.pass = props.getProperty("pop.password", props.getProperty("pop.pass", "grup003grup003*"))
                .trim().replace("\"", "").replace("'", "");

        TerminalUI.info("Cliente POP configurado para " + this.user + "@" + this.host + ":" + this.port
                + " (Origen: " + (origen != null ? origen : "default") + ")");
    }

    public void conectar() throws IOException {
        try {
            socket = new Socket(host, port);
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            salida = new DataOutputStream(socket.getOutputStream());
            String linea = entrada.readLine();
            TerminalUI.protocolo("POP: " + linea);

            enviarComando(salida, entrada, "USER " + user + "\r\n");
            enviarComando(salida, entrada, "PASS " + pass + "\r\n");
        } catch (java.net.SocketException e) {
            MensajesError.imprimirErrorTerminal("ERROR AL CONECTAR AL SERVIDOR POP", e);
            throw new IOException("No se pudo conectar al servidor POP (Socket reset/refused)", e);
        } catch (IOException e) {
            MensajesError.imprimirErrorTerminal("ERROR DE E/S AL CONECTAR AL SERVIDOR POP", e);
            throw e;
        }
    }

    private static String enviarComando(DataOutputStream salida, BufferedReader entrada, String comando)
            throws IOException {
        try {
            if (comando.startsWith("PASS")) {
                TerminalUI.protocolo("POP → PASS [credenciales ocultas]");
            } else {
                TerminalUI.protocolo("POP → " + comando.trim());
            }
            salida.writeBytes(comando);
            String response;
            if (comando.startsWith("RETR") || comando.startsWith("LIST")) {
                response = leerRespuestaMultilinea(entrada);
                return response;
            }
            return entrada.readLine();
        } catch (java.net.SocketException e) {
            MensajesError.imprimirErrorTerminal("ERROR DE CONEXIÓN POP - SOCKET CERRADO", e);
            throw new IOException("Socket cerrado o reset por el servidor: " + e.getMessage(), e);
        } catch (IOException e) {
            MensajesError.imprimirErrorTerminal("ERROR DE E/S EN LA COMUNICACIÓN POP", e);
            throw e;
        }
    }

    static protected String leerRespuestaMultilinea(BufferedReader in) throws IOException {
        StringBuilder lines = new StringBuilder();
        while (true) {
            String line = in.readLine();
            if (line == null)
                throw new IOException("S : Server unawares closed the connection.");
            if (line.equals("."))
                break;
            if (line.startsWith("."))
                line = line.substring(1);
            lines.append("\n").append(line);
        }
        return lines.toString();
    }

    public int obtenerTotalDeCorreos() throws IOException {
        String response = enviarComando(salida, entrada, "STAT\r\n");
        response = response.substring(4, response.length());
        int i = 1;
        while (response.charAt(i) != ' ') {
            i++;
        }
        response = response.substring(0, i);
        return Integer.parseInt(response);
    }

    public void desconectar() {
        try {
            enviarComando(salida, entrada, "QUIT\r\n");
            entrada.close();
            salida.close();
            socket.close();
            TerminalUI.ok("Conexión POP finalizada.");
        } catch (IOException e) {
            MensajesError.imprimirErrorTerminal("ERROR AL CERRAR LA CONEXIÓN POP", e);
        }
    }

    public String obtenerCorreo(int posicion) {
        try {
            return enviarComando(salida, entrada, "RETR " + posicion + "\r\n");
        } catch (IOException e) {
            MensajesError.imprimirErrorTerminal("ERROR AL OBTENER EL CORREO POP", e);
            return null;
        }
    }

    public String obtenerCorreoYEliminar(int posicion) {
        try {
            String correo = enviarComando(salida, entrada, "RETR " + posicion + "\r\n");
            enviarComando(salida, entrada, "DELE " + posicion + "\r\n");
            return correo;
        } catch (IOException e) {
            MensajesError.imprimirErrorTerminal("ERROR AL OBTENER EL CORREO Y MARCAR PARA ELIMINACIÓN (POP)", e);
            return null;
        }
    }
}
