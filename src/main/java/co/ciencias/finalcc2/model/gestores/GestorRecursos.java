package co.ciencias.finalcc2.model.gestores;

import co.ciencias.finalcc2.model.PuestoAtencion;
import co.ciencias.finalcc2.model.enums.EstadoTecnico;
import co.ciencias.finalcc2.model.enums.EstadoUnidad;
import co.ciencias.finalcc2.model.Tecnico;
import co.ciencias.finalcc2.model.UnidadServicio;

public class GestorRecursos {
    private static GestorRecursos instancia;
    private final PuestoAtencion[] puestos;

    private GestorRecursos() {
        puestos = new PuestoAtencion[4];
        for (int i = 0; i < 4; i++) {
            puestos[i] = new PuestoAtencion(i);
        }
    }

    public static GestorRecursos getInstancia() {
        if (instancia == null) {
            instancia = new GestorRecursos();
        }
        return instancia;
    }

    public PuestoAtencion[] getPuestos() {
        return puestos;
    }

    public PuestoAtencion getPuesto(int indice) {
        if (indice >= 0 && indice < 4) {
            return puestos[indice];
        }
        return null;
    }


    public boolean cambiarEstadoUnidad(UnidadServicio unidad, EstadoUnidad nuevoEstado) {
        if (unidad == null) return false;
        if (unidad.getEstado() == EstadoUnidad.EN_MANTENIMIENTO && nuevoEstado == EstadoUnidad.ASIGNADO) {
            return false;
        }
        unidad.setEstado(nuevoEstado);
        return true;
    }


    public boolean cambiarEstadoTecnico(Tecnico tecnico, EstadoTecnico nuevoEstado) {
        if (tecnico == null) return false;
        tecnico.setEstado(nuevoEstado);
        return true;
    }
}