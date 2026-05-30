package co.ciencias.finalcc.model.gestores;

import co.ciencias.finalcc.model.ListaEnlazada;
import co.ciencias.finalcc.model.Nodo;
import co.ciencias.finalcc.model.PuestoAtencion;
import co.ciencias.finalcc.model.SolicitudServicio;
import co.ciencias.finalcc.model.Tecnico;
import co.ciencias.finalcc.model.UnidadServicio;
import co.ciencias.finalcc.model.ZonaCalculadora;
import co.ciencias.finalcc.model.enums.Especialidad;
import co.ciencias.finalcc.model.enums.EstadoSolicitud;
import co.ciencias.finalcc.model.enums.TipoEmergencia;
import co.ciencias.finalcc.model.enums.TipoUnidad;


public class GestorSolicitudes {

    private static GestorSolicitudes instancia;
    private ListaEnlazada<SolicitudServicio> historial;

    private GestorSolicitudes() {
        historial = new ListaEnlazada<>();
    }

    public static GestorSolicitudes getInstance() {
        if (instancia == null) {
            instancia = new GestorSolicitudes();
        }
        return instancia;
    }

    public PuestoAtencion registrarSolicitud(SolicitudServicio solicitud) {
        int zona = ZonaCalculadora.calcularZona(solicitud.getUbicacion());
        solicitud.setZonaPuesto(zona);

        PuestoAtencion puesto = GestorPuestos.getInstance().getPuesto(zona);
        puesto.agregarSolicitud(solicitud);
        historial.agregar(solicitud);

        intentarAsignar(solicitud, puesto);

        return puesto;
    }

    public void intentarAsignar(SolicitudServicio solicitud, PuestoAtencion puesto) {
        TipoEmergencia tipo = solicitud.getTipoEmergencia();
        GestorRecursos gr = GestorRecursos.getInstance();

        UnidadServicio unidad = buscarUnidadParaEmergencia(tipo, puesto);
        Tecnico tecnico = buscarTecnicoParaEmergencia(tipo, puesto);

        if (unidad == null || tecnico == null) {
            return;
        }

        try {
            gr.asignarUnidad(unidad);
            gr.asignarTecnico(tecnico);
        } catch (IllegalStateException e) {
            return;
        }

        solicitud.setUnidadAsignada(unidad);
        solicitud.setTecnicoAsignado(tecnico);
        solicitud.setEstado(EstadoSolicitud.EN_ATENCION);

        puesto.getSolicitudesPendientes().eliminar(solicitud);
    }

    public void cerrarSolicitud(SolicitudServicio solicitud) {
        if (solicitud.getUnidadAsignada() == null || solicitud.getTecnicoAsignado() == null) {
            throw new IllegalStateException(
                "Regla 8: La solicitud no puede cerrarse sin asignación de recursos.");
        }
        GestorRecursos gr = GestorRecursos.getInstance();
        gr.liberarUnidad(solicitud.getUnidadAsignada());
        gr.liberarTecnico(solicitud.getTecnicoAsignado());
        solicitud.setEstado(EstadoSolicitud.CERRADA);
    }

    public void procesarSiguiente(int zona) {
        PuestoAtencion puesto = GestorPuestos.getInstance().getPuesto(zona);
        SolicitudServicio sig = puesto.verSiguienteSolicitud();
        if (sig != null) {
            intentarAsignar(sig, puesto);
        }
    }

    private UnidadServicio buscarUnidadParaEmergencia(
            TipoEmergencia tipo, PuestoAtencion puesto) {
        switch (tipo) {
            case MEDICA:
                return puesto.buscarUnidadDisponible(TipoUnidad.VEHICULO_LIVIANO);
            case SEGURIDAD_PUBLICA: {
                UnidadServicio u = puesto.buscarUnidadDisponible(TipoUnidad.CAMIONETA_ASISTENCIA);
                if (u == null) u = puesto.buscarUnidadDisponible(TipoUnidad.VEHICULO_LIVIANO);
                return u;
            }
            case PROTECCION_CIVIL: {
                UnidadServicio u = puesto.buscarUnidadDisponible(TipoUnidad.GRUA);
                if (u == null) u = puesto.buscarUnidadDisponible(TipoUnidad.CAMIONETA_ASISTENCIA);
                return u;
            }
            case SERVICIOS_PUBLICOS: {
                UnidadServicio u = puesto.buscarUnidadDisponible(TipoUnidad.GRUA);
                if (u == null) u = puesto.buscarUnidadDisponible(TipoUnidad.VEHICULO_LIVIANO);
                return u;
            }
            case SERVICIOS_APOYO:
                return puesto.buscarUnidadDisponible(TipoUnidad.MOTO);
            default:
                return null;
        }
    }

    private Tecnico buscarTecnicoParaEmergencia(
            TipoEmergencia tipo, PuestoAtencion puesto) {
        switch (tipo) {
            case MEDICA:
                return puesto.buscarTecnicoDisponible(Especialidad.BRIGADISTA);
            case SEGURIDAD_PUBLICA:
                return puesto.buscarTecnicoDisponible(Especialidad.SEGURIDAD_RUTA);
            case PROTECCION_CIVIL:
            case SERVICIOS_PUBLICOS:
            case SERVICIOS_APOYO:
                return puesto.buscarTecnicoDisponible(Especialidad.HANDYMAN);
            default:
                return null;
        }
    }

    public ListaEnlazada<SolicitudServicio> getHistorial() {
        return historial;
    }

    public ListaEnlazada<SolicitudServicio> getSolicitudesCerradas() {
        ListaEnlazada<SolicitudServicio> cerradas = new ListaEnlazada<>();
        Nodo<SolicitudServicio> actual = historial.getCabeza();
        while (actual != null) {
            if (actual.getDato().getEstado() ==
                    co.ciencias.finalcc.model.enums.EstadoSolicitud.CERRADA) {
                cerradas.agregar(actual.getDato());
            }
            actual = actual.getSiguiente();
        }
        return cerradas;
    }
}
