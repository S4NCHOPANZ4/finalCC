package co.ciencias.finalcc.model.gestores;

import co.ciencias.finalcc.model.Nodo;
import co.ciencias.finalcc.model.Operacion;
import co.ciencias.finalcc.model.Pila;
import co.ciencias.finalcc.model.Tecnico;
import co.ciencias.finalcc.model.UnidadServicio;
import co.ciencias.finalcc.model.enums.EstadoTecnico;
import co.ciencias.finalcc.model.enums.EstadoUnidad;


public class GestorOperaciones {

    private static GestorOperaciones instancia;

    private Pila<Operacion> pilaDeshacer;

    private GestorOperaciones() {
        pilaDeshacer = new Pila<>();
    }

    public static GestorOperaciones getInstance() {
        if (instancia == null) {
            instancia = new GestorOperaciones();
        }
        return instancia;
    }

    public void registrar(String descripcion, String idObjeto, String estadoAnterior) {
        pilaDeshacer.push(new Operacion(descripcion, idObjeto, estadoAnterior));
    }


    public Operacion deshacer() {
        Nodo<Operacion> nodo = pilaDeshacer.pop();
        if (nodo == null) return null;

        Operacion op = nodo.getDato();
        restaurarEstado(op);
        return op;
    }

    public Operacion verUltima() {
        Nodo<Operacion> nodo = pilaDeshacer.ver();
        return nodo != null ? nodo.getDato() : null;
    }


    public boolean hayOperaciones() {
        return !pilaDeshacer.esVacia();
    }


    private void restaurarEstado(Operacion op) {
        GestorRecursos gr = GestorRecursos.getInstance();

        UnidadServicio unidad = gr.buscarUnidad(op.getIdObjetoAfectado());
        if (unidad != null) {
            try {
                EstadoUnidad estadoAnterior = EstadoUnidad.valueOf(op.getEstadoAnterior());
                unidad.setEstado(estadoAnterior);
            } catch (IllegalArgumentException ignored) {
            }
            return;
        }

        Tecnico tecnico = gr.buscarTecnico(op.getIdObjetoAfectado());
        if (tecnico != null) {
            try {
                EstadoTecnico estadoAnterior = EstadoTecnico.valueOf(op.getEstadoAnterior());
                tecnico.setEstado(estadoAnterior);
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    public Pila<Operacion> getPilaDeshacer() { return pilaDeshacer; }
}
