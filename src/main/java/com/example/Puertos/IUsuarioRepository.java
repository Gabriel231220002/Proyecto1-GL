package com.example.Puertos;

import com.example.Dominio.Usuario;
import java.util.List;

public interface IUsuarioRepository {
    void guardar(Usuario usuario) throws Exception;
    void actualizar(Usuario usuario) throws Exception;
    void eliminar(int id) throws Exception;
    Usuario obtenerPorId(int id) throws Exception;
    Usuario obtenerPorEmail(String email) throws Exception;
    List<Usuario> listar() throws Exception;
}
