package com.example.Aplicacion;

import java.util.List;

/**
 * Resultado semántico de una operación de negocio.
 *
 * <p>Reemplaza el uso de HTML como contrato entre capas: los services devuelven
 * este DTO (qué pasó + datos), y el {@link RespuestaPresenter} se encarga de
 * convertirlo en el HTML que se envía por email. Así la capa de aplicación no
 * conoce nada de presentación.</p>
 */
public class ResultadoDTO {

    public enum Tipo {
        EXITO,
        LISTADO,
        HTML_CRUDO,
        NO_ENCONTRADO,
        PARAMETRO_INVALIDO,
        REGLA_NEGOCIO,
        ERROR
    }

    public final Tipo tipo;
    /** Mensaje amigable para EXITO / REGLA_NEGOCIO / PARAMETRO_INVALIDO. */
    public final String mensaje;
    /** Excepción original para errores clasificables (ERROR / REGLA_NEGOCIO). */
    public final Exception excepcion;
    /** Cabeceras de tabla para LISTADO. */
    public final String[] headers;
    /** Filas de tabla para LISTADO. */
    public final List<Object[]> filas;
    /** HTML ya generado (casos especiales: QR, HELP, documentos). */
    public final String htmlCrudo;
    /** Nombre de entidad para NO_ENCONTRADO. */
    public final String entidad;
    /** Identificador para NO_ENCONTRADO. */
    public final String idNoEncontrado;

    private ResultadoDTO(Tipo tipo, String mensaje, Exception excepcion, String[] headers,
                         List<Object[]> filas, String htmlCrudo, String entidad, String idNoEncontrado) {
        this.tipo = tipo;
        this.mensaje = mensaje;
        this.excepcion = excepcion;
        this.headers = headers;
        this.filas = filas;
        this.htmlCrudo = htmlCrudo;
        this.entidad = entidad;
        this.idNoEncontrado = idNoEncontrado;
    }

    public static ResultadoDTO exito(String mensaje) {
        return new ResultadoDTO(Tipo.EXITO, mensaje, null, null, null, null, null, null);
    }

    public static ResultadoDTO error(Exception e) {
        return new ResultadoDTO(Tipo.ERROR, null, e, null, null, null, null, null);
    }

    public static ResultadoDTO reglaNegocio(Exception e) {
        return new ResultadoDTO(Tipo.REGLA_NEGOCIO, null, e, null, null, null, null, null);
    }

    public static ResultadoDTO parametroInvalido(String mensaje) {
        return new ResultadoDTO(Tipo.PARAMETRO_INVALIDO, mensaje, null, null, null, null, null, null);
    }

    public static ResultadoDTO noEncontrado(String entidad, String id) {
        return new ResultadoDTO(Tipo.NO_ENCONTRADO, null, null, null, null, null, entidad, id);
    }

    public static ResultadoDTO lista(String[] headers, List<Object[]> filas) {
        return new ResultadoDTO(Tipo.LISTADO, null, null, headers, filas, null, null, null);
    }

    public static ResultadoDTO htmlCrudo(String html) {
        return new ResultadoDTO(Tipo.HTML_CRUDO, null, null, null, null, html, null, null);
    }

    public boolean esError() {
        return tipo == Tipo.ERROR || tipo == Tipo.NO_ENCONTRADO
            || tipo == Tipo.PARAMETRO_INVALIDO || tipo == Tipo.REGLA_NEGOCIO;
    }
}
