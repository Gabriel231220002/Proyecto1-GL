package com.example.Aplicacion;

public class CommandHelpHTML {

    public static String obtenerComandosDisponibles() {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n<html>\n<head>\n<meta charset='UTF-8'>\n");
        html.append("<style>\n");
        html.append("body { font-family: 'Segoe UI', Arial, sans-serif; margin: 20px; background: #f4f6f9; color: #333; line-height: 1.5; }\n");
        html.append(".container { max-width: 950px; margin: 0 auto; background: white; padding: 30px; border-radius: 8px; box-shadow: 0 4px 15px rgba(0,0,0,0.08); }\n");
        html.append("h1 { color: #1e3c72; border-bottom: 3px solid #2a5298; padding-bottom: 12px; margin-top: 0; font-size: 24px; }\n");
        html.append("h2 { color: #2c3e50; margin-top: 25px; padding-left: 8px; font-size: 18px; }\n");
        html.append(".badge-cu { display: inline-block; background: #2a5298; color: white; padding: 3px 8px; border-radius: 4px; font-size: 11px; font-weight: bold; margin-right: 6px; }\n");
        html.append("table { width: 100%; border-collapse: collapse; margin: 15px 0 25px 0; font-size: 13px; }\n");
        html.append("th { background: #2a5298; color: white; padding: 10px; text-align: left; font-weight: 600; }\n");
        html.append("td { padding: 9px 10px; border-bottom: 1px solid #e0e0e0; background: #fff; vertical-align: middle; }\n");
        html.append("tr:hover td { background: #f8fafc; }\n");
        html.append(".cmd-code { font-family: 'Courier New', monospace; font-weight: bold; color: #c0392b; background: #fef2f2; padding: 3px 6px; border-radius: 3px; display: inline-block; }\n");
        html.append(".tag-rol { display: inline-block; background: #e0e7ff; color: #3730a3; padding: 2px 6px; border-radius: 3px; font-size: 11px; font-weight: 600; }\n");
        html.append("</style>\n</head>\n<body>\n<div class='container'>\n");

        html.append("<h1>SISTEMA DE GESTIÓN DE LABORATORIOS - COMANDOS POR EMAIL</h1>\n");
        html.append("<p>Envía los comandos en el <strong>ASUNTO (Subject)</strong> del correo electrónico respetando la sintaxis y los corchetes <code>[...]</code>.</p>\n");

        // CU1
        html.append("<h2><span class='badge-cu'>CU1</span> GESTIÓN DE USUARIOS Y PRIVILEGIOS</h2>\n");
        html.append("<table><thead><tr><th>Comando</th><th>Descripción</th><th>Parámetros</th><th>Ejemplo</th></tr></thead><tbody>\n");
        html.append("<tr><td><span class='cmd-code'>CREATEUSUARIOS</span></td><td>Registrar nuevo usuario (Decano, Director, Encargado, Auxiliar)</td><td>nombres, apellidoP, apellidoM, genero, ci, fechaNac, telefono, rol, email, password, regInst</td><td><span class='cmd-code'>CREATEUSUARIOS[Pedro, Perez, Gomez, M, 7894561, 1999-05-12, 70011223, Auxiliar, pperez@uagrm.edu.bo, 12345678, 2190112]</span></td></tr>\n");
        html.append("<tr><td><span class='cmd-code'>LISTARUSUARIOS</span></td><td>Listar todos los usuarios</td><td>*</td><td><span class='cmd-code'>LISTARUSUARIOS[*]</span></td></tr>\n");
        html.append("<tr><td><span class='cmd-code'>GETUSUARIOS</span></td><td>Consultar usuario por ID</td><td>id</td><td><span class='cmd-code'>GETUSUARIOS[1]</span></td></tr>\n");
        html.append("<tr><td><span class='cmd-code'>UPDATEUSUARIOS</span></td><td>Actualizar usuario</td><td>id, nombres, apellidoP, apellidoM, ci, telefono, rol, email</td><td><span class='cmd-code'>UPDATEUSUARIOS[4, Pedro, Perez, Gomez, 7894561, 71122334, Auxiliar, pperez@uagrm.edu.bo]</span></td></tr>\n");
        html.append("<tr><td><span class='cmd-code'>DELETEUSUARIOS</span></td><td>Eliminar usuario</td><td>id</td><td><span class='cmd-code'>DELETEUSUARIOS[5]</span></td></tr>\n");
        html.append("<tr><td><span class='cmd-code'>ASIGNARPRIVILEGIO</span></td><td>Asignar privilegio a usuario</td><td>usuarioId, privilegioId</td><td><span class='cmd-code'>ASIGNARPRIVILEGIO[4, 2]</span></td></tr>\n");
        html.append("<tr><td><span class='cmd-code'>LISTARPRIVILEGIOS</span></td><td>Listar catálogo de privilegios</td><td>*</td><td><span class='cmd-code'>LISTARPRIVILEGIOS[*]</span></td></tr>\n");
        html.append("</tbody></table>\n");

        // CU2
        html.append("<h2><span class='badge-cu'>CU2</span> CONFIGURACIÓN SEMESTRAL, CARRERAS Y MATERIAS</h2>\n");
        html.append("<table><thead><tr><th>Comando</th><th>Descripción</th><th>Parámetros</th><th>Ejemplo</th></tr></thead><tbody>\n");
        html.append("<tr><td><span class='cmd-code'>CREATEPERIODOS</span></td><td>Crear periodo semestral</td><td>gestion, semestre</td><td><span class='cmd-code'>CREATEPERIODOS[2026, 2]</span></td></tr>\n");
        html.append("<tr><td><span class='cmd-code'>LISTARPERIODOS</span></td><td>Listar periodos académicos</td><td>*</td><td><span class='cmd-code'>LISTARPERIODOS[*]</span></td></tr>\n");
        html.append("<tr><td><span class='cmd-code'>CREATECARRERAS</span></td><td>Registrar carrera</td><td>codigo, nombre, sigla</td><td><span class='cmd-code'>CREATECARRERAS[187-4, Ingenieria en Sistemas, SIS]</span></td></tr>\n");
        html.append("<tr><td><span class='cmd-code'>LISTARCARRERAS</span></td><td>Listar carreras</td><td>*</td><td><span class='cmd-code'>LISTARCARRERAS[*]</span></td></tr>\n");
        html.append("<tr><td><span class='cmd-code'>CREATEMATERIAS</span></td><td>Registrar materia</td><td>nombre, sigla</td><td><span class='cmd-code'>CREATEMATERIAS[Estructuras de Datos, INF210]</span></td></tr>\n");
        html.append("<tr><td><span class='cmd-code'>LISTARMATERIAS</span></td><td>Listar materias</td><td>*</td><td><span class='cmd-code'>LISTARMATERIAS[*]</span></td></tr>\n");
        html.append("<tr><td><span class='cmd-code'>ASIGNARCARRERA</span></td><td>Asignar carrera a usuario</td><td>usuarioId, carreraId</td><td><span class='cmd-code'>ASIGNARCARRERA[4, 1]</span></td></tr>\n");
        html.append("<tr><td><span class='cmd-code'>CARRERAMATERIA</span></td><td>Vincular materia a carrera</td><td>carreraId, materiaId, nivelSemestre</td><td><span class='cmd-code'>CARRERAMATERIA[1, 1, 3]</span></td></tr>\n");
        html.append("</tbody></table>\n");

        // CU3
        html.append("<h2><span class='badge-cu'>CU3</span> CARGA HORARIA BASE Y CONSULTA DE DISPONIBILIDAD</h2>\n");
        html.append("<table><thead><tr><th>Comando</th><th>Descripción</th><th>Parámetros</th><th>Ejemplo</th></tr></thead><tbody>\n");
        html.append("<tr><td><span class='cmd-code'>CREATELABORATORIOS</span></td><td>Crear laboratorio</td><td>nombre, aula</td><td><span class='cmd-code'>CREATELABORATORIOS[Laboratorio Sistemas 2, 204]</span></td></tr>\n");
        html.append("<tr><td><span class='cmd-code'>LISTARLABORATORIOS</span></td><td>Listar laboratorios de la facultad</td><td>*</td><td><span class='cmd-code'>LISTARLABORATORIOS[*]</span></td></tr>\n");
        html.append("<tr><td><span class='cmd-code'>CREATEHORARIOS</span></td><td>Crear franja horaria</td><td>dia (1-6 o nombre), horaInicio, horaFin</td><td><span class='cmd-code'>CREATEHORARIOS[1, 07:00, 09:15]</span></td></tr>\n");
        html.append("<tr><td><span class='cmd-code'>LISTARHORARIOS</span></td><td>Listar horarios configurados</td><td>*</td><td><span class='cmd-code'>LISTARHORARIOS[*]</span></td></tr>\n");
        html.append("<tr><td><span class='cmd-code'>CREATEGRUPOS</span></td><td>Crear grupo académico</td><td>nombre, materiaId, usuarioId(opc), periodoId(opc)</td><td><span class='cmd-code'>CREATEGRUPOS[SC, 1, 4, 1]</span></td></tr>\n");
        html.append("<tr><td><span class='cmd-code'>LISTARGRUPOS</span></td><td>Listar grupos</td><td>*</td><td><span class='cmd-code'>LISTARGRUPOS[*]</span></td></tr>\n");
        html.append("<tr><td><span class='cmd-code'>ASIGNARHORARIO</span></td><td>Asignar grupo a horario y laboratorio</td><td>grupoId, horarioId, laboratorioId</td><td><span class='cmd-code'>ASIGNARHORARIO[1, 1, 1]</span></td></tr>\n");
        html.append("<tr><td><span class='cmd-code'>DISPONIBILIDAD</span></td><td>Consultar disponibilidad de laboratorios</td><td>laboratorioId, dia(opcional o *)</td><td><span class='cmd-code'>DISPONIBILIDAD[1, LUNES]</span></td></tr>\n");
        html.append("</tbody></table>\n");

        // CU4
        html.append("<h2><span class='badge-cu'>CU4</span> GESTIÓN DE RESERVAS Y RESOLUCIÓN DE CONFLICTOS</h2>\n");
        html.append("<table><thead><tr><th>Comando</th><th>Descripción</th><th>Parámetros</th><th>Ejemplo</th></tr></thead><tbody>\n");
        html.append("<tr><td><span class='cmd-code'>CREATERESERVAS</span></td><td>Solicitar reserva (con detección de choques)</td><td>usuarioId, labId, horarioId, fechaUso, motivo</td><td><span class='cmd-code'>CREATERESERVAS[4, 1, 1, 2026-10-20, Practica de Redes]</span></td></tr>\n");
        html.append("<tr><td><span class='cmd-code'>LISTARRESERVAS</span></td><td>Listar reservas registradas</td><td>*</td><td><span class='cmd-code'>LISTARRESERVAS[*]</span></td></tr>\n");
        html.append("<tr><td><span class='cmd-code'>ESTADORESERVA</span></td><td>Aprobar, rechazar o cancelar reserva</td><td>reservaHorarioId, estado (APROBADA, RECHAZADA, CANCELADA)</td><td><span class='cmd-code'>ESTADORESERVA[1, APROBADA]</span></td></tr>\n");
        html.append("</tbody></table>\n");

        // CU5
        html.append("<h2><span class='badge-cu'>CU5</span> PERMUTAS TEMPORALES DE TURNO Y CHAT INTERNO</h2>\n");
        html.append("<table><thead><tr><th>Comando</th><th>Descripción</th><th>Parámetros</th><th>Ejemplo</th></tr></thead><tbody>\n");
        html.append("<tr><td><span class='cmd-code'>CREATEPERMUTAS</span></td><td>Solicitar permuta de turno a otro auxiliar</td><td>solicitanteId, receptorId, horarioId, fechaAusencia, motivo</td><td><span class='cmd-code'>CREATEPERMUTAS[4, 5, 1, 2026-10-22, Examen de Grado]</span></td></tr>\n");
        html.append("<tr><td><span class='cmd-code'>RESPONDERPERMUTA</span></td><td>Aceptar o rechazar permuta de turno</td><td>permutaId, estado (ACEPTADA, RECHAZADA)</td><td><span class='cmd-code'>RESPONDERPERMUTA[1, ACEPTADA]</span></td></tr>\n");
        html.append("<tr><td><span class='cmd-code'>LISTARPERMUTAS</span></td><td>Listar permutas registradas</td><td>* o usuarioId</td><td><span class='cmd-code'>LISTARPERMUTAS[*]</span></td></tr>\n");
        html.append("<tr><td><span class='cmd-code'>CHAT</span></td><td>Enviar mensaje interno a otro usuario</td><td>remitenteId, destinatarioId, mensaje</td><td><span class='cmd-code'>CHAT[4, 5, Hola colega cubres mi turno el jueves?]</span></td></tr>\n");
        html.append("<tr><td><span class='cmd-code'>LISTARCHAT</span></td><td>Ver mensajes de chat</td><td>usuario1, usuario2</td><td><span class='cmd-code'>LISTARCHAT[4, 5]</span></td></tr>\n");
        html.append("</tbody></table>\n");

        // CU6
        html.append("<h2><span class='badge-cu'>CU6</span> INVENTARIO, INCIDENCIAS Y OBJETOS ENCONTRADOS</h2>\n");
        html.append("<table><thead><tr><th>Comando</th><th>Descripción</th><th>Parámetros</th><th>Ejemplo</th></tr></thead><tbody>\n");
        html.append("<tr><td><span class='cmd-code'>CREATEITEMS</span></td><td>Registrar ítem en inventario de laboratorio</td><td>laboratorioId, codigo, nombre, descripcion, estadoFisico</td><td><span class='cmd-code'>CREATEITEMS[1, PC-LAB1-05, PC Core i7, Computadora de fila 2, BUENO]</span></td></tr>\n");
        html.append("<tr><td><span class='cmd-code'>LISTARITEMS</span></td><td>Listar inventario de laboratorio</td><td>* o laboratorioId</td><td><span class='cmd-code'>LISTARITEMS[*]</span></td></tr>\n");
        html.append("<tr><td><span class='cmd-code'>CREATEINCIDENCIAS</span></td><td>Reportar falla técnica en equipo</td><td>itemId, reportaId, asunto, descripcion</td><td><span class='cmd-code'>CREATEINCIDENCIAS[1, 4, Mouse no responde, El puntero se traba]</span></td></tr>\n");
        html.append("<tr><td><span class='cmd-code'>ATENDERINCIDENCIA</span></td><td>Atender y resolver ticket de incidencia</td><td>ticketId, atiendeId, solucion, nuevoEstado</td><td><span class='cmd-code'>ATENDERINCIDENCIA[1, 3, Se reemplazo el mouse optico, RESUELTO]</span></td></tr>\n");
        html.append("<tr><td><span class='cmd-code'>LISTARINCIDENCIAS</span></td><td>Listar tickets de incidencia</td><td>* o estado (ABIERTO, RESUELTO)</td><td><span class='cmd-code'>LISTARINCIDENCIAS[*]</span></td></tr>\n");
        html.append("<tr><td><span class='cmd-code'>CREATEOBJETOS</span></td><td>Registrar objeto perdido encontrado en lab</td><td>laboratorioId, registraId, descripcionObjeto</td><td><span class='cmd-code'>CREATEOBJETOS[1, 4, Mochila negra marca Totto con cuadernos]</span></td></tr>\n");
        html.append("<tr><td><span class='cmd-code'>ENTREGAROBJETO</span></td><td>Marcar objeto entregado al propietario</td><td>objetoId, reclamaId</td><td><span class='cmd-code'>ENTREGAROBJETO[1, 4]</span></td></tr>\n");
        html.append("<tr><td><span class='cmd-code'>LISTAROBJETOS</span></td><td>Listar objetos perdidos / encontrados</td><td>*</td><td><span class='cmd-code'>LISTAROBJETOS[*]</span></td></tr>\n");
        html.append("</tbody></table>\n");

        // CU7
        html.append("<h2><span class='badge-cu'>CU7</span> SUPERVISIÓN Y ASISTENCIA DE AUXILIARES</h2>\n");
        html.append("<table><thead><tr><th>Comando</th><th>Descripción</th><th>Parámetros</th><th>Ejemplo</th></tr></thead><tbody>\n");
        html.append("<tr><td><span class='cmd-code'>CREATEASISTENCIAS</span></td><td>Registrar asistencia a turno</td><td>usuarioId, grupoId, fecha, estado (PRESENTE, FALTA, LICENCIA, RETRASO)</td><td><span class='cmd-code'>CREATEASISTENCIAS[4, 1, 2026-10-14, PRESENTE]</span></td></tr>\n");
        html.append("<tr><td><span class='cmd-code'>LISTARASISTENCIAS</span></td><td>Listar asistencias registradas</td><td>* o usuarioId</td><td><span class='cmd-code'>LISTARASISTENCIAS[*]</span></td></tr>\n");
        html.append("</tbody></table>\n");

        // CU8
        html.append("<h2><span class='badge-cu'>CU8</span> REPORTES Y ESTADÍSTICAS GERENCIALES</h2>\n");
        html.append("<table><thead><tr><th>Comando</th><th>Descripción</th><th>Parámetros</th><th>Ejemplo</th></tr></thead><tbody>\n");
        html.append("<tr><td><span class='cmd-code'>REPORTEASISTENCIA</span></td><td>Reporte estadístico de cumplimiento y asistencia de auxiliares</td><td>*</td><td><span class='cmd-code'>REPORTEASISTENCIA[*]</span></td></tr>\n");
        html.append("<tr><td><span class='cmd-code'>REPORTEOCUPACION</span></td><td>Reporte de ocupación y uso semanal por laboratorio</td><td>*</td><td><span class='cmd-code'>REPORTEOCUPACION[*]</span></td></tr>\n");
        html.append("<tr><td><span class='cmd-code'>REPORTEINCIDENCIAS</span></td><td>Resumen de incidencias técnicas por laboratorio</td><td>*</td><td><span class='cmd-code'>REPORTEINCIDENCIAS[*]</span></td></tr>\n");
        html.append("<tr><td><span class='cmd-code'>REPORTEINVENTARIO</span></td><td>Estadísticas del estado físico del inventario</td><td>*</td><td><span class='cmd-code'>REPORTEINVENTARIO[*]</span></td></tr>\n");
        html.append("<tr><td><span class='cmd-code'>REPORTEOBJETOS</span></td><td>Reporte de objetos perdidos vs entregados</td><td>*</td><td><span class='cmd-code'>REPORTEOBJETOS[*]</span></td></tr>\n");
        html.append("</tbody></table>\n");

        html.append("</div>\n</body>\n</html>");
        return html.toString();
    }
}
