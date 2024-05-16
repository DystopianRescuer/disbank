package com.rateroscoloniatesocongo.disbank.util;

import javafx.application.Platform;

import javax.swing.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Avisador {

    // Para que los mensajes no dejen en espera el programa hacemos un pool de hilos
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

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
}
