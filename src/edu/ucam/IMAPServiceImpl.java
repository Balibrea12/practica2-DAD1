package edu.ucam;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class IMAPServiceImpl extends UnicastRemoteObject implements IMAPService {

    private ServidorIMAP servidorIMAP;
    private ConfiguracionIMAP configuracionIMAP;

    public IMAPServiceImpl() throws RemoteException {
        servidorIMAP = new ServidorIMAP();
        configuracionIMAP = new ConfiguracionIMAP();
    }

    @Override
    public void listarCorreosBuzonPrincipal() throws RemoteException {
        servidorIMAP.listarCorreosBuzonPrincipal();
    }

    @Override
    public void leerContenidoMensaje(int numMensaje) throws RemoteException {
        servidorIMAP.leerContenidoMensaje(numMensaje);
    }

    @Override
    public void eliminarMensaje(int numMensaje) throws RemoteException {
        servidorIMAP.eliminarMensaje(numMensaje);
    }

    @Override
    public void configurarServidorIMAP(String usuarioIMAP, String contraseñaIMAP) throws RemoteException {
        configuracionIMAP.configurarServidorIMAP(usuarioIMAP, contraseñaIMAP);
        String[] configuracion = configuracionIMAP.getConfiguracion();
        if (configuracion != null) {
            servidorIMAP.configurarServidorIMAP(configuracion[0], configuracion[1], configuracion[2]);
        } else {
            throw new RemoteException("Error al configurar el servidor IMAP: Configuración no encontrada.");
        }
    }
}
