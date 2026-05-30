package co.ciencias.finalcc2.model;

import co.ciencias.finalcc2.model.enums.EstadoUnidad;
import co.ciencias.finalcc2.model.enums.TipoUnidad;
import java.util.UUID;


public class UnidadServicio {
    private final String id;
    private final TipoUnidad tipo;
    private EstadoUnidad estado;
    private final int zona;
    private final String codigo;

    public UnidadServicio(TipoUnidad tipo, EstadoUnidad estado, int zona, String codigo) {
        this.id = UUID.randomUUID().toString();
        this.tipo = tipo;
        this.estado = estado;
        this.zona = zona;
        this.codigo = codigo;
    }

    public String getId() { return id; }
    public TipoUnidad getTipo() { return tipo; }
    public EstadoUnidad getEstado() { return estado; }
    public void setEstado(EstadoUnidad estado) { this.estado = estado; }
    public int getZona() { return zona; }
    public String getCodigo() { return codigo; }

    @Override
    public String toString() {
        return "[" + codigo + "] " + tipo + " - Estado: " + estado;
    }
}