package com.example.Aplicacion;

import com.example.Dominio.ItemInventario;
import com.example.Dominio.ObjetoPerdido;
import com.example.Dominio.TicketIncidencia;
import com.example.Puertos.IItemInventarioRepository;
import com.example.Puertos.IObjetoPerdidoRepository;
import com.example.Puertos.ITicketIncidenciaRepository;
import java.sql.Date;
import java.util.List;

public class InventarioIncidenciaService {

    private final IItemInventarioRepository itemRepo;
    private final ITicketIncidenciaRepository ticketRepo;
    private final IObjetoPerdidoRepository objetoRepo;

    public InventarioIncidenciaService(IItemInventarioRepository itemRepo,
                                       ITicketIncidenciaRepository ticketRepo,
                                       IObjetoPerdidoRepository objetoRepo) {
        this.itemRepo = itemRepo;
        this.ticketRepo = ticketRepo;
        this.objetoRepo = objetoRepo;
    }

    // --- Items de Inventario ---
    public ItemInventario crearItem(int laboratorioId, String codigo, String nombre,
                                    String descripcion, String estadoFisico) throws Exception {
        ItemInventario item = new ItemInventario(0, laboratorioId, codigo, nombre, descripcion, estadoFisico);
        itemRepo.guardar(item);
        return item;
    }

    public List<ItemInventario> listarItems() throws Exception {
        return itemRepo.listar();
    }

    public List<ItemInventario> listarItemsPorLaboratorio(int labId) throws Exception {
        return itemRepo.listarPorLaboratorio(labId);
    }

    public ItemInventario obtenerItem(int id) throws Exception {
        return itemRepo.obtenerPorId(id);
    }

    public void eliminarItem(int id) throws Exception {
        itemRepo.eliminar(id);
    }

    // --- Tickets de Incidencia ---
    public TicketIncidencia reportarIncidencia(int itemId, int reportaId, String asunto, String descripcion) throws Exception {
        TicketIncidencia t = new TicketIncidencia(0, itemId, reportaId, null, asunto, descripcion,
                "ABIERTO", null, null, null);
        ticketRepo.guardar(t);
        return t;
    }

    public void atenderIncidencia(int ticketId, int atiendeId, String solucion, String nuevoEstado) throws Exception {
        TicketIncidencia t = ticketRepo.obtenerPorId(ticketId);
        if (t == null) {
            throw new IllegalArgumentException("No existe el ticket de incidencia con ID: " + ticketId);
        }
        t.setAtiendeId(atiendeId);
        t.setSolucionAplicada(solucion);
        t.setEstado(nuevoEstado != null ? nuevoEstado.toUpperCase() : "RESUELTO");
        t.setFechaSolucion(new Date(System.currentTimeMillis()));
        ticketRepo.actualizar(t);
    }

    public List<TicketIncidencia> listarIncidencias() throws Exception {
        return ticketRepo.listar();
    }

    public TicketIncidencia obtenerIncidencia(int id) throws Exception {
        return ticketRepo.obtenerPorId(id);
    }

    public void eliminarIncidencia(int id) throws Exception {
        ticketRepo.eliminar(id);
    }

    // --- Objetos Encontrados / Perdidos ---
    public ObjetoPerdido registrarObjeto(int labId, int registraId, String descripcion) throws Exception {
        ObjetoPerdido o = new ObjetoPerdido(0, labId, registraId, null, descripcion, "REPORTADO", null, null);
        objetoRepo.guardar(o);
        return o;
    }

    public void entregarObjeto(int objetoId, int reclamaId) throws Exception {
        ObjetoPerdido o = objetoRepo.obtenerPorId(objetoId);
        if (o == null) {
            throw new IllegalArgumentException("No existe el objeto perdido con ID: " + objetoId);
        }
        o.setReclamaId(reclamaId);
        o.setEstado("ENTREGADO");
        o.setFechaEntrega(new Date(System.currentTimeMillis()));
        objetoRepo.actualizar(o);
    }

    public List<ObjetoPerdido> listarObjetos() throws Exception {
        return objetoRepo.listar();
    }

    public ObjetoPerdido obtenerObjeto(int id) throws Exception {
        return objetoRepo.obtenerPorId(id);
    }

    public void eliminarObjeto(int id) throws Exception {
        objetoRepo.eliminar(id);
    }
}
