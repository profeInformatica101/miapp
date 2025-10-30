package vista;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class VentanaContador extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private final JLabel contador = new JLabel("0");
	private final JButton buttonContador = new JButton("Incrementa");

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VentanaContador frame = new VentanaContador();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public VentanaContador() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contador.setFont(new Font("Dialog", Font.BOLD, 21));
		contentPane.add(contador);
		buttonContador.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String contador_str = contador.getText();
				Integer contador_int = Integer.valueOf(contador_str);
				contador_int++;
				System.out.println(contador_int);
				contador.setText(""+contador_int);
			}
		});
		buttonContador.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPane.add(buttonContador);
		buttonContador.setForeground(new Color(255, 120, 0));
		buttonContador.setVerticalAlignment(SwingConstants.TOP);

	}

}
