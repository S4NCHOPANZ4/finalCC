package co.ciencias.finalcc.view;

import co.ciencias.finalcc.controller.GuiController;

public class JSBridge {

    private final GuiController controller;

    public JSBridge(GuiController controller) {
        this.controller = controller;
    }


    public void registrarSolicitud(String nombre, String telefono,String tipo, String indiceNodo,String descripcion) {
        try { controller.registrarSolicitud(nombre, telefono, tipo, indiceNodo, descripcion); }
        catch (Exception e) {}
    }


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


    public void terminarDiaNorte() {
        try { controller.terminarDiaNorte(); }
        catch (Exception e) {}
    }

    public void terminarDiaSur() {
        try { controller.terminarDiaSur(); }
        catch (Exception e) {}
    }


    public void agregarTecnico(String puestoId, String nombre, String especialidad) {
        try { controller.agregarTecnico(puestoId, nombre, especialidad); }
        catch (Exception e) {}
    }


    public void editarTecnico(String puestoId, String id,
                               String nuevoNombre, String nuevaEsp) {
        try { controller.editarTecnico(puestoId, id, nuevoNombre, nuevaEsp); }
        catch (Exception e) {}
    }


    public void agregarVehiculo(String puestoId, String tipo, String codigo) {
        try { controller.agregarVehiculo(puestoId, tipo, codigo); }
        catch (Exception e) {}
    }


    public void editarVehiculo(String puestoId, String id,
                                String nuevoCod, String nuevoTipo) {
        try { controller.editarVehiculo(puestoId, id, nuevoCod, nuevoTipo); }
        catch (Exception e) {}
    }

    public void eliminarTecnico(String puestoId, String id) {
        try { controller.eliminarTecnico(puestoId, id); }
        catch (Exception e) {}
    }

    public void eliminarVehiculo(String puestoId, String id) {
        try { controller.eliminarVehiculo(puestoId, id); }
        catch (Exception e) {}
    }
}