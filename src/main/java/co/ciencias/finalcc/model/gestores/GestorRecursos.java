package co.ciencias.finalcc.model.gestores;

import co.ciencias.finalcc.model.PuestoAtencion;
import co.ciencias.finalcc.model.enums.EstadoTecnico;
import co.ciencias.finalcc.model.enums.EstadoUnidad;
import co.ciencias.finalcc.model.Tecnico;
import co.ciencias.finalcc.model.UnidadServicio;

/**
 * Gestiona los dos puestos de atención: Norte (índice 0) y Sur (índice 1).
 *
 * <p>Reemplaza la versión anterior de 4 puestos. El índice de puesto que
 * entrega {@link co.ciencias.finalcc.model.GrafoCiudad#calcularPuesto(int)}
 * es 0 (Norte) o 1 (Sur), y corresponde directamente con {@code puestos[0]}
 * y {@code puestos[1]}.</p>
 */
public class GestorRecursos {

    private static GestorRecursos instancia;
    private final PuestoAtencion[] puestos;

    private GestorRecursos() {
        puestos = new PuestoAtencion[2];
        puestos[0] = new PuestoAtencion(0); // Norte
        puestos[1] = new PuestoAtencion(1); // Sur
    }

    public static GestorRecursos getInstancia() {
        if (instancia == null) instancia = new GestorRecursos();
        return instancia;
    }

    public PuestoAtencion[] getPuestos() { return puestos; }

    /**
     * Retorna el puesto por índice (0 = Norte, 1 = Sur).
     * Retorna {@code null} para cualquier otro valor.
     */
    public PuestoAtencion getPuesto(int indice) {
        if (indice == 0 || indice == 1) return puestos[indice];
        return null;
    }

    public boolean cambiarEstadoUnidad(UnidadServicio unidad, EstadoUnidad nuevoEstado) {
        if (unidad == null) return false;
        if (unidad.getEstado() == EstadoUnidad.EN_MANTENIMIENTO
                && nuevoEstado == EstadoUnidad.ASIGNADO) return false;
        unidad.setEstado(nuevoEstado);
        return true;
    }

    public boolean cambiarEstadoTecnico(Tecnico tecnico, EstadoTecnico nuevoEstado) {
        if (tecnico == null) return false;
        tecnico.setEstado(nuevoEstado);
        return true;
    }
}