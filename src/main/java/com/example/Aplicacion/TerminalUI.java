package com.example.Aplicacion;

import com.example.Dominio.ComandoDTO;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * UI y logger detallado de terminal para el sistema de gestión de laboratorios.
 *
 * Muestra información visual estructurada para:
 * 1. Lo que llega (correo entrante, remitente, subject, acción, parámetros).
 * 2. Errores de procesamiento (categoría, entidad, causa raíz, detalle técnico).
 * 3. Respuesta que se envía (destinatario, estado, tipo, resumen de contenido).
 */
public class TerminalUI {

    // Códigos ANSI
    private static final String RESET = "\u001B[0m";
    private static final String NEGRITA = "\u001B[1m";
    private static final String ROJO = "\u001B[31m";
    private static final String ROJO_FONDO = "\u001B[41m";
    private static final String VERDE = "\u001B[32m";
    private static final String AMARILLO = "\u001B[33m";
    private static final String AZUL = "\u001B[34m";
    private static final String MAGENTA = "\u001B[35m";
    private static final String CIAN = "\u001B[36m";
    private static final String CIAN_FONDO = "\u001B[46m";
    private static final String BLANCO = "\u001B[37m";
    private static final String GRIS = "\u001B[90m";

    private static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final int ANCHO_PANEL = 76;

    private static final boolean COLOR = detectarColor();

    private TerminalUI() {}

    private static boolean detectarColor() {
        if (System.getenv("NO_COLOR") != null) return false;
        String term = System.getenv("TERM");
        if (term != null && "dumb".equalsIgnoreCase(term)) return false;
        return true;
    }

    private static String now() {
        return LocalDateTime.now().format(FORMATO_FECHA_HORA);
    }

