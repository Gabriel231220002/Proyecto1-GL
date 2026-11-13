package com.example.Puertos;

import com.example.Dominio.PermutaTurno;
import java.util.List;

public interface IPermutaTurnoRepository {
    void guardar(PermutaTurno permuta, int horarioId) throws Exception;
    void actualizarEstado(int id, String estado) throws Exception;
    void eliminar(int id) throws Exception;
    PermutaTurno obtenerPorId(int id) throws Exception;
    List<PermutaTurno> listar() throws Exception;
    List<PermutaTurno> listarPorUsuario(int usuarioId) throws Exception;
}
