import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RandomDogAutoPublisher {

    // 🧠 Configuración rápida
    private static final String REPO_PATH = "/home/ehko/Documents/git_proyectos/miapp"; // ruta local del repo
    private static final String OUTPUT_PATH = "docs/index.html"; // o "index.html"
    private static final String REMOTE = "origin";               // normalmente 'origin'
    private static final String BRANCH = "perro";                // la rama donde quieres publicar
    private static final int INTERVAL_SECONDS = 10;

    public static void main(String[] args) {
        System.out.printf("🚀 Publicador automático cada %ds → %s (%s/%s)%n",
                INTERVAL_SECONDS, OUTPUT_PATH, REMOTE, BRANCH);

        while (true) {
            try {
                // 1️⃣ Obtener imagen
                String json = runCapture("curl", "-s", "https://dog.ceo/api/breeds/image/random");
                String url = extraerURL(json);
                if (url == null) {
                    System.out.println("⚠️ No se pudo extraer la URL, reintentando...");
                    sleep(INTERVAL_SECONDS);
                    continue;
                }
                System.out.println("🐶 Nueva imagen: " + url);

                // 2️⃣ Generar HTML
                escribirHtml(REPO_PATH + "/" + OUTPUT_PATH, url);

                // 3️⃣ Git add / commit / push
                run(REPO_PATH, "git", "add", ".");
                int commit = run(REPO_PATH, "git", "commit", "-m", "feat(dog): auto-update " + timestamp());
                if (commit != 0) System.out.println("ℹ️ Sin cambios que commitear.");
                run(REPO_PATH, "git", "push", REMOTE, BRANCH);

                System.out.println("✅ Subido a rama '" + BRANCH + "' del repo 'miapp'.\n");

            } catch (Exception e) {
                System.err.println("❌ Error: " + e.getMessage());
            }
            sleep(INTERVAL_SECONDS);
        }
    }

    // --- Helpers ---
    private static int run(String directory, String... cmd) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.directory(new File(directory));       // ejecuta dentro del repo miapp
        pb.redirectErrorStream(true);            // mezcla stdout+stderr
        Process p = pb.start();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
            String line; while ((line = br.readLine()) != null) System.out.println(line);
        }
        return p.waitFor();
    }

    private static String runCapture(String... cmd) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);
        Process p = pb.start();
        StringBuilder out = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
            String line; while ((line = br.readLine()) != null) out.append(line);
        }
        p.waitFor();
        return out.toString();
    }

    private static void escribirHtml(String path, String imageUrl) throws IOException {
        String html = """
            <!doctype html><html lang="es"><head>
            <meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1">
            <title>Perrito 🐶</title>
            <style>
              body{font-family:sans-serif;display:grid;place-items:center;padding:2rem}
              img{max-width:min(90vw,720px);border-radius:12px;box-shadow:0 8px 24px rgba(0,0,0,.15)}
              .t{margin-top:1rem;opacity:.6}
            </style></head><body>
            <h1>Perrito aleatorio 🐶</h1>
            <img src="%s" alt="Perrito">
            <div class="t">Actualizado: %s</div>
            </body></html>
        """.formatted(imageUrl, timestamp());

        Path out = Paths.get(path);
        if (out.getParent() != null) Files.createDirectories(out.getParent());
        Files.writeString(out, html, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        System.out.println("📝 HTML actualizado en " + out);
    }

    private static String extraerURL(String json) {
        int s = json.indexOf("\"message\":\"");
        if (s < 0) return null;
        s += 11;
        int e = json.indexOf("\"", s);
        if (e < 0) return null;
        return json.substring(s, e).replace("\\/", "/");
    }

    private static String timestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private static void sleep(int s) {
        try { Thread.sleep(s * 1000L); } catch (InterruptedException ignored) {}
    }
}
