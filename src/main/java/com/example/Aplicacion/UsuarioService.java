package com.example.Aplicacion;

import com.example.Dominio.Usuario;
import com.example.Dominio.UsuarioPrivilegio;
import com.example.Puertos.IPrivilegioRepository;
import com.example.Puertos.IUsuarioRepository;
import java.sql.Date;
import java.util.List;

public class UsuarioService {

    private final IUsuarioRepository usuarioRepo;
    private final IPrivilegioRepository privilegioRepo;

    public UsuarioService(IUsuarioRepository usuarioRepo, IPrivilegioRepository privilegioRepo) {
        this.usuarioRepo = usuarioRepo;
        this.privilegioRepo = privilegioRepo;
    }

    public Usuario crearUsuario(String nombres, String apellidoP, String apellidoM, String genero,
                                String foto, int ci, Date fechaNac, int telefono, String rol,
                                String email, String passwordPlano, int regInst, boolean estado) throws Exception {
        if (nombres == null || nombres.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del usuario no puede estar vacío.");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Debe proporcionar un email válido.");
        }
        if (rol == null || rol.trim().isEmpty()) {
            rol = "Auxiliar";
        }
        String hash = (passwordPlano != null && !passwordPlano.trim().isEmpty())
                ? PasswordHasher.hash(passwordPlano)
                : PasswordHasher.hash("12345678");

        Usuario u = new Usuario(0, nombres.trim(), apellidoP, apellidoM, genero, foto, ci, fechaNac,
                telefono, rol.trim(), email.trim().toLowerCase(), hash, regInst, estado);
        usuarioRepo.guardar(u);
        return u;
    }

    public void actualizarUsuario(int id, String nombres, String apellidoP, String apellidoM, String genero,
                                  String foto, int ci, Date fechaNac, int telefono, String rol,
                                  String email, String passwordPlano, int regInst, boolean estado) throws Exception {
        Usuario u = usuarioRepo.obtenerPorId(id);
        if (u == null) {
            throw new IllegalArgumentException("Usuario no encontrado con ID: " + id);
        }
        if (nombres != null && !nombres.trim().isEmpty()) u.setNombres(nombres.trim());
        if (apellidoP != null) u.setApellidoP(apellidoP.trim());
        if (apellidoM != null) u.setApellidoM(apellidoM.trim());
        if (genero != null) u.setGenero(genero.trim());
        if (foto != null) u.setFoto(foto.trim());
        if (ci > 0) u.setCi(ci);
        if (fechaNac != null) u.setFechaNacimiento(fechaNac);
        if (telefono > 0) u.setTelefono(telefono);
        if (rol != null && !rol.trim().isEmpty()) u.setRol(rol.trim());
        if (email != null && email.contains("@")) u.setEmail(email.trim().toLowerCase());
        if (passwordPlano != null && !passwordPlano.trim().isEmpty()) {
            u.setPasswordHash(PasswordHasher.hash(passwordPlano));
        }
        if (regInst > 0) u.setRegistroInstitucional(regInst);
        u.setEstado(estado);

        usuarioRepo.actualizar(u);
    }

    public void eliminarUsuario(int id) throws Exception {
        usuarioRepo.eliminar(id);
    }

    public Usuario obtenerUsuario(int id) throws Exception {
        return usuarioRepo.obtenerPorId(id);
    }

    public Usuario obtenerPorEmail(String email) throws Exception {
        return usuarioRepo.obtenerPorEmail(email);
    }

    public List<Usuario> listarUsuarios() throws Exception {
        return usuarioRepo.listar();
    }

    public void asignarPrivilegio(int usuarioId, int privilegioId) throws Exception {
        privilegioRepo.asignarPrivilegio(usuarioId, privilegioId);
    }

    public void revocarPrivilegio(int usuarioId, int privilegioId) throws Exception {
        privilegioRepo.revocarPrivilegio(usuarioId, privilegioId);
    }

    public List<UsuarioPrivilegio> listarPrivilegios(int usuarioId) throws Exception {
        return privilegioRepo.listarPrivilegiosDeUsuario(usuarioId);
    }
}
