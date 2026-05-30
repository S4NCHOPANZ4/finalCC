package co.ciencias.finalcc2.model.gestores;

import co.ciencias.finalcc2.model.Operacion;
import co.ciencias.finalcc2.model.Pila;

public class GestorOperaciones {
    private static GestorOperaciones instancia;
    private final Pila<Operacion> pilaDeshacer;

    private GestorOperaciones() {
        this.pilaDeshacer = new Pila<>();
    }

    public static GestorOperaciones getInstancia() {
        if (instancia == null) instancia = new GestorOperaciones();
        return instancia;
    }

    public void registrarOperacion(String desc, String idAfectado, String estadoAnterior) {
        pilaDeshacer.push(new Operacion(desc, idAfectado, estadoAnterior));
    }

    public Operacion removerUltimaOperacion() {
        return pilaDeshacer.pop();
    }

    public Pila<Operacion> getPilaDeshacer() { 
        return pilaDeshacer; 
    }
}