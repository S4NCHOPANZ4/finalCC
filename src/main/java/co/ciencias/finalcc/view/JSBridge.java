package co.ciencias.finalcc.view;

import co.ciencias.finalcc.controller.GuiController;

/**
 * Puente JavaScript → Java para el WebView.
 *
 * <p><b>Rol en MVC:</b> esta clase pertenece a la capa <b>Vista</b>.
 * Su única responsabilidad es recibir llamadas desde el JavaScript del WebView
 * y delegarlas al {@link GuiController} (capa Controlador).
 * <b>No contiene ninguna lógica de negocio ni accede al modelo directamente.</b></p>
 *
 * <p><b>CRÍTICO:</b> ningún método puede propagar una excepción al WebEngine
 * de JavaFX. Si una excepción escapa, el WebEngine congela el hilo JS de forma
 * permanente para esa sesión. Por eso cada método tiene su propio try/catch.</p>
 *
 * <p>La instancia de {@link GuiController} se recibe por constructor para
 * garantizar que Vista y Controlador comparten la misma instancia que maneja
 * el polling, evitando estados inconsistentes.</p>
 */
public class JSBridge {

    private final GuiController controller;

    /**
     * Construye el puente inyectando el controlador activo.
     *
     * @param controller controlador que gestiona modelo y vista
     */
    public JSBridge(GuiController controller) {
        this.controller = controller;
    }

    // ------------------------------------------------------------------
    // REGISTRO DE SOLICITUD
    // ------------------------------------------------------------------

    /**
     * Registra una nueva solicitud de servicio.
     * Delega completamente a {@link GuiController#registrarSolicitud}.
     *
     * @param nombre      nombre del cliente
     * @param telefono    teléfono del cliente
     * @param tipo        nombre del enum TipoEmergencia
     * @param indiceNodo  índice del nodo del grafo (String "0"–"9")
     * @param descripcion descripción del incidente
     */
    public void registrarSolicitud(String nombre, String telefono,
                                   String tipo, String indiceNodo,
                                   String descripcion) {
        try {
            controller.registrarSolicitud(nombre, telefono, tipo, indiceNodo, descripcion);
        } catch (Exception e) {
            System.err.println("[JSBridge] Error en registrarSolicitud: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------
    // ACCIONES DE PUESTO
    // ------------------------------------------------------------------

    /**
     * Atiende la siguiente solicitud de mayor prioridad del puesto.
     * Delega a {@link GuiController#accionAtender}.
     *
     * @param puestoId "norte" o "sur"
     */
    public void accionAtender(String puestoId) {
        try {
            controller.accionAtender(puestoId);
        } catch (Exception e) {
            System.err.println("[JSBridge] Error en accionAtender(" + puestoId + "): " + e.getMessage());
        }
    }

    /**
     * Finaliza la solicitud activa más antigua (FIFO) del puesto.
     * Delega a {@link GuiController#accionTerminar}.
     *
     * @param puestoId "norte" o "sur"
     */
    public void accionTerminar(String puestoId) {
        try {
            controller.accionTerminar(puestoId);
        } catch (Exception e) {
            System.err.println("[JSBridge] Error en accionTerminar(" + puestoId + "): " + e.getMessage());
        }
    }

    /**
     * Repara el kit del tope de la pila de mantenimiento del puesto.
     * Delega a {@link GuiController#accionReparar}.
     *
     * @param puestoId "norte" o "sur"
     */
    public void accionReparar(String puestoId) {
        try {
            controller.accionReparar(puestoId);
        } catch (Exception e) {
            System.err.println("[JSBridge] Error en accionReparar(" + puestoId + "): " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------
    // CIERRE DE DÍA
    // ------------------------------------------------------------------

    /**
     * Cierra el día operativo del Puesto Norte.
     * Delega a {@link GuiController#terminarDiaNorte}.
     * Java notifica el resultado a la vista mediante {@code onDiaCerrado()} en JS.
     */
    public void terminarDiaNorte() {
        try {
            controller.terminarDiaNorte();
        } catch (Exception e) {
            System.err.println("[JSBridge] Error en terminarDiaNorte: " + e.getMessage());
        }
    }

    /**
     * Cierra el día operativo del Puesto Sur.
     * Delega a {@link GuiController#terminarDiaSur}.
     * Java notifica el resultado a la vista mediante {@code onDiaCerrado()} en JS.
     */
    public void terminarDiaSur() {
        try {
            controller.terminarDiaSur();
        } catch (Exception e) {
            System.err.println("[JSBridge] Error en terminarDiaSur: " + e.getMessage());
        }
    }
}