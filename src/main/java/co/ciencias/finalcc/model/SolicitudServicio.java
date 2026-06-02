package co.ciencias.finalcc.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import co.ciencias.finalcc.model.enums.EstadoSolicitud;
import co.ciencias.finalcc.model.enums.TipoEmergencia;


public class SolicitudServicio {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");
    private static final AtomicLong CONTADOR = new AtomicLong(0);
    private final String id;
    private final long secuencia;
    private final Cliente cliente;
    private final TipoEmergencia tipoEmergencia;
    private String descripcion;
    private final LocalDateTime   timestamp;
    private EstadoSolicitud estado;
    private final int indiceNodo;
    private int indicePuesto;  
    private UnidadServicio unidadAsignada;
    private Tecnico tecnicoAsignado;

    public SolicitudServicio(Cliente cliente, TipoEmergencia tipoEmergencia, String descripcion, int indiceNodo, int indicePuesto) {
        this.id = UUID.randomUUID().toString();
        this.secuencia = CONTADOR.getAndIncrement();
        this.cliente = cliente;
        this.tipoEmergencia = tipoEmergencia;
        this.descripcion = descripcion;
        this.indiceNodo = indiceNodo;
        this.indicePuesto = indicePuesto;
        this.estado = EstadoSolicitud.PENDIENTE;
        this.timestamp = LocalDateTime.now();
        this.unidadAsignada = null;
        this.tecnicoAsignado = null;
    }


    public String getId() { return id; }
    public long getSecuencia() { return secuencia; }
    public Cliente getCliente() {return cliente; }
    public TipoEmergencia getTipoEmergencia(){ return tipoEmergencia; }
    public TipoEmergencia getTipo(){ return tipoEmergencia; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String d) { this.descripcion = d; }
    public LocalDateTime getTimestamp(){ return timestamp; }
    public String getTimestampFormateado() { return timestamp.format(FMT); }
    public EstadoSolicitud getEstado() { return estado; }
    public void setEstado(EstadoSolicitud e) { this.estado = e; }
    public int getIndiceNodo() { return indiceNodo; }
    public int getIndicePuesto() { return indicePuesto; }
    public void setIndicePuesto(int p) { this.indicePuesto = p; }
    public UnidadServicio getUnidadAsignada(){ return unidadAsignada; }
    public void setUnidadAsignada(UnidadServicio u) { this.unidadAsignada = u; }
    public Tecnico getTecnicoAsignado()  { return tecnicoAsignado; }
    public void setTecnicoAsignado(Tecnico t) { this.tecnicoAsignado = t; }

    @Override
    public String toString() {
        return "[" + tipoEmergencia + " | P" + tipoEmergencia.getValor()
                + "] " + cliente.getNombre()
                + " → Puesto " + GrafoCiudad.NOMBRES_PUESTOS[indicePuesto]
                + " | nodo=" + GrafoCiudad.getInstancia().nombreNodo(indiceNodo)
                + " | " + estado
                + " @ " + timestamp.format(FMT)
                + " #" + secuencia;
    }
}