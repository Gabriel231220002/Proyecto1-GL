package com.example.Aplicacion;

import com.example.Dominio.Reserva;
import com.example.Dominio.ReservaHorario;
import com.example.Puertos.IReservaRepository;
import java.sql.Date;
import java.util.List;

public class ReservaService {

    private final IReservaRepository reservaRepo;

    public ReservaService(IReservaRepository reservaRepo) {
        this.reservaRepo = reservaRepo;
    }

    public int solicitarReserva(int usuarioId, int laboratorioId, int horarioId, Date fechaUso, String motivo) throws Exception {
        if (fechaUso == null) {
            throw new IllegalArgumentException("Debe especificar la fecha de uso de la reserva (formato YYYY-MM-DD).");
        }
        return reservaRepo.crearReservaConHorario(usuarioId, laboratorioId, horarioId, fechaUso, motivo);
    }

    public void actualizarEstado(int reservaHorarioId, String estado) throws Exception {
        reservaRepo.actualizarEstadoReservaHorario(reservaHorarioId, estado.toUpperCase());
    }

    public List<Reserva> listarReservas() throws Exception {
        return reservaRepo.listar();
    }

    public List<ReservaHorario> listarSlotsReserva() throws Exception {
        return reservaRepo.listarReservasHorario();
    }

    public List<Object[]> listarReservasDetalladas() throws Exception {
        return reservaRepo.listarReservasDetalladas();
    }

    public void eliminarReserva(int id) throws Exception {
        reservaRepo.eliminar(id);
    }
}
