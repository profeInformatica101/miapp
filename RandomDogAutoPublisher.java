import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RandomDogAutoPublisher {

    // Config rápida
    private static final String PROJECT_NAME = "miapp";     // solo para auto-detección si no hay .git en el dir actual
    private static final String OUTPUT_PATH  = "index.html"; // Pages desde raíz
    private static final String REMOTE       = "origin";
    private static final String BRANCH       = "perro";
    private static final int    INTERVAL_SECONDS = 10;

    public static void main(String[] args) {
        Path repoPath = detectarRepo();
        System.out.printf(
            "📁 Repo: %s%n🚀 Cada %ds → %s (%s/%s)%n%n",
            repoPath.toAbsolutePath(), INTERVAL_SECONDS, OUTPUT_PATH, REMOTE, BRANCH
        );

        while (true) {
            try {
                // 1) Imagen aleatoria
                String json = runCapture(null, "curl", "-s", "https://dog.ceo/api/breeds/image/random");
                String url  = extraerURL(json);
                if (url == null) { System.out.println("⚠️ No se pudo extraer URL"); sleep(INTERVAL_SECONDS); continue; }
                System.out.println("🐶 " + url);

                // 2) Escribir index.html en la RAÍZ del repo
                Path out = repoPath.resolve(OUTPUT_PATH);
                escribirHtml(out, url);
                System.out.println("📝 HTML → " + out.toAbsolutePath());

                // 3) Asegurar rama correcto (crea si no existe)
                ensureBranch(repoPath, BRANCH);

                // 4) Git add / commit / push
                run(repoPath, "git", "add", ".");
                int commit = run(repoPath, "git", "commit", "-m", "feat(dog): auto-update " + timestamp());
                if (commit != 0) System.out.println("ℹ️ Sin cambios que commitear.");
                run(repoPath, "git", "push", REMOTE, BRANCH);

                System.out.println("✅ Push a " + REMOTE + "/" + BRANCH + "\n");
            } catch (Exception e) {
                System.err.println("❌ " + e.getMessage());
            }
            sleep(INTERVAL_SECONDS);
        }
    }

    /* -------------------- Utilidades -------------------- */

    // Detecta repo: si el dir actual tiene .git, úsalo; si no, prueba ./miapp; si no, el actual.
    private static Path detectarRepo() {
        Path cwd = Paths.get("").toAbsolutePath();
        if (Files.isDirectory(cwd.resolve(".git"))) return cwd;
        Path candidate = cwd.resolve(PROJECT_NAME);
        if (Files.isDirectory(candidate.resolve(".git"))) return candidate;
        return cwd; // fallback
    }

    // Cambia a la rama; si no existe, la crea. Siempre deja HEAD en BRANCH.
    private static void ensureBranch(Path repo, String branch) throws IOException, InterruptedException {
        // ¿Ya estamos en esa rama?
        String head = runCapture(repo, "git", "rev-parse", "--abbrev-ref", "HEAD").trim();
        if (branch.equals(head)) return;

        // ¿Existe localmente?
        int existsLocal = run(repo, "git", "show-ref", "--verify", "refs/heads/" + branch);
        if (existsLocal == 0) {
            run(repo, "git", "switch", branch);
            return;
        }
        // ¿Existe remoto?
        int existsRemote = run(repo, "git", "ls-remote", "--heads", "origin", branch);
        if (existsRemote == 0) {
            run(repo, "git", "switch", "-c", branch);
            run(repo, "git", "branch", "--set-upstream-to", "origin/" + branch, branch);
        } else {
            // crea rama y establece upstream al hacer push -u
            run(repo, "git", "switch", "-c", branch);
            run(repo, "git", "push", "-u", "origin", branch);
        }
    }

    private static int run(Path dir, String... cmd) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(cmd);
        if (dir != null) pb.directory(dir.toFile());
        pb.redirectErrorStream(true);
        Process p = pb.start();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
            String line; while ((line = br.readLine()) != null) System.out.println(line);
        }
        return p.waitFor();
    }

    private static String runCapture(Path dir, String... cmd) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(cmd);
        if (dir != null) pb.directory(dir.toFile());
        pb.redirectErrorStream(true);
        Process p = pb.start();
        StringBuilder out = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
            String line; while ((line = br.readLine()) != null) out.append(line);
        }
        p.waitFor();
        return out.toString();
    }

    private static void escribirHtml(Path path, String imageUrl) throws IOException {
        String html = """
            <!doctype html><html lang="es"><head>
              <meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1">
              <title>Perrito Aleatorio 🐶</title>
              <style>
                body{font-family:sans-serif;display:grid;place-items:center;padding:2rem;background:#fafafa}
                img{max-width:min(90vw,720px);border-radius:12px;box-shadow:0 8px 24px rgba(0,0,0,.15)}
                .t{margin-top:1rem;opacity:.6}
              </style>
            </head><body>
              <h1>Perrito aleatorio 🐶</h1>
              <img src="%s" alt="Perrito">
              <div class="t">Actualizado: %s</div>
              <a href="https://dog.ceo/dog-api/" target="_blank">Fuente: Dog CEO API</a>
            </body></html>
        """.formatted(imageUrl, timestamp());

        if (path.getParent() != null) Files.createDirectories(path.getParent());
        Files.writeString(path, html, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
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

    private static void sleep(int sec) {
        try { Thread.sleep(sec * 1000L); } catch (InterruptedException ignored) {}
    }
}

