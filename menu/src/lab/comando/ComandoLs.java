package lab.comando;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lab.console.RegistroComandos;

public class ComandoLs extends ComandoEjecutable {

    @Override
    public int ejecutarProceso(List<String> argumentos, RegistroComandos registro) throws Exception {
        // Si se pasa una ruta, usarla; si no, usar el directorio actual
        String ruta = argumentos.isEmpty() ? "." : argumentos.get(0);

        File dir = new File(ruta);
        if (!dir.exists()) {
            System.err.println("❌ No existe la ruta: " + ruta);
            return RegistroComandos.RC_ERR;
        }
        if (!dir.isDirectory()) {
            System.err.println("⚠️ No es un directorio: " + ruta);
            return RegistroComandos.RC_ERR;
        }

        // Usamos 'ls' si está disponible (Linux/macOS), o mostramos manualmente
        List<String> comando = new ArrayList<>();

        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            comando.addAll(Arrays.asList("cmd", "/c", "dir"));
            if (!".".equals(ruta)) comando.add(ruta);
        } else {
            comando.add("ls");
            comando.add("-lh");
            if (!".".equals(ruta)) comando.add(ruta);
        }

        // Lanza el proceso del sistema
        return lanzarProceso(comando);
    }

    @Override
    public String ayuda() {
        return """
               Lista los archivos del directorio actual o del indicado.
               Uso:
                 ls [ruta]
               """;
    }


}

