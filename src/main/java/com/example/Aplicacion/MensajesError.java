package com.example.Aplicacion;

import java.sql.SQLException;

/**
 * Utilidad para traducir excepciones a mensajes en español y mostrarlos
 * destacados en la terminal (base de datos, POP, SMTP y red).
 *
 * <p>Es la contraparte de {@link ErrorCentralizador}: mientras ese genera la
 * tarjeta HTML para el cliente por email, esta clase imprime en la consola
 * del operador un bloque visual con el error traducido y el detalle técnico
 * real (útil para soporte/diagnóstico).</p>
 */
public final class MensajesError {

    public static final String PREFIJO_DETALLE = "Detalle del error: ";

    private static final String RESET = "\u001B[0m";
    private static final String NEGRITA = "\u001B[1m";
    private static final String ROJO = "\u001B[31m";
    private static final String ROJO_FONDO = "\u001B[41m";
    private static final String AMARILLO = "\u001B[33m";
    private static final String CYAN = "\u001B[36m";

    private MensajesError() {
        // Clase de utilidad estática
    }

    /**
     * Mensaje amigable para mostrar al cliente (o en logs): contexto + detalle traducido.
     *
     * @param contexto verbo/acción que falló, en infinitivo (ej. "enviar el correo").
     */
    public static String paraCliente(String contexto, Throwable e) {
        return "No se pudo " + contexto + ". Intente nuevamente o contacte al administrador.\n"
                + PREFIJO_DETALLE + traducir(e);
    }

    public static String paraCliente(Throwable e) {
        return paraCliente("completar la operación", e);
    }

    /**
     * Desenrolla la cadena de causas hasta la raíz (la excepción que originó el problema).
     */
    private static Throwable desenrollarCausa(Throwable e) {
        Throwable causa = e;
        while (causa.getCause() != null && causa.getCause() != causa) {
            causa = causa.getCause();
        }
        return causa;
    }

    /**
     * Traduce la excepción raíz a un mensaje entendible en español.
     */
    public static String traducir(Throwable e) {
        if (e == null) {
            return "Error desconocido.";
        }
        Throwable causa = desenrollarCausa(e);
        String msg = causa.getMessage() == null ? "" : causa.getMessage();
        String bajo = msg.toLowerCase();

        if (causa instanceof java.net.UnknownHostException
                || bajo.contains("unknown host")
                || bajo.contains("temporary failure in name resolution")
                || bajo.contains("no address associated with hostname")) {
            return "No se pudo resolver el servidor. Verifique que la IP o el dominio estén bien configurados.";
        }
        if (causa instanceof java.net.SocketTimeoutException
                || bajo.contains("timed out")
                || bajo.contains("timeout")) {
            return "Se agotó el tiempo de espera al conectar con el servidor.";
        }
        if (bajo.contains("relaying denied")
                || bajo.contains("relay access denied")
                || bajo.contains("ip name lookup failed")) {
            return "El servidor rechazó el envío del correo (Relaying denied): la IP no está autorizada para usar el servidor. "
                    + "Contacte al administrador del servidor para que habilite el rango de IP.";
        }
        if (causa instanceof java.net.ConnectException
                || bajo.contains("refused")
                || bajo.contains("connection attempt failed")
                || bajo.contains("no route to host")) {
            return "Conexión rechazada por el servidor. Verifique la IP, el puerto y que el servicio esté activo.";
        }
        if (bajo.contains("unreachable")) {
            return "La red no está disponible o el servidor está inaccesible.";
        }
        if (causa instanceof java.net.SocketException
                || bajo.contains("broken pipe")
                || bajo.contains("socket closed")
                || bajo.contains("connection reset")) {
            return "La conexión con el servidor se cortó de forma inesperada.";
        }
        if (causa instanceof SQLException) {
            SQLException sqle = (SQLException) causa;
            String estado = sqle.getSQLState();
            if ("28P01".equals(estado)
                    || bajo.contains("password authentication failed")
                    || bajo.contains("password fall")
                    || bajo.contains("autentificaci")) {
                return "Autenticación de base de datos fallida. Revise el usuario y la contraseña.";
            }
            if ("3D000".equals(estado)
                    || bajo.contains("does not exist")
                    || bajo.contains("no existe")) {
                return "La base de datos indicada no existe en el servidor.";
            }
            return "Ocurrió un error de base de datos: " + (msg.isEmpty() ? estado : msg);
        }
        return "Ocurrió un error inesperado: " + (msg.isEmpty() ? causa.getClass().getSimpleName() : msg);
    }

    /**
     * Indica si la excepción (o su causa raíz) es de red/POP/SMTP, para destacar
     * el error con un banner más llamativo.
     */
    public static boolean esErrorDeRed(Throwable e) {
        if (e == null) {
            return false;
        }
        Throwable causa = desenrollarCausa(e);
        String msg = causa.getMessage() == null ? "" : causa.getMessage().toLowerCase();
        return causa instanceof java.net.UnknownHostException
                || causa instanceof java.net.ConnectException
                || causa instanceof java.net.SocketException
                || causa instanceof java.net.SocketTimeoutException
                || msg.contains("refused")
                || msg.contains("unknown host")
                || msg.contains("temporary failure in name resolution")
                || msg.contains("unreachable")
                || msg.contains("timed out")
                || msg.contains("no route to host")
                || msg.contains("no address associated with hostname")
                || msg.contains("relaying denied")
                || msg.contains("relay access denied")
                || msg.contains("ip name lookup failed");
    }

    /**
     * Imprime en la terminal un bloque destacado con el error traducido al español.
     * Usa System.err y códigos ANSI: borde y título en rojo (con fondo rojo si es
     * error de red), detalle amigable en amarillo y detalle técnico en cian.
     */
    public static void imprimirErrorTerminal(String titulo, Throwable e) {
        boolean esRed = esErrorDeRed(e);
        String detalle = traducir(e);
        String crudo = e == null ? "-" : e.toString();
        String borde = "=".repeat(esRed ? 82 : 66);
        String colorBorde = esRed ? ROJO_FONDO + NEGRITA + ROJO : ROJO;
        String tituloFinal = esRed ? "  !!!  " + titulo + "  !!!" : "  " + titulo;

        System.err.println();
        System.err.println(colorBorde + borde + RESET);
        System.err.println(colorBorde + tituloFinal + RESET);
        System.err.println(colorBorde + borde + RESET);
        System.err.println(AMARILLO + "Detalle: " + detalle + RESET);
        System.err.println(CYAN + "Técnico: " + crudo + RESET);
        System.err.println(colorBorde + borde + RESET);
        System.err.println();
    }
}
