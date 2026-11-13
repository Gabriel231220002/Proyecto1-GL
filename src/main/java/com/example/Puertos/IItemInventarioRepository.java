package com.example.Puertos;

import com.example.Dominio.ItemInventario;
import java.util.List;

public interface IItemInventarioRepository {
    void guardar(ItemInventario item) throws Exception;
    void actualizar(ItemInventario item) throws Exception;
    void eliminar(int id) throws Exception;
    ItemInventario obtenerPorId(int id) throws Exception;
    ItemInventario obtenerPorCodigo(String codigo) throws Exception;
    List<ItemInventario> listar() throws Exception;
    List<ItemInventario> listarPorLaboratorio(int laboratorioId) throws Exception;
}
