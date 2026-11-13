#!/usr/bin/env python3
"""
Test Runner y Despachador de Comandos por Correo Electrónico
para el Sistema de Gestión de Laboratorios de Facultad (POP3/SMTP).
"""

import sys
import os
import time
import socket
import smtplib
import ssl
import poplib
import argparse
from email.message import EmailMessage
from pathlib import Path

# Cargar catálogo de casos de uso
try:
    from flujo_casos_uso import CASOS_DE_PRUEBA
except ImportError:
    from pruebas_flujo.flujo_casos_uso import CASOS_DE_PRUEBA

def cargar_config_properties():
    """Lee credenciales de Gmail, POP3 y SMTP desde config.properties si existe."""
    base_dir = Path(__file__).resolve().parent.parent
    candidatos = [
        base_dir / "src" / "main" / "resources" / "config.properties",
        base_dir / "config.properties"
    ]
    props = {}
    for ruta in candidatos:
        if ruta.exists():
            with open(ruta, "r", encoding="utf-8", errors="ignore") as f:
                for line in f:
                    line = line.strip()
                    if line and not line.startswith("#") and "=" in line:
                        k, v = line.split("=", 1)
                        props[k.strip()] = v.strip().strip('"').strip("'")
            break
    return props

PROPS_GLOBAL = cargar_config_properties()

# Constantes del Sistema
TECNO_HOST = PROPS_GLOBAL.get("pop.host", PROPS_GLOBAL.get("smtp.host", "mail.tecnoweb.org.bo"))
TECNO_SMTP_PORT = int(PROPS_GLOBAL.get("smtp.port", "25"))
TECNO_POP_PORT = int(PROPS_GLOBAL.get("pop.port", "110"))
TECNO_POP_USER = PROPS_GLOBAL.get("pop.user", "grupo08sc")
TECNO_POP_PASS = PROPS_GLOBAL.get("pop.password", PROPS_GLOBAL.get("pop.pass", ""))
TECNO_MAILBOX = PROPS_GLOBAL.get("smtp.sender", f"{TECNO_POP_USER}@{TECNO_HOST}")

GMAIL_HOST = "smtp.gmail.com"
GMAIL_PORT = 465

# Códigos ANSI para salida elegante en terminal
RESET = "\033[0m"
BOLD = "\033[1m"
RED = "\033[31m"
GREEN = "\033[32m"
YELLOW = "\033[33m"
BLUE = "\033[34m"
MAGENTA = "\033[35m"
CYAN = "\033[36m"
WHITE = "\033[37m"


def banner():
    print(f"{CYAN}{BOLD}")
    print("╔══════════════════════════════════════════════════════════════════════════════╗")
    print("║        TEST RUNNER - GESTIÓN DE LABORATORIOS DE FACULTAD (EMAIL)             ║")
    print("║             Despachador de Comandos y Pruebas Automatizadas                  ║")
    print("╚══════════════════════════════════════════════════════════════════════════════╝")
    print(f"{RESET}")




