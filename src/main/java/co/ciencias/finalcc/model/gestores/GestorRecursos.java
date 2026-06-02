package co.ciencias.finalcc.model.gestores;

import co.ciencias.finalcc.model.PuestoAtencion;
import co.ciencias.finalcc.model.enums.EstadoTecnico;
import co.ciencias.finalcc.model.enums.EstadoUnidad;
import co.ciencias.finalcc.model.Tecnico;
import co.ciencias.finalcc.model.UnidadServicio;


public class GestorRecursos {

    private static GestorRecursos instancia;
    private final PuestoAtencion[] puestos;

    private GestorRecursos() {
        puestos = new PuestoAtencion[2];
        puestos[0] = new PuestoAtencion(0); 
        puestos[1] = new PuestoAtencion(1);
    }

    public static GestorRecursos getInstancia() {
        if (instancia == null) instancia = new GestorRecursos();
        return instancia;
    }

    public PuestoAtencion[] getPuestos() { return puestos; }


    public PuestoAtencion getPuesto(int indice) {
        if (indice == 0 || indice == 1) return puestos[indice];
        return null;
    }

    public boolean cambiarEstadoUnidad(UnidadServicio unidad, EstadoUnidad nuevoEstado) {
        if (unidad == null) return false;
        if (unidad.getEstado() == EstadoUnidad.EN_MANTENIMIENTO && nuevoEstado == EstadoUnidad.ASIGNADO) return false;
        unidad.setEstado(nuevoEstado);
        return true;
    }

    public boolean cambiarEstadoTecnico(Tecnico tecnico, EstadoTecnico nuevoEstado) {
        if (tecnico == null) return false;
        tecnico.setEstado(nuevoEstado);
        return true;
    }
}