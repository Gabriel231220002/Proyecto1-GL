package com.example.Aplicacion;

import java.sql.SQLException;

/**
 * Centraliza la generación de tarjetas HTML de error para las respuestas por email.
 * Clasifica la excepción en un tipo de error y produce una tarjeta con:
 * - Título y mensaje amigable en español (para el cliente final).
 * - Detalle técnico real en gris pequeño (para soporte/diagnóstico).
 * - Color de acento según el tipo de error, legible en fondos claros (Gmail)
 *   y con contraste suficiente en temas oscuros.
 *
 * La tarjeta es un fragmento HTML (div) que NO empieza con &lt;!DOCTYPE html&gt;
 * para que el orquestador la envuelva en la plantilla HTML completa.
 */
public class ErrorCentralizador {

    public enum TipoError {
        CONEXION_BD,
        DUPLICADO,
        RESTRICCION_FK,
        NO_ENCONTRADO,
        PARAMETRO_INVALIDO,
        REGLA_NEGOCIO,
        ERROR_INTERNO
    }

    private ErrorCentralizador() {
        // Clase de utilidad estática
    }

    /**
     * Clasifica la excepción y devuelve la tarjeta HTML de error completa.
     */
    public static String generarTarjeta(Exception e) {
        if (e == null) {
            return construirTarjeta(TipoError.ERROR_INTERNO,
                "Ocurrió un error inesperado al procesar su solicitud. Intente nuevamente.", null);
        }
        TipoError tipo = clasificar(e);
        String detalle = MensajesError.traducir(e);
        return construirTarjeta(tipo, mensajeAmigable(tipo, e), detalle);
    }

    /**
     * Genera la tarjeta de "registro no encontrado" para un recurso dado.
     *
     * @param entidad nombre de la entidad en español (ej. "Aula", "Usuario", "Inscripción").
     * @param id      identificador del registro consultado.
     */
    public static String generarTarjetaNoEncontrado(String entidad, String id) {
        String mensaje = "No se encontró el registro de " + entidad + " con ID " + id + ".";
        return construirTarjeta(TipoError.NO_ENCONTRADO, mensaje, null);
    }

    /**
     * Genera la tarjeta para un comando cuya entidad no existe para la operación enviada
     * (ej. LISTARUSUARIO en vez de LISTARUSUARIOS).
     *
     * @param operacion la operación del comando (LISTAR, GET, CREATE, UPDATE, DELETE).
     * @param entidad   el texto de entidad que se envió (puede estar mal escrito).
     */
    public static String generarTarjetaComandoInvalido(String operacion, String entidad) {
        String mensaje = "El comando enviado no es válido. No existe la entidad '" + entidad
            + "' para la operación " + operacion
            + ". Revise la escritura del comando y consulte la ayuda enviando HELP.";
        return construirTarjeta(TipoError.PARAMETRO_INVALIDO, mensaje, null);
    }

    /**
     * Genera la tarjeta para un comando que no se reconoce o tiene un formato inválido.
     */
    public static String generarTarjetaComandoInvalido() {
        String mensaje = "El comando enviado no se reconoce o tiene un formato inválido. Consulte la ayuda enviando HELP.";
        return construirTarjeta(TipoError.PARAMETRO_INVALIDO, mensaje, null);
    }

    // ------------------------------------------------------------------
    // Clasificación
    // ------------------------------------------------------------------

    private static TipoError clasificar(Exception e) {
        String msg = obtenerMensaje(e);
        if (msg == null) {
            msg = "";
        }
        String m = msg.toLowerCase();
        boolean esSql = esCausaSQL(e);

        if (esSql && (m.contains("onnect") || m.contains("timed out") || m.contains("timeout")
                || m.contains("host no responde"))) {
            return TipoError.CONEXION_BD;
        }
        if (m.contains("duplicate key") || m.contains("unique constraint")) {
            return TipoError.DUPLICADO;
        }
        if (m.contains("foreign key") || (m.contains("violates") && m.contains("constraint"))) {
            return TipoError.RESTRICCION_FK;
        }
        if (e instanceof NumberFormatException || e instanceof java.time.format.DateTimeParseException) {
            return TipoError.PARAMETRO_INVALIDO;
        }
        if (e instanceof IllegalArgumentException) {
            return TipoError.REGLA_NEGOCIO;
        }
        return TipoError.ERROR_INTERNO;
    }