class EmailDispatcher:
    def __init__(self, remitente, transport="tecnoweb", gmail_user=None, gmail_pass=None):
        self.remitente = remitente
        self.transport = transport.lower()
        self.gmail_user = gmail_user
        self.gmail_pass = gmail_pass

    def enviar_via_tecnoweb_socket(self, asunto, cuerpo=""):
        """Envío directo vía socket SMTP al puerto 25 de mail.tecnoweb.org.bo."""
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        s.settimeout(12)
        try:
            s.connect((TECNO_HOST, TECNO_SMTP_PORT))
            banner_resp = s.recv(1024).decode(errors="ignore")
            if not banner_resp.startswith("220"):
                raise IOError(f"Banner SMTP inesperado: {banner_resp}")

            def cmd(c, exp="250"):
                s.sendall(c.encode("utf-8"))
                r = s.recv(1024).decode(errors="ignore")
                if not r.startswith(exp) and not r.startswith("354"):
                    raise IOError(f"Fallo en '{c.strip()}': {r.strip()}")
                return r

            cmd(f"HELO {TECNO_HOST}\r\n", "250")
            cmd(f"MAIL FROM:<{self.remitente}>\r\n", "250")
            cmd(f"RCPT TO:<{TECNO_MAILBOX}>\r\n", "250")
            cmd("DATA\r\n", "354")

            msg_raw = (
                f"From: <{self.remitente}>\r\n"
                f"To: <{TECNO_MAILBOX}>\r\n"
                f"Subject: {asunto}\r\n"
                f"MIME-Version: 1.0\r\n"
                f"Content-Type: text/plain; charset=utf-8\r\n"
                f"\r\n"
                f"{cuerpo or asunto}\r\n"
                f".\r\n"
            )
            s.sendall(msg_raw.encode("utf-8"))
            data_resp = s.recv(1024).decode(errors="ignore")
            if not data_resp.startswith("250"):
                raise IOError(f"Fallo al confirmar DATA: {data_resp.strip()}")

            try:
                cmd("QUIT\r\n", "221")
            except Exception:
                pass
            return True, "Enviado exitosamente por TecnoWeb SMTP (Port 25)"
        except Exception as e:
            return False, str(e)
        finally:
            try:
                s.close()
            except Exception:
                pass

    def enviar_via_gmail_ssl(self, asunto, cuerpo=""):
        """Envío autenticado vía Gmail SMTP (Port 465 SSL) usando Contraseña de Aplicación."""
        if not self.gmail_user or not self.gmail_pass:
            return False, "Credenciales de Gmail incompletas (smtp.gmail.user o smtp.gmail.appPassword faltante)."

        msg = EmailMessage()
        msg["Subject"] = asunto
        msg["From"] = self.gmail_user
        msg["To"] = TECNO_MAILBOX
        msg.set_content(cuerpo or f"Comando ejecutado desde Test Runner: {asunto}")

        context = ssl.create_default_context()
        try:
            with smtplib.SMTP_SSL(GMAIL_HOST, GMAIL_PORT, context=context, timeout=15) as server:
                server.login(self.gmail_user, self.gmail_pass)
                server.send_message(msg)
            return True, f"Enviado exitosamente por Gmail SSL (From: {self.gmail_user})"
        except Exception as e:
            return False, str(e)

    def enviar(self, asunto, cuerpo=""):
        if self.transport == "gmail":
            return self.enviar_via_gmail_ssl(asunto, cuerpo)
        else:
            return self.enviar_via_tecnoweb_socket(asunto, cuerpo)


def consultar_estado_pop3():
    """Consulta la cantidad de correos en la bandeja de entrada del servidor."""
    try:
        pop = poplib.POP3(TECNO_HOST, TECNO_POP_PORT, timeout=10)
        pop.user(TECNO_POP_USER)
        pop.pass_(TECNO_POP_PASS)
        num_msgs, total_bytes = pop.stat()
        pop.quit()
        return num_msgs, total_bytes
    except Exception as e:
        return -1, str(e)


def imprimir_item_comando(cu_id, item, status="LISTO"):
    print(f" {CYAN}┌─[{cu_id} Paso {item['orden']}]{RESET} {BOLD}{item['comando']}{RESET}")
    print(f" {CYAN}└─►{RESET} {WHITE}{item['descripcion']}{RESET}")


def ejecutar_lista_comandos(dispatcher, lista_comandos, cu_id, interactivo=False, delay=12):
    total = len(lista_comandos)
    for idx, item in enumerate(lista_comandos, 1):
        print(f"\n{BLUE}[{idx}/{total}]{RESET} Preparando comando:")
        imprimir_item_comando(cu_id, item)

        if interactivo:
            print(f"{YELLOW}Presione [ENTER] para enviar, [s] para saltear, [q] para cancelar: {RESET}", end="")
            opc = input().strip().lower()
            if opc == "s":
                print(f"{YELLOW}⏩ Comando salteado.{RESET}")
                continue
            elif opc == "q":
                print(f"{RED}⏹ Flujo cancelado por el usuario.{RESET}")
                break

        print(f" {MAGENTA}⚡ Despachando a {TECNO_MAILBOX} vía {dispatcher.transport.upper()}...{RESET}")
        ok, msg = dispatcher.enviar(item["comando"])
        if ok:
            print(f" {GREEN}✔ Éxito:{RESET} {msg}")
        else:
            print(f" {RED}✖ Error al despachar:{RESET} {msg}")

        # Si es modo automático y no es el último, pausar para permitir que el Daemon Java procese
        if not interactivo and idx < total:
            print(f"{YELLOW}⏳ Esperando {delay}s para que el daemon POP3 procese el correo...{RESET}")
            time.sleep(delay)


