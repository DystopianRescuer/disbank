package com.rateroscoloniatesocongo.disbank.transacciones;

import com.rateroscoloniatesocongo.disbank.api.ControladorClip;
import com.rateroscoloniatesocongo.disbank.modelo.Asociado;
import com.rateroscoloniatesocongo.disbank.telegramservice.ControladorTelegram;

import java.util.LinkedList;
import java.util.List;

public class GestorTransacciones {

    private static GestorTransacciones instance;

    private final List<Transaccion> pendientes;
    private final ControladorTelegram controladorTelegram;
    private final ControladorClip controladorClip;
    private boolean iniciado;

    private GestorTransacciones() {
        pendientes = new LinkedList<>();
        controladorTelegram = ControladorTelegram.getInstance();
        controladorClip = ControladorClip.getInstance();
    }

    public void iniciar() {
        if(!iniciado) {
            // TBD
        }
    }

    public boolean nuevaTransaccion(Asociado asociado, Cobro cobro) {
        // Crea el objeto transacción
        // Se la da a Clip y espera la respuesta positiva de este
        // Si esto ocurrió correctamente entonces registra la transacción en pendientes
        return false;
    }


    public static GestorTransacciones getInstance() {
        if(instance == null) {
            instance = new GestorTransacciones();
        }
        return instance;
    }
}
