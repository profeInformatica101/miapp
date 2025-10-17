package com.example.swingapp;

import javax.swing.SwingUtilities;

import com.example.swingapp.vista.MainWindow;

public class Main {
	
	/** 
	 * SwingUtilities.invokeLater(...) garantiza que la creación
	 * y manipulación de componentes gráficos (Swing) se ejecute
	 * en el hilo de envío de eventos (EDT: Event Dispatch Thread),
	 * evitando errores de concurrencia y bloqueos en la interfaz.*/
	
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainWindow::new);
    }
}