def menu_interactivo(dispatcher):
    while True:
        banner()
        print(f"{WHITE}Configuración Actual de Pruebas:{RESET}")
        print(f" • Remitente:      {GREEN}{dispatcher.remitente}{RESET}")
        print(f" • Canal Saliente: {YELLOW}{dispatcher.transport.upper()}{RESET} ({'Port 465 SSL' if dispatcher.transport == 'gmail' else 'Port 25 Directo'})")
        print(f" • Buzón Destino:  {CYAN}{TECNO_MAILBOX}{RESET}")
        print("────────────────────────────────────────────────────────────────────────")
        print(f"{BOLD}SELECCIONE UNA OPCIÓN:{RESET}")
        print(" 1. Ejecutar Caso de Uso Específico (CU0 a CU8)")
        print(" 2. Ejecutar Flujo Completo Paso a Paso (Interactivo)")
        print(" 3. Ejecutar Flujo Completo Automático (Secuencial con Delay)")
        print(" 4. Enviar Comando Personalizado Directo")
        print(" 5. Consultar Estado de Bandeja POP3 (Correos en cola)")
        print(" 6. Cambiar Remitente o Canal (TecnoWeb / Gmail)")
        print(" 0. Salir")
        print("────────────────────────────────────────────────────────────────────────")

        try:
            opcion = input(f"{BOLD}Opción [0-6]: {RESET}").strip()
        except (KeyboardInterrupt, EOFError):
            print("\nSaliendo...")
            break

        if opcion == "0":
            print("Finalizando runner.")
            break

        elif opcion == "1":
            print(f"\n{BOLD}Seleccione el Caso de Uso a probar:{RESET}")
            for idx, cu in enumerate(CASOS_DE_PRUEBA):
                print(f" {idx}. {cu['titulo']} ({len(cu['comandos'])} comandos)")
            try:
                sub_opc = input(f"{BOLD}Selección [0-{len(CASOS_DE_PRUEBA)-1}]: {RESET}").strip()
                cu_sel = CASOS_DE_PRUEBA[int(sub_opc)]
                print(f"\n{CYAN}{BOLD}=== {cu_sel['titulo']} ==={RESET}")
                print(f"{WHITE}{cu_sel['descripcion']}{RESET}\n")
                ejecutar_lista_comandos(dispatcher, cu_sel["comandos"], cu_sel["id"], interactivo=True)
            except Exception as e:
                print(f"{RED}Selección inválida: {e}{RESET}")
            input(f"\n{WHITE}Presione ENTER para continuar...{RESET}")

        elif opcion == "2":
            print(f"\n{BOLD}Iniciando Flujo Completo Paso a Paso...{RESET}")
            for cu in CASOS_DE_PRUEBA:
                print(f"\n{CYAN}{BOLD}=== {cu['titulo']} ==={RESET}")
                ejecutar_lista_comandos(dispatcher, cu["comandos"], cu["id"], interactivo=True)
            input(f"\n{WHITE}Presione ENTER para continuar...{RESET}")

        elif opcion == "3":
            try:
                retardo_str = input(f"Delay entre comandos en segundos [por defecto 12s]: ").strip()
                retardo = int(retardo_str) if retardo_str else 12
            except ValueError:
                retardo = 12

            print(f"\n{YELLOW}Iniciando despacho automático con delay de {retardo}s entre mensajes...{RESET}")
            for cu in CASOS_DE_PRUEBA:
                print(f"\n{CYAN}{BOLD}=== {cu['titulo']} ==={RESET}")
                ejecutar_lista_comandos(dispatcher, cu["comandos"], cu["id"], interactivo=False, delay=retardo)
            print(f"\n{GREEN}{BOLD}✔ Flujo completo despachado.{RESET}")
            input(f"\n{WHITE}Presione ENTER para continuar...{RESET}")

        elif opcion == "4":
            cmd_custom = input(f"\n{BOLD}Ingrese el comando (ej: GETUSUARIOS[4]): {RESET}").strip()
            if cmd_custom:
                print(f" {MAGENTA}⚡ Despachando '{cmd_custom}' a {TECNO_MAILBOX}...{RESET}")
                ok, msg = dispatcher.enviar(cmd_custom)
                if ok:
                    print(f" {GREEN}✔ Éxito:{RESET} {msg}")
                else:
                    print(f" {RED}✖ Error:{RESET} {msg}")
            input(f"\n{WHITE}Presione ENTER para continuar...{RESET}")

        elif opcion == "5":
            print(f"\n{CYAN}Consultando servidor POP3 ({TECNO_HOST}:{TECNO_POP_PORT})...{RESET}")
            msgs, b = consultar_estado_pop3()
            if msgs >= 0:
                print(f" {GREEN}✔ Bandeja POP3:{RESET} {msgs} correos pendientes ({b} bytes).")
                if msgs == 0:
                    print(f"   {WHITE}(La bandeja está vacía: el Daemon Java ya procesó los mensajes pendientes){RESET}")
                else:
                    print(f"   {YELLOW}(Hay {msgs} mensajes esperando ser leídos por el Daemon Java){RESET}")
            else:
                print(f" {RED}✖ Error al conectar con POP3:{RESET} {b}")
            input(f"\n{WHITE}Presione ENTER para continuar...{RESET}")

        elif opcion == "6":
            print(f"\n{BOLD}Configuración de Transporte:{RESET}")
            print(" 1. TecnoWeb Directo (Port 25 - sin credenciales externas)")
            print(" 2. Google Gmail SMTP (Port 465 SSL - usa tu cuenta y AppPassword)")
            trans_sel = input("Opción [1-2]: ").strip()
            if trans_sel == "2":
                dispatcher.transport = "gmail"
                new_user = input(f"Correo de Gmail [{dispatcher.gmail_user or ''}]: ").strip()
                if new_user:
                    dispatcher.gmail_user = new_user
                    dispatcher.remitente = new_user
                new_pass = input("App Password (16 caracteres) [dejar vacío para mantener]: ").strip()
                if new_pass:
                    dispatcher.gmail_pass = new_pass
            else:
                dispatcher.transport = "tecnoweb"
                new_rem = input(f"Correo remitente para respuestas [{dispatcher.remitente}]: ").strip()
                if new_rem:
                    dispatcher.remitente = new_rem
            print(f"{GREEN}✔ Configuración actualizada.{RESET}")
            time.sleep(1)


