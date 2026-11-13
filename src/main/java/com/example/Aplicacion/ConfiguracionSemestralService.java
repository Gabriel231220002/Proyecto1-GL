package com.example.Aplicacion;

import com.example.Dominio.*;
import com.example.Puertos.*;
import java.util.List;

public class ConfiguracionSemestralService {

    private final IPeriodoRepository periodoRepo;
    private final ICarreraRepository carreraRepo;
    private final IMateriaRepository materiaRepo;
    private final IPrivilegioRepository privilegioRepo;

    public ConfiguracionSemestralService(IPeriodoRepository periodoRepo,
                                         ICarreraRepository carreraRepo,
                                         IMateriaRepository materiaRepo,
                                         IPrivilegioRepository privilegioRepo) {
        this.periodoRepo = periodoRepo;
        this.carreraRepo = carreraRepo;
        this.materiaRepo = materiaRepo;
        this.privilegioRepo = privilegioRepo;
    }

    // --- Periodos ---
    public Periodo crearPeriodo(int gestion, int semestre) throws Exception {
        Periodo p = new Periodo(0, gestion, semestre);
        periodoRepo.guardar(p);
        return p;
    }

    public List<Periodo> listarPeriodos() throws Exception {
        return periodoRepo.listar();
    }

    public Periodo obtenerPeriodo(int id) throws Exception {
        return periodoRepo.obtenerPorId(id);
    }

    public void eliminarPeriodo(int id) throws Exception {
        periodoRepo.eliminar(id);
    }

    // --- Carreras ---
    public Carrera crearCarrera(String codigo, String nombre, String sigla) throws Exception {
        Carrera c = new Carrera(0, codigo, nombre, sigla);
        carreraRepo.guardar(c);
        return c;
    }

    public List<Carrera> listarCarreras() throws Exception {
        return carreraRepo.listar();
    }

    public Carrera obtenerCarrera(int id) throws Exception {
        return carreraRepo.obtenerPorId(id);
    }

    public void eliminarCarrera(int id) throws Exception {
        carreraRepo.eliminar(id);
    }

    public void asignarCarreraUsuario(int usuarioId, int carreraId) throws Exception {
        carreraRepo.asignarUsuarioCarrera(usuarioId, carreraId);
    }

    public void asociarMateriaCarrera(int carreraId, int materiaId, int nivelAcademico) throws Exception {
        carreraRepo.asociarMateria(carreraId, materiaId, nivelAcademico);
    }

    // --- Materias ---
    public Materia crearMateria(String nombre, String sigla) throws Exception {
        Materia m = new Materia(0, nombre, sigla);
        materiaRepo.guardar(m);
        return m;
    }

    public List<Materia> listarMaterias() throws Exception {
        return materiaRepo.listar();
    }

    public Materia obtenerMateria(int id) throws Exception {
        return materiaRepo.obtenerPorId(id);
    }

    public void eliminarMateria(int id) throws Exception {
        materiaRepo.eliminar(id);
    }

    // --- Privilegios ---
    public Privilegio crearPrivilegio(String codigo, String nombre, String descripcion) throws Exception {
        Privilegio p = new Privilegio(0, codigo, nombre, descripcion);
        privilegioRepo.guardar(p);
        return p;
    }

    public List<Privilegio> listarPrivilegios() throws Exception {
        return privilegioRepo.listar();
    }

    public Privilegio obtenerPrivilegio(int id) throws Exception {
        return privilegioRepo.obtenerPorId(id);
    }

    public void eliminarPrivilegio(int id) throws Exception {
        privilegioRepo.eliminar(id);
    }
}
