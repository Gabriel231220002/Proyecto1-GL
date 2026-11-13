package com.example.Puertos;

import com.example.Dominio.Horario;
import java.util.List;

public interface IHorarioRepository {
    void guardar(Horario horario) throws Exception;
    void actualizar(Horario horario) throws Exception;
    void eliminar(int id) throws Exception;
    Horario obtenerPorId(int id) throws Exception;
    List<Horario> listar() throws Exception;
}
