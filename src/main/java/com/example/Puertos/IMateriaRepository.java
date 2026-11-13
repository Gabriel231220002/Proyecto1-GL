package com.example.Puertos;

import com.example.Dominio.Materia;
import java.util.List;

public interface IMateriaRepository {
    void guardar(Materia materia) throws Exception;
    void actualizar(Materia materia) throws Exception;
    void eliminar(int id) throws Exception;
    Materia obtenerPorId(int id) throws Exception;
    List<Materia> listar() throws Exception;
}
