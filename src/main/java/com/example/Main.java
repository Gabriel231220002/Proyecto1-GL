package com.example;

import com.example.Adaptadores.Entrada.Pop3EmailDaemon;
import com.example.Adaptadores.Salida.*;
import com.example.Aplicacion.*;
import com.example.Puertos.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        TerminalUI.banner("SISTEMA DE GESTIÓN DE LABORATORIOS DE FACULTAD");

        // 1. Instanciar Adaptadores de Salida (Driven Repositories)
        IUsuarioRepository usuarioRepo = new PostgresUsuarioRepository();
        IPrivilegioRepository privilegioRepo = new PostgresPrivilegioRepository();
        ICarreraRepository carreraRepo = new PostgresCarreraRepository();
        IMateriaRepository materiaRepo = new PostgresMateriaRepository();
        IPeriodoRepository periodoRepo = new PostgresPeriodoRepository();
        ILaboratorioRepository labRepo = new PostgresLaboratorioRepository();
        IHorarioRepository horarioRepo = new PostgresHorarioRepository();
        IGrupoRepository grupoRepo = new PostgresGrupoRepository();
        IAsistenciaRepository asistenciaRepo = new PostgresAsistenciaRepository();
        IPermutaTurnoRepository permutaRepo = new PostgresPermutaTurnoRepository();
        IChatRepository chatRepo = new PostgresChatRepository();
        IItemInventarioRepository itemRepo = new PostgresItemInventarioRepository();
        ITicketIncidenciaRepository ticketRepo = new PostgresTicketIncidenciaRepository();
        IObjetoPerdidoRepository objetoRepo = new PostgresObjetoPerdidoRepository();
        IReservaRepository reservaRepo = new PostgresReservaRepository();

        // 2. Selección de Servidor SMTP para Respuestas
        System.out.println("Seleccione el servidor SMTP para enviar las respuestas:");
        System.out.println("1. Tecnoweb SMTP (mail.tecnoweb.org.bo) [Por defecto]");
        System.out.println("2. Google SMTP (smtp.gmail.com)");
        System.out.print("Opción [1-2]: ");

        boolean usarGoogleSMTP = false;
        try {
            Scanner scanner = new Scanner(System.in);
            if (scanner.hasNextLine()) {
                String opcion = scanner.nextLine().trim();
                usarGoogleSMTP = "2".equals(opcion);
            }
        } catch (Exception ignored) {}

        TerminalUI.info("Servidor SMTP activo: " + (usarGoogleSMTP ? "Google SMTP" : "Tecnoweb SMTP"));
        IEmailResponderPort notificador = new SmtpEmailResponderAdapter(usarGoogleSMTP);

        // 3. Instanciar Casos de Uso / Servicios de Aplicación (Core CU1 - CU8)
        UsuarioService usuarioService = new UsuarioService(usuarioRepo, privilegioRepo);
        ConfiguracionSemestralService configService = new ConfiguracionSemestralService(periodoRepo, carreraRepo, materiaRepo, privilegioRepo);
        CargaHorariaService cargaHorariaService = new CargaHorariaService(labRepo, horarioRepo, grupoRepo);
        ReservaService reservaService = new ReservaService(reservaRepo);
        PermutaChatService permutaChatService = new PermutaChatService(permutaRepo, chatRepo);
        InventarioIncidenciaService inventarioService = new InventarioIncidenciaService(itemRepo, ticketRepo, objetoRepo);
        AsistenciaService asistenciaService = new AsistenciaService(asistenciaRepo);
        ReporteService reporteService = new ReporteService();

        IProcesarComandoUseCase useCase = new ComandoOrchestratorService(
            usuarioService,
            configService,
            cargaHorariaService,
            reservaService,
            permutaChatService,
            inventarioService,
            asistenciaService,
            reporteService,
            notificador,
            new RespuestaPresenter()
        );

        // 4. Adaptador de Entrada (Daemon POP3)
        Pop3EmailDaemon daemon = new Pop3EmailDaemon(useCase);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nSeñal de apagado recibida. Deteniendo daemon...");
            daemon.stopDaemon();
        }));

        daemon.startDaemon();
    }
}
