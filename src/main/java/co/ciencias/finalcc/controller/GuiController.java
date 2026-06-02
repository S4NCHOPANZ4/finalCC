package co.ciencias.finalcc.controller;

import co.ciencias.finalcc.model.Cliente;
import co.ciencias.finalcc.model.GrafoCiudad;
import co.ciencias.finalcc.model.Kit;
import co.ciencias.finalcc.model.Nodo;
import co.ciencias.finalcc.model.PuestoAtencion;
import co.ciencias.finalcc.model.SolicitudServicio;
import co.ciencias.finalcc.model.Tecnico;
import co.ciencias.finalcc.model.UnidadServicio;
import co.ciencias.finalcc.model.enums.*;
import co.ciencias.finalcc.model.gestores.GestorRecursos;
import co.ciencias.finalcc.model.gestores.GestorSolicitudes;
import co.ciencias.finalcc.view.Gui;
import javafx.application.Platform;

public class GuiController {

    private static final String[] IDS = { "norte", "sur" };
    private static final int[] INDICES = { 0, 1 };

    private Thread hiloPolling;
    private volatile boolean corriendo = false;

    public void iniciarPolling() {
        corriendo = false;
        if (hiloPolling != null) hiloPolling.interrupt();

        corriendo = true;
        hiloPolling = new Thread(() -> {
            try { Thread.sleep(500); } catch (InterruptedException e) { return; }
            while (corriendo) {
                Platform.runLater(() -> {
                    for (int i = 0; i < 2; i++) empujarEstado(IDS[i], INDICES[i]);
                });
                try { Thread.sleep(500); } catch (InterruptedException e) { break; }
            }
        });
        hiloPolling.setDaemon(true);
        hiloPolling.start();
    }

    public void detener() {
        corriendo = false;
        if (hiloPolling != null) { hiloPolling.interrupt(); hiloPolling = null; }
    }

    public void registrarSolicitud(String nombre, String telefono, String tipo, String indiceNodo, String descripcion) {
        try {
            if (nombre == null || nombre.trim().isEmpty()) return;
            if (telefono == null || telefono.trim().isEmpty()) return;
            Cliente cliente = new Cliente(nombre.trim(), telefono.trim());
            int idx = 0;
            try { idx = Integer.parseInt(indiceNodo.trim()); } catch (NumberFormatException ignored) {}
            int maxNodo = GrafoCiudad.getInstancia().getCantidadNodos() - 1;
            if (idx < 0) idx = 0;
            if (idx > maxNodo) idx = maxNodo;
            GestorSolicitudes.getInstancia().crearSolicitud( cliente, TipoEmergencia.valueOf(tipo.trim()), descripcion == null ? "" : descripcion, idx);
        } catch (Exception e) {}
    }

    public void accionAtender(String puestoId) {
        try { GestorRecursos.getInstancia().getPuesto(indiceDe(puestoId)).atenderSiguiente(); }
        catch (Exception e) {}
    }

    public void accionTerminar(String puestoId) {
        try { GestorRecursos.getInstancia().getPuesto(indiceDe(puestoId)).terminarSolicitudActiva(); }
        catch (Exception e) {}
    }

    public void accionReparar(String puestoId) {
        try { GestorRecursos.getInstancia().getPuesto(indiceDe(puestoId)).repararKitTope(); }
        catch (Exception e) {}
    }

    public void accionRevertir(String puestoId) {
        try { GestorRecursos.getInstancia().getPuesto(indiceDe(puestoId)).revertirUltimaEjecucion(); }
        catch (Exception e) {}
    }


    public void agregarTecnico(String puestoId, String nombre, String especialidad) {
        try {
            if (nombre == null || nombre.trim().isEmpty()) return;
            Especialidad esp = Especialidad.valueOf(especialidad.trim());
            GestorRecursos.getInstancia().getPuesto(indiceDe(puestoId)).agregarTecnico(nombre.trim(), esp);
        } catch (Exception e) {}
    }

    public void editarTecnico(String puestoId, String id, String nuevoNombre, String nuevaEsp) {
        try {
            Especialidad esp = null;
            if (nuevaEsp != null && !nuevaEsp.trim().isEmpty()) {
                esp = Especialidad.valueOf(nuevaEsp.trim());
            }
            GestorRecursos.getInstancia().getPuesto(indiceDe(puestoId)).editarTecnico(id, nuevoNombre, esp);
        } catch (Exception e) {}
    }

