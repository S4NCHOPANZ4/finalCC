package co.ciencias.finalcc.view;

import co.ciencias.finalcc.controller.GuiController;

public class JSBridge {

    private final GuiController controller;

    public JSBridge(GuiController controller) {
        this.controller = controller;
    }

    // ── Solicitudes ────────────────────────────────────────────────────

    public void registrarSolicitud(String nombre, String telefono,
                                   String tipo, String indiceNodo,
                                   String descripcion) {
        try { controller.registrarSolicitud(nombre, telefono, tipo, indiceNodo, descripcion); }
        catch (Exception e) {}
    }

    // ── Acciones de puesto ─────────────────────────────────────────────

    public void accionAtender(String puestoId) {
        try { controller.accionAtender(puestoId); }
        catch (Exception e) {}
    }

    public void accionTerminar(String puestoId) {
        try { controller.accionTerminar(puestoId); }
        catch (Exception e) {}
    }

    public void accionReparar(String puestoId) {
        try { controller.accionReparar(puestoId); }
        catch (Exception e) {}
    }

    public void accionRevertir(String puestoId) {
        try { controller.accionRevertir(puestoId); }
        catch (Exception e) {}
    }

    // ── Cierre de día ──────────────────────────────────────────────────

    public void terminarDiaNorte() {
        try { controller.terminarDiaNorte(); }
        catch (Exception e) {}
    }

    public void terminarDiaSur() {
        try { controller.terminarDiaSur(); }
        catch (Exception e) {}
    }

    // ── CRUD Técnicos (RF-07) ──────────────────────────────────────────

    /**
     * Agrega un nuevo técnico al puesto indicado.
     *
     * @param puestoId    "norte" | "sur"
     * @param nombre      nombre/código del técnico
     * @param especialidad "BRIGADISTA" | "SEGURIDAD_RUTA" | "HANDYMAN"
     */
    public void agregarTecnico(String puestoId, String nombre, String especialidad) {
        try { controller.agregarTecnico(puestoId, nombre, especialidad); }
        catch (Exception e) {}
    }

    /**
     * Edita nombre y/o especialidad de un técnico existente.
     * Si el técnico está OCUPADA la operación se ignora silenciosamente.
     *
     * @param puestoId     "norte" | "sur"
     * @param id           UUID completo del técnico
     * @param nuevoNombre  nuevo nombre (vacío = sin cambio)
     * @param nuevaEsp     nueva especialidad (vacío = sin cambio)
     */
    public void editarTecnico(String puestoId, String id,
                               String nuevoNombre, String nuevaEsp) {
        try { controller.editarTecnico(puestoId, id, nuevoNombre, nuevaEsp); }
        catch (Exception e) {}
    }

    // ── CRUD Vehículos (RF-08) ─────────────────────────────────────────

    /**
     * Agrega una nueva unidad de servicio al puesto indicado.
     *
     * @param puestoId "norte" | "sur"
     * @param tipo     "GRUA" | "MOTO" | "CAMIONETA_ASISTENCIA" | "VEHICULO_LIVIANO"
     * @param codigo   código identificador (ej. "N-GRU-04")
     */
    public void agregarVehiculo(String puestoId, String tipo, String codigo) {
        try { controller.agregarVehiculo(puestoId, tipo, codigo); }
        catch (Exception e) {}
    }

    /**
     * Edita código y/o tipo de una unidad existente.
     * Si la unidad está ASIGNADO la operación se ignora silenciosamente.
     *
     * @param puestoId  "norte" | "sur"
     * @param id        UUID completo de la unidad
     * @param nuevoCod  nuevo código (vacío = sin cambio)
     * @param nuevoTipo nuevo tipo (vacío = sin cambio)
     */
    public void editarVehiculo(String puestoId, String id,
                                String nuevoCod, String nuevoTipo) {
        try { controller.editarVehiculo(puestoId, id, nuevoCod, nuevoTipo); }
        catch (Exception e) {}
    }
}