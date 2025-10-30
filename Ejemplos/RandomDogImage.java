package lab;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;


public class RandomDogImage {
    public static void main(String[] args) {
        String url = "https://dog.ceo/api/breeds/image/random";

        ProcessBuilder pb = new ProcessBuilder("curl", "-s", url);
        pb.redirectErrorStream(true);

        try {
            Process proceso = pb.start();
            ProcessHandle handle = proceso.toHandle();

            System.out.println("🐶 Consultando imagen aleatoria (PID: " + handle.pid() + ")...");
            Instant inicio = Instant.now();

            StringBuilder salida = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(proceso.getInputStream()))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    salida.append(linea);
                }
            }

            proceso.waitFor();
            Duration duracion = Duration.between(inicio, Instant.now());
            System.out.println("⏱️ Duración: " + duracion.toMillis() + " ms");

            // 🔍 Extraer la URL del JSON
            String json = salida.toString();
            String imageUrl = extraerURL(json);
            System.out.println("🌐 Imagen: " + imageUrl);

            // 🖼️ Abrir la imagen en el navegador predeterminado
            if (imageUrl != null) {
                abrirEnNavegador(imageUrl);
            }

            if (handle.isAlive()) {
                handle.destroyForcibly();
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Extrae la URL entre comillas del campo "message"
    private static String extraerURL(String json) {
        int start = json.indexOf("\"message\":\"");
        if (start < 0) return null;
        start += 11;
        int end = json.indexOf("\"", start);
        if (end < 0) return null;
        String raw = json.substring(start, end);
        return raw.replace("\\/", "/"); // limpiar los escapes
    }

    // Abre la URL en el navegador según el sistema operativo
    private static void abrirEnNavegador(String imageUrl) {
        String os = System.getProperty("os.name").toLowerCase();
        ProcessBuilder abrir;
        try {
            if (os.contains("win")) {
                abrir = new ProcessBuilder("cmd", "/c", "start", imageUrl);
            } else if (os.contains("mac")) {
                abrir = new ProcessBuilder("open", imageUrl);
            } else {
                abrir = new ProcessBuilder("xdg-open", imageUrl);
            }
            abrir.start();
            System.out.println("🖥️  Abriendo imagen en el navegador...");
        } catch (IOException e) {
            System.err.println("No se pudo abrir la imagen: " + e.getMessage());
        }
    }
}

