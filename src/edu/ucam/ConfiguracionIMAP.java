package edu.ucam;

import java.util.Hashtable;

public class ConfiguracionIMAP {

    private Hashtable<String, String[]> configuracionesIMAP;

    public ConfiguracionIMAP() {
        configuracionesIMAP = new Hashtable<>();
    }

    public void configurarServidorIMAP(String usuarioIMAP, String contraseñaIMAP) {
        String servidorIMAP = "imap.gmail.com";
        int puertoIMAP = 993;
        configuracionesIMAP.put("configuracionPrincipal", new String[]{usuarioIMAP, String.valueOf(puertoIMAP), contraseñaIMAP});
    }

    public String[] getConfiguracion() {
        return configuracionesIMAP.get("configuracionPrincipal");
    }
}
