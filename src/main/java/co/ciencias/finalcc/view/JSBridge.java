package co.ciencias.finalcc.view;

import co.ciencias.finalcc.controller.GuiController;

public class JSBridge {

    private final GuiController controller;

    public JSBridge(GuiController controller) {
        this.controller = controller;
    }

    public void registrarSolicitud(String nombre, String telefono,
                                   String tipo, String indiceNodo,
                                   String descripcion) {
        try {
            controller.registrarSolicitud(nombre, telefono, tipo, indiceNodo, descripcion);
        } catch (Exception e) {}
    }

    public void accionAtender(String puestoId) {
        try {
            controller.accionAtender(puestoId);
        } catch (Exception e) {}
    }

    public void accionTerminar(String puestoId) {
        try {
            controller.accionTerminar(puestoId);
        } catch (Exception e) {}
    }

    public void accionReparar(String puestoId) {
        try {
            controller.accionReparar(puestoId);
        } catch (Exception e) {}
    }

    public void terminarDiaNorte() {
        try {
            controller.terminarDiaNorte();
        } catch (Exception e) {}
    }

    public void terminarDiaSur() {
        try {
            controller.terminarDiaSur();
        } catch (Exception e) {}
    }
}