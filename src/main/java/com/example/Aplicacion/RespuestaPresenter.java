package com.example.Aplicacion;

import java.util.List;

/**
 * Convierte un {@link ResultadoDTO} en el HTML que se envía por email.
 *
 * <p>Es el único lugar donde se construye presentación (colores, tablas, tarjetas).
 * La capa de aplicación (services) devuelve datos semánticos y este presenter los
 * transforma en HTML.</p>
 */
public class RespuestaPresenter {

    public String presentar(ResultadoDTO r) {
        if (r == null) {
            return "";
        }
        switch (r.tipo) {
            case EXITO:
                return "<p style='color: #27ae60; font-weight: bold;'>"
                    + escaparHtml(r.mensaje) + "</p>";
            case LISTADO:
                return tabla(r.headers, r.filas);
            case HTML_CRUDO:
                return r.htmlCrudo != null ? r.htmlCrudo : "";
            case NO_ENCONTRADO:
                return ErrorCentralizador.generarTarjetaNoEncontrado(r.entidad, r.idNoEncontrado);
            case PARAMETRO_INVALIDO:
                return "<p style='color: #e74c3c; font-weight: bold;'>"
                    + escaparHtml(r.mensaje) + "</p>";
            case REGLA_NEGOCIO:
                return r.excepcion != null
                    ? ErrorCentralizador.generarTarjeta(r.excepcion)
                    : "<p style='color: #e74c3c; font-weight: bold;'>Regla de Negocio</p>";
            case ERROR:
                return r.excepcion != null
                    ? ErrorCentralizador.generarTarjeta(r.excepcion)
                    : "<p style='color: #e74c3c; font-weight: bold;'>Ocurrió un error inesperado.</p>";
            default:
                return "";
        }
    }

    /** Tabla HTML de listados (antes vivía en ComandoOrchestratorService.formatListToHTML). */
    private String tabla(String[] headers, List<Object[]> filas) {
        if (filas == null || filas.isEmpty()) {
            return "<p style='color: #7f8c8d;'>No hay registros disponibles.</p>";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<table border='1' style='border-collapse: collapse; width: 100%; margin: 10px 0;'>");
        sb.append("<thead><tr style='background-color: #3498db; color: white;'>");
        if (headers != null) {
            for (String h : headers) {
                sb.append("<th style='padding: 8px; text-align: left;'>").append(escaparHtml(h)).append("</th>");
            }
        }
        sb.append("</tr></thead><tbody>");
        for (Object[] fila : filas) {
            sb.append("<tr>");
            if (fila != null) {
                for (Object val : fila) {
                    sb.append("<td style='padding: 8px; border-bottom: 1px solid #ddd;'>")
                      .append(val != null ? escaparHtml(val.toString()).trim() : "NULL")
                      .append("</td>");
                }
            }
            sb.append("</tr>");
        }
        sb.append("</tbody></table>");
        return sb.toString();
    }

    private String escaparHtml(String texto) {
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
