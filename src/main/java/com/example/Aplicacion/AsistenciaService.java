package com.example.Aplicacion;

import com.example.Dominio.Asistencia;
import com.example.Puertos.IAsistenciaRepository;
import java.sql.Date;
import java.util.List;

public class AsistenciaService {

    private final IAsistenciaRepository asistenciaRepo;

    public AsistenciaService(IAsistenciaRepository asistenciaRepo) {
        this.asistenciaRepo = asistenciaRepo;
    }

    public Asistencia registrarAsistencia(int usuarioId, Integer grupoId, Date fecha, String estado) throws Exception {
        if (estado == null || estado.trim().isEmpty()) {
            estado = "PRESENTE";
        }
        Asistencia a = new Asistencia(0, usuarioId, grupoId, fecha, estado.trim().toUpperCase());
        asistenciaRepo.guardar(a);
        return a;
    }

    public void actualizarEstado(int id, String estado) throws Exception {
        Asistencia a = asistenciaRepo.obtenerPorId(id);
        if (a == null) {
            throw new IllegalArgumentException("No existe registro de asistencia con ID: " + id);
        }
        a.setEstadoAsistencia(estado.toUpperCase());
        asistenciaRepo.actualizar(a);
    }

    public List<Asistencia> listar() throws Exception {
        return asistenciaRepo.listar();
    }

    public List<Asistencia> listarPorUsuario(int usuarioId) throws Exception {
        return asistenciaRepo.listarPorUsuario(usuarioId);
    }

    public List<Asistencia> listarPorGrupo(int grupoId) throws Exception {
        return asistenciaRepo.listarPorGrupo(grupoId);
    }

    public void eliminar(int id) throws Exception {
        asistenciaRepo.eliminar(id);
    }
}
