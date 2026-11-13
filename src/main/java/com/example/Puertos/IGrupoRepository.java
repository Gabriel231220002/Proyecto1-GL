package com.example.Puertos;

import com.example.Dominio.Grupo;
import com.example.Dominio.GrupoHorario;
import java.util.List;

public interface IGrupoRepository {
    void guardar(Grupo grupo) throws Exception;
    void actualizar(Grupo grupo) throws Exception;
    void eliminar(int id) throws Exception;
    Grupo obtenerPorId(int id) throws Exception;
    List<Grupo> listar() throws Exception;

    void asignarHorario(GrupoHorario gh) throws Exception;
    List<GrupoHorario> listarHorariosPorGrupo(int grupoId) throws Exception;
    List<GrupoHorario> listarTodosGrupoHorarios() throws Exception;
}
