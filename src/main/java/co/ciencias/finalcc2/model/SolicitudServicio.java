package co.ciencias.finalcc2.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import co.ciencias.finalcc2.model.enums.EstadoSolicitud;
import co.ciencias.finalcc2.model.enums.TipoEmergencia;

public class SolicitudServicio {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");
    private final String id;
    private final Cliente cliente;
    private final TipoEmergencia tipoEmergencia;
    private String descripcion;
    private final LocalDateTime timestamp;
    private EstadoSolicitud estado;
    private final PuntoVia ubicacion;
    private int zonaPuesto;
    private UnidadServicio unidadAsignada;
    private Tecnico tecnicoAsignado;

    public SolicitudServicio(Cliente cliente, TipoEmergencia tipoEmergencia, String descripcion, PuntoVia ubicacion, int zonaPuesto) {
        this.id = UUID.randomUUID().toString();
        this.cliente = cliente;
        this.tipoEmergencia = tipoEmergencia;
        this.descripcion = descripcion;
        this.ubicacion = ubicacion;
        this.zonaPuesto = zonaPuesto;
        this.estado = EstadoSolicitud.PENDIENTE;
        this.timestamp = LocalDateTime.now();
        this.unidadAsignada = null;
        this.tecnicoAsignado = null;
    }

    public String getId() { return id; }
    public Cliente getCliente() { return cliente; }
    public TipoEmergencia getTipoEmergencia() { return tipoEmergencia; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getTimestampFormateado() { return timestamp.format(FMT); }
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
        return "[" + tipoEmergencia + " | P" + tipoEmergencia.getValor()
                + "] " + cliente.getNombre()
                + " → Puesto " + zonaPuesto
                + " | " + estado
                + " @ " + timestamp.format(FMT);
    }
}