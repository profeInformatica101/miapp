package lab.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Utilidades de análisis sintáctico para una línea de comandos.
 * <p>
 * Características:
 * <ul>
 *   <li>Tokenización estilo shell (respeta comillas simples y dobles).</li>
 *   <li>Parseo de opciones con formato <code>--clave=valor</code> o <code>--clave valor</code>.</li>
 *   <li>Las opciones sin valor explícito se interpretan como <code>true</code>.</li>
 * </ul>
 */
public final class AnalisisSintactico {
	// 📘 Patrón Utility Class (Clase de Utilidad) :::  Sirve para impedir que alguien cree instancias de la clase
    private AnalisisSintactico() {}

    /**
     * Tokeniza una línea respetando comillas simples y dobles.
     *
     * @param linea Texto de entrada (tal cual lo escribe el usuario).
     * @return Lista de tokens (sin comillas).
     */
    public static List<String> tokenizar(String linea) {
        List<String> tokens = new ArrayList<>();
        StringBuilder actual = new StringBuilder();
        boolean enSimples = false, enDobles = false;

        for (int i = 0; i < linea.length(); i++) {
            char c = linea.charAt(i);
            if (c == '\'' && !enDobles) { enSimples = !enSimples; continue; }
            if (c == '\"' && !enSimples) { enDobles = !enDobles; continue; }

            if (Character.isWhitespace(c) && !enSimples && !enDobles) {
                if (actual.length() > 0) {
                    tokens.add(actual.toString());
                    actual.setLength(0);
                }
            } else {
                actual.append(c);
            }
        }
        if (actual.length() > 0) tokens.add(actual.toString());
        return tokens;
    }

    /**
     * Analiza una lista de tokens separando opciones y argumentos posicionales.
     * <p>
     * Reglas:
     * <ul>
     *   <li><code>--k=v</code> ➜ opción <code>k</code> con valor <code>v</code>.</li>
     *   <li><code>--k v</code> ➜ opción <code>k</code> con valor <code>v</code>.</li>
     *   <li><code>--flag</code> ➜ opción <code>flag</code> con valor <code>true</code>.</li>
     *   <li>Todo lo que no empiece por <code>--</code> se considera argumento posicional.</li>
     * </ul>
     *
     * @param tokens Lista de tokens (por ejemplo, salida de {@link #tokenizar(String)}).
     * @return Resultado con <code>opciones</code> y <code>argumentos</code>.
     */
    public static ResultadoAnalisis analizarOpciones(List<String> tokens) {
        Map<String, String> opciones = new LinkedHashMap<>();
        List<String> argumentos = new ArrayList<>();

        for (int i = 0; i < tokens.size(); i++) {
            String t = tokens.get(i);
            if (t.startsWith("--")) {
                int eq = t.indexOf('=');
                if (eq > 2) {
                    opciones.put(t.substring(2, eq), t.substring(eq + 1));
                } else if (i + 1 < tokens.size() && !tokens.get(i + 1).startsWith("--")) {
                    opciones.put(t.substring(2), tokens.get(++i));
                } else {
                    opciones.put(t.substring(2), "true");
                }
            } else {
                argumentos.add(t);
            }
        }
        return new ResultadoAnalisis(opciones, argumentos);
    }

    /**
     * Resultado del análisis: opciones (por clave) y argumentos posicionales.
     */
    public record ResultadoAnalisis(Map<String, String> opciones, List<String> argumentos) {}


    public static List<String> tokenize(String line) { return tokenizar(line); }


    public static Parsed parseOptions(List<String> tokens) {
        ResultadoAnalisis r = analizarOpciones(tokens);
        return new Parsed(r.opciones(), r.argumentos());
    }


    public record Parsed(Map<String, String> options, List<String> args) {}
}
