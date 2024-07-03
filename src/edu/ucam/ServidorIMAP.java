package edu.ucam;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.util.*;

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
    
    public void crearCarpeta(String nombreCarpeta) {
        try {
            writer.write("A7 CREATE " + nombreCarpeta + "\r\n");
            writer.flush();
            String response;
            while (!(response = reader.readLine()).startsWith("A7 OK")) {
                System.out.println(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void eliminarCarpeta(String nombreCarpeta) {
        try {
            writer.write("A8 DELETE " + nombreCarpeta + "\r\n");
            writer.flush();
            String response;
            while (!(response = reader.readLine()).startsWith("A8 OK")) {
                System.out.println(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void listarCorreosCarpeta(String nombreCarpeta) {
        try {
            writer.write("A9 SELECT " + nombreCarpeta + "\r\n");
            writer.flush();
            String response;
            while (!(response = reader.readLine()).startsWith("A9 OK")) {
                System.out.println(response);
            }
            writer.write("A10 FETCH 1:* (BODY[HEADER.FIELDS (SUBJECT DATE FROM)])\r\n");
            writer.flush();
            while (!(response = reader.readLine()).startsWith("A10 OK")) {
                System.out.println(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void moverMensaje(String origenCarpeta, String destinoCarpeta, int numMensaje) {
        try {
            writer.write("A11 SELECT " + origenCarpeta + "\r\n");
            writer.flush();
            String response;
            while (!(response = reader.readLine()).startsWith("A11 OK")) {
                System.out.println(response);
            }

            writer.write("A12 COPY " + numMensaje + " " + destinoCarpeta + "\r\n");
            writer.flush();
            while (!(response = reader.readLine()).startsWith("A12 OK")) {
                System.out.println(response);
            }

            writer.write("A13 STORE " + numMensaje + " +FLAGS \\Deleted\r\n");
            writer.flush();
            while (!(response = reader.readLine()).startsWith("A13 OK")) {
                System.out.println(response);
            }

            writer.write("A14 EXPUNGE\r\n");
            writer.flush();
            while (!(response = reader.readLine()).startsWith("A14 OK")) {
                System.out.println(response);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void descargarAdjuntosPDF(int numMensaje, String rutaDestino) {
        try {
            writer.write("A2 SELECT INBOX\r\n");
            writer.flush();
            String response;
            while (!(response = reader.readLine()).startsWith("A2 OK")) {
                System.out.println(response);
            }

            writer.write("A3 FETCH " + numMensaje + " BODY[]\r\n");
            writer.flush();
            List<String> responseLines = new ArrayList<>();
            while (!(response = reader.readLine()).startsWith("A3 OK")) {
                responseLines.add(response);
            }

            boolean isAttachment = false;
            StringBuilder base64Content = new StringBuilder();
            for (String line : responseLines) {
                if (line.contains("Content-Type: application/pdf")) {
                    isAttachment = true;
                }
                if (isAttachment) {
                    if (line.contains("Content-Transfer-Encoding: base64")) {
                        continue;
                    }
                    if (line.startsWith("Content-Disposition: attachment")) {
                        continue;
                    }
                    if (line.contains("--")) {
                        break;
                    }
                    base64Content.append(line.trim());
                }
            }

            byte[] decodedBytes = Base64.getDecoder().decode(base64Content.toString());

            try (OutputStream out = new FileOutputStream(rutaDestino + "/adjunto_" + numMensaje + ".pdf")) {
                out.write(decodedBytes);
            }
            System.out.println("Archivo adjunto guardado en: " + rutaDestino + "/adjunto_" + numMensaje + ".pdf");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
