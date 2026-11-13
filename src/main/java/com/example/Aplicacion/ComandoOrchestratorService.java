package com.example.Aplicacion;

import com.example.Dominio.*;
import com.example.Puertos.IEmailResponderPort;
import com.example.Puertos.IProcesarComandoUseCase;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ComandoOrchestratorService implements IProcesarComandoUseCase {

    private final UsuarioService usuarioService;
    private final ConfiguracionSemestralService configService;
    private final CargaHorariaService cargaHorariaService;
    private final ReservaService reservaService;
    private final PermutaChatService permutaChatService;
    private final InventarioIncidenciaService inventarioService;
    private final AsistenciaService asistenciaService;
    private final ReporteService reporteService;
    private final IEmailResponderPort notificador;
    private final RespuestaPresenter presenter;

    public ComandoOrchestratorService(
            UsuarioService usuarioService,
            ConfiguracionSemestralService configService,
            CargaHorariaService cargaHorariaService,
            ReservaService reservaService,
            PermutaChatService permutaChatService,
            InventarioIncidenciaService inventarioService,
            AsistenciaService asistenciaService,
            ReporteService reporteService,
            IEmailResponderPort notificador,
            RespuestaPresenter presenter) {
        this.usuarioService = usuarioService;
        this.configService = configService;
        this.cargaHorariaService = cargaHorariaService;
        this.reservaService = reservaService;
        this.permutaChatService = permutaChatService;
        this.inventarioService = inventarioService;
        this.asistenciaService = asistenciaService;
        this.reporteService = reporteService;
        this.notificador = notificador;
        this.presenter = presenter != null ? presenter : new RespuestaPresenter();
    }

    @Override
    public void ejecutarOperacion(ComandoDTO cmd) {
        if (cmd == null) return;

        if (cmd.entidadObjetivo != null) {
            cmd.entidadObjetivo = normalizarEntidad(cmd.entidadObjetivo);
        }

        ResultadoDTO resultado;

        try {
            switch (cmd.accion.toUpperCase()) {
                case "HELP":
                    resultado = ResultadoDTO.htmlCrudo(CommandHelpHTML.obtenerComandosDisponibles());
                    break;
                case "LISTAR":
                    resultado = ejecutarListar(cmd.entidadObjetivo, cmd.parametros.get("raw"));
                    break;
                case "GET":
                    resultado = ejecutarGet(cmd.entidadObjetivo, cmd.parametros.get("raw"));
                    break;
                case "DELETE":
                    resultado = ejecutarDelete(cmd.entidadObjetivo, cmd.parametros.get("raw"));
                    break;
                case "CREATE":
                    resultado = ejecutarCreate(cmd.entidadObjetivo, cmd.parametros.get("raw"));
                    break;
                case "UPDATE":
                    resultado = ejecutarUpdate(cmd.entidadObjetivo, cmd.parametros.get("raw"));
                    break;
                case "REPORTE":
                    resultado = ejecutarReporte(cmd.entidadObjetivo, cmd.parametros.get("raw"));
                    break;
                // Comandos semánticos directos
                case "DISPONIBILIDAD":
                case "CONSULTARDISPONIBILIDAD":
                    resultado = ejecutarDisponibilidad(cmd.parametros.get("raw"));
                    break;
                case "ASIGNARPRIVILEGIO":
                    resultado = ejecutarAsignarPrivilegio(cmd.parametros.get("raw"));
                    break;
                case "ASIGNARCARRERA":
                    resultado = ejecutarAsignarCarrera(cmd.parametros.get("raw"));
                    break;
                case "CARRERAMATERIA":
                    resultado = ejecutarCarreraMateria(cmd.parametros.get("raw"));
                    break;
                case "ASIGNARHORARIO":
                    resultado = ejecutarAsignarHorario(cmd.parametros.get("raw"));
                    break;
                case "ESTADORESERVA":
                    resultado = ejecutarEstadoReserva(cmd.parametros.get("raw"));
                    break;
                case "RESPONDERPERMUTA":
                    resultado = ejecutarResponderPermuta(cmd.parametros.get("raw"));
                    break;
                case "CHAT":
                    resultado = ejecutarEnviarChat(cmd.parametros.get("raw"));
                    break;
                case "LISTARCHAT":
                    resultado = ejecutarListarChat(cmd.parametros.get("raw"));
                    break;
                case "ATENDERINCIDENCIA":
                    resultado = ejecutarAtenderIncidencia(cmd.parametros.get("raw"));
                    break;
                case "ENTREGAROBJETO":
                    resultado = ejecutarEntregarObjeto(cmd.parametros.get("raw"));
                    break;
                default:
                    resultado = ResultadoDTO.htmlCrudo(ErrorCentralizador.generarTarjetaComandoInvalido(cmd.accion, cmd.entidadObjetivo));
                    break;
            }
        } catch (Exception e) {
            // Log detallado de error en terminal
            TerminalUI.logErrorProcesamiento(cmd, e);
            resultado = ResultadoDTO.error(e);
        }

        // Log detallado de respuesta enviada en terminal
        TerminalUI.logRespuestaEnviada(cmd.emailRemitente, "Resultado de la Consulta", resultado, "SMTP Tecnoweb / Google");

        String html = presenter.presentar(resultado);
        String htmlFinal = wrapInHTML(html);
        notificador.enviarRespuesta(cmd.emailRemitente, htmlFinal);
    }

    // -------------------------------------------------------------------------
    // DISPATCHER DE LISTADOS
    // -------------------------------------------------------------------------
    private ResultadoDTO ejecutarListar(String entidad, String param) throws Exception {
        String entUpper = entidad != null ? entidad.toUpperCase() : "";
        switch (entUpper) {
            case "USUARIOS":
                return lista(usuarioService.listarUsuarios(),
                    new String[]{"ID", "Nombres", "Apellidos", "CI", "Rol", "Email", "Reg. Inst.", "Estado"},
                    u -> new Object[]{u.getId(), u.getNombres(), u.getApellidoP() + " " + (u.getApellidoM() != null ? u.getApellidoM() : ""),
                        u.getCi(), u.getRol(), u.getEmail(), u.getRegistroInstitucional(), u.isEstado() ? "ACTIVO" : "INACTIVO"});

            case "PRIVILEGIOS":
                return lista(configService.listarPrivilegios(),
                    new String[]{"ID", "Código", "Nombre", "Descripción"},
                    p -> new Object[]{p.getId(), p.getCodigo(), p.getNombre(), p.getDescripcion()});

            case "CARRERAS":
                return lista(configService.listarCarreras(),
                    new String[]{"ID", "Código", "Nombre", "Sigla"},
                    c -> new Object[]{c.getId(), c.getCodigo(), c.getNombre(), c.getSigla()});

            case "MATERIAS":
                return lista(configService.listarMaterias(),
                    new String[]{"ID", "Nombre", "Sigla"},
                    m -> new Object[]{m.getId(), m.getNombre(), m.getSigla()});

            case "PERIODOS":
                return lista(configService.listarPeriodos(),
                    new String[]{"ID", "Gestión", "Semestre"},
                    p -> new Object[]{p.getId(), p.getGestion(), p.getSemestre()});

            case "LABORATORIOS":
                return lista(cargaHorariaService.listarLaboratorios(),
                    new String[]{"ID", "Nombre", "Aula"},
                    l -> new Object[]{l.getId(), l.getNombre(), l.getAula()});

            case "HORARIOS":
                return lista(cargaHorariaService.listarHorarios(),
                    new String[]{"ID", "Día", "Hora Inicio", "Hora Fin"},
                    h -> new Object[]{h.getId(), h.getDiaNombre(), h.getHoraInicio(), h.getHoraFin()});

            case "GRUPOS":
                return lista(cargaHorariaService.listarGrupos(),
                    new String[]{"ID", "Nombre", "Materia ID", "Docente/Auxiliar ID", "Periodo ID"},
                    g -> new Object[]{g.getId(), g.getNombre(), g.getMateriaId(), g.getUsuarioId(), g.getPeriodoId()});

            case "RESERVAS":
                return ResultadoDTO.lista(
                    new String[]{"ID", "Solicitante", "Laboratorio", "Día", "Horario", "Fecha Uso", "Estado", "Motivo"},
                    reservaService.listarReservasDetalladas()
                );

            case "PERMUTAS":
                return lista(permutaChatService.listarPermutas(),
                    new String[]{"ID", "Solicitante ID", "Receptor ID", "Fecha Ausencia", "Motivo", "Estado", "Fecha Solicitud"},
                    p -> new Object[]{p.getId(), p.getSolicitanteId(), p.getReceptorId(), p.getFechaAusencia(), p.getMotivo(), p.getEstado(), p.getFechaSolicitud()});

            case "ITEMS":
            case "INVENTARIO":
                if (param != null && !param.equals("*") && !param.isEmpty()) {
                    int labId = Integer.parseInt(param.trim());
                    return lista(inventarioService.listarItemsPorLaboratorio(labId),
                        new String[]{"ID", "Lab ID", "Código", "Nombre", "Descripción", "Estado Físico"},
                        i -> new Object[]{i.getId(), i.getLaboratorioId(), i.getCodigo(), i.getNombre(), i.getDescripcion(), i.getEstadoFisico()});
                }
                return lista(inventarioService.listarItems(),
                    new String[]{"ID", "Lab ID", "Código", "Nombre", "Descripción", "Estado Físico"},
                    i -> new Object[]{i.getId(), i.getLaboratorioId(), i.getCodigo(), i.getNombre(), i.getDescripcion(), i.getEstadoFisico()});

            case "INCIDENCIAS":
                return lista(inventarioService.listarIncidencias(),
                    new String[]{"ID", "Item ID", "Reporta ID", "Atiende ID", "Asunto", "Estado", "Solución", "Fecha"},
                    t -> new Object[]{t.getId(), t.getItemInventarioId(), t.getReportaId(), t.getAtiendeId(), t.getAsunto(), t.getEstado(), t.getSolucionAplicada(), t.getFechaIncidencia()});

            case "OBJETOS":
            case "OBJETOSPERDIDOS":
                return lista(inventarioService.listarObjetos(),
                    new String[]{"ID", "Lab ID", "Registra ID", "Reclama ID", "Descripción", "Estado", "Fecha Hallazgo", "Fecha Entrega"},
                    o -> new Object[]{o.getId(), o.getLaboratorioId(), o.getRegistraId(), o.getReclamaId(), o.getDescripcionObjeto(), o.getEstado(), o.getFechaHallazgo(), o.getFechaEntrega()});

            case "ASISTENCIAS":
                return lista(asistenciaService.listar(),
                    new String[]{"ID", "Auxiliar ID", "Grupo ID", "Fecha", "Estado Asistencia"},
                    a -> new Object[]{a.getId(), a.getUsuarioId(), a.getGrupoId(), a.getFecha(), a.getEstadoAsistencia()});

            case "CHAT":
            case "CHATS":
                return ejecutarListarChat(param);

            default:
                return ResultadoDTO.htmlCrudo(ErrorCentralizador.generarTarjetaComandoInvalido("LISTAR", entidad));
        }
    }

    // -------------------------------------------------------------------------
    // DISPATCHER DE CREACIONES (CREATE)
    // -------------------------------------------------------------------------
    private ResultadoDTO ejecutarCreate(String entidad, String raw) throws Exception {
        String[] partes = parseParams(raw);
        String entUpper = entidad != null ? entidad.toUpperCase() : "";

        switch (entUpper) {
            case "USUARIOS": {
                // nombres, apellidoP, apellidoM, genero, ci, fechaNac, telefono, rol, email, password, regInst
                if (partes.length < 9) {
                    return ResultadoDTO.parametroInvalido("CREATEUSUARIOS requiere al menos 9 parámetros: nombres, apellidoP, apellidoM, genero, ci, fechaNac, telefono, rol, email [password, regInst]");
                }
                String pass = partes.length > 9 ? partes[9] : "12345678";
                int regInst = partes.length > 10 ? parseInt(partes[10]) : 0;
                Usuario u = usuarioService.crearUsuario(
                    partes[0], partes[1], partes[2], partes[3], "",
                    parseInt(partes[4]), parseDate(partes[5]), parseInt(partes[6]),
                    partes[7], partes[8], pass, regInst, true
                );
                return ResultadoDTO.exito("Usuario creado exitosamente con ID " + u.getId() + " (" + u.getNombreCompleto() + ").");
            }
            case "CARRERAS": {
                if (partes.length < 3) return ResultadoDTO.parametroInvalido("CREATECARRERAS requiere: codigo, nombre, sigla");
                Carrera c = configService.crearCarrera(partes[0], partes[1], partes[2]);
                return ResultadoDTO.exito("Carrera creada exitosamente con ID " + c.getId() + " (" + c.getNombre() + ").");
            }
            case "MATERIAS": {
                if (partes.length < 2) return ResultadoDTO.parametroInvalido("CREATEMATERIAS requiere: nombre, sigla");
                Materia m = configService.crearMateria(partes[0], partes[1]);
                return ResultadoDTO.exito("Materia creada exitosamente con ID " + m.getId() + " (" + m.getNombre() + ").");
            }
            case "PERIODOS": {
                if (partes.length < 2) return ResultadoDTO.parametroInvalido("CREATEPERIODOS requiere: gestion, semestre");
                Periodo p = configService.crearPeriodo(parseInt(partes[0]), parseInt(partes[1]));
                return ResultadoDTO.exito("Periodo creado exitosamente con ID " + p.getId() + " (" + p.getDescripcion() + ").");
            }
            case "LABORATORIOS": {
                if (partes.length < 2) return ResultadoDTO.parametroInvalido("CREATELABORATORIOS requiere: nombre, horaApertura (HH:mm) [horaCierre (HH:mm)] o nombre, aula");
                Laboratorio lab;
                if (partes[1].contains(":")) {
                    Time ap = parseTime(partes[1]);
                    Time ci = partes.length > 2 ? parseTime(partes[2]) : Time.valueOf("21:30:00");
                    lab = cargaHorariaService.crearLaboratorio(partes[0], ap, ci);
                } else {
                    lab = cargaHorariaService.crearLaboratorio(partes[0], parseInt(partes[1]));
                }
                return ResultadoDTO.exito("Laboratorio creado exitosamente con ID " + lab.getId() + " (" + lab.getNombre() + ").");
            }
            case "HORARIOS": {
                if (partes.length < 3) return ResultadoDTO.parametroInvalido("CREATEHORARIOS requiere: dia, horaInicio (HH:mm), horaFin (HH:mm) [laboratorioId]");
                Integer labId = partes.length > 3 ? parseInt(partes[3]) : null;
                Horario h = cargaHorariaService.crearHorario(partes[0], parseTime(partes[1]), parseTime(partes[2]), labId);
                return ResultadoDTO.exito("Horario creado exitosamente con ID " + h.getId() + " (" + h.getDiaNombre() + " " + h.getHoraInicio() + "-" + h.getHoraFin() + ").");
            }
            case "GRUPOS": {
                if (partes.length < 2) return ResultadoDTO.parametroInvalido("CREATEGRUPOS requiere: nombre, materiaId [usuarioId, periodoId]");
                Integer uId = partes.length > 2 && !partes[2].isEmpty() ? parseInt(partes[2]) : null;
                Integer pId = partes.length > 3 && !partes[3].isEmpty() ? parseInt(partes[3]) : null;
                Grupo g = cargaHorariaService.crearGrupo(partes[0], parseInt(partes[1]), uId, pId);
                return ResultadoDTO.exito("Grupo creado exitosamente con ID " + g.getId() + " (" + g.getNombre() + ").");
            }
            case "RESERVAS": {
                if (partes.length < 4) return ResultadoDTO.parametroInvalido("CREATERESERVAS requiere: usuarioId, laboratorioId, horarioId, fechaUso (YYYY-MM-DD) [motivo]");
                String motivo = partes.length > 4 ? partes[4] : "Reserva académica";
                int rhId = reservaService.solicitarReserva(parseInt(partes[0]), parseInt(partes[1]), parseInt(partes[2]), parseDate(partes[3]), motivo);
                return ResultadoDTO.exito("Reserva procesada y autorizada exitosamente. Slot ID #" + rhId + " para el día " + partes[3] + ".");
            }
            case "PERMUTAS": {
                if (partes.length < 4) return ResultadoDTO.parametroInvalido("CREATEPERMUTAS requiere: solicitanteId, receptorId, horarioId, fechaAusencia [motivo]");
                String motivo = partes.length > 4 ? partes[4] : "Permuta de turno de laboratorio";
                PermutaTurno p = permutaChatService.solicitarPermuta(parseInt(partes[0]), parseInt(partes[1]), parseInt(partes[2]), parseDate(partes[3]), motivo);
                return ResultadoDTO.exito("Permuta de turno registrada con ID " + p.getId() + " en estado PENDIENTE de aceptación.");
            }
            case "ITEMS":
            case "INVENTARIO": {
                if (partes.length < 4) return ResultadoDTO.parametroInvalido("CREATEITEMS requiere: laboratorioId, codigo, nombre, descripcion [estadoFisico]");
                String estado = partes.length > 4 ? partes[4] : "BUENO";
                ItemInventario item = inventarioService.crearItem(parseInt(partes[0]), partes[1], partes[2], partes[3], estado);
                return ResultadoDTO.exito("Item de inventario registrado con ID " + item.getId() + " (" + item.getCodigo() + " - " + item.getNombre() + ").");
            }
            case "INCIDENCIAS": {
                if (partes.length < 4) return ResultadoDTO.parametroInvalido("CREATEINCIDENCIAS requiere: itemId, reportaId, asunto, descripcion");
                TicketIncidencia t = inventarioService.reportarIncidencia(parseInt(partes[0]), parseInt(partes[1]), partes[2], partes[3]);
                return ResultadoDTO.exito("Ticket de incidencia registrado con ID " + t.getId() + " en estado ABIERTO.");
            }
            case "OBJETOS":
            case "OBJETOSPERDIDOS": {
                if (partes.length < 3) return ResultadoDTO.parametroInvalido("CREATEOBJETOS requiere: laboratorioId, registraId, descripcionObjeto");
                ObjetoPerdido o = inventarioService.registrarObjeto(parseInt(partes[0]), parseInt(partes[1]), partes[2]);
                return ResultadoDTO.exito("Objeto perdido registrado con ID " + o.getId() + " en estado REPORTADO.");
            }
            case "ASISTENCIAS": {
                if (partes.length < 4) return ResultadoDTO.parametroInvalido("CREATEASISTENCIAS requiere: usuarioId, grupoId, fecha (YYYY-MM-DD), estado (PRESENTE, FALTA, LICENCIA, RETRASO)");
                Asistencia a = asistenciaService.registrarAsistencia(parseInt(partes[0]), parseInt(partes[1]), parseDate(partes[2]), partes[3]);
                return ResultadoDTO.exito("Asistencia registrada exitosamente con ID " + a.getId() + " (" + a.getEstadoAsistencia() + ").");
            }
            case "PRIVILEGIOS": {
                if (partes.length < 3) return ResultadoDTO.parametroInvalido("CREATEPRIVILEGIOS requiere: codigo, nombre, descripcion");
                Privilegio p = configService.crearPrivilegio(partes[0], partes[1], partes[2]);
                return ResultadoDTO.exito("Privilegio creado exitosamente con ID " + p.getId() + " (" + p.getNombre() + ").");
            }
            default:
                return ResultadoDTO.htmlCrudo(ErrorCentralizador.generarTarjetaComandoInvalido("CREATE", entidad));
        }
    }

    // -------------------------------------------------------------------------
    // DISPATCHER DE CONSULTAS (GET)
    // -------------------------------------------------------------------------
    private ResultadoDTO ejecutarGet(String entidad, String raw) throws Exception {
        int id = parseInt(raw);
        String entUpper = entidad != null ? entidad.toUpperCase() : "";

        switch (entUpper) {
            case "USUARIOS": {
                Usuario u = usuarioService.obtenerUsuario(id);
                if (u == null) return ResultadoDTO.noEncontrado("Usuario", String.valueOf(id));
                return lista(List.of(u),
                    new String[]{"ID", "Nombres", "Apellidos", "CI", "Rol", "Email", "Reg. Inst.", "Estado"},
                    x -> new Object[]{x.getId(), x.getNombres(), x.getApellidoP() + " " + (x.getApellidoM() != null ? x.getApellidoM() : ""),
                        x.getCi(), x.getRol(), x.getEmail(), x.getRegistroInstitucional(), x.isEstado() ? "ACTIVO" : "INACTIVO"});
            }
            case "LABORATORIOS": {
                Laboratorio l = cargaHorariaService.obtenerLaboratorio(id);
                if (l == null) return ResultadoDTO.noEncontrado("Laboratorio", String.valueOf(id));
                return lista(List.of(l), new String[]{"ID", "Nombre", "Aula"}, x -> new Object[]{x.getId(), x.getNombre(), x.getAula()});
            }
            case "GRUPOS": {
                Grupo g = cargaHorariaService.obtenerGrupo(id);
                if (g == null) return ResultadoDTO.noEncontrado("Grupo", String.valueOf(id));
                return lista(List.of(g), new String[]{"ID", "Nombre", "Materia ID", "Docente/Auxiliar ID", "Periodo ID"},
                    x -> new Object[]{x.getId(), x.getNombre(), x.getMateriaId(), x.getUsuarioId(), x.getPeriodoId()});
            }
            case "ITEMS": {
                ItemInventario item = inventarioService.obtenerItem(id);
                if (item == null) return ResultadoDTO.noEncontrado("Item", String.valueOf(id));
                return lista(List.of(item), new String[]{"ID", "Lab ID", "Código", "Nombre", "Descripción", "Estado Físico"},
                    x -> new Object[]{x.getId(), x.getLaboratorioId(), x.getCodigo(), x.getNombre(), x.getDescripcion(), x.getEstadoFisico()});
            }
            case "INCIDENCIAS": {
                TicketIncidencia t = inventarioService.obtenerIncidencia(id);
                if (t == null) return ResultadoDTO.noEncontrado("Incidencia", String.valueOf(id));
                return lista(List.of(t), new String[]{"ID", "Item ID", "Reporta ID", "Atiende ID", "Asunto", "Estado", "Solución", "Fecha"},
                    x -> new Object[]{x.getId(), x.getItemInventarioId(), x.getReportaId(), x.getAtiendeId(), x.getAsunto(), x.getEstado(), x.getSolucionAplicada(), x.getFechaIncidencia()});
            }
            case "OBJETOS": {
                ObjetoPerdido o = inventarioService.obtenerObjeto(id);
                if (o == null) return ResultadoDTO.noEncontrado("Objeto", String.valueOf(id));
                return lista(List.of(o), new String[]{"ID", "Lab ID", "Registra ID", "Reclama ID", "Descripción", "Estado", "Fecha Hallazgo", "Fecha Entrega"},
                    x -> new Object[]{x.getId(), x.getLaboratorioId(), x.getRegistraId(), x.getReclamaId(), x.getDescripcionObjeto(), x.getEstado(), x.getFechaHallazgo(), x.getFechaEntrega()});
            }
            default:
                return ResultadoDTO.htmlCrudo(ErrorCentralizador.generarTarjetaComandoInvalido("GET", entidad));
        }
    }

    // -------------------------------------------------------------------------
    // DISPATCHER DE ELIMINACIONES (DELETE)
    // -------------------------------------------------------------------------
    private ResultadoDTO ejecutarDelete(String entidad, String raw) throws Exception {
        int id = parseInt(raw);
        String entUpper = entidad != null ? entidad.toUpperCase() : "";

        switch (entUpper) {
            case "USUARIOS":
                usuarioService.eliminarUsuario(id);
                return ResultadoDTO.exito("Usuario con ID " + id + " eliminado exitosamente.");
            case "LABORATORIOS":
                cargaHorariaService.eliminarLaboratorio(id);
                return ResultadoDTO.exito("Laboratorio con ID " + id + " eliminado exitosamente.");
            case "GRUPOS":
                cargaHorariaService.eliminarGrupo(id);
                return ResultadoDTO.exito("Grupo con ID " + id + " eliminado exitosamente.");
            case "ITEMS":
                inventarioService.eliminarItem(id);
                return ResultadoDTO.exito("Item con ID " + id + " eliminado exitosamente.");
            case "RESERVAS":
                reservaService.eliminarReserva(id);
                return ResultadoDTO.exito("Reserva con ID " + id + " eliminada exitosamente.");
            case "ASISTENCIAS":
                asistenciaService.eliminar(id);
                return ResultadoDTO.exito("Registro de asistencia con ID " + id + " eliminado exitosamente.");
            default:
                return ResultadoDTO.htmlCrudo(ErrorCentralizador.generarTarjetaComandoInvalido("DELETE", entidad));
        }
    }

    // -------------------------------------------------------------------------
    // DISPATCHER DE ACTUALIZACIONES (UPDATE)
    // -------------------------------------------------------------------------
    private ResultadoDTO ejecutarUpdate(String entidad, String raw) throws Exception {
        String[] partes = parseParams(raw);
        if (partes.length < 1) return ResultadoDTO.parametroInvalido("UPDATE requiere al menos el ID.");
        int id = parseInt(partes[0]);
        String entUpper = entidad != null ? entidad.toUpperCase() : "";

        switch (entUpper) {
            case "USUARIOS": {
                // id, nombres, apellidoP, apellidoM, ci, telefono, rol, email
                if (partes.length < 8) return ResultadoDTO.parametroInvalido("UPDATEUSUARIOS requiere: id, nombres, apellidoP, apellidoM, ci, telefono, rol, email");
                usuarioService.actualizarUsuario(id, partes[1], partes[2], partes[3], null, null,
                        parseInt(partes[4]), null, parseInt(partes[5]), partes[6], partes[7], null, 0, true);
                return ResultadoDTO.exito("Usuario con ID " + id + " actualizado exitosamente.");
            }
            case "LABORATORIOS": {
                if (partes.length < 3) return ResultadoDTO.parametroInvalido("UPDATELABORATORIOS requiere: id, nombre, aula");
                Laboratorio l = cargaHorariaService.obtenerLaboratorio(id);
                if (l == null) return ResultadoDTO.noEncontrado("Laboratorio", String.valueOf(id));
                l.setNombre(partes[1]);
                l.setAula(parseInt(partes[2]));
                // Actualizar directamente en base
                return ResultadoDTO.exito("Laboratorio con ID " + id + " actualizado exitosamente.");
            }
            default:
                return ResultadoDTO.htmlCrudo(ErrorCentralizador.generarTarjetaComandoInvalido("UPDATE", entidad));
        }
    }

    // -------------------------------------------------------------------------
    // DISPATCHER DE COMANDOS DIRECTOS
    // -------------------------------------------------------------------------
    private ResultadoDTO ejecutarDisponibilidad(String raw) throws Exception {
        String[] partes = parseParams(raw);
        if (partes.length < 1) return ResultadoDTO.parametroInvalido("DISPONIBILIDAD requiere: laboratorioId [dia]");
        int labId = parseInt(partes[0]);
        String dia = partes.length > 1 ? partes[1] : "*";
        List<Object[]> reporte = cargaHorariaService.consultarDisponibilidad(labId, dia);
        return ResultadoDTO.lista(
            new String[]{"Laboratorio", "Ubicación", "Día", "Horario", "Disponibilidad", "Detalle de Ocupación"},
            reporte
        );
    }

    private ResultadoDTO ejecutarAsignarPrivilegio(String raw) throws Exception {
        String[] partes = parseParams(raw);
        if (partes.length < 2) return ResultadoDTO.parametroInvalido("ASIGNARPRIVILEGIO requiere: usuarioId, privilegioId");
        usuarioService.asignarPrivilegio(parseInt(partes[0]), parseInt(partes[1]));
        return ResultadoDTO.exito("Privilegio #" + partes[1] + " asignado exitosamente al usuario #" + partes[0] + ".");
    }

    private ResultadoDTO ejecutarAsignarCarrera(String raw) throws Exception {
        String[] partes = parseParams(raw);
        if (partes.length < 2) return ResultadoDTO.parametroInvalido("ASIGNARCARRERA requiere: usuarioId, carreraId");
        configService.asignarCarreraUsuario(parseInt(partes[0]), parseInt(partes[1]));
        return ResultadoDTO.exito("Carrera #" + partes[1] + " asignada al usuario #" + partes[0] + ".");
    }

    private ResultadoDTO ejecutarCarreraMateria(String raw) throws Exception {
        String[] partes = parseParams(raw);
        if (partes.length < 3) return ResultadoDTO.parametroInvalido("CARRERAMATERIA requiere: carreraId, materiaId, nivelSemestre");
        configService.asociarMateriaCarrera(parseInt(partes[0]), parseInt(partes[1]), parseInt(partes[2]));
        return ResultadoDTO.exito("Materia vinculada a carrera en el semestre " + partes[2] + ".");
    }

    private ResultadoDTO ejecutarAsignarHorario(String raw) throws Exception {
        String[] partes = parseParams(raw);
        if (partes.length < 3) return ResultadoDTO.parametroInvalido("ASIGNARHORARIO requiere: grupoId, horarioId, laboratorioId");
        GrupoHorario gh = cargaHorariaService.asignarGrupoHorario(parseInt(partes[0]), parseInt(partes[1]), parseInt(partes[2]), true, false, null);
        return ResultadoDTO.exito("Grupo #" + partes[0] + " asignado al Horario #" + partes[1] + " en el Laboratorio #" + partes[2] + " (ID Asignación: " + gh.getId() + ").");
    }

    private ResultadoDTO ejecutarEstadoReserva(String raw) throws Exception {
        String[] partes = parseParams(raw);
        if (partes.length < 2) return ResultadoDTO.parametroInvalido("ESTADORESERVA requiere: reservaHorarioId, nuevoEstado (APROBADA, RECHAZADA, CANCELADA)");
        reservaService.actualizarEstado(parseInt(partes[0]), partes[1]);
        return ResultadoDTO.exito("Estado de la reserva #" + partes[0] + " actualizado a " + partes[1].toUpperCase() + ".");
    }

    private ResultadoDTO ejecutarResponderPermuta(String raw) throws Exception {
        String[] partes = parseParams(raw);
        if (partes.length < 2) return ResultadoDTO.parametroInvalido("RESPONDERPERMUTA requiere: permutaId, nuevoEstado (ACEPTADA, RECHAZADA)");
        permutaChatService.responderPermuta(parseInt(partes[0]), partes[1]);
        return ResultadoDTO.exito("Permuta #" + partes[0] + " actualizada a " + partes[1].toUpperCase() + ".");
    }

    private ResultadoDTO ejecutarEnviarChat(String raw) throws Exception {
        String[] partes = parseParams(raw);
        if (partes.length < 3) return ResultadoDTO.parametroInvalido("CHAT requiere: remitenteId, destinatarioId, mensaje");
        Chat c = permutaChatService.enviarMensaje(parseInt(partes[0]), parseInt(partes[1]), partes[2]);
        return ResultadoDTO.exito("Mensaje enviado exitosamente a usuario #" + partes[1] + " (ID Mensaje: " + c.getId() + ").");
    }

    private ResultadoDTO ejecutarListarChat(String raw) throws Exception {
        String[] partes = parseParams(raw);
        if (partes.length < 2) return ResultadoDTO.parametroInvalido("LISTARCHAT requiere: usuario1, usuario2");
        return lista(permutaChatService.listarConversacion(parseInt(partes[0]), parseInt(partes[1])),
            new String[]{"ID", "De (Remitente)", "Para (Destinatario)", "Mensaje", "Fecha Envío", "Leído"},
            c -> new Object[]{c.getId(), c.getRemitenteId(), c.getDestinatarioId(), c.getContenido(), c.getFechaEnvio(), c.isEstadoLectura() ? "SÍ" : "NO"});
    }

    private ResultadoDTO ejecutarAtenderIncidencia(String raw) throws Exception {
        String[] partes = parseParams(raw);
        if (partes.length < 4) return ResultadoDTO.parametroInvalido("ATENDERINCIDENCIA requiere: ticketId, atiendeId, solucion, nuevoEstado (EN_PROCESO, RESUELTO, CERRADO)");
        inventarioService.atenderIncidencia(parseInt(partes[0]), parseInt(partes[1]), partes[2], partes[3]);
        return ResultadoDTO.exito("Incidencia #" + partes[0] + " atendida con estado " + partes[3].toUpperCase() + ".");
    }

    private ResultadoDTO ejecutarEntregarObjeto(String raw) throws Exception {
        String[] partes = parseParams(raw);
        if (partes.length < 2) return ResultadoDTO.parametroInvalido("ENTREGAROBJETO requiere: objetoId, reclamaId");
        inventarioService.entregarObjeto(parseInt(partes[0]), parseInt(partes[1]));
        return ResultadoDTO.exito("Objeto perdido #" + partes[0] + " entregado exitosamente al usuario #" + partes[1] + ".");
    }

    // -------------------------------------------------------------------------
    // DISPATCHER DE REPORTES (CU8)
    // -------------------------------------------------------------------------
    private ResultadoDTO ejecutarReporte(String tipo, String param) throws Exception {
        String tUpper = tipo != null ? tipo.toUpperCase() : "";
        switch (tUpper) {
            case "ASISTENCIA":
                return ResultadoDTO.lista(
                    new String[]{"ID Auxiliar", "Nombre Auxiliar", "Total Turnos", "Presentes", "Faltas", "Licencias", "% Cumplimiento"},
                    reporteService.reporteAsistenciaAuxiliares()
                );
            case "OCUPACION":
                return ResultadoDTO.lista(
                    new String[]{"ID Lab", "Nombre Laboratorio", "Ubicación", "Clases Fijas / Sem", "Reservas Registradas"},
                    reporteService.reporteOcupacionLaboratorios()
                );
            case "INCIDENCIAS":
                return ResultadoDTO.lista(
                    new String[]{"Laboratorio", "Total Incidencias", "Abiertas", "En Proceso", "Resueltas"},
                    reporteService.reporteIncidenciasInventario()
                );
            case "INVENTARIO":
                return ResultadoDTO.lista(
                    new String[]{"Estado Físico", "Cantidad de Equipos"},
                    reporteService.reporteEstadoInventario()
                );
            case "OBJETOS":
                return ResultadoDTO.lista(
                    new String[]{"Estado", "Cantidad de Objetos"},
                    reporteService.reporteObjetosPerdidos()
                );
            default:
                return ResultadoDTO.parametroInvalido("Tipo de reporte no reconocido: " + tipo + ". Disponibles: ASISTENCIA, OCUPACION, INCIDENCIAS, INVENTARIO, OBJETOS");
        }
    }

    // -------------------------------------------------------------------------
    // HELPERS
    // -------------------------------------------------------------------------
    private <T> ResultadoDTO lista(List<T> items, String[] headers, Function<T, Object[]> rowMapper) {
        List<Object[]> rows = new ArrayList<>();
        if (items != null) {
            for (T item : items) {
                rows.add(rowMapper.apply(item));
            }
        }
        return ResultadoDTO.lista(headers, rows);
    }

    private String[] parseParams(String raw) {
        if (raw == null || raw.trim().isEmpty() || raw.equals("*")) {
            return new String[0];
        }
        String[] partes = raw.split(",");
        for (int i = 0; i < partes.length; i++) {
            partes[i] = partes[i].trim();
        }
        return partes;
    }

    private int parseInt(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    private Date parseDate(String s) {
        try {
            return Date.valueOf(s.trim());
        } catch (Exception e) {
            return new Date(System.currentTimeMillis());
        }
    }

    private Time parseTime(String s) {
        try {
            String val = s.trim();
            if (val.length() == 5) val += ":00";
            return Time.valueOf(val);
        } catch (Exception e) {
            return Time.valueOf("07:00:00");
        }
    }

    private String normalizarEntidad(String entidad) {
        if (entidad == null) return "";
        String e = entidad.trim().toUpperCase();
        if (e.equals("USUARIO")) return "USUARIOS";
        if (e.equals("CARRERA")) return "CARRERAS";
        if (e.equals("MATERIA")) return "MATERIAS";
        if (e.equals("PERIODO")) return "PERIODOS";
        if (e.equals("LABORATORIO") || e.equals("LABAROTARIO") || e.equals("LABAROTARIOS")) return "LABORATORIOS";
        if (e.equals("HORARIO")) return "HORARIOS";
        if (e.equals("GRUPO")) return "GRUPOS";
        if (e.equals("RESERVA")) return "RESERVAS";
        if (e.equals("PERMUTA")) return "PERMUTAS";
        if (e.equals("ITEM")) return "ITEMS";
        if (e.equals("INCIDENCIA")) return "INCIDENCIAS";
        if (e.equals("OBJETO") || e.equals("OBJETOPERDIDO")) return "OBJETOS";
        if (e.equals("ASISTENCIA")) return "ASISTENCIAS";
        if (e.equals("PRIVILEGIO")) return "PRIVILEGIOS";
        return e;
    }

    private String wrapInHTML(String content) {
        if (content.toLowerCase().contains("<!doctype html>") || content.toLowerCase().contains("<html")) {
            return content;
        }
        return "<!DOCTYPE html>\n<html>\n<head>\n<meta charset='UTF-8'>\n"
            + "<style>\nbody { font-family: 'Segoe UI', Arial, sans-serif; margin: 20px; background-color: #f9f9f9; color: #333; }\n"
            + ".container { max-width: 900px; margin: 0 auto; background: white; padding: 25px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }\n"
            + "h2 { color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 10px; margin-top: 0; }\n"
            + "</style>\n</head>\n<body>\n<div class='container'>\n"
            + "<h2>SISTEMA DE GESTIÓN DE LABORATORIOS</h2>\n"
            + content
            + "\n</div>\n</body>\n</html>";
    }
}
