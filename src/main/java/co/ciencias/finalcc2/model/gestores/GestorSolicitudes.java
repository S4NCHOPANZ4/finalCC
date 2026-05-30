package co.ciencias.finalcc2.model.gestores;

import co.ciencias.finalcc2.model.*;
import co.ciencias.finalcc2.model.enums.*;


public class GestorSolicitudes {
    private static GestorSolicitudes instancia;
    private final ListaEnlazada<SolicitudServicio> historicoGlobal;

    private GestorSolicitudes() {
        this.historicoGlobal = new ListaEnlazada<>();
    }

    public static GestorSolicitudes getInstancia() {
        if (instancia == null) {
            instancia = new GestorSolicitudes();
        }
        return instancia;
    }


    public SolicitudServicio crearSolicitud(Cliente cliente, TipoEmergencia tipo, String descripcion, PuntoVia ubicacion) {
        if (cliente == null || ubicacion == null) return null; 
        int zonaPuesto = ZonaCalculadora.getInstancia().calcularPuesto(ubicacion);
        SolicitudServicio nuevaSolicitud = new SolicitudServicio(cliente, tipo, descripcion, ubicacion, zonaPuesto);
        GestorRecursos.getInstancia().getPuesto(zonaPuesto).encolarSolicitud(nuevaSolicitud);
        historicoGlobal.agregar(nuevaSolicitud);
        
        return nuevaSolicitud;
    }


    public boolean despacharSiguiente(int indicePuesto) {
        PuestoAtencion puesto = GestorRecursos.getInstancia().getPuesto(indicePuesto);
        if (puesto == null) return false;

        SolicitudServicio solicitud = puesto.verSiguiente();
        if (solicitud == null) return false;

        TipoUnidad unidadRequerida = null;
        Especialidad especialidadRequerida = null;

        switch (solicitud.getTipoEmergencia()) {
            case MEDICA:
                unidadRequerida = TipoUnidad.VEHICULO_LIVIANO;
                especialidadRequerida = Especialidad.BRIGADISTA;
                break;
            case SEGURIDAD_PUBLICA:
                unidadRequerida = TipoUnidad.CAMIONETA_ASISTENCIA;
                especialidadRequerida = Especialidad.SEGURIDAD_RUTA;
                break;
            case PROTECCION_CIVIL:
            case SERVICIOS_PUBLICOS:
                unidadRequerida = TipoUnidad.GRUA;
                especialidadRequerida = Especialidad.HANDYMAN;
                break;
            case SERVICIOS_DE_APOYO:
                unidadRequerida = TipoUnidad.MOTO;
                especialidadRequerida = Especialidad.HANDYMAN;
                break;
        }

        UnidadServicio unidad = puesto.buscarUnidad(unidadRequerida);
        Tecnico tecnico = puesto.buscarTecnico(especialidadRequerida);

        if (unidad == null || tecnico == null) {
            return false;
        }

        puesto.extraerSiguiente();
        solicitud.setUnidadAsignada(unidad);
        solicitud.setTecnicoAsignado(tecnico);
        solicitud.setEstado(EstadoSolicitud.EN_ATENCION);

        unidad.setEstado(EstadoUnidad.ASIGNADO);
        tecnico.setEstado(EstadoTecnico.ASIGNADO);

        GestorOperaciones.getInstancia().registrarOperacion("Despacho de Auxilio", solicitud.getId(), "PENDIENTE");

        return true;
    }

    public void finalizarSolicitud(SolicitudServicio solicitud) {
        if (solicitud == null || solicitud.getEstado() != EstadoSolicitud.EN_ATENCION) return;

        solicitud.setEstado(EstadoSolicitud.CERRADA);
        
        if (solicitud.getUnidadAsignada() != null) {
            solicitud.getUnidadAsignada().setEstado(EstadoUnidad.DISPONIBLE);
        }
        if (solicitud.getTecnicoAsignado() != null) {
            solicitud.getTecnicoAsignado().setEstado(EstadoTecnico.DISPONIBLE);
        }
    }

    public ListaEnlazada<SolicitudServicio> getHistoricoGlobal() {
        return historicoGlobal;
    }
}