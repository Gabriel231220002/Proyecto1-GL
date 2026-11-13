package com.example.Aplicacion;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utilidad estática para hashing bcrypt de contraseñas.
 *
 * <p>COMPATIBILIDAD CON LARAVEL: el proyecto web Laravel comparte la tabla
 * {@code users} y guarda las contraseñas con {@code Hash::make()} (bcrypt,
 * formato {@code $2y$...}, cost 12). Laravel valida el algoritmo ANTES de
 * verificar con {@code password_get_info()}: en PHP 8.2+ (y Laravel 10/11/12)
 * los hashes {@code $2a$} se reportan como {@code unknown} y lanzan "This
 * password does not use the Bcrypt algorithm". Por eso jBCrypt genera
 * {@code $2a$} y este método lo normaliza a {@code $2y$}: el salt y el hash
 * subyacentes son idénticos, solo cambia la etiqueta de versión, y PHP
 * {@code password_verify()} lo verifica correctamente.</p>
 *
 * <p>Se usa cost 12 para coincidir con el default de Laravel (PASSWORD_BCRYPT).
 * El hash generado NO contiene la contraseña en claro.</p>
 */
public class PasswordHasher {

    private PasswordHasher() {
        // Clase de utilidad estática
    }

    /**
     * Genera el hash bcrypt de una contraseña en claro.
     *
     * <p>jBCrypt genera el prefijo {@code $2a$}; se normaliza a {@code $2y$}
     * porque Laravel (PHP 8.2+) rechaza {@code $2a$} al validar el algoritmo
     * con {@code password_get_info()}.</p>
     *
     * @param passwordClaro contraseña en claro (ya validada por el dominio).
     * @return hash bcrypt con prefijo {@code $2y$} y cost 12.
     */
    public static String hash(String passwordClaro) {
        String hash = BCrypt.hashpw(passwordClaro, BCrypt.gensalt(12));
        return hash.startsWith("$2a$") ? "$2y$" + hash.substring(4) : hash;
    }

    /**
     * Verifica una contraseña en claro contra un hash bcrypt almacenado.
     *
     * <p>jBCrypt rechaza la revisión {@code $2y$} ("Invalid salt revision"),
     * así que se normaliza a {@code $2a$} antes de verificar: el salt y el
     * checksum subyacentes son idénticos, solo cambia la etiqueta de versión.</p>
     *
     * @param passwordClaro   contraseña en claro a verificar.
     * @param hashAlmacenado  hash bcrypt almacenado (puede ser {@code $2a$},
     *                        {@code $2b$} o {@code $2y$}).
     * @return {@code true} si la contraseña coincide con el hash.
     */
    public static boolean verificar(String passwordClaro, String hashAlmacenado) {
        String hashNormalizado = hashAlmacenado.startsWith("$2y$")
                ? "$2a$" + hashAlmacenado.substring(4)
                : hashAlmacenado;
        return BCrypt.checkpw(passwordClaro, hashNormalizado);
    }

    /**
     * Indica si el texto tiene el formato de un hash bcrypt (prefijo {@code $2a$},
     * {@code $2b$} o {@code $2y$}). Útil para detectar contraseñas guardadas aún
     * en texto plano.
     *
     * @param hash texto a inspeccionar.
     * @return {@code true} si empieza con un prefijo bcrypt válido.
     */
    public static boolean formatoValido(String hash) {
        if (hash == null) {
            return false;
        }
        return hash.startsWith("$2a$") || hash.startsWith("$2b$") || hash.startsWith("$2y$");
    }
}
