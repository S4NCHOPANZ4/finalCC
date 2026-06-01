package co.ciencias.finalcc.controller;

import co.ciencias.finalcc.model.Kit;
import co.ciencias.finalcc.model.Nodo;
import co.ciencias.finalcc.model.PuestoAtencion;
import co.ciencias.finalcc.model.SolicitudServicio;
import co.ciencias.finalcc.model.Tecnico;
import co.ciencias.finalcc.model.enums.EstadoTecnico;
import co.ciencias.finalcc.model.gestores.GestorRecursos;
import co.ciencias.finalcc.view.Gui;
import javafx.application.Platform;

import java.util.Timer;
import java.util.TimerTask;


public class GuiController {

    private static final String[] IDS     = { "norte", "sur" };
    private static final int[]    INDICES = { 0, 1 };

    private Timer timer;

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

    public void detener() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }


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

        return "{\"kits\":" + kits
             + ",\"ejecucion\":" + ejecucion
             + ",\"personal\":{\"bri\":" + bri + ",\"seg\":" + seg + ",\"han\":" + han + "}"
             + ",\"cola\":" + cola
             + ",\"pila\":" + pila
             + "}";
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