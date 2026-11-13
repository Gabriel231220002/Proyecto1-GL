package com.example.Puertos;

import com.example.Dominio.TicketIncidencia;
import java.util.List;

public interface ITicketIncidenciaRepository {
    void guardar(TicketIncidencia ticket) throws Exception;
    void actualizar(TicketIncidencia ticket) throws Exception;
    void eliminar(int id) throws Exception;
    TicketIncidencia obtenerPorId(int id) throws Exception;
    List<TicketIncidencia> listar() throws Exception;
    List<TicketIncidencia> listarPorEstado(String estado) throws Exception;
}
