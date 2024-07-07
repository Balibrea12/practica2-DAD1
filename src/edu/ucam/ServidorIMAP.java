package edu.ucam;

import java.io.*;
import java.util.*;
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
            writer.write("A2 SELECT INBOX\r\n");
            writer.flush();
            String response;
            while (!(response = reader.readLine()).startsWith("A2 OK")) {
                System.out.println(response);
            }
            writer.write("A3 FETCH 1:* (BODY[HEADER.FIELDS (SUBJECT DATE FROM)])\r\n");
            writer.flush();
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
            writer.write("A3 SELECT INBOX\r\n");
            writer.flush();
            String response;
            while (!(response = reader.readLine()).startsWith("A3 OK")) {
                System.out.println(response);
            }
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
            writer.write("A3 SELECT INBOX\r\n");
            writer.flush();
            String response;
            while (!(response = reader.readLine()).startsWith("A3 OK")) {
                System.out.println(response);
            }
            writer.write("A5 STORE " + numMensaje + " +FLAGS \\Deleted\r\n");
            writer.flush();
            while (!(response = reader.readLine()).startsWith("A5 OK")) {
                System.out.println(response);
            }
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

    // implementación de descargarAdjuntosPDF
    public void descargarAdjuntos(int numMensaje, String rutaDestino) {
        try {
            writer.write("A3 SELECT INBOX\r\n");
            writer.flush();
            leerRespuestaCompleta("A3 OK");

            writer.write("A4 FETCH " + numMensaje + " BODY[HEADER]\r\n");
            writer.flush();

            String respuesta = leerRespuestaCompleta("A4 OK");

            String boundary = obtenerBoundary(respuesta);
            if (boundary == null) {
                System.out.println("No se pudo obtener el boundary del mensaje.");
                return;
            }

            writer.write("A5 FETCH " + numMensaje + " BODY[]\r\n");
            writer.flush();
            respuesta = leerRespuestaCompleta("A5 OK");

            guardarAdjuntos(respuesta, boundary, rutaDestino);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String obtenerBoundary(String response) {
        String boundaryPrefix = "boundary=\"";
        int boundaryIndex = response.indexOf(boundaryPrefix);
        if (boundaryIndex != -1) {
            int startIndex = boundaryIndex + boundaryPrefix.length();
            int endIndex = response.indexOf("\"", startIndex);
            return response.substring(startIndex, endIndex);
        } else {
            boundaryPrefix = "boundary=";
            boundaryIndex = response.indexOf(boundaryPrefix);
            if (boundaryIndex != -1) {
                int startIndex = boundaryIndex + boundaryPrefix.length();
                int endIndex = response.indexOf("\r\n", startIndex);
                return response.substring(startIndex, endIndex).replace("\"", "");
            }
        }
        return null;
    }

    private void guardarAdjuntos(String response, String boundary, String rutaDestino) throws IOException {
        String[] parts = response.split("--" + boundary);
        for (String part : parts) {
            if (part.contains("Content-Disposition: attachment")) {
                String[] lines = part.split("\r\n");
                String filename = null;
                StringBuilder fileContent = new StringBuilder();
                boolean contentStarted = false;

                for (String line : lines) {
                    if (line.startsWith("Content-Disposition: attachment;")) {
                        int filenameIndex = line.indexOf("filename=\"");
                        if (filenameIndex != -1) {
                            int startIndex = filenameIndex + "filename=\"".length();
                            int endIndex = line.indexOf("\"", startIndex);
                            filename = line.substring(startIndex, endIndex);
                        }
                    } else if (contentStarted) {
                        fileContent.append(line).append("\r\n");
                    } else if (line.isEmpty()) {
                        contentStarted = true;
                    }
                }

                if (filename != null) {
                    File file = new File(rutaDestino, filename);
                    try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                        bw.write(fileContent.toString());
                    }
                    System.out.println("Archivo adjunto guardado: " + filename);
                }
            }
        }
    }

    private String leerRespuestaCompleta(String terminator) throws IOException {
        StringBuilder responseBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            responseBuilder.append(line).append("\r\n");
            if (line.startsWith(terminator)) {
                break;
            }
        }
        return responseBuilder.toString();
    }
}