    public void agregarVehiculo(String puestoId, String tipo, String codigo) {
        try {
            if (codigo == null || codigo.trim().isEmpty()) return;
            TipoUnidad tu = TipoUnidad.valueOf(tipo.trim());
            GestorRecursos.getInstancia().getPuesto(indiceDe(puestoId)).agregarUnidad(tu, codigo.trim());
        } catch (Exception e) {}
    }

    public void editarVehiculo(String puestoId, String id, String nuevoCod, String nuevoTipo) {
        try {
            TipoUnidad tu = null;
            if (nuevoTipo != null && !nuevoTipo.trim().isEmpty()) {
                tu = TipoUnidad.valueOf(nuevoTipo.trim());
            }
            GestorRecursos.getInstancia().getPuesto(indiceDe(puestoId)).editarUnidad(id, nuevoCod, tu);
        } catch (Exception e) {}
    }

    public void eliminarTecnico(String puestoId, String id) {
        try { GestorRecursos.getInstancia().getPuesto(indiceDe(puestoId)).eliminarTecnico(id); }
        catch (Exception e) {}
    }

    public void eliminarVehiculo(String puestoId, String id) {
        try { GestorRecursos.getInstancia().getPuesto(indiceDe(puestoId)).eliminarUnidad(id); }
        catch (Exception e) {}
    }

    public void terminarDiaNorte() { terminarDiaPuesto(0); }
    public void terminarDiaSur()   { terminarDiaPuesto(1); }

    private void terminarDiaPuesto(int idx) {
        try {
            GestorRecursos.getInstancia().getPuesto(idx).cerrarDia();
            String ruta = GestorSolicitudes.getInstancia().generarCsvDia(idx);
            String script;
            if (ruta != null && !ruta.isEmpty() && !ruta.startsWith("ERROR")) {
                String rutaEscapada = ruta.replace("\\", "\\\\").replace("'", "\\'");
                script = "onDiaCerrado(true, '" + rutaEscapada + "');";
            } else {
                script = "onDiaCerrado(false, 'No se pudo generar el CSV');";
            }
            final String scriptFinal = script;
            Platform.runLater(() -> {
                try { 
                    Gui.getEngine().executeScript(scriptFinal); 
                } catch (Exception e) {}
            });
        } catch (Exception e) {
            String raw = e.getMessage();
            String msg = raw == null ? "error desconocido" : raw.replace("\\", "\\\\").replace("'", "\\'");
            final String msgFinal = msg;
            Platform.runLater(() -> {
                try { 
                    Gui.getEngine().executeScript("onDiaCerrado(false, 'ERROR: " + msgFinal + "');");
                } catch (Exception ex) {}
            });
        }
    }


    private void empujarEstado(String id, int idx) {
        try {
            PuestoAtencion p = GestorRecursos.getInstancia().getPuesto(idx);
            if (p == null) return;
            Gui.getEngine().executeScript("actualizarPuesto('" + id + "', " + construirJson(p) + ");");
        } catch (Exception e) {}
    }

