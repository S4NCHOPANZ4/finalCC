package co.ciencias.finalcc.model;

import java.util.UUID;

public class Kit {

    private String id;
    private String descripcion;
    private boolean completo;

    public Kit(String descripcion, boolean completo) {
        this.id = UUID.randomUUID().toString();
        this.descripcion = descripcion;
        this.completo = completo;
    }

    public String getId() { return id; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public boolean isCompleto() { return completo; }
    public void setCompleto(boolean completo) { this.completo = completo; }

    @Override
    public String toString() {
        return "Kit{id='" + id + "', descripcion='" + descripcion
               + "', completo=" + completo + "}";
    }
}
