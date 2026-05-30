package co.ciencias.finalcc2.model;

import co.ciencias.finalcc2.model.enums.Especialidad;
import co.ciencias.finalcc2.model.enums.EstadoTecnico;
import java.util.UUID;


public class Tecnico {
    private final String id;
    private final String nombre;
    private final Especialidad especialidad;
    private EstadoTecnico estado;
    private final int zona;

    public Tecnico(String nombre, Especialidad especialidad, EstadoTecnico estado, int zona) {
        this.id = UUID.randomUUID().toString();
        this.nombre = nombre;
        this.especialidad = especialidad;
        this.estado = estado;
        this.zona = zona;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public Especialidad getEspecialidad() { return especialidad; }
    public EstadoTecnico getEstado() { return estado; }
    public void setEstado(EstadoTecnico estado) { this.estado = estado; }
    public int getZona() { return zona; }

    @Override
    public String toString() {
        return nombre + " (" + especialidad + ") - " + estado;
    }
}