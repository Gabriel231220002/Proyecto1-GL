# Sistema de Gestión de Laboratorios de Facultad (Email POP3 / SMTP)

Backend en Java basado en **Arquitectura Hexagonal (Puertos y Adaptadores)** que opera de forma asíncrona mediante comandos enviados y respondidos por correo electrónico. Diseñado para la gestión integral de laboratorios universitarios, carga horaria, asignaciones, control de inventario, incidencias, asistencia y reportes gerenciales.

---

## 🏛 Arquitectura y Diseño

El sistema aplica estrictamente el desacoplamiento entre las reglas de negocio y los adaptadores de infraestructura:

```text
com.example/
├── Dominio/                 # 21 Entidades de negocio puras (POJOs independientes)
├── Puertos/                 # Interfaces de casos de uso y repositorios (In/Out)
├── Aplicacion/              # Servicios de aplicación (CU1 - CU8), orquestador y logging ANSI
│   ├── TerminalUI.java      # Paneles ANSI para entrada, errores con contexto y salida
│   ├── ComandoOrchestratorService.java
│   └── ...
└── Adaptadores/
    ├── Entrada/             # Daemon POP3 (Sondeo cada 10s) y Parser de comandos MIME
    └── Salida/              # Repositorios PostgreSQL (JDBC) y despachadores SMTP
```

---

## 📋 Casos de Uso Implementados

| Caso de Uso | Paquete Arquitectónico | Funcionalidad Principal |
| :--- | :--- | :--- |
| **CU1** | Administración y Seguridad | CRUD de usuarios, roles (Decano, Director, Encargado, Auxiliar), hash BCrypt y privilegios. |
| **CU2** | Administración y Seguridad | Periodos semestrales, carreras, materias y asignaciones curriculares. |
| **CU3** | Planificación y Espacios | Laboratorios físicos, franjas horarias, grupos y motor de consulta de disponibilidad. |
| **CU4** | Planificación y Espacios | Reservas extraordinarias con resolución preventiva de conflictos de solapamiento. |
| **CU5** | Planificación y Espacios | Permutas temporales de turno entre auxiliares y mensajería interna (Chat). |
| **CU6** | Operaciones y Control | Control de inventario de equipos, tickets de incidencias técnicas y objetos perdidos. |
| **CU7** | Operaciones y Control | Supervisión y registro de puntualidad/asistencia de personal auxiliar. |
| **CU8** | Operaciones y Control | Generación de reportes analíticos con métricas agregadas (cumplimiento, ocupación, fallas). |

---

## 🛠 Requisitos Previos

- **Java JDK 11** o superior.
- **Apache Maven 3.6+**.
- **PostgreSQL 14+** (local o remoto).
- **Python 3.10+** (para el runner de pruebas E2E en `pruebas_flujo/`).

---

## ⚙ Configuración

Crear el archivo `src/main/resources/config.properties` a partir de `config_copy.properties`:

```properties
# Base de Datos PostgreSQL (Local o TecnoWeb)
db.url=jdbc:postgresql://127.0.0.1:5432/academico26
db.user=postgres
db.password=tu_password

# Credenciales Google SMTP (Opcional - para despacho por smtp.gmail.com:465)
smtp.gmail.user=tu_correo@gmail.com
smtp.gmail.appPassword=xxxx xxxx xxxx xxxx
```

### Inicialización de Base de Datos
Ejecutar los scripts SQL incluidos en el repositorio:
```bash
# 1. Estructura de tablas (DDL)
psql -h 127.0.0.1 -U postgres -d academico26 -f schema_laboratorios.sql

# 2. Datos semilla y usuarios con BCrypt (DML)
psql -h 127.0.0.1 -U postgres -d academico26 -f seeds_laboratorios.sql
```

---

## 🚀 Compilación y Ejecución

Compilar el proyecto y ejecutar los tests unitarios:
```bash
mvn clean test
```

Iniciar el backend daemon:
```bash
mvn compile exec:java
```

Al iniciar, el sistema solicitará en consola seleccionar el canal de salida para las respuestas:
- `1`: **TecnoWeb SMTP** (`mail.tecnoweb.org.bo:25`).
- `2`: **Google SMTP** (`smtp.gmail.com:465` con SSL).

---

## 🧪 Pruebas de Flujo de Punta a Punta (E2E)

El proyecto incluye un entorno de pruebas interactivo y automatizado en la carpeta `pruebas_flujo/`:

```bash
# Iniciar el runner interactivo
./pruebas_flujo/ejecutar_pruebas.sh
```

Opciones de prueba directa por CLI:
```bash
# Enviar comando individual
python3 pruebas_flujo/runner.py --cmd "HELP"
python3 pruebas_flujo/runner.py --cmd "DISPONIBILIDAD[1, LUNES]"

# Ejecutar un Caso de Uso específico paso a paso
python3 pruebas_flujo/runner.py --cu CU4 --interactive

# Ejecutar todo el catálogo de pruebas con espera secuencial de 12s
python3 pruebas_flujo/runner.py --all --delay 12
```

Catálogo completo de comandos de prueba disponible en [prueba_flujo_laboratorios.txt](prueba_flujo_laboratorios.txt).

---

## 📖 Documentación PUD

La especificación formal basada en el Proceso Unificado de Desarrollo se encuentra en [DOCUMENTACION_PUDS.md](DOCUMENTACION_PUDS.md):
- Captura de Requisitos Funcionales y No Funcionales.
- Modelado de Actores.
- Paquetes de Casos de Uso y diagramas de dependencia en Mermaid.
- Especificación en tablas formales PUD de CU1 a CU8.
- Diseño de Base de Datos: Conceptual (ER), Lógico (3FN) y Físico (DDL PostgreSQL).
- Diagramas de Secuencia detallados para los flujos críticos.
