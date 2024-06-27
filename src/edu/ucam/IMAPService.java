package edu.ucam;

import java.rmi.RemoteException;

public interface IMAPService extends java.rmi.Remote {

	public void listarCorreosBuzonPrincipal() throws RemoteException;
    public void leerContenidoMensaje(int numMensaje) throws RemoteException;
    public void eliminarMensaje(int numMensaje) throws RemoteException;
    public void configurarServidorIMAP(String usuarioIMAP, String contraseñaIMAP) throws RemoteException;
}
