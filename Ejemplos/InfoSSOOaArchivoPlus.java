package lab;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * InfoSSOOaArchivoPlus
 * --------------------
 * Genera un informe del sistema (en texto o Markdown) con:
 *  1) Propiedades clave de la JVM.
 *  2) Salida de un comando del S.O. (con timeout y medición).
 *  3) Datos del proceso actual usando ProcessHandle.
 *
 * Cambios principales:
 *  - ✅ Uso de iconos UTF-8 centralizados como constantes (clase Icons).
 *  - 📝 Comentarios didácticos explicando decisiones y técnicas (timeout, lectura no bloqueante, etc.).
 *  - 🌍 Escritura del archivo en UTF-8 para que los iconos se conserven correctamente.
 *
 * Flags:
 *  --md           → genera Markdown (ssoo_info.md). Por defecto, texto plano (ssoo_info.txt).
 *  --open         → intenta abrir el archivo resultante con la app por defecto del S.O.
 *  --timeout=SEG  → timeout para el comando del sistema (por defecto, 10s).
 */
public class InfoSSOOaArchivoPlus {

    /* =========================== Iconos (UTF-8) =========================== */
    // Centralizar los iconos en una clase evita "magia" en cadenas sueltas,
    // facilita su reutilización y permite cambiarlos desde un único sitio.
    private static final class Icons {
        static final String OK       = "✅";
        static final String ERROR    = "❌";
        static final String WARN     = "⚠️";
        static final String INFO     = "ℹ️";
        static final String FILE     = "📄";
        static final String CLOCK    = "⏱️";
        static final String GEAR     = "⚙️";
        static final String CHIP     = "🧰";
        static final String TERMINAL = "🖥️";
        static final String ROCKET   = "🚀";
        static final String USER     = "👤";
        static final String PID      = "🔢";
        static final String CPU      = "🧠";
        static final String LINK     = "🔗";
    }

