package co.ciencias.finalcc.model;
import java.util.UUID;
import co.ciencias.finalcc.model.enums.Especialidad;
import co.ciencias.finalcc.model.enums.EstadoTecnico;

public class Tecnico {
    private String id; 
    private String nombre;
    private Especialidad especialidad;
    private EstadoTecnico estado;
    private int zona;
    
    public Tecnico(String nombre, Especialidad especialidad, EstadoTecnico estado, int zona){
        this.id = UUID.randomUUID().toString();
        this.nombre = nombre;
        this.especialidad = especialidad; 
        this.estado = estado;
        this.zona = zona;
    }
    
    public String getId(){return id;}
    public String getNombre(){return nombre;}
    public void setNombre(String nombre){this.nombre = nombre;}
    public Especialidad getEspecialidad(){return especialidad;}
    public void setEspecialidad(Especialidad especialidad){this.especialidad = especialidad;}
    public EstadoTecnico getEstado(){return estado;}
    public void setEstado(EstadoTecnico estado){this.estado = estado;}
    public int getZona(){return zona;}
    public void setZona(int zona){this.zona = zona;}
    
    @Override
    public String toString() {
        return "Tecnico{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", especialidad=" + especialidad +
                ", estado=" + estado +
                ", zona=" + zona +
                '}';
    }
    
}
