package co.ciencias.finalcc.model;

import java.time.LocalDateTime;
import java.util.UUID;
import co.ciencias.finalcc.model.enums.EstadoSolicitud;
import co.ciencias.finalcc.model.enums.TipoEmergencia;

public class SolicitudServicio implements Comparable<SolicitudServicio> {

    private String id;
    private Cliente cliente;
    private TipoEmergencia tipoEmergencia;
    private String descripcion;
    private LocalDateTime timestamp;
    private EstadoSolicitud estado;
    private PuntoVia ubicacion;
    private int zonaPuesto;
    private UnidadServicio unidadAsignada;
    private Tecnico tecnicoAsignado;
    public SolicitudServicio(Cliente cliente, TipoEmergencia tipoEmergencia, String descripcion, PuntoVia ubicacion) {
        if (cliente == null) {
            throw new IllegalArgumentException("La solicitud debe estar asociada a un cliente.");
        }
        this.id = UUID.randomUUID().toString();
        this.cliente = cliente;
        this.tipoEmergencia = tipoEmergencia;
        this.descripcion = descripcion;
        this.ubicacion = ubicacion;
        this.timestamp = LocalDateTime.now();
        this.estado = EstadoSolicitud.PENDIENTE;
        this.zonaPuesto = -1;
        this.unidadAsignada = null;
        this.tecnicoAsignado = null;
    }

    @Override
    public int compareTo(SolicitudServicio otra) {
        int cmpPrioridad = Integer.compare(
            this.tipoEmergencia.getValor(),
            otra.tipoEmergencia.getValor()
        );
        if (cmpPrioridad != 0) return cmpPrioridad;
        return this.timestamp.compareTo(otra.timestamp);
    }

    public String getId() { return id; }
    public Cliente getCliente() { return cliente; }
    public TipoEmergencia getTipoEmergencia() { return tipoEmergencia; }
    public String getDescripcion() { return descripcion; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public EstadoSolicitud getEstado() { return estado; }
    public void setEstado(EstadoSolicitud estado) { this.estado = estado; }
    public PuntoVia getUbicacion() { return ubicacion; }
    public int getZonaPuesto() { return zonaPuesto; }
    public void setZonaPuesto(int zonaPuesto) { this.zonaPuesto = zonaPuesto; }
    public UnidadServicio getUnidadAsignada() { return unidadAsignada; }
    public void setUnidadAsignada(UnidadServicio unidad) { this.unidadAsignada = unidad; }
    public Tecnico getTecnicoAsignado() { return tecnicoAsignado; }
    public void setTecnicoAsignado(Tecnico tecnico) { this.tecnicoAsignado = tecnico; }

    @Override
    public String toString() {
        return "Solicitud{id=" + id.substring(0, 8) + "..."
               + ", cliente=" + cliente.getNombre()
               + ", tipo=" + tipoEmergencia
               + ", estado=" + estado
               + ", zona=" + zonaPuesto + "}";
    }
}
