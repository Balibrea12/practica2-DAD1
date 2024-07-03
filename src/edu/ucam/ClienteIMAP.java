package edu.ucam;

import java.rmi.Naming;
import java.util.Scanner;

//Correo de pruebas 1: correopruebasdad@gmail.com
//Contraseña: ukevzowqnrrmstsm 
///Users/salvamc/Downloads/cosas

public class ClienteIMAP {

    @SuppressWarnings("resource")
	public static void main(String[] args) {
        try {
            IMAPService imapService = (IMAPService) Naming.lookup("rmi://localhost/IMAPService");

            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("--- MENÚ IMAP ---");
                System.out.println("1. Configurar servidor IMAP");
                System.out.println("2. Listar correos del buzón principal");
                System.out.println("3. Leer contenido de un mensaje");
                System.out.println("4. Eliminar un mensaje");
                System.out.println("5. Crear carpeta");
                System.out.println("6. Eliminar carpeta");
                System.out.println("7. Listar correos de una carpeta");
                System.out.println("8. Mover mensaje a otra carpeta");
                System.out.println("9. Descargar adjuntos PDF de un mensaje");
                System.out.println("0. Salir");
                System.out.print("Seleccione una opción: ");
                int opcion = scanner.nextInt();
                scanner.nextLine();

                switch (opcion) {
                    case 1:
                        System.out.print("Ingrese usuario IMAP: ");
                        String usuario = scanner.nextLine();
                        System.out.print("Ingrese contraseña IMAP: ");
                        String contraseña = scanner.nextLine();
                        imapService.configurarServidorIMAP(usuario, contraseña);
                        break;
                    case 2:
                        imapService.listarCorreosBuzonPrincipal();
                        break;
                    case 3:
                        System.out.print("Ingrese número de mensaje: ");
                        int numMensajeLeer = scanner.nextInt();
                        imapService.leerContenidoMensaje(numMensajeLeer);
                        break;
                    case 4:
                        System.out.print("Ingrese número de mensaje: ");
                        int numMensajeEliminar = scanner.nextInt();
                        imapService.eliminarMensaje(numMensajeEliminar);
                        break;
                    case 5:
                        System.out.print("Ingrese nombre de la carpeta: ");
                        String nombreCarpetaCrear = scanner.nextLine();
                        imapService.crearCarpeta(nombreCarpetaCrear);
                        break;
                    case 6:
                        System.out.print("Ingrese nombre de la carpeta: ");
                        String nombreCarpetaEliminar = scanner.nextLine();
                        imapService.eliminarCarpeta(nombreCarpetaEliminar);
                        break;
                    case 7:
                        System.out.print("Ingrese nombre de la carpeta: ");
                        String nombreCarpetaListar = scanner.nextLine();
                        imapService.listarCorreosCarpeta(nombreCarpetaListar);
                        break;
                    case 8:
                        System.out.print("Ingrese carpeta de origen: ");
                        String origenCarpeta = scanner.nextLine();
                        System.out.print("Ingrese carpeta de destino: ");
                        String destinoCarpeta = scanner.nextLine();
                        System.out.print("Ingrese número de mensaje: ");
                        int numMensajeMover = scanner.nextInt();
                        imapService.moverMensaje(origenCarpeta, destinoCarpeta, numMensajeMover);
                        break;
                    case 9:
                        System.out.print("Ingrese número de mensaje: ");
                        int numMensajeAdjunto = scanner.nextInt();
                        scanner.nextLine();
                        System.out.print("Ingrese ruta de destino para el archivo: ");
                        String rutaDestino = scanner.nextLine();
                        imapService.descargarAdjuntosPDF(numMensajeAdjunto, rutaDestino);
                        break;
                    case 0:
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Opción no válida.");
                        break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
