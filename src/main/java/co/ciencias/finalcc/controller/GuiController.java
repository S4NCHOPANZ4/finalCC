package co.ciencias.finalcc.controller;

import co.ciencias.finalcc.model.Cliente;
import co.ciencias.finalcc.model.GrafoCiudad;
import co.ciencias.finalcc.model.Kit;
import co.ciencias.finalcc.model.Nodo;
import co.ciencias.finalcc.model.PuestoAtencion;
import co.ciencias.finalcc.model.SolicitudServicio;
import co.ciencias.finalcc.model.Tecnico;
import co.ciencias.finalcc.model.enums.Especialidad;
import co.ciencias.finalcc.model.enums.EstadoTecnico;
import co.ciencias.finalcc.model.enums.TipoEmergencia;
import co.ciencias.finalcc.model.gestores.GestorRecursos;
import co.ciencias.finalcc.model.gestores.GestorSolicitudes;
import co.ciencias.finalcc.view.Gui;
import javafx.application.Platform;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Controlador principal de la interfaz gráfica — capa C del patrón MVC.
 *
 * <p>Responsabilidades:</p>
 * <ul>
 *   <li>Recibir todas las acciones del usuario provenientes de {@link co.ciencias.finalcc.view.JSBridge}
 *       y delegarlas al modelo ({@link GestorSolicitudes}, {@link GestorRecursos},
 *       {@link PuestoAtencion}).</li>
 *   <li>Empujar el estado actualizado de cada puesto al JS del WebView cada 500 ms
 *       llamando a {@code actualizarPuesto(id, datos)}.</li>
 *   <li>Notificar a la vista el resultado del cierre de día llamando a
 *       {@code onDiaCerrado(exito, mensaje)} desde el hilo de JavaFX.</li>
 * </ul>
 *
 * <p><b>Regla MVC:</b> esta clase es la ÚNICA que habla con el modelo.
 * {@link co.ciencias.finalcc.view.JSBridge} solo llama métodos de este controlador.</p>
 */
public class GuiController {

    private static final String[] IDS     = { "norte", "sur" };
    private static final int[]    INDICES = { 0, 1 };

    private Timer timer;

    // ------------------------------------------------------------------
    // POLLING — empuja estado al JS cada 500 ms
    // ------------------------------------------------------------------