    public static void main(String[] args) {
        // Flags sencillos (pattern tipo --flag o --k=v)
        boolean asMarkdown = hasFlag(args, "--md");
        boolean autoOpen   = hasFlag(args, "--open");
        long timeoutSec    = parseTimeout(args, 10); // por defecto 10s
        String outName     = asMarkdown ? "ssoo_info.md" : "ssoo_info.txt";

        try {
            StringBuilder sb = new StringBuilder();

            // 1) Cabecera + tabla corta de props JVM
            if (asMarkdown) {
                sb.append("# ").append(Icons.GEAR).append(" Informe de Sistema (Java)\n\n");
                sb.append("_").append(Icons.CLOCK).append(" Generado: ").append(Instant.now()).append("_\n\n");
                sb.append("## ").append(Icons.CHIP).append(" Propiedades clave de la JVM\n\n");
                sb.append(mdPropsTable());
            } else {
                sb.append("=== ").append(Icons.GEAR).append(" Informe de Sistema (Java) ===\n");
                sb.append(Icons.CLOCK).append(" Generado: ").append(Instant.now()).append("\n\n");
                sb.append(jvmPropsPlain());
            }

            // 2) Comando del S.O. (medido y con timeout)
            //    - Se usa ProcessBuilder + lectura no bloqueante para evitar deadlocks por buffers.
            //    - Timeout cooperativo: si se supera, se destruye el proceso con destroy()/destroyForcibly().
            CommandResult res = ejecutarComandoSO(timeoutSec);
            if (asMarkdown) {
                sb.append("\n## ").append(Icons.TERMINAL).append(" Comando del S.O.\n\n");
                sb.append("**").append(Icons.PID).append(" PID:** ").append(res.pid)
                  .append(" · **exit:** ").append(res.exitCode)
                  .append(" · **").append(Icons.CLOCK).append(" duración:** ").append(res.durationMs).append(" ms\n\n");
                sb.append("```text\n").append(res.output).append("\n```\n");
            } else {
                sb.append("=== ").append(Icons.TERMINAL).append(" Comando del S.O. ===\n");
                sb.append(String.format("[%s PID=%d, exit=%d, %s %d ms]\n",
                        Icons.PID, res.pid, res.exitCode, Icons.CLOCK, res.durationMs));
                sb.append(res.output).append("\n");
            }

            // 3) Información del proceso actual (didáctico: ProcessHandle.current)
            //    - Muestra cómo consultar metadatos de proceso de forma segura con Optional.
            var self = ProcessHandle.current();
            var info = self.info();
            if (asMarkdown) {
                sb.append("\n## ").append(Icons.INFO).append(" Proceso actual (ProcessHandle.current)\n\n");
                sb.append("- **").append(Icons.PID).append(" PID:** ").append(self.pid()).append("\n");
                sb.append("- **").append(Icons.USER).append(" Usuario:** ").append(info.user().orElse("?")).append("\n");
                sb.append("- **").append(Icons.LINK).append(" Comando:** ").append(info.command().orElse("?")).append("\n");
                sb.append("- **").append(Icons.CLOCK).append(" Inicio:** ").append(info.startInstant().orElse(null)).append("\n");
                sb.append("- **").append(Icons.CPU).append(" CPU total:** ").append(info.totalCpuDuration().orElse(Duration.ZERO)).append("\n");
            } else {
                sb.append("=== ").append(Icons.INFO).append(" Proceso actual (ProcessHandle.current) ===\n");
                sb.append(Icons.PID).append(" PID: ").append(self.pid()).append("\n");
                sb.append(Icons.USER).append(" Usuario: ").append(info.user().orElse("?")).append("\n");
                sb.append(Icons.LINK).append(" Comando: ").append(info.command().orElse("?")).append("\n");
                sb.append(Icons.CLOCK).append(" Inicio: ").append(info.startInstant().orElse(null)).append("\n");
                sb.append(Icons.CPU).append(" CPU total: ").append(info.totalCpuDuration().orElse(Duration.ZERO)).append("\n");
            }

            // 4) Escribir a archivo en UTF-8
            Path destino = Path.of(outName);
            // Importante: forzamos UTF-8 para conservar iconos y evitar diferencias de plataforma.
            Files.writeString(destino, sb.toString(), StandardCharsets.UTF_8);
            System.out.println(Icons.OK + " Archivo generado: " + destino.toAbsolutePath());

            // 5) Abrir el archivo si se pide (--open)
            if (autoOpen) abrirEnSO(destino.toAbsolutePath().toString());

        } catch (IOException e) {
            System.err.printf("%s Error de E/S: %s%n", Icons.ERROR, e.getMessage());
        }
    }

    /* ============================ Utilidades ============================ */

    // Comprueba si existe un flag exacto (case-insensitive)
    private static boolean hasFlag(String[] args, String flag) {
        for (var a : args) if (a.equalsIgnoreCase(flag)) return true;
        return false;
    }

    // Parsea --timeout=SEG con valor por defecto si no está o es inválido
    private static long parseTimeout(String[] args, long def) {
        for (var a : args) {
            if (a.startsWith("--timeout=")) {
                try { return Long.parseLong(a.substring("--timeout=".length())); }
                catch (NumberFormatException ignored) { return def; }
            }
        }
        return def;
    }

    // Tabla Markdown con propiedades clave de la JVM.
    // Se escapan barras verticales para no romper la tabla.
    private static String mdPropsTable() {
        Properties p = System.getProperties();
        String[][] rows = {
                {"os.name",      p.getProperty("os.name")},
                {"os.arch",      p.getProperty("os.arch")},
                {"os.version",   p.getProperty("os.version")},
                {"user.name",    p.getProperty("user.name")},
                {"java.version", p.getProperty("java.version")},
                {"java.vendor",  p.getProperty("java.vendor")},
                {"java.home",    p.getProperty("java.home")}
        };
        StringBuilder t = new StringBuilder();
        t.append("| ").append(Icons.INFO).append(" Propiedad | Valor |\n|---|---|\n");
        for (var r : rows) t.append("| ").append(r[0]).append(" | ").append(safe(r[1])).append(" |\n");
        return t.append("\n").toString();
    }

