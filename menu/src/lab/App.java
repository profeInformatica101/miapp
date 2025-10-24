package lab;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import lab.console.RegistroComandos;
import lab.comando.ComandoChafa;
import lab.comando.ComandoFortuna;
import lab.comando.ComandoLs;
import lab.comando.ComandoSalir;
import lab.comando.ComandoAyuda;

/**
 * Punto de entrada de la aplicación.
 * Inicia una consola interactiva con los comandos registrados.
 */
public class App {

    public static void main(String[] args) {
        // Crear el registro e inicializar comandos
        RegistroComandos registro = new RegistroComandos()
                .registrar("ayuda", new ComandoAyuda())
                .registrar("salir", new ComandoSalir())
                .registrar("exit", new ComandoSalir())  // alias
                .registrar("fortune", new ComandoFortuna())
                .registrar("chafa", new ComandoChafa())
        .registrar("ls", new ComandoLs());

        System.out.println("💻 Consola interactiva de comandos (escribe 'ayuda' para ver opciones)\n");

        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            String linea;
            while (true) {
                System.out.print("menu> ");
                linea = br.readLine();
                if (linea == null) break; // EOF (Ctrl+D)

                int rc = registro.ejecutarLinea(linea);

                if (rc == RegistroComandos.RC_SALIR) {
                    System.out.println("👋 Saliendo...");
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error en la consola: " + e.getMessage());
        }

        System.out.println("Fin 🔚");
    }
}
