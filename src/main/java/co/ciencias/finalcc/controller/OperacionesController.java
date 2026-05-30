package co.ciencias.finalcc.controller;

import co.ciencias.finalcc.model.Operacion;
import co.ciencias.finalcc.model.gestores.GestorOperaciones;


public class OperacionesController {

    private GestorOperaciones gestorOperaciones;
    public OperacionesController() {
        this.gestorOperaciones = GestorOperaciones.getInstance();
    }

    public String deshacer() {
        Operacion op = gestorOperaciones.deshacer();
        if (op == null) return "No hay operaciones para deshacer.";
        return "Deshecho: " + op.getDescripcion()
            + " → estado restaurado a " + op.getEstadoAnterior();
    }

    public Operacion verUltimaOperacion() {
        return gestorOperaciones.verUltima();
    }


    public boolean hayParaDeshacer() {
        return gestorOperaciones.hayOperaciones();
    }
}