    // Salida en texto plano de las mismas propiedades.
    private static String jvmPropsPlain() {
        Properties p = System.getProperties();
        return new StringBuilder()
                .append("os.name: ").append(p.getProperty("os.name")).append("\n")
                .append("os.arch: ").append(p.getProperty("os.arch")).append("\n")
                .append("os.version: ").append(p.getProperty("os.version")).append("\n")
                .append("user.name: ").append(p.getProperty("user.name")).append("\n")
                .append("java.version: ").append(p.getProperty("java.version")).append("\n")
                .append("java.vendor: ").append(p.getProperty("java.vendor")).append("\n")
                .append("java.home: ").append(p.getProperty("java.home")).append("\n\n")
                .toString();
    }

    // Evita null y escapa '|' en Markdown
    private static String safe(String s) { return s == null ? "" : s.replace("|","\\|"); }

    /* --------- Ejecutar comando del S.O. con ProcessBuilder/Handle --------- */
    // Técnica: lectura "intercalada" del InputStream + espera con pequeños time-slices.
    // Ventaja: reduce el riesgo de bloqueo por buffer lleno y nos permite vigilar el timeout.
    private static CommandResult ejecutarComandoSO(long timeoutSec) throws IOException {
        String os = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        ProcessBuilder pb;

        if (os.contains("win")) {
            pb = new ProcessBuilder("cmd", "/c", "systeminfo");
        } else {
            // En Linux/Unix: uname -a siempre disponible; lsb_release puede no estar.
            String shCmd = "uname -a; command -v lsb_release >/dev/null 2>&1 && lsb_release -a || true";
            pb = new ProcessBuilder("bash", "-c", shCmd);
        }

        pb.redirectErrorStream(true); // Unir stderr en stdout simplifica la lectura

        try {
            Instant t0 = Instant.now();
            Process p = pb.start();
            ProcessHandle h = p.toHandle();

            StringBuilder out = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    // Usamos el charset por defecto del proceso hijo; si quisieras forzar UTF-8 aquí,
                    // asegúrate de que el comando también emite UTF-8 (depende de la locale del S.O.).
                    new InputStreamReader(p.getInputStream()))) {
                String line;
                // Bucle de lectura + espera no bloqueante
                while (true) {
                    if (br.ready() && (line = br.readLine()) != null) {
                        out.append(line).append('\n');
                    } else {
                        // ¿Terminó el proceso?
                        if (p.waitFor(50, TimeUnit.MILLISECONDS)) break;
                        // ¿Superó el timeout global?
                        if (Duration.between(t0, Instant.now()).toSeconds() >= timeoutSec) {
                            h.destroy();
                            if (h.isAlive()) h.destroyForcibly();
                            out.append("\n").append(Icons.WARN)
                               .append(" Se alcanzó el timeout de ")
                               .append(timeoutSec).append("s y se terminó el proceso.\n");
                            return new CommandResult(h.pid(), -1, out.toString(),
                                    Duration.between(t0, Instant.now()).toMillis());
                        }
                    }
                }
            }

            int exit = p.exitValue();
            long ms = Duration.between(t0, Instant.now()).toMillis();
            return new CommandResult(h.pid(), exit, out.toString(), ms);

        } catch (InterruptedException e) {
            // Restablecer el estado de interrupción es buena práctica.
            Thread.currentThread().interrupt();
            throw new IOException("Proceso interrumpido", e);
        }
    }

    /* ---------------------- Abrir el archivo de salida ---------------------- */
    // Abre el archivo con la aplicación por defecto según el S.O.
    private static void abrirEnSO(String path) {
        String os = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        ProcessBuilder abrir;
        if (os.contains("win")) {
            abrir = new ProcessBuilder("cmd", "/c", "start", "", path);
        } else if (os.contains("mac")) {
            abrir = new ProcessBuilder("open", path);
        } else {
            abrir = new ProcessBuilder("xdg-open", path);
        }
        try {
            Process p = abrir.start();
            // onExit() es útil para logging ligero cuando el abridor termina.
            p.toHandle().onExit().thenRun(() -> System.out.println(Icons.FILE + " Abridor finalizó."));
        } catch (IOException e) {
            System.err.println(Icons.WARN + " No se pudo abrir el archivo automáticamente: " + e.getMessage());
        }
    }

    /* --------------------------- DTO de resultado --------------------------- */
    private record CommandResult(long pid, int exitCode, String output, long durationMs) {}
}
