package co.ciencias.finalcc2.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Operacion {

    private String descripcion;
    private String idObjetoAfectado;
    private String estadoAnterior;
    private LocalDateTime timestamp;

    public Operacion(String descripcion, String idObjetoAfectado, String estadoAnterior) {
        this.descripcion = descripcion;
        this.idObjetoAfectado = idObjetoAfectado;
        this.estadoAnterior = estadoAnterior;
        this.timestamp = LocalDateTime.now();
    }

    public String getDescripcion() { return descripcion; }
    public String getIdObjetoAfectado() { return idObjetoAfectado; }
    public String getEstadoAnterior() { return estadoAnterior; }
    public LocalDateTime getTimestamp() { return timestamp; }
    @Override
    public String toString() {
        String ts = timestamp.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        return "Operacion{" + ts + " | " + descripcion + " | id=" + idObjetoAfectado
               + " | anterior=" + estadoAnterior + "}";
    }
  

}