    private static String repetir(String s, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) sb.append(s);
        return sb.toString();
    }

    private static String padRight(String s, int n) {
        if (s == null) s = "";
        if (s.length() >= n) return s.substring(0, n);
        return s + repetir(" ", n - s.length());
    }

    // =========================================================================
    // 1. LOG DE LLEGADA (CORREO Y COMANDO ENTRANTE)
    // =========================================================================
    public static void logLlegadaCorreo(String remitente, String subjectOriginal, ComandoDTO cmd) {
        String borde = repetir("═", ANCHO_PANEL);
        String sep = repetir("─", ANCHO_PANEL);

        System.out.println();
        System.out.println(CIAN + "╔" + borde + "╗" + RESET);
        System.out.println(CIAN + "║ " + NEGRITA + "📥 CORREO ENTRANTE RECIBIDO EN POP3" + RESET + CIAN
                + repetir(" ", ANCHO_PANEL - 35) + "║" + RESET);
        System.out.println(CIAN + "╠" + borde + "╣" + RESET);
        System.out.println(CIAN + "║ " + BLANCO + "Timestamp : " + GRIS + now() + CIAN
                + repetir(" ", Math.max(0, ANCHO_PANEL - 13 - now().length())) + "║" + RESET);
        System.out.println(CIAN + "║ " + BLANCO + "Remitente : " + VERDE + padRight(remitente != null ? remitente : "(desconocido)", ANCHO_PANEL - 13) + CIAN + "║" + RESET);
        System.out.println(CIAN + "║ " + BLANCO + "Asunto    : " + AMARILLO + padRight(subjectOriginal != null ? subjectOriginal : "(vacío)", ANCHO_PANEL - 13) + CIAN + "║" + RESET);
        System.out.println(CIAN + "╟" + sep + "╢" + RESET);

        if (cmd != null) {
            String accionStr = cmd.accion != null ? cmd.accion : "DESCONOCIDO";
            String entidadStr = cmd.entidadObjetivo != null && !cmd.entidadObjetivo.isEmpty() ? cmd.entidadObjetivo : "(N/A)";
            System.out.println(CIAN + "║ " + NEGRITA + "Comando   : " + RESET + MAGENTA + "ACCIÓN: " + accionStr + "  |  ENTIDAD: " + entidadStr + CIAN
                    + repetir(" ", Math.max(0, ANCHO_PANEL - 13 - ("ACCIÓN: " + accionStr + "  |  ENTIDAD: " + entidadStr).length())) + "║" + RESET);

            if (cmd.parametros != null && !cmd.parametros.isEmpty()) {
                System.out.println(CIAN + "║ " + BLANCO + "Parámetros: " + RESET);
                for (Map.Entry<String, String> e : cmd.parametros.entrySet()) {
                    String paramLine = "    • " + e.getKey() + " = " + e.getValue();
                    System.out.println(CIAN + "║ " + GRIS + padRight(paramLine, ANCHO_PANEL - 2) + CIAN + "║" + RESET);
                }
            } else {
                System.out.println(CIAN + "║ " + GRIS + padRight("Parámetros: (ninguno)", ANCHO_PANEL - 2) + CIAN + "║" + RESET);
            }
        } else {
            System.out.println(CIAN + "║ " + ROJO + padRight("No se pudo interpretar ningún comando del mensaje.", ANCHO_PANEL - 2) + CIAN + "║" + RESET);
        }

        System.out.println(CIAN + "╚" + borde + "╝" + RESET);
        System.out.println();
    }

    // =========================================================================
    // 2. LOG DE ERRORES DE PROCESAMIENTO
    // =========================================================================
    public static void logErrorProcesamiento(ComandoDTO cmd, Exception e) {
        String borde = repetir("═", ANCHO_PANEL);
        String sep = repetir("─", ANCHO_PANEL);

        System.err.println();
        System.err.println(ROJO + "╔" + borde + "╗" + RESET);
        System.err.println(ROJO + "║ " + NEGRITA + "❌ ERROR DE PROCESAMIENTO" + RESET + ROJO
                + repetir(" ", ANCHO_PANEL - 25) + "║" + RESET);
        System.err.println(ROJO + "╠" + borde + "╣" + RESET);

        if (cmd != null) {
            String comandoDesc = cmd.accion + " " + (cmd.entidadObjetivo != null ? cmd.entidadObjetivo : "");
            System.err.println(ROJO + "║ " + BLANCO + "Comando   : " + AMARILLO + padRight(comandoDesc, ANCHO_PANEL - 13) + ROJO + "║" + RESET);
            System.err.println(ROJO + "║ " + BLANCO + "Usuario   : " + GRIS + padRight(cmd.emailRemitente != null ? cmd.emailRemitente : "?", ANCHO_PANEL - 13) + ROJO + "║" + RESET);
        }

        String clasificacion = e != null ? e.getClass().getSimpleName() : "Desconocido";
        String detalle = MensajesError.traducir(e);
        String mensajeTecnico = e != null && e.getMessage() != null ? e.getMessage() : "Sin mensaje técnico";

        System.err.println(ROJO + "╟" + sep + "╢" + RESET);
        System.err.println(ROJO + "║ " + NEGRITA + "Tipo Error: " + RESET + ROJO + padRight(clasificacion, ANCHO_PANEL - 13) + ROJO + "║" + RESET);
        System.err.println(ROJO + "║ " + BLANCO + "Motivo    : " + AMARILLO + padRight(detalle, ANCHO_PANEL - 13) + ROJO + "║" + RESET);
        System.err.println(ROJO + "║ " + BLANCO + "Técnico   : " + GRIS + padRight(mensajeTecnico, ANCHO_PANEL - 13) + ROJO + "║" + RESET);

        // Imprimir primera línea de traza donde ocurrió el fallo en el paquete com.example
        if (e != null && e.getStackTrace() != null) {
            for (StackTraceElement ste : e.getStackTrace()) {
                if (ste.getClassName().startsWith("com.example")) {
                    String origen = "Origen    : " + ste.getClassName() + "." + ste.getMethodName() + "(Línea " + ste.getLineNumber() + ")";
                    System.err.println(ROJO + "║ " + GRIS + padRight(origen, ANCHO_PANEL - 2) + ROJO + "║" + RESET);
                    break;
                }
            }
        }

        System.err.println(ROJO + "╚" + borde + "╝" + RESET);
        System.err.println();
    }

    // =========================================================================
    // 3. LOG DE RESPUESTA ENVIADA
    // =========================================================================
    public static void logRespuestaEnviada(String emailDestino, String subject, ResultadoDTO r, String servidorUsado) {
        String borde = repetir("═", ANCHO_PANEL);
        String sep = repetir("─", ANCHO_PANEL);
        boolean exito = r != null && !r.esError();
        String colorPrincipal = exito ? VERDE : ROJO;
        String estadoTexto = exito ? "ÉXITO" : "ERROR / RECHAZO";

        System.out.println();
        System.out.println(colorPrincipal + "╔" + borde + "╗" + RESET);
        System.out.println(colorPrincipal + "║ " + NEGRITA + "📤 RESPUESTA SALIENTE POR EMAIL (" + estadoTexto + ")" + RESET + colorPrincipal
                + repetir(" ", Math.max(0, ANCHO_PANEL - 35 - estadoTexto.length())) + "║" + RESET);
        System.out.println(colorPrincipal + "╠" + borde + "╣" + RESET);
        System.out.println(colorPrincipal + "║ " + BLANCO + "Destinatario : " + CIAN + padRight(emailDestino, ANCHO_PANEL - 16) + colorPrincipal + "║" + RESET);
        System.out.println(colorPrincipal + "║ " + BLANCO + "Asunto       : " + GRIS + padRight(subject, ANCHO_PANEL - 16) + colorPrincipal + "║" + RESET);
        System.out.println(colorPrincipal + "║ " + BLANCO + "Servidor     : " + MAGENTA + padRight(servidorUsado, ANCHO_PANEL - 16) + colorPrincipal + "║" + RESET);
        System.out.println(colorPrincipal + "╟" + sep + "╢" + RESET);

        String tipoRespuesta = r != null ? r.tipo.name() : "NULL";
        String resumenContenido = resumirResultado(r);

        System.out.println(colorPrincipal + "║ " + BLANCO + "Tipo Resumen : " + AMARILLO + padRight(tipoRespuesta, ANCHO_PANEL - 16) + colorPrincipal + "║" + RESET);
        System.out.println(colorPrincipal + "║ " + BLANCO + "Contenido    : " + GRIS + padRight(resumenContenido, ANCHO_PANEL - 16) + colorPrincipal + "║" + RESET);
        System.out.println(colorPrincipal + "╚" + borde + "╝" + RESET);
        System.out.println();
    }

    private static String resumirResultado(ResultadoDTO r) {
        if (r == null) return "(sin resultado)";
        if (r.tipo == ResultadoDTO.Tipo.LISTADO) {
            int count = r.filas != null ? r.filas.size() : 0;
            return "Tabla con " + count + " registro(s)";
        }
        if (r.tipo == ResultadoDTO.Tipo.HTML_CRUDO) {
            if (r.htmlCrudo != null && r.htmlCrudo.contains("SISTEMA DE GESTIÓN")) {
                return "Documento interactivo de Ayuda HELP";
            }
            return "Contenido HTML (" + (r.htmlCrudo != null ? r.htmlCrudo.length() : 0) + " bytes)";
        }
        if (r.tipo == ResultadoDTO.Tipo.EXITO) {
            return r.mensaje != null ? r.mensaje : "Operación completada con éxito";
        }
        if (r.tipo == ResultadoDTO.Tipo.NO_ENCONTRADO) {
            return "No se encontró " + r.entidad + " con ID " + r.idNoEncontrado;
        }
        if (r.tipo == ResultadoDTO.Tipo.PARAMETRO_INVALIDO) {
            return "Parámetro inválido: " + (r.mensaje != null ? r.mensaje : "");
        }
        if (r.tipo == ResultadoDTO.Tipo.REGLA_NEGOCIO || r.tipo == ResultadoDTO.Tipo.ERROR) {
            String m = r.excepcion != null ? r.excepcion.getMessage() : r.mensaje;
            return m != null ? m : "Error en ejecución";
        }
        return r.tipo.name();
    }

    // =========================================================================
    // UTILIDADES GENERALES
    // =========================================================================
    public static void banner(String titulo) {
        String borde = repetir("═", ANCHO_PANEL);
        int lado = Math.max(0, (ANCHO_PANEL - titulo.length()) / 2);
        int resto = Math.max(0, ANCHO_PANEL - lado - titulo.length());
        System.out.println(CIAN + "\n╔" + borde + "╗");
        System.out.println("║" + repetir(" ", lado) + NEGRITA + titulo + RESET + CIAN + repetir(" ", resto) + "║");
        System.out.println("╚" + borde + "╝\n" + RESET);
    }

    public static void info(String msg) {
        System.out.println(GRIS + "[" + now() + "] " + CIAN + "[INFO] " + RESET + msg);
    }

    public static void ok(String msg) {
        System.out.println(GRIS + "[" + now() + "] " + VERDE + "[OK] " + RESET + msg);
    }

    public static void warn(String msg) {
        System.out.println(GRIS + "[" + now() + "] " + AMARILLO + "[WARN] " + RESET + msg);
    }

    public static void error(String msg) {
        System.err.println(GRIS + "[" + now() + "] " + ROJO + "[ERROR] " + RESET + msg);
    }

    public static void comando(String msg) {
        System.out.println(GRIS + "[" + now() + "] " + AMARILLO + "[CMD] ⚙ " + RESET + msg);
    }

    public static void protocolo(String msg) {
        System.out.println(GRIS + "[" + now() + "] [PROTO] " + msg + RESET);
    }

    public static void debug(String msg) {
        if ("true".equals(System.getProperty("terminal.debug"))) {
            System.out.println(GRIS + "[" + now() + "] [DEBUG] " + msg + RESET);
        }
    }
}
