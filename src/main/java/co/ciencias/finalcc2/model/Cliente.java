package co.ciencias.finalcc2.model;
import java.util.UUID;

public class Cliente {

    private String id;
    private String nombre;
    private String telefono;
    
    public Cliente(String nombre, String telefono){
        this.id = UUID.randomUUID().toString();
        this.nombre = nombre;
        this.telefono = telefono;
    }
    
    public String getId(){return id;}
    public String getNombre(){return nombre;}
    public void setNombre(String nombre){this.nombre = nombre;}
    public String getTelefono(){return telefono;}
    public void setTelefono(String telefono){this.telefono = telefono;}
    
    @Override
    public String toString() {
        return "Cliente{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", telefono='" + telefono + '\'' +
                '}';
    }
    
    
}
