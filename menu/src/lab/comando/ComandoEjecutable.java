package lab.comando;

import java.util.List;


import lab.util.GestorComandos;
/**
 * Clase base abstracta para comandos que lanzan procesos del sistema.
 * Proporciona el helper {@code ejecutarProceso} para reutilizar lógica
 * de ejecución en todas las subclases.
 */
public abstract class ComandoEjecutable implements IComando {

	   /**
     * Ejecuta un proceso externo heredando stdout/stderr mediante GestorComandos.
     *
     * @param argumentos Lista con el binario y sus argumentos.
     *                   Ej: List.of("chafa", "--size", "80x40", "img.png")
     * @return Código de retorno del proceso (0 = OK, !=0 error del proceso, 1 si falla la ejecución).
     */
    protected int lanzarProceso(List<String> argumentos) {
        return GestorComandos.ejecutar(argumentos);
    }


}