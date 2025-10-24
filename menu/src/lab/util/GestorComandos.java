package lab.util;

import java.io.IOException;
import java.util.List;

public class GestorComandos {
	
	// 📘 Patrón Utility Class (Clase de Utilidad) :::  Sirve para impedir que alguien cree instancias de la clase
    private GestorComandos() {}

    /**
     * Ejecuta un proceso externo heredando la salida y error estándar.
     *
     * @param comando lista con el binario y sus argumentos
     * @return código de retorno del proceso (0 = OK)
     */
    public static int ejecutar(List<String> comando) {
        if (comando == null || comando.isEmpty()) return 1;

        ProcessBuilder pb = new ProcessBuilder(comando);
        pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        pb.redirectError(ProcessBuilder.Redirect.INHERIT);

        try {
            Process proceso = pb.start();
            int rc = proceso.waitFor();
            if (rc != 0) {
                System.err.println("⚠️ '" + comando.get(0) + "' terminó con código: " + rc);
            } else {
                System.out.println("✅ '" + comando.get(0) + "' finalizó correctamente (RC=0).");
            }
            return rc;
        } catch (IOException e) {
            System.err.println("❌ Error ejecutando '" + comando.get(0) + "': " + e.getMessage());
            return 1;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("⏹️ Proceso interrumpido.");
            return 1;
        }
    }
}

