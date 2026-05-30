package co.ciencias.finalcc2.controller;

import co.ciencias.finalcc2.model.UnidadServicio;
import co.ciencias.finalcc2.model.Tecnico;

public class MonitoreoMantenimiento {
    private final UnidadServicio unidad;
    private final Tecnico tecnico;
    private int segundosRestantes;

    public MonitoreoMantenimiento(UnidadServicio unidad, Tecnico tecnico, int segundosRestantes) {
        this.unidad = unidad;
        this.tecnico = tecnico;
        this.segundosRestantes = segundosRestantes;
    }

    public UnidadServicio getUnidad() { return unidad; }
    public Tecnico getTecnico() { return tecnico; }
    public int getSegundosRestantes() { return segundosRestantes; }
    public void decrementarSegundos() { this.segundosRestantes--; }
}