    /**
     * Inicia el ciclo de polling que actualiza la vista cada 500 ms.
     * Cancela cualquier timer previo antes de crear uno nuevo.
     */
    public void iniciarPolling() {
        if (timer != null) timer.cancel();
        timer = new Timer("gui-polling", true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    for (int i = 0; i < 2; i++) {
                        empujarEstado(IDS[i], INDICES[i]);
                    }
                });
            }
        }, 500, 500);
    }

    /**
     * Detiene el polling. Debe llamarse al cerrar la ventana principal.
     */
    public void detener() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    // ------------------------------------------------------------------
    // ACCIONES DEL USUARIO — delegadas desde JSBridge
    // ------------------------------------------------------------------

    /**
     * Registra una nueva solicitud de servicio.
     * El puesto más cercano se calcula mediante BFS en {@link GrafoCiudad}.
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
            int maxNodo = GrafoCiudad.getInstancia().getCantidadNodos() - 1;
            if (idx < 0)       idx = 0;
            if (idx > maxNodo) idx = maxNodo;

            GestorSolicitudes.getInstancia()
                             .crearSolicitud(cliente, tipoEmergencia,
                                             descripcion == null ? "" : descripcion, idx);

            System.out.printf("[GuiController] Solicitud registrada → cliente=%s | nodo=%s | tipo=%s%n",
                              nombre.trim(),
                              GrafoCiudad.getInstancia().nombreNodo(idx),
                              tipo.trim());

        } catch (IllegalArgumentException e) {
            System.err.println("[GuiController] Tipo de emergencia invalido: " + tipo + " | " + e.getMessage());
        } catch (Exception e) {
            System.err.println("[GuiController] Error en registrarSolicitud: "
                    + e.getClass().getSimpleName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Atiende la siguiente solicitud de mayor prioridad del puesto,
     * respetando la disponibilidad de técnicos por especialidad.
     *
     * @param puestoId "norte" o "sur"
     */
    public void accionAtender(String puestoId) {
        try {
            GestorRecursos.getInstancia()
                          .getPuesto(indiceDe(puestoId))
                          .atenderSiguiente();
        } catch (Exception e) {
            System.err.println("[GuiController] Error en accionAtender(" + puestoId + "): " + e.getMessage());
        }
    }

    /**
     * Finaliza la solicitud activa más antigua (FIFO) del puesto,
     * libera el técnico asignado y genera un kit dañado en la pila.
     *
     * @param puestoId "norte" o "sur"
     */
    public void accionTerminar(String puestoId) {
        try {
            GestorRecursos.getInstancia()
                          .getPuesto(indiceDe(puestoId))
                          .terminarSolicitudActiva();
        } catch (Exception e) {
            System.err.println("[GuiController] Error en accionTerminar(" + puestoId + "): " + e.getMessage());
        }
    }

    /**
     * Repara el kit en el tope de la pila de mantenimiento del puesto
     * y lo reintegra al stock disponible.
     *
     * @param puestoId "norte" o "sur"
     */
    public void accionReparar(String puestoId) {
        try {
            GestorRecursos.getInstancia()
                          .getPuesto(indiceDe(puestoId))
                          .repararKitTope();
        } catch (Exception e) {
            System.err.println("[GuiController] Error en accionReparar(" + puestoId + "): " + e.getMessage());
        }
    }

    /**
     * Cierra el día operativo del Puesto Norte:
     * finaliza solicitudes en ejecución, genera el CSV y notifica a la vista.
     */
    public void terminarDiaNorte() {
        System.out.println("[GuiController] terminarDiaNorte() llamado");
        terminarDiaPuesto(0);
    }

    /**
     * Cierra el día operativo del Puesto Sur:
     * finaliza solicitudes en ejecución, genera el CSV y notifica a la vista.
     */
    public void terminarDiaSur() {
        System.out.println("[GuiController] terminarDiaSur() llamado");
        terminarDiaPuesto(1);
    }

    // ------------------------------------------------------------------
    // LÓGICA INTERNA DE CIERRE DE DÍA
    // ------------------------------------------------------------------

    /**
     * Ejecuta el cierre de día para el puesto indicado:
     * <ol>
     *   <li>Llama a {@link PuestoAtencion#cerrarDia()} para finalizar solicitudes en ejecución.</li>
     *   <li>Genera el CSV mediante {@link GestorSolicitudes#generarCsvDia(int)}.</li>
     *   <li>Notifica el resultado a la vista llamando a {@code onDiaCerrado()} en el JS.</li>
     * </ol>
     *
     * @param idx 0 = Norte, 1 = Sur
     */
    private void terminarDiaPuesto(int idx) {
        try {
            System.out.println("[GuiController] Cerrando dia puesto " + idx);
            GestorRecursos.getInstancia().getPuesto(idx).cerrarDia();

            System.out.println("[GuiController] Generando CSV puesto " + idx);
            String ruta = GestorSolicitudes.getInstancia().generarCsvDia(idx);
            System.out.println("[GuiController] Ruta CSV: '" + ruta + "'");

            boolean exito = ruta != null && !ruta.isEmpty() && !ruta.startsWith("ERROR");
            String rutaEscapada = ruta == null ? "" : ruta.replace("\\", "\\\\").replace("'", "\\'");
            String script = exito
                    ? "onDiaCerrado(true, '"  + rutaEscapada + "');"
                    : "onDiaCerrado(false, 'No se pudo generar el CSV');";

            Platform.runLater(() -> {
                try {
                    Gui.getEngine().executeScript(script);
                } catch (Exception e) {
                    System.err.println("[GuiController] Error al notificar JS: " + e.getMessage());
                }
            });

        } catch (Exception e) {
            System.err.println("[GuiController] EXCEPCION en terminarDiaPuesto(" + idx + "): "
                    + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
            String msg = (e.getMessage() == null ? "error desconocido" : e.getMessage())
                             .replace("\\", "\\\\").replace("'", "\\'");
            Platform.runLater(() -> {
                try {
                    Gui.getEngine().executeScript("onDiaCerrado(false, 'ERROR: " + msg + "');");
                } catch (Exception ex) {
                    System.err.println("[GuiController] Error al notificar fallo JS: " + ex.getMessage());
                }
            });
        }
    }

    // ------------------------------------------------------------------
    // POLLING INTERNO — construcción del JSON de estado
    // ------------------------------------------------------------------

    private void empujarEstado(String id, int idx) {
        try {
            PuestoAtencion p = GestorRecursos.getInstancia().getPuesto(idx);
            if (p == null) return;
            String json   = construirJson(p);
            String script = "actualizarPuesto('" + id + "', " + json + ");";
            Gui.getEngine().executeScript(script);
        } catch (Exception e) {
            System.err.println("[GuiController] Error al empujar estado '" + id + "': " + e.getMessage());
        }
    }

    private String construirJson(PuestoAtencion p) {
        int kits = p.getContadorKits();

        // Cola FIFO de ejecución
        StringBuilder ejecucion = new StringBuilder("[");
        Nodo<SolicitudServicio> nodoEj = p.getSolicitudesEnEjecucion().getNodoFrente();
        boolean primero = true;
        while (nodoEj != null) {
            if (!primero) ejecucion.append(",");
            ejecucion.append("\"").append(esc(nodoEj.getDato().getCliente().getNombre())).append("\"");
            primero = false;
            nodoEj  = nodoEj.getSiguiente();
        }
        ejecucion.append("]");

        // Personal disponible
        int bri = 0, seg = 0, han = 0;
        Nodo<Tecnico> nodoTec = p.getTecnicos().getCabeza();
        while (nodoTec != null) {
            Tecnico t = nodoTec.getDato();
            if (t.getEstado() == EstadoTecnico.DISPONIBLE) {
                switch (t.getEspecialidad()) {
                    case BRIGADISTA     -> bri++;
                    case SEGURIDAD_RUTA -> seg++;
                    case HANDYMAN       -> han++;
                }
            }
            nodoTec = nodoTec.getSiguiente();
        }

        // Cola de prioridad (pendientes)
        StringBuilder cola = new StringBuilder("[");
        Nodo<SolicitudServicio> nodoCola = p.getSolicitudesPendientes().getCabeza();
        boolean primeroCola = true;
        while (nodoCola != null) {
            if (!primeroCola) cola.append(",");
            String item = nodoCola.getDato().getCliente().getNombre()
                        + " (" + nodoCola.getDato().getTipoEmergencia().name() + ")";
            cola.append("\"").append(esc(item)).append("\"");
            primeroCola = false;
            nodoCola    = nodoCola.getSiguiente();
        }
        cola.append("]");

        // Pila de kits dañados
        StringBuilder pila = new StringBuilder("[");
        Nodo<Kit> nodoKit = p.getPilaKitsDañados().getTope();
        boolean primeroPila = true;
        while (nodoKit != null) {
            if (!primeroPila) pila.append(",");
            String kitId = nodoKit.getDato().getId();
            String subId = kitId.substring(0, Math.min(5, kitId.length())).toUpperCase();
            pila.append("\"").append(esc("Kit ID: " + subId)).append("\"");
            primeroPila = false;
            nodoKit     = nodoKit.getSiguiente();
        }
        pila.append("]");

        return "{\"kits\":"     + kits
             + ",\"ejecucion\":" + ejecucion
             + ",\"personal\":{\"bri\":" + bri + ",\"seg\":" + seg + ",\"han\":" + han + "}"
             + ",\"cola\":"     + cola
             + ",\"pila\":"     + pila
             + "}";
    }

    // ------------------------------------------------------------------
    // UTILIDAD
    // ------------------------------------------------------------------

    /**
     * Convierte el identificador de puesto del JS al índice del array.
     *
     * @param id "norte" o "sur"
     * @return 0 para Norte, 1 para Sur
     */
    private int indiceDe(String id) {
        if (id == null) return 0;
        return "sur".equalsIgnoreCase(id.trim()) ? 1 : 0;
    }

    private String esc(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}