package co.ciencias.finalcc.model.gestores;

import co.ciencias.finalcc.model.enums.EstadoSolicitud;
import co.ciencias.finalcc.model.enums.TipoEmergencia;
import co.ciencias.finalcc.model.GrafoCiudad;
import co.ciencias.finalcc.model.PuestoAtencion;
import co.ciencias.finalcc.model.SolicitudServicio;
import co.ciencias.finalcc.model.ListaEnlazada;
import co.ciencias.finalcc.model.Cliente;

/**
 * Gestor de solicitudes de servicio.
 *
 * <p>Usa {@link GrafoCiudad} para determinar el puesto más cercano al nodo
 * del cliente mediante BFS, eliminando por completo la dependencia de
 * {@code ZonaCalculadora} y la conversión ángulo/círculo → coordenada.</p>
 *
 * <p>Solo existen 2 puestos: Norte (0) y Sur (1).</p>
 */
public class GestorSolicitudes {

    private static GestorSolicitudes instancia;
    private final ListaEnlazada<SolicitudServicio> historicoGlobal;

    private GestorSolicitudes() {
        this.historicoGlobal = new ListaEnlazada<>();
    }

    public static GestorSolicitudes getInstancia() {
        if (instancia == null) instancia = new GestorSolicitudes();
        return instancia;
    }

    /**
     * Crea una solicitud y la encola en el puesto más cercano al nodo del cliente.
     *
     * @param cliente          cliente que reporta la emergencia
     * @param tipo             tipo de emergencia
     * @param descripcion      descripción del incidente
     * @param indiceNodoCliente índice del nodo del grafo donde está el cliente (0–9)
     * @return la solicitud creada, o {@code null} si los parámetros son inválidos
     */
    public SolicitudServicio crearSolicitud(Cliente cliente,
                                            TipoEmergencia tipo,
                                            String descripcion,
                                            int indiceNodoCliente) {
        if (cliente == null) return null;

        // BFS determina el puesto (0 = Norte, 1 = Sur)
        int indicePuesto = GrafoCiudad.getInstancia().calcularPuesto(indiceNodoCliente);

        SolicitudServicio solicitud = new SolicitudServicio(
                cliente, tipo, descripcion, indiceNodoCliente, indicePuesto);

        PuestoAtencion puesto = GestorRecursos.getInstancia().getPuesto(indicePuesto);
        puesto.encolarSolicitud(solicitud);

        System.out.printf("[GestorSolicitudes] Solicitud creada → cliente=%s | nodo=%s | puesto=%s | emergencia=%s%n",
                cliente.getNombre(),
                GrafoCiudad.getInstancia().nombreNodo(indiceNodoCliente),
                GrafoCiudad.NOMBRES_PUESTOS[indicePuesto],
                tipo);

        historicoGlobal.agregar(solicitud);
        return solicitud;
    }

    public void finalizarSolicitud(SolicitudServicio solicitud) {
        if (solicitud == null || solicitud.getEstado() != EstadoSolicitud.EN_PROCESO) return;
        solicitud.setEstado(EstadoSolicitud.FINALIZADA);
    }

    public ListaEnlazada<SolicitudServicio> getHistoricoGlobal() {
        return historicoGlobal;
    }
}