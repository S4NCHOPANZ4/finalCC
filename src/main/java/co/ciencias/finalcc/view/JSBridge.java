package co.ciencias.finalcc.view;

import co.ciencias.finalcc.model.Cliente;
import co.ciencias.finalcc.model.GrafoCiudad;
import co.ciencias.finalcc.model.enums.TipoEmergencia;
import co.ciencias.finalcc.model.gestores.GestorRecursos;
import co.ciencias.finalcc.model.gestores.GestorSolicitudes;

/**
 * Puente JavaScript → Java para el WebView.
 *
 * <p><b>CRÍTICO:</b> Ningún método puede propagar una excepción al WebEngine
 * de JavaFX. Si una excepción escapa, el WebEngine congela el hilo JS de forma
 * permanente para esa sesión — el formulario queda bloqueado sin mensaje de error.
 * Por eso cada método tiene su propio try/catch que absorbe TODO.</p>
 *
 * <p>Recibe {@code indiceNodo} (String "0"–"9") en lugar del par
 * circulo/angulo anterior. El BFS en {@link GrafoCiudad} calcula
 * cuál de los dos puestos (Norte=0, Sur=1) es el más cercano.</p>
 */
public class JSBridge {

    // ------------------------------------------------------------------
    // REGISTRO DE SOLICITUD
    // ------------------------------------------------------------------

    /**
     * Registra una nueva solicitud de servicio.
     *
     * @param nombre      nombre del cliente
     * @param telefono    teléfono del cliente
     * @param tipo        nombre del enum {@link TipoEmergencia}
     * @param indiceNodo  índice del nodo del grafo (String "0"–"9")
     * @param descripcion descripción del incidente
     */
    public void registrarSolicitud(String nombre, String telefono,
                                   String tipo, String indiceNodo,
                                   String descripcion) {
        try {
            if (nombre == null || nombre.trim().isEmpty()) return;
            if (telefono == null || telefono.trim().isEmpty()) return;

            Cliente cliente = new Cliente(nombre.trim(), telefono.trim());
            TipoEmergencia tipoEmergencia = TipoEmergencia.valueOf(tipo.trim());

            int idx = 0;
            try { idx = Integer.parseInt(indiceNodo.trim()); } catch (NumberFormatException ignored) {}
            // Clamp al rango válido [0, cantidad de nodos - 1]
            int maxNodo = GrafoCiudad.getInstancia().getCantidadNodos() - 1;
            if (idx < 0) idx = 0;
            if (idx > maxNodo) idx = maxNodo;

            GestorSolicitudes.getInstancia()
                             .crearSolicitud(cliente, tipoEmergencia,
                                             descripcion == null ? "" : descripcion, idx);

            System.out.printf("[JSBridge] OK → cliente=%s | nodo=%s | tipo=%s%n",
                              nombre.trim(),
                              GrafoCiudad.getInstancia().nombreNodo(idx),
                              tipo.trim());

        } catch (IllegalArgumentException e) {
            System.err.println("[JSBridge] Tipo de emergencia inválido: " + tipo + " | " + e.getMessage());
        } catch (Exception e) {
            System.err.println("[JSBridge] Error en registrarSolicitud: " + e.getClass().getSimpleName() + ": " + e.getMessage());
            e.printStackTrace();
        }
        // NUNCA relanzar — el WebEngine no debe ver excepciones Java
    }

    // ------------------------------------------------------------------
    // ACCIONES DE PUESTO
    // ------------------------------------------------------------------

    /** Atiende la siguiente solicitud de mayor prioridad del puesto. */
    public void accionAtender(String puestoId) {
        try {
            GestorRecursos.getInstancia().getPuesto(indiceDe(puestoId)).atenderSiguiente();
        } catch (Exception e) {
            System.err.println("[JSBridge] Error en accionAtender(" + puestoId + "): " + e.getMessage());
        }
    }

    /** Finaliza la solicitud activa más antigua (FIFO) del puesto. */
    public void accionTerminar(String puestoId) {
        try {
            GestorRecursos.getInstancia().getPuesto(indiceDe(puestoId)).terminarSolicitudActiva();
        } catch (Exception e) {
            System.err.println("[JSBridge] Error en accionTerminar(" + puestoId + "): " + e.getMessage());
        }
    }

    /** Repara el kit del tope de la pila de mantenimiento del puesto. */
    public void accionReparar(String puestoId) {
        try {
            GestorRecursos.getInstancia().getPuesto(indiceDe(puestoId)).repararKitTope();
        } catch (Exception e) {
            System.err.println("[JSBridge] Error en accionReparar(" + puestoId + "): " + e.getMessage());
        }
    }

    /**
     * Cierra el día del Puesto Norte, escribe el CSV en disco y retorna
     * la ruta absoluta del archivo para mostrarla en la UI.
     */
public void terminarDiaNorte() {
    System.out.println("[JSBridge] terminarDiaNorte() LLAMADO");
    terminarDiaPuesto(0);
}

public void terminarDiaSur() {
    System.out.println("[JSBridge] terminarDiaSur() LLAMADO");
    terminarDiaPuesto(1);
}

private void terminarDiaPuesto(int idx) {
    try {
        System.out.println("[JSBridge] cerrarDia puesto " + idx);
        GestorRecursos.getInstancia().getPuesto(idx).cerrarDia();

        System.out.println("[JSBridge] generando CSV puesto " + idx);
        String ruta = GestorSolicitudes.getInstancia().generarCsvDia(idx);
        System.out.println("[JSBridge] ruta resultado: '" + ruta + "'");

        // Notificar al JS con el resultado
        String rutaEscapada = ruta == null ? "" : ruta.replace("\\", "\\\\").replace("'", "\\'");
        String script = ruta != null && !ruta.isEmpty() && !ruta.startsWith("ERROR")
                ? "onDiaCerrado(true, '" + rutaEscapada + "');"
                : "onDiaCerrado(false, 'No se pudo generar el CSV');";

        javafx.application.Platform.runLater(() ->
            co.ciencias.finalcc.view.Gui.getEngine().executeScript(script)
        );

    } catch (Exception e) {
        System.err.println("[JSBridge] EXCEPCION: " + e.getClass().getName() + ": " + e.getMessage());
        e.printStackTrace();
        String msg = e.getMessage() == null ? "error desconocido" : e.getMessage().replace("'", "\\'");
        javafx.application.Platform.runLater(() ->
            co.ciencias.finalcc.view.Gui.getEngine().executeScript(
                "onDiaCerrado(false, 'ERROR Java: " + msg + "');"
            )
        );
    }
}
    // ------------------------------------------------------------------
    // UTILIDAD
    // ------------------------------------------------------------------

    private int indiceDe(String id) {
        if (id == null) return 0;
        return "sur".equalsIgnoreCase(id.trim()) ? 1 : 0;
    }
}