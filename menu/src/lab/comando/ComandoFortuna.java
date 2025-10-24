package lab.comando;

import java.util.ArrayList;
import java.util.List;

import lab.console.RegistroComandos;

/**
 * Comando que ejecuta el programa 'fortune' del sistema
 * para mostrar una frase aleatoria.
 */
public class ComandoFortuna extends ComandoEjecutable {

    @Override
    public String ayuda() {
        return "Muestra una frase aleatoria (requiere tener instalado 'fortune').";
    }

    @Override
    public String nombre() {
        return "fortune";
    }

    @Override
    public int ejecutarProceso(List<String> argumentos, RegistroComandos registro) throws Exception {
        List<String> cmd = new ArrayList<>();
        cmd.add("fortune"); // Requiere que el programa 'fortune' esté disponible en el sistema
        return lanzarProceso(cmd); 
    }
}
