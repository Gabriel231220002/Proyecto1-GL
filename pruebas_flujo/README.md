# Entorno y Runner de Pruebas E2E (Flujo por Correo)
## Sistema de Gestión de Laboratorios de Facultad (POP3 / SMTP)

Esta carpeta contiene el despachador automatizado e interactivo de pruebas para validar de punta a punta todo el catálogo de casos de uso (CU0 a CU8) descritos en `prueba_flujo_laboratorios.txt`.

---

### 1. Estructura de la Carpeta

```text
pruebas_flujo/
├── flujo_casos_uso.py      # Catálogo estructurado de comandos clasificados por caso de uso
├── runner.py               # Motor interactivo y CLI para despacho por socket/SMTP
├── ejecutar_pruebas.sh     # Script lanzador rápido (Bash)
└── README.md               # Esta guía de uso
```

---

### 2. Canales de Despacho Soportados

El runner permite despachar comandos simulando el envío de correos hacia la cuenta configurada en `config.properties` (por defecto `grupo08sc@tecnoweb.org.bo`):

1. **TecnoWeb Directo (`tecnoweb` - Puerto 25):**
   - Conexión socket SMTP directa hacia `mail.tecnoweb.org.bo:25`.
   - Permite definir cualquier remitente (`From:`) sin requerir credenciales externas (ideal para pruebas rápidas).
2. **Google Gmail SMTP (`gmail` - Puerto 465 SSL):**
   - Conexión cifrada a `smtp.gmail.com:465`.
   - Autenticación con `smtp.gmail.user` y `smtp.gmail.appPassword` (leídos automáticamente de `config.properties`).
   - Envía un correo real desde tu cuenta de Gmail.

---

### 3. Cómo Ejecutar las Pruebas

#### Paso 1: Iniciar el Backend Java (Terminal 1)
En la raíz del proyecto, iniciar el servidor daemon:
```bash
mvn compile exec:java
```
Al solicitar el servidor de respuesta:
- Seleccioná `1` para Tecnoweb SMTP, o `2` para responder por Google SMTP (según desees recibir la respuesta por Gmail).

#### Paso 2: Lanzar el Test Runner (Terminal 2)

##### Opción A: Menú Interactivo (Recomendado)
```bash
./pruebas_flujo/ejecutar_pruebas.sh
```
Te presentará un menú con opciones:
- `1`: Seleccionar y ejecutar un caso de uso específico (ej. CU4 Reservas, CU6 Inventario, CU8 Reportes).
- `2`: Flujo completo paso a paso (presionar `[Enter]` en cada comando para dispararlo).
- `3`: Flujo automático secuencial con retardo (por defecto 12s para respetar el loop de 10s del daemon POP3).
- `4`: Enviar cualquier comando manual arbitrario.
- `5`: Consultar el estado de la bandeja POP3 en `mail.tecnoweb.org.bo` (ver si quedan correos pendientes).
- `6`: Alternar canal de envío (TecnoWeb / Gmail) y remitente.

##### Opción B: Ejecución por Línea de Comandos (CLI)
- **Probar un único comando:**
  ```bash
  python3 pruebas_flujo/runner.py --cmd "HELP"
  python3 pruebas_flujo/runner.py --cmd "DISPONIBILIDAD[1, LUNES]"
  ```

- **Probar un caso de uso específico:**
  ```bash
  python3 pruebas_flujo/runner.py --cu CU4 --interactive
  ```

- **Enviar usando tu cuenta de Gmail:**
  ```bash
  python3 pruebas_flujo/runner.py --cmd "REPORTEASISTENCIA[*]" --transport gmail
  ```

- **Ejecutar todo el flujo automáticamente con 15s de espera:**
  ```bash
  python3 pruebas_flujo/runner.py --all --delay 15
  ```
