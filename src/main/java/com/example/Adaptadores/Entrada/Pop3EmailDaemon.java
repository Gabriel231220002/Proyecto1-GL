package com.example.Adaptadores.Entrada;

import com.example.Aplicacion.MensajesError;
import com.example.Aplicacion.TerminalUI;
import com.example.Dominio.ComandoDTO;
import com.example.Puertos.IProcesarComandoUseCase;
import java.io.IOException;

public class Pop3EmailDaemon {
    private final EmailCommandParser parser;
    private final IProcesarComandoUseCase useCase;
    private final ClientePOP clientePOP;
    private boolean activo;

    public Pop3EmailDaemon(IProcesarComandoUseCase useCase) {
        this.useCase = useCase;
        this.parser = new EmailCommandParser();
        this.clientePOP = new ClientePOP();
        this.activo = false;
    }

    public void leerCorreosEntrantes() {
        try {
            clientePOP.conectar();
            int totalCorreos = clientePOP.obtenerTotalDeCorreos();
            if (totalCorreos > 0) {
                TerminalUI.info("Nuevos correos en bandeja POP3: " + totalCorreos);
            }

            for (int i = 1; i <= totalCorreos; i++) {
                String rawEmail = clientePOP.obtenerCorreoYEliminar(i);
                if (rawEmail != null) {
                    ComandoDTO cmd = parser.parsearCuerpo(rawEmail);
                    if (cmd != null) {
                        useCase.ejecutarOperacion(cmd);
                    }
                }
            }
        } catch (IOException e) {
            MensajesError.imprimirErrorTerminal("ERROR EN LA LECTURA DE CORREOS POP3", e);
        } finally {
            clientePOP.desconectar();
        }
    }

    public void startDaemon() {
        this.activo = true;
        TerminalUI.banner("SISTEMA DE GESTIÓN DE LABORATORIOS (FACULTAD)");
        TerminalUI.ok("Daemon POP3 iniciado. Monitoreando cada 10s...");

        while (activo) {
            try {
                leerCorreosEntrantes();
            } catch (Exception e) {
                MensajesError.imprimirErrorTerminal("EXCEPCIÓN NO CONTROLADA EN EL LOOP DEL DAEMON", e);
            }

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                TerminalUI.warn("Daemon interrumpido. Apagando...");
                this.activo = false;
            }
        }
    }

    public void stopDaemon() {
        this.activo = false;
        TerminalUI.ok("Daemon POP3 detenido.");
    }
}