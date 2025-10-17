package com.example.swingapp.controlador;

import javax.swing.*;

import com.example.swingapp.vista.ControlPanel;
import com.example.swingapp.vista.OutputPanel;

import java.awt.event.ActionEvent;

public class AppController {
    private final ControlPanel controlPanel;
    private final OutputPanel outputPanel;

    public AppController(ControlPanel c, OutputPanel o) {
        this.controlPanel = c;
        this.outputPanel  = o;

        /**##############################
         * #    Accciones de Botones    #
         * ##############################
         */
        controlPanel.btnCurl.addActionListener(this::onCurl);
        controlPanel.btnRun.addActionListener(this::onRun);
        controlPanel.btnFecha.addActionListener(this::onDate);
        controlPanel.btnClear.addActionListener(this::onClear);
        controlPanel.btnExit.addActionListener(e -> System.exit(0));
    }
    private void onDate(ActionEvent e) {
    	outputPanel.append("Ejecutando comando...");
    	 try {
             ProcessBuilder pb = new ProcessBuilder("date");
             pb.redirectErrorStream(true);
             Process process = pb.start();

             new Thread(() -> {
                 try (var reader = new java.io.BufferedReader(
                         new java.io.InputStreamReader(process.getInputStream()))) {
                     String line;
                     while ((line = reader.readLine()) != null) {
                         outputPanel.append(line);
                     }
                 } catch (Exception ex) {
                     outputPanel.append("Error: " + ex.getMessage());
                 }
             }).start();

         } catch (Exception ex) {
             JOptionPane.showMessageDialog(null, "Error ejecutando proceso:\n" + ex.getMessage());
         }
    }

    private void onRun(ActionEvent e) {
        outputPanel.append("Ejecutando comando...");
        try {
            ProcessBuilder pb = new ProcessBuilder("ping", "-c", "3", "8.8.8.8");
            pb.redirectErrorStream(true);
            Process process = pb.start();

            new Thread(() -> {
                try (var reader = new java.io.BufferedReader(
                        new java.io.InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        outputPanel.append(line);
                    }
                } catch (Exception ex) {
                    outputPanel.append("Error: " + ex.getMessage());
                }
            }).start();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error ejecutando proceso:\n" + ex.getMessage());
        }
    }

    private void onClear(ActionEvent e) {
        outputPanel.clear();
    }
    
    private void onCurl(ActionEvent e) {
        String url = controlPanel.txtUrl.getText().trim();
        if (url.isEmpty()) {
          JOptionPane.showMessageDialog(null, "Introduce una URL.", "Aviso", JOptionPane.WARNING_MESSAGE);
          return;
        }
        outputPanel.append("$curl " + url);
        System.out.println(">>> "+ url);
        try {
        	ProcessBuilder pb = new ProcessBuilder("curl", "-L", "-sS", url);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            new Thread(() -> {
                try (var reader = new java.io.BufferedReader(
                        new java.io.InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        outputPanel.append(line);
                    }
                } catch (Exception ex) {
                    outputPanel.append("Error: " + ex.getMessage());
                }
            }).start();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error ejecutando proceso:\n" + ex.getMessage());
        }
    
    }
}
