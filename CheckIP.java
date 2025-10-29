package lab;
import java.io.*;
import java.time.*;

public class CheckIP {
    public static void main(String[] args) {
        String url = "https://api.ipify.org?format=json";
        ProcessBuilder pb = new ProcessBuilder("curl", "-s", url); // -s = modo silencioso

        try {
            Process process = pb.start();
            ProcessHandle handle = process.toHandle();

            // Información inicial
            System.out.println("🛰️  Consultando IP pública...");
            System.out.println("PID del proceso: " + handle.pid());

            Instant inicio = Instant.now();

            // Leer la salida del proceso (JSON)
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    System.out.println("🌐 Respuesta API: " + line);
                }
            }

            // Esperar a que termine y mostrar tiempo
            int code = process.waitFor();
            Duration duracion = Duration.between(inicio, Instant.now());
            System.out.println("Código de salida: " + code);
            System.out.println("Duración: " + duracion.toMillis() + " ms");

            // Monitorización final
            if (handle.isAlive()) {
                System.out.println("⚠️ El proceso sigue activo, se forzará su cierre.");
                handle.destroyForcibly();
            } else {
                System.out.println("✅ Proceso finalizado correctamente.");
            }

        } catch (IOException e) {
            System.err.println("Error al ejecutar curl: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("La espera fue interrumpida.");
        }
    }
}
