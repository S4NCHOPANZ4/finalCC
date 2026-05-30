package co.ciencias.finalcc.model.gestores;

import co.ciencias.finalcc.model.ListaEnlazada;
import co.ciencias.finalcc.model.Nodo;
import co.ciencias.finalcc.model.Tecnico;
import co.ciencias.finalcc.model.UnidadServicio;
import co.ciencias.finalcc.model.enums.EstadoTecnico;
import co.ciencias.finalcc.model.enums.EstadoUnidad;


public class GestorRecursos {

    private static GestorRecursos instancia;
    private ListaEnlazada<UnidadServicio> todasUnidades;
    private ListaEnlazada<Tecnico> todosTecnicos;

    private GestorRecursos() {
        todasUnidades = new ListaEnlazada<>();
        todosTecnicos = new ListaEnlazada<>();
    }


    public static GestorRecursos getInstance() {
        if (instancia == null) {
            instancia = new GestorRecursos();
        }
        return instancia;
    }

    public void registrarUnidad(UnidadServicio unidad) {
        todasUnidades.agregar(unidad);
    }


    public void registrarTecnico(Tecnico tecnico) {
        todosTecnicos.agregar(tecnico);
    }

    public boolean asignarUnidad(UnidadServicio unidad) {
        // Regla 6: no asignar si está en mantenimiento
        if (unidad.getEstado() == EstadoUnidad.EN_MANTENIMIENTO) {
            throw new IllegalStateException(
                "Regla 6: La unidad " + unidad.getCodigo() + " está en mantenimiento.");
        }
        // Regla 4: no puede estar disponible y asignada
        if (unidad.getEstado() == EstadoUnidad.ASIGNADO) {
            throw new IllegalStateException(
                "Regla 4: La unidad " + unidad.getCodigo() + " ya está asignada.");
        }
        if (unidad.getEstado() != EstadoUnidad.DISPONIBLE) {
            return false;
        }
        unidad.setEstado(EstadoUnidad.ASIGNADO);
        return true;
    }


    public void liberarUnidad(UnidadServicio unidad) {
        unidad.setEstado(EstadoUnidad.DISPONIBLE);
    }


    public boolean asignarTecnico(Tecnico tecnico) {
        // Regla 5: no puede atender dos servicios a la vez
        if (tecnico.getEstado() == EstadoTecnico.ASIGNADO) {
            throw new IllegalStateException(
                "Regla 5: El técnico " + tecnico.getNombre() + " ya está asignado.");
        }
        if (tecnico.getEstado() != EstadoTecnico.DISPONIBLE) {
            return false;
        }
        tecnico.setEstado(EstadoTecnico.ASIGNADO);
        return true;
    }


    public void liberarTecnico(Tecnico tecnico) {
        tecnico.setEstado(EstadoTecnico.DISPONIBLE);
    }


    public void enviarAMantenimiento(UnidadServicio unidad) {
        if (unidad.getEstado() == EstadoUnidad.ASIGNADO) {
            throw new IllegalStateException(
                "No se puede enviar a mantenimiento: la unidad está asignada a un servicio.");
        }
        unidad.setEstado(EstadoUnidad.EN_MANTENIMIENTO);
    }

    public UnidadServicio buscarUnidad(String id) {
        Nodo<UnidadServicio> actual = todasUnidades.getCabeza();
        while (actual != null) {
            if (actual.getDato().getId().equals(id)) return actual.getDato();
            actual = actual.getSiguiente();
        }
        return null;
    }


    public Tecnico buscarTecnico(String id) {
        Nodo<Tecnico> actual = todosTecnicos.getCabeza();
        while (actual != null) {
            if (actual.getDato().getId().equals(id)) return actual.getDato();
            actual = actual.getSiguiente();
        }
        return null;
    }

    public ListaEnlazada<UnidadServicio> getTodasUnidades() { return todasUnidades; }

    public ListaEnlazada<Tecnico> getTodosTecnicos() { return todosTecnicos; }
}
