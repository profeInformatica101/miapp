package lab.comando;

import java.util.List;

import lab.console.RegistroComandos;

public interface IComando {
	/** @return código de retorno. Usa RC_EXIT del registro para salir del bucle. */
	public int ejecutarProceso(List<String> argumentos, RegistroComandos registro) throws Exception;


	default String ayuda() { return "(sin ayuda)"; }
	
	  /**
     * Nombre del comando (por defecto, el nombre simple de la clase).
     */
    default String nombre() { return getClass().getSimpleName(); }
}
