package com.example.Puertos;

import com.example.Dominio.Periodo;
import java.util.List;

public interface IPeriodoRepository {
    void guardar(Periodo periodo) throws Exception;
    void actualizar(Periodo periodo) throws Exception;
    void eliminar(int id) throws Exception;
    Periodo obtenerPorId(int id) throws Exception;
    List<Periodo> listar() throws Exception;
}
