package co.ciencias.finalcc.controller;

import co.ciencias.finalcc.model.ListaEnlazada;
import co.ciencias.finalcc.model.Operacion;
import co.ciencias.finalcc.model.Tecnico;
import co.ciencias.finalcc.model.UnidadServicio;
import co.ciencias.finalcc.model.enums.EstadoUnidad;
import co.ciencias.finalcc.model.gestores.GestorOperaciones;
import co.ciencias.finalcc.model.gestores.GestorRecursos;


public class RecursosController {

    private GestorRecursos gestorRecursos;
    private GestorOperaciones gestorOperaciones;
    public RecursosController() {
        this.gestorRecursos = GestorRecursos.getInstance();
        this.gestorOperaciones = GestorOperaciones.getInstance();
    }
    public String enviarAMantenimiento(String idUnidad) {
        UnidadServicio u = gestorRecursos.buscarUnidad(idUnidad);
        if (u == null) return "ERROR: Unidad no encontrada.";
        try {
            String estadoAnterior = u.getEstado().name();
            gestorRecursos.enviarAMantenimiento(u);
            gestorOperaciones.registrar(
                "Enviar a mantenimiento: " + u.getCodigo(), idUnidad, estadoAnterior);
            return "Unidad " + u.getCodigo() + " enviada a mantenimiento.";
        } catch (IllegalStateException e) {
            return "ERROR: " + e.getMessage();
        }
    }

    public String liberarUnidad(String idUnidad) {
        UnidadServicio u = gestorRecursos.buscarUnidad(idUnidad);
        if (u == null) return "ERROR: Unidad no encontrada.";
        String estadoAnterior = u.getEstado().name();
        gestorRecursos.liberarUnidad(u);
        gestorOperaciones.registrar(
            "Liberar unidad: " + u.getCodigo(), idUnidad, estadoAnterior);
        return "Unidad " + u.getCodigo() + " liberada (DISPONIBLE).";
    }

    public String liberarTecnico(String idTecnico) {
        Tecnico t = gestorRecursos.buscarTecnico(idTecnico);
        if (t == null) return "ERROR: Técnico no encontrado.";
        String estadoAnterior = t.getEstado().name();
        gestorRecursos.liberarTecnico(t);
        gestorOperaciones.registrar(
            "Liberar técnico: " + t.getNombre(), idTecnico, estadoAnterior);
        return "Técnico " + t.getNombre() + " liberado (DISPONIBLE).";
    }

    public ListaEnlazada<UnidadServicio> getTodasUnidades() {
        return gestorRecursos.getTodasUnidades();
    }
    public ListaEnlazada<Tecnico> getTodosTecnicos() {
        return gestorRecursos.getTodosTecnicos();
    }


    public Operacion deshacer() {
        return gestorOperaciones.deshacer();
    }
}
