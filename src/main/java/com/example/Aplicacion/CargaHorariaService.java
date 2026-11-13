package com.example.Aplicacion;

import com.example.Dominio.*;
import com.example.Puertos.*;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class CargaHorariaService {

    private final ILaboratorioRepository laboratorioRepo;
    private final IHorarioRepository horarioRepo;
    private final IGrupoRepository grupoRepo;

    public CargaHorariaService(ILaboratorioRepository laboratorioRepo,
                               IHorarioRepository horarioRepo,
                               IGrupoRepository grupoRepo) {
        this.laboratorioRepo = laboratorioRepo;
        this.horarioRepo = horarioRepo;
        this.grupoRepo = grupoRepo;
    }

    // --- Laboratorios ---
    public Laboratorio crearLaboratorio(String nombre, int aula) throws Exception {
        Laboratorio lab = new Laboratorio(0, nombre, aula);
        laboratorioRepo.guardar(lab);
        return lab;
    }

    public Laboratorio crearLaboratorio(String nombre, Time apertura, Time cierre) throws Exception {
        Laboratorio lab = new Laboratorio(0, nombre, apertura, cierre);
        laboratorioRepo.guardar(lab);
        return lab;
    }

    public List<Laboratorio> listarLaboratorios() throws Exception {
        return laboratorioRepo.listar();
    }

    public Laboratorio obtenerLaboratorio(int id) throws Exception {
        return laboratorioRepo.obtenerPorId(id);
    }

    public void eliminarLaboratorio(int id) throws Exception {
        laboratorioRepo.eliminar(id);
    }

    // --- Horarios ---
    public Horario crearHorario(String dia, Time inicio, Time fin) throws Exception {
        Horario h = new Horario(0, dia, inicio, fin);
        horarioRepo.guardar(h);
        return h;
    }

    public Horario crearHorario(String dia, Time inicio, Time fin, Integer labId) throws Exception {
        Horario h = new Horario(0, dia, inicio, fin, labId);
        horarioRepo.guardar(h);
        return h;
    }

    public List<Horario> listarHorarios() throws Exception {
        return horarioRepo.listar();
    }

    public Horario obtenerHorario(int id) throws Exception {
        return horarioRepo.obtenerPorId(id);
    }

    public void eliminarHorario(int id) throws Exception {
        horarioRepo.eliminar(id);
    }

    // --- Grupos ---
    public Grupo crearGrupo(String nombre, int materiaId, Integer usuarioId, Integer periodoId) throws Exception {
        Grupo g = new Grupo(0, nombre, materiaId, usuarioId, periodoId);
        grupoRepo.guardar(g);
        return g;
    }

    public List<Grupo> listarGrupos() throws Exception {
        return grupoRepo.listar();
    }

    public Grupo obtenerGrupo(int id) throws Exception {
        return grupoRepo.obtenerPorId(id);
    }

    public void eliminarGrupo(int id) throws Exception {
        grupoRepo.eliminar(id);
    }

    // --- Asignación Grupo Horario ---
    public GrupoHorario asignarGrupoHorario(int grupoId, int horarioId, Integer labId,
                                            boolean estado, boolean esParcial, Date fechaParcial) throws Exception {
        // Si se provee labId, asociarlo también en el horario si no lo tenía
        if (labId != null && labId > 0) {
            Horario h = horarioRepo.obtenerPorId(horarioId);
            if (h != null && (h.getLaboratorioId() == null || h.getLaboratorioId() == 0)) {
                h.setLaboratorioId(labId);
                horarioRepo.actualizar(h);
            }
        }
        GrupoHorario gh = new GrupoHorario(0, grupoId, horarioId, labId, estado, esParcial, fechaParcial);
        grupoRepo.asignarHorario(gh);
        return gh;
    }

    public List<GrupoHorario> listarGrupoHorarios() throws Exception {
        return grupoRepo.listarTodosGrupoHorarios();
    }

    // --- Consultar Disponibilidad de Laboratorio ---
    public List<Object[]> consultarDisponibilidad(int labId, String dia) throws Exception {
        Laboratorio lab = laboratorioRepo.obtenerPorId(labId);
        if (lab == null) {
            throw new IllegalArgumentException("No existe el laboratorio con ID: " + labId);
        }

        List<Horario> todosHorarios = horarioRepo.listar();
        List<GrupoHorario> asignaciones = grupoRepo.listarTodosGrupoHorarios();
        List<Grupo> grupos = grupoRepo.listar();

        List<Object[]> reporte = new ArrayList<>();
        for (Horario h : todosHorarios) {
            // Si el horario está explícitamente asignado a otro laboratorio, saltearlo
            if (h.getLaboratorioId() != null && h.getLaboratorioId() > 0 && h.getLaboratorioId() != labId) {
                continue;
            }

            if (dia != null && !dia.isEmpty() && !dia.equals("*") && !dia.equalsIgnoreCase(h.getDia()) && !dia.equalsIgnoreCase(h.getDiaNombre())) {
                continue;
            }

            boolean ocupado = false;
            String detalleOcupacion = "LIBRE / DISPONIBLE";

            for (GrupoHorario gh : asignaciones) {
                boolean coincideLab = (gh.getLaboratorioId() != null && gh.getLaboratorioId() == labId)
                        || (h.getLaboratorioId() != null && h.getLaboratorioId() == labId);

                if (gh.isEstado() && coincideLab && gh.getHorarioId() == h.getId()) {
                    ocupado = true;
                    String nombreGrupo = "Grupo #" + gh.getGrupoId();
                    for (Grupo g : grupos) {
                        if (g.getId() == gh.getGrupoId()) {
                            nombreGrupo = g.getNombre();
                            break;
                        }
                    }
                    detalleOcupacion = "OCUPADO (" + nombreGrupo + ")";
                    break;
                }
            }

            reporte.add(new Object[]{
                lab.getNombre(),
                "Horario: " + lab.getHoraApertura() + " - " + lab.getHoraCierre(),
                h.getDiaNombre(),
                h.getHoraInicio() + " - " + h.getHoraFin(),
                ocupado ? "OCUPADO" : "DISPONIBLE",
                detalleOcupacion
            });
        }
        return reporte;
    }
}
