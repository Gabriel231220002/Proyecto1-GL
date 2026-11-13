package com.example.Puertos;

import com.example.Dominio.Laboratorio;
import java.util.List;

public interface ILaboratorioRepository {
    void guardar(Laboratorio laboratorio) throws Exception;
    void actualizar(Laboratorio laboratorio) throws Exception;
    void eliminar(int id) throws Exception;
    Laboratorio obtenerPorId(int id) throws Exception;
    List<Laboratorio> listar() throws Exception;
}
