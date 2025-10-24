package lab.comando;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import lab.console.RegistroComandos;
import lab.util.AnalisisSintactico;

/**
 * Comando que utiliza la utilidad 'chafa' para renderizar imágenes
 * en formato ASCII directamente en la consola.
 */
public class ComandoChafa extends ComandoEjecutable {

    @Override
    public int ejecutarProceso(List<String> args, RegistroComandos registro) throws Exception {
        // 1️⃣ Analizar las opciones y argumentos
        AnalisisSintactico.ResultadoAnalisis resultado = AnalisisSintactico.analizarOpciones(args);
        Map<String, String> opciones = resultado.opciones();
        List<String> posicionales = resultado.argumentos();

        // 2️⃣ Obtener tamaño o usar valor por defecto
        String size = opciones.getOrDefault("size", "80x40");

        // 3️⃣ Validar que se ha proporcionado una ruta de imagen
        if (posicionales.isEmpty()) {
            System.out.println("Uso: chafa --size 80x40 <ruta-imagen>");
            return RegistroComandos.RC_ERR;
        }

        Path ruta = Paths.get(posicionales.get(0));
        if (!Files.isRegularFile(ruta)) {
            System.err.println("❌ No existe el fichero: " + ruta);
            return RegistroComandos.RC_ERR;
        }

        // 4️⃣ Mostrar información y ejecutar el proceso
        System.out.printf("🖼️ Generando ASCII con tamaño %s desde %s...%n", size, ruta);

        List<String> comando = Arrays.asList("chafa", "--size", size, ruta.toString());
        return lanzarProceso(comando); // ✅ llama al helper correcto
    }

    @Override
    public String ayuda() {
        return """
               Renderiza imágenes como ASCII usando la utilidad 'chafa'.
               Uso:
                 chafa --size 80x40 <ruta-imagen>
               """;
    }

    @Override
    public String nombre() {
        return "chafa";
    }
}
