package lab.console;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import lab.comando.IComando;
import lab.util.AnalisisSintactico;

/**
 * Registro central de comandos disponibles en la consola.
 * Permite registrar, listar y ejecutar comandos por su nombre.
 */
public class RegistroComandos {
    public static final int RC_OK    = 0;
    public static final int RC_ERR   = 1;
    public static final int RC_SALIR = 100; // señal para salir del bucle principal

    private final Map<String, IComando> comandos = new LinkedHashMap<>();

    /** Registra un comando por nombre. */
    public RegistroComandos registrar(String nombre, IComando comando) {
        comandos.put(Objects.requireNonNull(nombre), Objects.requireNonNull(comando));
        return this;
    }

    /** Devuelve los nombres de comandos registrados. */
    public Set<String> nombres() {
        return comandos.keySet();
    }

    /**
     * Ejecuta un comando específico por nombre y lista de argumentos.
     */
    public int ejecutar(String nombre, List<String> args) {
        IComando cmd = comandos.get(nombre);
        if (cmd == null) {
            System.out.println("Comando no reconocido: " + nombre + ". Prueba 'ayuda'.");
            return RC_ERR;
        }
        try {
            return cmd.ejecutarProceso(args, this);
        } catch (Exception ex) {
            System.err.println("❌ Error ejecutando '" + nombre + "': " + ex.getMessage());
            return RC_ERR;
        }
    }

    /**
     * Ejecuta una línea completa introducida por el usuario.
     * Ejemplo:  "chafa --size 80x40 imagen.png"
     */
    public int ejecutarLinea(String linea) {
        if (linea == null || linea.isBlank()) return RC_OK;

        List<String> tokens = AnalisisSintactico.tokenizar(linea);
        if (tokens.isEmpty()) return RC_OK;

        String nombre = tokens.get(0);
        List<String> args = tokens.subList(1, tokens.size());

        return ejecutar(nombre, args);
    }
}
