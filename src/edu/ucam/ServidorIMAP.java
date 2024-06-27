package edu.ucam;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class ServidorIMAP {

    private SSLSocket socket;
    private BufferedWriter writer;
    private BufferedReader reader;

    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1099);

            IMAPService imapService = new IMAPServiceImpl();

            Naming.rebind("rmi://localhost/IMAPService", imapService);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void configurarServidorIMAP(String usuarioIMAP, String puertoIMAP, String contraseñaIMAP) {
        try {
            String servidorIMAP = "imap.gmail.com";
            int puerto = Integer.parseInt(puertoIMAP);
            SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            socket = (SSLSocket) socketFactory.createSocket(servidorIMAP, puerto);

            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Leer la respuesta inicial del servidor
            String response = reader.readLine();
            System.out.println(response);

            // Enviar el comando de login con el usuario y la contraseña
            writer.write("A1 LOGIN " + usuarioIMAP + " " + contraseñaIMAP + "\r\n");
            writer.flush();

            boolean authenticated = false;

            while ((response = reader.readLine()) != null) {
                System.out.println(response);
                if (response.startsWith("A1 OK")) {
                    authenticated = true;
                    break;
                } else if (response.startsWith("A1 NO")) {
                    throw new Exception("Authentication failed: " + response);
                }
            }

            if (!authenticated) {
                throw new Exception("Failed to authenticate with the IMAP server.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void listarCorreosBuzonPrincipal() {
        try {
            // Enviar el comando para seleccionar el buzón INBOX
            writer.write("A2 SELECT INBOX\r\n");
            writer.flush();
            // Leer la respuesta del servidor
            String response;
            while (!(response = reader.readLine()).startsWith("A2 OK")) {
                System.out.println(response);
            }

            // Enviar el comando para obtener información sobre los correos en la bandeja de entrada
            writer.write("A3 FETCH 1:* (BODY[HEADER.FIELDS (SUBJECT DATE FROM)])\r\n");
            writer.flush();
            // Leer la respuesta del servidor
            while ((response = reader.readLine()) != null) {
                System.out.println(response);
                if (response.startsWith("A3 OK")) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void leerContenidoMensaje(int numMensaje) {
        try {
            // Seleccionar el buzón INBOX
            writer.write("A3 SELECT INBOX\r\n");
            writer.flush();
            String response;
            while (!(response = reader.readLine()).startsWith("A3 OK")) {
                System.out.println(response);
            }

            // Ahora fetch el contenido del mensaje
            writer.write("A4 FETCH " + numMensaje + " BODY[TEXT]\r\n");
            writer.flush();
            while (!(response = reader.readLine()).startsWith("A4 OK")) {
                System.out.println(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void eliminarMensaje(int numMensaje) {
        try {
            // Seleccionar el buzón INBOX
            writer.write("A3 SELECT INBOX\r\n");
            writer.flush();
            String response;
            while (!(response = reader.readLine()).startsWith("A3 OK")) {
                System.out.println(response);
            }

            // Enviar el comando para marcar el mensaje como eliminado
            writer.write("A5 STORE " + numMensaje + " +FLAGS \\Deleted\r\n");
            writer.flush();
            while (!(response = reader.readLine()).startsWith("A5 OK")) {
                System.out.println(response);
            }

            // Enviar el comando para expurgar el mensaje marcado
            writer.write("A6 EXPUNGE\r\n");
            writer.flush();
            while (!(response = reader.readLine()).startsWith("A6 OK")) {
                System.out.println(response);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
