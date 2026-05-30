package co.ciencias.finalcc.model;

import co.ciencias.finalcc.model.enums.Especialidad;
import co.ciencias.finalcc.model.enums.EstadoTecnico;
import co.ciencias.finalcc.model.enums.EstadoUnidad;
import co.ciencias.finalcc.model.enums.TipoUnidad;


public class PuestoAtencion {

    private int id;
    private String nombre;
    private double anguloRadianes;
    private double x;
    private double y;
    private ColaPrioridad<SolicitudServicio> solicitudesPendientes;
    private ListaEnlazada<UnidadServicio> unidades;
    private ListaEnlazada<Tecnico> tecnicos;


    public PuestoAtencion(int id, String nombre, double anguloRadianes, double x, double y) {
        this.id = id;
        this.nombre = nombre;
        this.anguloRadianes = anguloRadianes;
        this.x = x;
        this.y = y;
        this.solicitudesPendientes = new ColaPrioridad<>();
        this.unidades = new ListaEnlazada<>();
        this.tecnicos = new ListaEnlazada<>();
        inicializarRecursos();
    }


    private void inicializarRecursos() {
        String prefijo = nombre.substring(0, 2).toUpperCase();

        for (int i = 1; i <= 3; i++) {
            unidades.agregar(new UnidadServicio(
                TipoUnidad.GRUA, EstadoUnidad.DISPONIBLE, id,
                prefijo + "-GRU-" + String.format("%02d", i)));
        }
        for (int i = 1; i <= 5; i++) {
            unidades.agregar(new UnidadServicio(
                TipoUnidad.MOTO, EstadoUnidad.DISPONIBLE, id,
                prefijo + "-MOT-" + String.format("%02d", i)));
        }
        for (int i = 1; i <= 3; i++) {
            unidades.agregar(new UnidadServicio(
                TipoUnidad.CAMIONETA_ASISTENCIA, EstadoUnidad.DISPONIBLE, id,
                prefijo + "-CAM-" + String.format("%02d", i)));
        }
        for (int i = 1; i <= 3; i++) {
            unidades.agregar(new UnidadServicio(
                TipoUnidad.VEHICULO_LIVIANO, EstadoUnidad.DISPONIBLE, id,
                prefijo + "-VLV-" + String.format("%02d", i)));
        }

        for (int i = 1; i <= 3; i++) {
            tecnicos.agregar(new Tecnico(
                prefijo + "-Brigadista-" + i,
                Especialidad.BRIGADISTA, EstadoTecnico.DISPONIBLE, id));
        }
        for (int i = 1; i <= 4; i++) {
            tecnicos.agregar(new Tecnico(
                prefijo + "-SegRuta-" + i,
                Especialidad.SEGURIDAD_RUTA, EstadoTecnico.DISPONIBLE, id));
        }
        for (int i = 1; i <= 7; i++) {
            tecnicos.agregar(new Tecnico(
                prefijo + "-Handyman-" + i,
                Especialidad.HANDYMAN, EstadoTecnico.DISPONIBLE, id));
        }
    }


    public void agregarSolicitud(SolicitudServicio solicitud) {
        solicitudesPendientes.insertar(solicitud);
    }


    public SolicitudServicio extraerSiguienteSolicitud() {
        return solicitudesPendientes.extraerMinimo();
    }


    public SolicitudServicio verSiguienteSolicitud() {
        return solicitudesPendientes.verMinimo();
    }


    public UnidadServicio buscarUnidadDisponible(
            co.ciencias.finalcc.model.enums.TipoUnidad tipo) {
        Nodo<UnidadServicio> actual = unidades.getCabeza();
        while (actual != null) {
            UnidadServicio u = actual.getDato();
            if (u.getTipo() == tipo
                    && u.getEstado() == EstadoUnidad.DISPONIBLE) {
                return u;
            }
            actual = actual.getSiguiente();
        }
        return null;
    }


    public Tecnico buscarTecnicoDisponible(Especialidad especialidad) {
        Nodo<Tecnico> actual = tecnicos.getCabeza();
        while (actual != null) {
            Tecnico t = actual.getDato();
            if (t.getEspecialidad() == especialidad
                    && t.getEstado() == EstadoTecnico.DISPONIBLE) {
                return t;
            }
            actual = actual.getSiguiente();
        }
        return null;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public double getAnguloRadianes() { return anguloRadianes; }
    public double getX() { return x; }
    public double getY() { return y; }

    public ColaPrioridad<SolicitudServicio> getSolicitudesPendientes() {
        return solicitudesPendientes;
    }

    public ListaEnlazada<UnidadServicio> getUnidades() { return unidades; }

    public ListaEnlazada<Tecnico> getTecnicos() { return tecnicos; }

    @Override
    public String toString() {
        return "PuestoAtencion{id=" + id + ", nombre='" + nombre
               + "', pendientes=" + solicitudesPendientes.getTamanio()
               + ", unidades=" + unidades.getTamanio()
               + ", tecnicos=" + tecnicos.getTamanio() + "}";
    }
}
