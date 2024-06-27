package edu.ucam;

import java.rmi.Naming;
import java.util.Scanner;

//Correo de pruebas 1: correopruebasdad@gmail.com
//Contraseña: ukevzowqnrrmstsm

public class ClienteIMAP {

    public static void main(String[] args) {
        try {
            IMAPService imapService = (IMAPService) Naming.lookup("rmi://localhost/IMAPService");

            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("--- MENÚ IMAP ---");
                System.out.println("1. Configurar servidor IMAP");
                System.out.println("2. Listar correos del buzón principal");
                System.out.println("3. Leer contenido de un mensaje");
                System.out.println("4. Eliminar mensajes");
                System.out.println("5. Salir");

                System.out.print("Seleccione una opción: ");
                int opcion = Integer.parseInt(scanner.nextLine());

                switch (opcion) {
                    case 1:
                        System.out.print("Ingrese el correo IMAP: ");
                        String usuarioIMAP = scanner.nextLine();
                        System.out.print("Ingrese la contraseña IMAP: ");
                        String contraseñaIMAP = scanner.nextLine();

                        imapService.configurarServidorIMAP(usuarioIMAP, contraseñaIMAP);
                        System.out.println("Servidor IMAP configurado exitosamente.");
                        break;
                    case 2:
                        imapService.listarCorreosBuzonPrincipal();
                        break;
                    case 3:
                        System.out.print("Ingrese el número del mensaje: ");
                        int numMensaje = Integer.parseInt(scanner.nextLine());
                        imapService.leerContenidoMensaje(numMensaje);
                        break;
                    case 4:
                        System.out.print("Ingrese el número del mensaje a eliminar: ");
                        int numMensajeEliminar = Integer.parseInt(scanner.nextLine());
                        imapService.eliminarMensaje(numMensajeEliminar);
                        break;
                    case 5:
                        System.out.println("Saliendo...");
                        return;
                    default:
                        System.out.println("Opción no válida. Intente de nuevo.");
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
