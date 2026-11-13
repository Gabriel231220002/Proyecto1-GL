package com.example.Adaptadores.Salida;

import com.example.Aplicacion.MensajesError;
import com.example.Aplicacion.TerminalUI;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

    private static String dbUrl;
    private static String dbUser;
    private static String dbPass;
    private static boolean inicializado = false;

    private DatabaseConnection() {
    }

    private static synchronized void inicializar() {
        if (inicializado) {
            return;
        }

        Properties props = new Properties();
        String origen = null;

        try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                props.load(input);
                origen = "classpath:config.properties";
            }
        } catch (Exception ignored) {}

        if (origen == null) {
            Path[] rutas = new Path[] {
                Paths.get("config.properties"),
                Paths.get("src/main/resources/config.properties")
            };
            for (Path ruta : rutas) {
                if (Files.exists(ruta)) {
                    try (InputStream input = new FileInputStream(ruta.toFile())) {
                        props.load(input);
                        origen = ruta.toAbsolutePath().toString();
                        break;
                    } catch (Exception ignored) {}
                }
            }
        }

        dbUrl = props.getProperty("db.url", "jdbc:postgresql://mail.tecnoweb.org.bo:5432/db_grupo03sc")
                .trim().replace("\"", "").replace("'", "");
        dbUser = props.getProperty("db.user", "grupo03sc")
                .trim().replace("\"", "").replace("'", "");
        dbPass = props.getProperty("db.password", "grup003grup003*")
                .trim().replace("\"", "").replace("'", "");

        TerminalUI.info("Base de datos activa: " + dbUrl + " (Origen: " + (origen != null ? origen : "default") + ")");
        inicializado = true;
    }

    public static Connection getConnection() throws SQLException {
        if (!inicializado) {
            inicializar();
        }
        try {
            return DriverManager.getConnection(dbUrl, dbUser, dbPass);
        } catch (SQLException e) {
            MensajesError.imprimirErrorTerminal("ERROR DE CONEXIÓN A BASE DE DATOS (" + dbUrl + ")", e);
            throw e;
        }
    }
}
