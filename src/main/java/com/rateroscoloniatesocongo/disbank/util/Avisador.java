package com.rateroscoloniatesocongo.disbank.util;

import javax.swing.*;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Avisador {

    // Para que los mensajes no dejen en espera el programa hacemos un pool de hilos
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static String IPTerminal;

    public static void mandarAviso(String mensaje) {
        executorService.submit(() -> JOptionPane.showMessageDialog(null, mensaje, "Aviso", JOptionPane.INFORMATION_MESSAGE));
    }

    public static void mandarError(String mensaje) {
        executorService.submit(() -> JOptionPane.showMessageDialog(null, mensaje, "Error", JOptionPane.ERROR_MESSAGE));
    }

    public static void mandarErrorFatal(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
        System.exit(-1);
    }

    public static void setIP(String ip) {
        Avisador.IPTerminal = ip;
    }

    public static void mandarMensajeRemoto(String mensaje) {
        if(IPTerminal != null) {
            try {
                new RemoteMessagePassing<String>(new Socket(IPTerminal, 8080)).send(mensaje);
            } catch (Exception ignored) {
            }
        }
    }
}
