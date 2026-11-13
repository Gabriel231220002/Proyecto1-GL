"""
Definición estructurada del catálogo de pruebas de flujo para el
Sistema de Gestión de Laboratorios de Facultad (vía Correo Electrónico).
Compatible con el esquema de base de datos singular y ComandoOrchestratorService.
"""

CASOS_DE_PRUEBA = [
    {
        "id": "CU0",
        "titulo": "CU0: Ayuda General y Sintaxis del Sistema",
        "descripcion": "Verificación del comando HELP para consultar la lista de operaciones permitidas.",
        "comandos": [
            {
                "orden": 1,
                "comando": "HELP",
                "descripcion": "Solicitar catálogo general de ayuda y sintaxis"
            }
        ]
    },
    {
        "id": "CU1",
        "titulo": "CU1: Gestión de Usuarios y Privilegios",
        "descripcion": "Alta, consulta, actualización de usuarios y asignación de privilegios.",
        "comandos": [
            {
                "orden": 1,
                "comando": "LISTARUSUARIOS[*]",
                "descripcion": "Listar todos los usuarios activos en la base de datos"
            },
            {
                "orden": 2,
                "comando": "CREATEUSUARIOS[Mario, Gutierrez, Pinto, M, 8877665, 2001-09-15, 77001122, Auxiliar, mgutierrez@uagrm.edu.bo, 12345678, 2200551]",
                "descripcion": "Registrar nuevo usuario con rol de Auxiliar"
            },
            {
                "orden": 3,
                "comando": "GETUSUARIOS[4]",
                "descripcion": "Consultar datos detallados del usuario ID 4 (Mateo Mendoza - Auxiliar)"
            },
            {
                "orden": 4,
                "comando": "UPDATEUSUARIOS[4, Mateo, Mendoza, Flores, 1000004, 75544332, Auxiliar, auxiliar.mateo@uagrm.edu.bo]",
                "descripcion": "Actualizar teléfono y correo del usuario ID 4"
            },
            {
                "orden": 5,
                "comando": "LISTARPRIVILEGIOS[*]",
                "descripcion": "Listar privilegios registrados en el sistema"
            },
            {
                "orden": 6,
                "comando": "ASIGNARPRIVILEGIO[4, 2]",
                "descripcion": "Asignar privilegio ID 2 (PRIV_RESERVA) al usuario ID 4"
            }
        ]
    },
    {
        "id": "CU2",
        "titulo": "CU2: Configuración Semestral, Carreras y Materias",
        "descripcion": "Configuración de periodos semestrales, carreras, materias y mallas curriculares.",
        "comandos": [
            {
                "orden": 1,
                "comando": "CREATEPERIODOS[2027, 1]",
                "descripcion": "Crear periodo académico gestión 2027 semestre 1 (2026-2 ya existe como ID 1)"
            },
            {
                "orden": 2,
                "comando": "LISTARPERIODOS[*]",
                "descripcion": "Listar periodos semestrales registrados"
            },
            {
                "orden": 3,
                "comando": "CREATECARRERAS[105, Ingenieria Biomedica, BIO]",
                "descripcion": "Registrar nueva carrera universitaria (101-104 ya existen en semillas)"
            },
            {
                "orden": 4,
                "comando": "LISTARCARRERAS[*]",
                "descripcion": "Listar carreras registradas"
            },
            {
                "orden": 5,
                "comando": "CREATEMATERIAS[Sistemas Distribuidos, INF412]",
                "descripcion": "Registrar materia con sigla INF412"
            },
            {
                "orden": 6,
                "comando": "LISTARMATERIAS[*]",
                "descripcion": "Listar materias registradas"
            },
            {
                "orden": 7,
                "comando": "ASIGNARCARRERA[4, 1]",
                "descripcion": "Asignar carrera ID 1 (SIS) al usuario ID 4"
            },
            {
                "orden": 8,
                "comando": "CARRERAMATERIA[1, 1, 4]",
                "descripcion": "Vincular materia ID 1 a carrera ID 1 en nivel semestral 4"
            }
        ]
    },
    {
        "id": "CU3",
        "titulo": "CU3: Carga Horaria Base y Disponibilidad de Espacios",
        "descripcion": "Administración de laboratorios, franjas horarias, grupos y consulta de disponibilidad.",
        "comandos": [
            {
                "orden": 1,
                "comando": "CREATELABORATORIOS[Laboratorio de Inteligencia Artificial, 07:00, 21:30]",
                "descripcion": "Registrar nuevo laboratorio físico con horas de apertura y cierre"
            },
            {
                "orden": 2,
                "comando": "LISTARLABORATORIOS[*]",
                "descripcion": "Listar laboratorios registrados"
            },
            {
                "orden": 3,
                "comando": "CREATEHORARIOS[JUEVES, 14:00, 16:15, 1]",
                "descripcion": "Crear franja horaria de Jueves de 14:00 a 16:15 asociada al Laboratorio 1"
            },
            {
                "orden": 4,
                "comando": "LISTARHORARIOS[*]",
                "descripcion": "Listar horarios configurados"
            },
            {
                "orden": 5,
                "comando": "CREATEGRUPOS[SI, 1, 4, 1]",
                "descripcion": "Crear grupo académico SI para materia 1, docente/auxiliar 4 y periodo 1"
            },
            {
                "orden": 6,
                "comando": "LISTARGRUPOS[*]",
                "descripcion": "Listar grupos registrados"
            },
            {
                "orden": 7,
                "comando": "ASIGNARHORARIO[1, 1, 1]",
                "descripcion": "Asignar grupo 1 al horario 1 en el laboratorio 1"
            },
            {
                "orden": 8,
                "comando": "DISPONIBILIDAD[1, LUNES]",
                "descripcion": "Consultar disponibilidad del Laboratorio 1 para el día Lunes"
            },
            {
                "orden": 9,
                "comando": "DISPONIBILIDAD[1, *]",
                "descripcion": "Consultar disponibilidad semanal completa del Laboratorio 1"
            }
        ]
    },
    {
        "id": "CU4",
        "titulo": "CU4: Gestión de Reservas y Resolución de Conflictos",
        "descripcion": "Solicitud de reservas extraordinarias con algoritmo de detección de colisiones horarias.",
        "comandos": [
            {
                "orden": 1,
                "comando": "CREATERESERVAS[4, 1, 2, 2026-10-28, Taller de Robotica]",
                "descripcion": "Auxiliar 4 solicita reserva en Laboratorio 1, Horario 2 para el 2026-10-28"
            },
            {
                "orden": 2,
                "comando": "CREATERESERVAS[4, 1, 2, 2026-10-28, Intento Solapado]",
                "descripcion": "Prueba de conflicto: solicitud idéntica en fecha/horario/lab ocupado"
            },
            {
                "orden": 3,
                "comando": "LISTARRESERVAS[*]",
                "descripcion": "Listar reservas registradas en el sistema"
            },
            {
                "orden": 4,
                "comando": "ESTADORESERVA[1, APROBADA]",
                "descripcion": "Encargado autoriza y aprueba la reserva ID 1"
            }
        ]
    },
    {
        "id": "CU5",
        "titulo": "CU5: Permutas Temporales de Turno y Chat Interno",
        "descripcion": "Intercambio temporal de turnos entre auxiliares y mensajería institucional.",
        "comandos": [
            {
                "orden": 1,
                "comando": "CREATEPERMUTAS[4, 6, 1, 2026-10-30, Tramite personal en decanatura]",
                "descripcion": "Auxiliar 4 solicita permuta a usuario 6 para el horario 1 en fecha 2026-10-30"
            },
            {
                "orden": 2,
                "comando": "LISTARPERMUTAS[*]",
                "descripcion": "Listar solicitudes de permuta"
            },
            {
                "orden": 3,
                "comando": "RESPONDERPERMUTA[1, ACEPTADA]",
                "descripcion": "Usuario receptor responde aceptando la permuta ID 1"
            },
            {
                "orden": 4,
                "comando": "CHAT[4, 6, Hola colega quedamos confirmados con la permuta del viernes]",
                "descripcion": "Enviar mensaje de chat interno de usuario 4 a usuario 6"
            },
            {
                "orden": 5,
                "comando": "LISTARCHAT[4, 6]",
                "descripcion": "Listar conversación histórica entre usuario 4 y usuario 6"
            }
        ]
    },
    {
        "id": "CU6",
        "titulo": "CU6: Inventario, Incidencias y Objetos Encontrados",
        "descripcion": "Control de equipamiento físico, reporte/atención de fallas y custodia de objetos.",
        "comandos": [
            {
                "orden": 1,
                "comando": "CREATEITEMS[1, PC-LAB1-06, PC HP EliteDesk, Computadora fila 3, BUENO]",
                "descripcion": "Registrar nuevo ítem en inventario del Laboratorio 1"
            },
            {
                "orden": 2,
                "comando": "LISTARITEMS[1]",
                "descripcion": "Listar inventario del Laboratorio ID 1"
            },
            {
                "orden": 3,
                "comando": "CREATEINCIDENCIAS[1, 4, Teclado averiado, La tecla Enter no hace contacto]",
                "descripcion": "Reportar falla técnica sobre ítem 1 por auxiliar 4"
            },
            {
                "orden": 4,
                "comando": "LISTARINCIDENCIAS[*]",
                "descripcion": "Listar incidencias y estado de tickets"
            },
            {
                "orden": 5,
                "comando": "ATENDERINCIDENCIA[1, 3, Se limpio switch de membrana y funciona normal, RESUELTO]",
                "descripcion": "Encargado 3 atiende y resuelve ticket ID 1"
            },
            {
                "orden": 6,
                "comando": "CREATEOBJETOS[1, 4, Cuaderno espiral tapa dura con apuntes de Calculo I]",
                "descripcion": "Registrar objeto olvidado encontrado en Laboratorio 1 por auxiliar 4"
            },
            {
                "orden": 7,
                "comando": "LISTAROBJETOS[*]",
                "descripcion": "Listar objetos perdidos registrados"
            },
            {
                "orden": 8,
                "comando": "ENTREGAROBJETO[1, 6]",
                "descripcion": "Registrar entrega de objeto ID 1 a estudiante reclamante ID 6"
            }
        ]
    },
    {
        "id": "CU7",
        "titulo": "CU7: Supervisión y Asistencia de Auxiliares",
        "descripcion": "Registro y control de puntualidad y asistencia a turnos.",
        "comandos": [
            {
                "orden": 1,
                "comando": "CREATEASISTENCIAS[4, 1, 2026-10-14, PRESENTE]",
                "descripcion": "Registrar asistencia con estado PRESENTE para auxiliar 4 en horario/grupo 1"
            },
            {
                "orden": 2,
                "comando": "CREATEASISTENCIAS[4, 2, 2026-10-14, RETRASO]",
                "descripcion": "Registrar asistencia con estado RETRASO para auxiliar 4 en horario/grupo 2"
            },
            {
                "orden": 3,
                "comando": "LISTARASISTENCIAS[*]",
                "descripcion": "Listar registros históricos de asistencias"
            }
        ]
    },
    {
        "id": "CU8",
        "titulo": "CU8: Reportes y Estadísticas Gerenciales",
        "descripcion": "Consultas analíticas agregadas para autoridades (asistencia, ocupación, inventario).",
        "comandos": [
            {
                "orden": 1,
                "comando": "REPORTEASISTENCIA[*]",
                "descripcion": "Reporte de cumplimiento, puntualidad y faltas por auxiliar"
            },
            {
                "orden": 2,
                "comando": "REPORTEOCUPACION[*]",
                "descripcion": "Reporte de ocupación y uso semanal por laboratorio"
            },
            {
                "orden": 3,
                "comando": "REPORTEINCIDENCIAS[*]",
                "descripcion": "Reporte estadístico de incidencias técnicas (abiertas vs resueltas)"
            },
            {
                "orden": 4,
                "comando": "REPORTEINVENTARIO[*]",
                "descripcion": "Reporte de estado físico de equipamiento por laboratorio"
            },
            {
                "orden": 5,
                "comando": "REPORTEOBJETOS[*]",
                "descripcion": "Reporte de objetos encontrados y entregados"
            }
        ]
    }
]
