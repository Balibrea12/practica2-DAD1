package edu.ucam;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

@SuppressWarnings("serial")
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

    @Override
    public void crearCarpeta(String nombreCarpeta) throws RemoteException {
        servidorIMAP.crearCarpeta(nombreCarpeta);
    }

    @Override
    public void eliminarCarpeta(String nombreCarpeta) throws RemoteException {
        servidorIMAP.eliminarCarpeta(nombreCarpeta);
    }

    @Override
    public void listarCorreosCarpeta(String nombreCarpeta) throws RemoteException {
        servidorIMAP.listarCorreosCarpeta(nombreCarpeta);
    }

    @Override
    public void moverMensaje(String origenCarpeta, String destinoCarpeta, int numMensaje) throws RemoteException {
        servidorIMAP.moverMensaje(origenCarpeta, destinoCarpeta, numMensaje);
    }

    @Override
    public void descargarAdjuntosPDF(int numMensaje, String rutaDestino) throws RemoteException {
        servidorIMAP.descargarAdjuntos(numMensaje, rutaDestino);
    }
}