    private static boolean esCausaSQL(Throwable e) {
        Throwable t = e;
        while (t != null) {
            if (t instanceof SQLException) {
                return true;
            }
            t = t.getCause();
        }
        return false;
    }

    private static String obtenerMensaje(Throwable e) {
        Throwable t = e;
        while (t != null) {
            if (t.getMessage() != null && !t.getMessage().trim().isEmpty()) {
                return t.getMessage();
            }
            t = t.getCause();
        }
        return null;
    }

    // ------------------------------------------------------------------
    // Mensajes amigables
    // ------------------------------------------------------------------

    private static String mensajeAmigable(TipoError tipo, Exception e) {
        switch (tipo) {
            case CONEXION_BD:
                return "No se pudo conectar con la base de datos. Intente nuevamente en unos minutos.";
            case DUPLICADO:
                return "Ya existe un registro con esos datos. Verifique que no esté duplicando la información.";
            case RESTRICCION_FK:
                return "No se puede completar la operación porque el registro está siendo usado por otros datos relacionados.";
            case PARAMETRO_INVALIDO:
                return "El comando contiene parámetros inválidos. Revise que los números, fechas y formatos sean correctos.";
            case REGLA_NEGOCIO:
                return "Regla de Negocio: " + obtenerMensaje(e);
            case NO_ENCONTRADO:
                return "No se encontró el registro solicitado.";
            case ERROR_INTERNO:
            default:
                return "Ocurrió un error inesperado al procesar su solicitud. Intente nuevamente.";
        }
    }

    private static String titulo(TipoError tipo) {
        switch (tipo) {
            case CONEXION_BD:
                return "Error de conexión";
            case DUPLICADO:
                return "Registro duplicado";
            case RESTRICCION_FK:
                return "Operación no completada";
            case NO_ENCONTRADO:
                return "Registro no encontrado";
            case PARAMETRO_INVALIDO:
                return "Parámetros inválidos";
            case REGLA_NEGOCIO:
                return "Regla de Negocio";
            case ERROR_INTERNO:
            default:
                return "Error interno";
        }
    }

    private static String color(TipoError tipo) {
        switch (tipo) {
            case CONEXION_BD:
                return "#1a5fb4";
            case DUPLICADO:
                return "#c2410c";
            case RESTRICCION_FK:
                return "#b45309";
            case NO_ENCONTRADO:
                return "#64748b";
            case PARAMETRO_INVALIDO:
                return "#b91c1c";
            case REGLA_NEGOCIO:
                return "#b45309";
            case ERROR_INTERNO:
            default:
                return "#b91c1c";
        }
    }

    private static String colorFondo(TipoError tipo) {
        switch (tipo) {
            case CONEXION_BD:
                return "#eaf3fc";
            case DUPLICADO:
                return "#fff1e7";
            case RESTRICCION_FK:
                return "#fdf4e3";
            case NO_ENCONTRADO:
                return "#f1f5f9";
            case PARAMETRO_INVALIDO:
                return "#fdecec";
            case REGLA_NEGOCIO:
                return "#fdf4e3";
            case ERROR_INTERNO:
            default:
                return "#fdecec";
        }
    }

    // ------------------------------------------------------------------
    // Construcción de la tarjeta HTML
    // ------------------------------------------------------------------

    private static String construirTarjeta(TipoError tipo, String mensajeAmigable, String detalleTecnico) {
        String c = color(tipo);
        String fondo = colorFondo(tipo);

        StringBuilder sb = new StringBuilder();
        sb.append("<div style='border-left: 6px solid ").append(c)
          .append("; background-color: ").append(fondo)
          .append("; padding: 16px 18px; margin: 12px 0; border-radius: 6px;'>");
        sb.append("<div style='font-weight: bold; color: ").append(c)
          .append("; font-size: 15px; margin-bottom: 6px;'>⚠️ ")
          .append(titulo(tipo)).append("</div>");
        sb.append("<p style='color: #111827; margin: 0 0 6px 0; font-size: 14px;'><strong>")
          .append(escaparHtml(mensajeAmigable)).append("</strong></p>");

        if (detalleTecnico != null && !detalleTecnico.trim().isEmpty()) {
            sb.append("<small style='color:#64748b; font-size: 12px;'>Detalle técnico: <span style='color:#334155;'>")
              .append(escaparHtml(detalleTecnico))
              .append("</span></small>");
        }

        sb.append("</div>");
        return sb.toString();
    }

    private static String escaparHtml(String texto) {
        if (texto == null) {
            return "";
        }
        return texto
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;");
    }
}
