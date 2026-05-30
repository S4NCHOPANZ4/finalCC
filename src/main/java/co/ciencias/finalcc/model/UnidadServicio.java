package co.ciencias.finalcc.model;
import java.util.UUID;
import co.ciencias.finalcc.model.enums.EstadoUnidad;
import co.ciencias.finalcc.model.enums.TipoUnidad;

public class UnidadServicio {
    private String id;
    private TipoUnidad tipo;
    private EstadoUnidad estado;
    private int zona;
    private String codigo;
    
    public UnidadServicio(TipoUnidad tipo,EstadoUnidad estado, int zona, String codigo){
        this.id = UUID.randomUUID().toString();
        this.tipo = tipo;
        this.estado = estado;
        this.zona = zona;
        this.codigo = codigo;
    }
    
    public String getId(){return id;}
    public TipoUnidad getTipo(){return tipo;}
    public void setTipo(TipoUnidad tipo){this.tipo = tipo;}
    public EstadoUnidad getEstado(){return estado;}
    public void setEstado(EstadoUnidad estado){this.estado = estado;}
    public int getZona(){return zona;}
    public String getCodigo(){return codigo;}
    
    @Override
    public String toString() {
        return "UnidadServicio{" +
                "id='" + id + '\'' +
                ", tipo=" + tipo +
                ", estado=" + estado +
                ", zona=" + zona +
                ", codigo='" + codigo + '\'' +
                '}';
    }
}
