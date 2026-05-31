package co.ciencias.finalcc.model;

import java.util.UUID;


public class Kit {
    private final String id;
    private final String descripcion;
    private boolean completo;

    public Kit(String descripcion) {
        this.id = UUID.randomUUID().toString();
        this.descripcion = descripcion;
        this.completo = true;
    }

    public String getId() { return id; }
    public String getDescripcion() { return descripcion; }
    public boolean isCompleto() { return completo; }
    public void setCompleto(boolean completo) { this.completo = completo; }

    @Override
    public String toString() {
        return "Kit ID: " + id.substring(0, 8) + " | " + descripcion + " | Completo: " + completo;
    }
}