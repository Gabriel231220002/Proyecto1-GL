package com.example.Puertos;

import com.example.Dominio.Privilegio;
import com.example.Dominio.UsuarioPrivilegio;
import java.util.List;

public interface IPrivilegioRepository {
    void guardar(Privilegio privilegio) throws Exception;
    void actualizar(Privilegio privilegio) throws Exception;
    void eliminar(int id) throws Exception;
    Privilegio obtenerPorId(int id) throws Exception;
    List<Privilegio> listar() throws Exception;

    void asignarPrivilegio(int usuarioId, int privilegioId) throws Exception;
    void revocarPrivilegio(int usuarioId, int privilegioId) throws Exception;
    List<UsuarioPrivilegio> listarPrivilegiosDeUsuario(int usuarioId) throws Exception;
}
