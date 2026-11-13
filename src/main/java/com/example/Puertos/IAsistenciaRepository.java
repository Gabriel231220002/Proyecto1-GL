package com.example.Puertos;

import com.example.Dominio.Asistencia;
import java.util.List;

public interface IAsistenciaRepository {
    void guardar(Asistencia asistencia) throws Exception;
    void actualizar(Asistencia asistencia) throws Exception;
    void eliminar(int id) throws Exception;
    Asistencia obtenerPorId(int id) throws Exception;
    List<Asistencia> listar() throws Exception;
    List<Asistencia> listarPorUsuario(int usuarioId) throws Exception;
    List<Asistencia> listarPorGrupo(int grupoId) throws Exception;
}
