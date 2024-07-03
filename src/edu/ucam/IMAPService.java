package edu.ucam;

import java.rmi.RemoteException;

public interface IMAPService extends java.rmi.Remote {
    void listarCorreosBuzonPrincipal() throws RemoteException;
    void leerContenidoMensaje(int numMensaje) throws RemoteException;
    void eliminarMensaje(int numMensaje) throws RemoteException;
    void configurarServidorIMAP(String usuarioIMAP, String contraseñaIMAP) throws RemoteException;
    void crearCarpeta(String nombreCarpeta) throws RemoteException;
    void eliminarCarpeta(String nombreCarpeta) throws RemoteException;
    void listarCorreosCarpeta(String nombreCarpeta) throws RemoteException;
    void moverMensaje(String origenCarpeta, String destinoCarpeta, int numMensaje) throws RemoteException;
    void descargarAdjuntosPDF(int numMensaje, String rutaDestino) throws RemoteException;
}
