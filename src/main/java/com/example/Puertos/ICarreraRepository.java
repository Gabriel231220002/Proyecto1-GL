package com.example.Puertos;

import com.example.Dominio.Carrera;
import java.util.List;

public interface ICarreraRepository {
    void guardar(Carrera carrera) throws Exception;
    void actualizar(Carrera carrera) throws Exception;
    void eliminar(int id) throws Exception;
    Carrera obtenerPorId(int id) throws Exception;
    List<Carrera> listar() throws Exception;

    void asignarUsuarioCarrera(int usuarioId, int carreraId) throws Exception;
    void asociarMateria(int carreraId, int materiaId, int nivelAcademico) throws Exception;
}
