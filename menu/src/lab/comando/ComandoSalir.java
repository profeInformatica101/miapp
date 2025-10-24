package lab.comando;

import java.util.List;

import lab.console.RegistroComandos;

public class ComandoSalir implements IComando {


    @Override
    public String ayuda() { return "Termina la aplicación."; }

    @Override
    public String nombre() { return "salir"; }

	@Override
	public int ejecutarProceso(List<String> argumentos, RegistroComandos registro) throws Exception {
		 return RegistroComandos.RC_SALIR;
	}
}