    private String construirJson(PuestoAtencion p) {

        StringBuilder ejecucion = new StringBuilder("[");
        Nodo<SolicitudServicio> nodoEj = p.getSolicitudesEnEjecucion().getNodoFrente();
        boolean primero = true;
        while (nodoEj != null) {
            if (!primero) ejecucion.append(",");
            SolicitudServicio sol = nodoEj.getDato();
            String tec = "";
            if (sol.getTecnicoAsignado() != null) {
                tec = sol.getTecnicoAsignado().getNombre();
            } else {
                tec = "Sin tecnico";
            }
            String uni = "";
            if (sol.getUnidadAsignada() != null) {
                uni = sol.getUnidadAsignada().getCodigo();
            } else {
                uni = "Sin unidad";
            }
            ejecucion.append("{").append("\"cliente\":\"").append(esc(sol.getCliente().getNombre())).append("\",").append("\"tecnico\":\"").append(esc(tec)).append("\",").append("\"unidad\":\"").append(esc(uni)).append("\"").append("}");
            primero = false;
            nodoEj  = nodoEj.getSiguiente();
        }
        ejecucion.append("]");

        int bri = 0;
        int seg = 0;
        int han = 0;
        Nodo<Tecnico> nodoTec = p.getTecnicos().getCabeza();
        while (nodoTec != null) {
            Tecnico t = nodoTec.getDato();
            if (t.getEstado() == EstadoTecnico.DISPONIBLE) {
                if (t.getEspecialidad() == Especialidad.BRIGADISTA) bri++;
                if (t.getEspecialidad() == Especialidad.SEGURIDAD_RUTA) seg++;
                if (t.getEspecialidad() == Especialidad.HANDYMAN) han++;
            }
            nodoTec = nodoTec.getSiguiente();
        }

        int vGrua = 0, vMoto = 0, vCam = 0, vLiv = 0;
        Nodo<UnidadServicio> nodoUni = p.getUnidades().getCabeza();
        while (nodoUni != null) {
            UnidadServicio u = nodoUni.getDato();
            if (u.getEstado() == EstadoUnidad.DISPONIBLE) {
                if (u.getTipo() == TipoUnidad.GRUA) vGrua++;
                if (u.getTipo() == TipoUnidad.MOTO) vMoto++;
                if (u.getTipo() == TipoUnidad.CAMIONETA_ASISTENCIA) vCam++;
                if (u.getTipo() == TipoUnidad.VEHICULO_LIVIANO) vLiv++;
            }
            nodoUni = nodoUni.getSiguiente();
        }

        StringBuilder cola = new StringBuilder("[");
        Nodo<SolicitudServicio> nodoCola = p.getSolicitudesPendientes().getCabeza();
        boolean primeroCola = true;
        while (nodoCola != null) {
            if (!primeroCola) cola.append(",");
            String item = nodoCola.getDato().getCliente().getNombre() + " (" + nodoCola.getDato().getTipoEmergencia().name() + ")";
            cola.append("\"").append(esc(item)).append("\"");
            primeroCola = false;
            nodoCola = nodoCola.getSiguiente();
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
            nodoKit = nodoKit.getSiguiente();
        }
        pila.append("]");

        StringBuilder tecLista = new StringBuilder("[");
        Nodo<Tecnico> nt = p.getTecnicos().getCabeza();
        boolean pt = true;
        while (nt != null) {
            if (!pt) tecLista.append(",");
            Tecnico t = nt.getDato();
            tecLista.append("{").append("\"id\":\"").append(esc(t.getId())).append("\",").append("\"nombre\":\"").append(esc(t.getNombre())).append("\",").append("\"especialidad\":\"").append(t.getEspecialidad().name()).append("\",").append("\"estado\":\"").append(t.getEstado().name()).append("\"").append("}");
            pt = false;
            nt = nt.getSiguiente();
        }
        tecLista.append("]");

        StringBuilder vehLista = new StringBuilder("[");
        Nodo<UnidadServicio> nv = p.getUnidades().getCabeza();
        boolean pv = true;
        while (nv != null) {
            if (!pv) vehLista.append(",");
            UnidadServicio u = nv.getDato();
            vehLista.append("{").append("\"id\":\"").append(esc(u.getId())).append("\",").append("\"codigo\":\"").append(esc(u.getCodigo())).append("\",").append("\"tipo\":\"").append(u.getTipo().name()).append("\",").append("\"estado\":\"").append(u.getEstado().name()).append("\"").append("}");
            pv = false;
            nv = nv.getSiguiente();
        }
        vehLista.append("]");

        return "{\"kits\":" + p.getContadorKits()
             + ",\"ejecucion\":"    + ejecucion
             + ",\"personal\":{\"bri\":" + bri + ",\"seg\":" + seg + ",\"han\":" + han + "}"
             + ",\"vehiculos\":{\"grua\":" + vGrua + ",\"moto\":" + vMoto + ",\"cam\":"  + vCam  + ",\"liv\":"  + vLiv  + "}"
             + ",\"cola\":" + cola
             + ",\"pila\":" + pila
             + ",\"tecnicosLista\":" + tecLista
             + ",\"vehiculosLista\":" + vehLista
             + "}";
    }


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