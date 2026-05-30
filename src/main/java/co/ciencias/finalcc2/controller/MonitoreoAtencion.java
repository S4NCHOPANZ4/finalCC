package co.ciencias.finalcc2.controller;

import co.ciencias.finalcc2.model.SolicitudServicio;

public class MonitoreoAtencion {
    private final SolicitudServicio solicitud;
    private int segundosRestantes;

    public MonitoreoAtencion(SolicitudServicio solicitud, int segundosRestantes) {
        this.solicitud = solicitud;
        this.segundosRestantes = segundosRestantes;
    }

    public SolicitudServicio getSolicitud() { return solicitud; }
    public int getSegundosRestantes() { return segundosRestantes; }
    public void decrementarSegundos() { this.segundosRestantes--; }
}