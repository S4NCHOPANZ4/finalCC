package co.ciencias.finalcc2.model;

import co.ciencias.finalcc2.model.enums.Especialidad;
import static co.ciencias.finalcc2.model.enums.Especialidad.BRIGADISTA;
import static co.ciencias.finalcc2.model.enums.Especialidad.HANDYMAN;
import static co.ciencias.finalcc2.model.enums.Especialidad.SEGURIDAD_RUTA;
import co.ciencias.finalcc2.model.enums.EstadoTecnico;
import co.ciencias.finalcc2.model.enums.EstadoUnidad;
import co.ciencias.finalcc2.model.enums.TipoUnidad;

public class PuestoAtencion {

    public static final int ESTE  = 0;
    public static final int NORTE = 1;
    public static final int OESTE = 2;
    public static final int SUR   = 3;
    private final int indice;
    private final String nombre;
    private final ColaPrioridad solicitudesPendientes;
    private final ListaEnlazada<UnidadServicio> unidades;
    private final ListaEnlazada<Tecnico> tecnicos;

    public PuestoAtencion(int indice) {
        this.indice = indice;
        this.nombre = ZonaCalculadora.NOMBRES[indice];
        this.solicitudesPendientes = new ColaPrioridad();
        this.unidades = new ListaEnlazada<>();
        this.tecnicos = new ListaEnlazada<>();
        inicializarRecursos();
    }


    /**
     * Encola una solicitud en la cola de prioridad del puesto.
     *
     * @param solicitud Solicitud a encolar.
     */
    public void encolarSolicitud(SolicitudServicio solicitud) {
        solicitudesPendientes.insertar(solicitud);
    }
    public SolicitudServicio extraerSiguiente() {
        return solicitudesPendientes.extraer();
    }

    public SolicitudServicio verSiguiente() {
        return solicitudesPendientes.verFrente();
    }

    public UnidadServicio buscarUnidad(TipoUnidad tipo) {
        Nodo<UnidadServicio> actual = unidades.getCabeza();
        while (actual != null) {
            UnidadServicio u = actual.getDato();
            if (u.getTipo() == tipo && u.getEstado() == EstadoUnidad.DISPONIBLE) {
                return u;
            }
            actual = actual.getSiguiente();
        }
        return null;
    }

    public Tecnico buscarTecnico(Especialidad esp) {
        Nodo<Tecnico> actual = tecnicos.getCabeza();
        while (actual != null) {
            Tecnico t = actual.getDato();
            if (t.getEspecialidad() == esp && t.getEstado() == EstadoTecnico.DISPONIBLE) {
                return t;
            }
            actual = actual.getSiguiente();
        }
        return null;
    }

    public int getIndice() { return indice; }
    public String getNombre() { return nombre; }
    public ColaPrioridad getSolicitudesPendientes() { return solicitudesPendientes; }
    public ListaEnlazada<UnidadServicio> getUnidades() { return unidades; }
    public ListaEnlazada<Tecnico> getTecnicos() { return tecnicos; }


    private void inicializarRecursos() {
        String p = nombre.substring(0, 1);  
        for (int i = 1; i <= 3; i++)
            unidades.agregar(new UnidadServicio(
                    TipoUnidad.GRUA,
                    EstadoUnidad.DISPONIBLE, indice, p + "-GRU-" + String.format("%02d", i)));
        for (int i = 1; i <= 5; i++)
            unidades.agregar(new UnidadServicio(
                   TipoUnidad.MOTO,
                    EstadoUnidad.DISPONIBLE, indice, p + "-MOT-" + String.format("%02d", i)));
        for (int i = 1; i <= 3; i++)
            unidades.agregar(new UnidadServicio(
                    TipoUnidad.CAMIONETA_ASISTENCIA,
                    EstadoUnidad.DISPONIBLE, indice, p + "-CAM-" + String.format("%02d", i)));
        for (int i = 1; i <= 3; i++)
            unidades.agregar(new UnidadServicio(
                   TipoUnidad.VEHICULO_LIVIANO,
                    EstadoUnidad.DISPONIBLE, indice, p + "-VLI-" + String.format("%02d", i)));
        for (int i = 1; i <= 3; i++)
            tecnicos.agregar(new Tecnico(
                    p + "-BRI-" + String.format("%02d", i),
                    BRIGADISTA,
                    EstadoTecnico.DISPONIBLE, indice));
        for (int i = 1; i <= 4; i++)
            tecnicos.agregar(new Tecnico(
                    p + "-SEG-" + String.format("%02d", i),
                    SEGURIDAD_RUTA,
                    EstadoTecnico.DISPONIBLE, indice));
        for (int i = 1; i <= 7; i++)
            tecnicos.agregar(new Tecnico(
                    p + "-HAN-" + String.format("%02d", i),
                    HANDYMAN,
                    EstadoTecnico.DISPONIBLE, indice));
    }

    @Override
    public String toString() {
        return "Puesto " + nombre + " | Pendientes: "
                + solicitudesPendientes.getTamanio()
                + " | Unidades: " + unidades.getTamanio()
                + " | Técnicos: " + tecnicos.getTamanio();
    }
}