package com.example.Puertos;

import com.example.Dominio.ObjetoPerdido;
import java.util.List;

public interface IObjetoPerdidoRepository {
    void guardar(ObjetoPerdido objeto) throws Exception;
    void actualizar(ObjetoPerdido objeto) throws Exception;
    void eliminar(int id) throws Exception;
    ObjetoPerdido obtenerPorId(int id) throws Exception;
    List<ObjetoPerdido> listar() throws Exception;
    List<ObjetoPerdido> listarPorLaboratorio(int laboratorioId) throws Exception;
}
