package lab.comando;

import java.util.List;

import lab.console.RegistroComandos;

public class ComandoAyuda implements IComando {

    @Override
    public String ayuda() { return "Muestra esta ayuda."; }

    @Override
    public String nombre() { return "ayuda"; }

	@Override
	public int ejecutarProceso(List<String> argumentos, RegistroComandos registro) throws Exception {
		 System.out.println("Comandos disponibles:");
	        registro.nombres().forEach(n -> System.out.println("  • " + n));

	        System.out.println("\nEjemplos:");
	        System.out.println("  ayuda");
	        System.out.println("  chafa --size=80x40 \"./ruta con espacios.png\"");
	        System.out.println("  fortune");
	        System.out.println("  salir");
	        return RegistroComandos.RC_OK;
	}
}