def main():
    parser = argparse.ArgumentParser(description="Test Runner para el Sistema de Gestión de Laboratorios")
    parser.add_argument("--all", action="store_true", help="Ejecutar todo el catálogo de pruebas de forma automática")
    parser.add_argument("--interactive", action="store_true", help="Ejecutar todo el catálogo paso a paso")
    parser.add_argument("--cu", type=str, help="Ejecutar un caso de uso específico (ej: CU4 o CU8)")
    parser.add_argument("--cmd", type=str, help="Enviar un único comando específico")
    parser.add_argument("--transport", type=str, choices=["tecnoweb", "gmail"], default=None, help="Canal SMTP de envío")
    parser.add_argument("--sender", type=str, default=None, help="Correo electrónico del remitente")
    parser.add_argument("--delay", type=int, default=12, help="Segundos de espera entre envíos en modo automático")
    args = parser.parse_args()

    # Cargar defaults de config.properties
    props = cargar_config_properties()
    gmail_user = props.get("smtp.gmail.user", "78042869ale@gmail.com")
    gmail_pass = props.get("smtp.gmail.appPassword", "")

    # Determinar transporte y remitente inicial
    transport = args.transport or "tecnoweb"
    sender = args.sender or (gmail_user if gmail_user else "78042869ale@gmail.com")

    dispatcher = EmailDispatcher(
        remitente=sender,
        transport=transport,
        gmail_user=gmail_user,
        gmail_pass=gmail_pass
    )

    # Modos de ejecución por CLI
    if args.cmd:
        banner()
        print(f"{MAGENTA}⚡ Enviando comando directo:{RESET} {args.cmd}")
        ok, msg = dispatcher.enviar(args.cmd)
        print(f"Resultado: {msg}")
        sys.exit(0 if ok else 1)

    if args.cu:
        banner()
        cu_encontrado = next((c for c in CASOS_DE_PRUEBA if c["id"].upper() == args.cu.upper()), None)
        if not cu_encontrado:
            print(f"{RED}Caso de uso '{args.cu}' no encontrado.{RESET}")
            sys.exit(1)
        print(f"{CYAN}{BOLD}=== {cu_encontrado['titulo']} ==={RESET}")
        ejecutar_lista_comandos(dispatcher, cu_encontrado["comandos"], cu_encontrado["id"], interactivo=args.interactive, delay=args.delay)
        sys.exit(0)

    if args.all:
        banner()
        print(f"{YELLOW}Ejecutando catálogo completo (Modo: {'Paso a Paso' if args.interactive else f'Automático con {args.delay}s delay'})...{RESET}")
        for cu in CASOS_DE_PRUEBA:
            print(f"\n{CYAN}{BOLD}=== {cu['titulo']} ==={RESET}")
            ejecutar_lista_comandos(dispatcher, cu["comandos"], cu["id"], interactivo=args.interactive, delay=args.delay)
        sys.exit(0)

    # Si no se pasó ningún flag, abrir el menú interactivo
    menu_interactivo(dispatcher)


if __name__ == "__main__":
    main()
