package com.example.Puertos;

import com.example.Dominio.Reserva;
import com.example.Dominio.ReservaHorario;
import java.sql.Date;
import java.util.List;

public interface IReservaRepository {
    void guardar(Reserva reserva) throws Exception;
    void eliminar(int id) throws Exception;
    Reserva obtenerPorId(int id) throws Exception;
    List<Reserva> listar() throws Exception;

    int crearReservaConHorario(int usuarioId, int laboratorioId, int horarioId, Date fechaUso, String motivo) throws Exception;
    boolean verificarConflicto(int laboratorioId, int horarioId, Date fechaUso) throws Exception;
    void actualizarEstadoReservaHorario(int reservaHorarioId, String nuevoEstado) throws Exception;
    List<ReservaHorario> listarReservasHorario() throws Exception;
    List<Object[]> listarReservasDetalladas() throws Exception;
}
