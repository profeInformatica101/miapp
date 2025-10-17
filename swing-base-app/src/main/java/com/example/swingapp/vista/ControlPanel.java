package com.example.swingapp.vista;

import javax.swing.*;
import java.awt.*;


/**  
 * {utf8-icons} 🌹 https://www.utf8icons.com/
 */
public class ControlPanel extends JPanel {
    private static final long serialVersionUID = 1L;
	
    /**
	 * Botonoes
	 */
    public JButton btnRun, btnClear, btnExit, btnFecha, btnCurl;
    public JTextField txtUrl;
    public ControlPanel() {

  /**
//      En este caso:  GridLayout(3, 1, 10, 10)
//        - 3 filas  → el panel se divide en tres secciones verticales (una por botón).
//        - 1 columna → todos los botones estarán alineados en una única columna.
//        - 10, 10    → separaciones (en píxeles) entre los componentes, tanto horizontal como vertical.   
   
   En este caso: (BorderFactory.createEmptyBorder(20, 10, 20, 10));
//      - Deja 20 píxeles arriba y abajo.
//      - Deja 10 píxeles a izquierda y derecha.
 *
 */  
//                    GridLayout(<FILAS>, <COLUMNAS>, <ESPACIO_HORIZONTAL>, <ESPACIO_VERTICAL>)
        setLayout(new GridLayout(6, 1, 10, 10)); //Resultado visual: una columna con tres botones separados entre sí.
//Esto evita que los botones queden pegados al borde del panel y mejora la estética.
//                BorderFactory.createEmptyBorder(<ARRIBA>, <IZQUIERDA>, <ABAJO>, <DERECHA>)
        setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        
        txtUrl = new JTextField("https://example.com");
        btnCurl = new JButton("🌐 Consultar");
        btnRun   = new JButton("▶  Ejecutar");    
        btnFecha = new JButton("⏲ Día y hora");   
        btnClear = new JButton("▷ Limpiar");      
        btnExit  = new JButton("⏻ Salir");    
        
        add(txtUrl);
        add(btnCurl);
        add(btnRun);
        add(btnFecha);
        add(btnClear);
        add(btnExit);
        
        System.out.println("codificación (UTF-8) ♜♞♝♛♚♟");
        System.out.println("representación (fuente con glifos). ▶ ⏰ 🧹 🚪");
       

    }
}