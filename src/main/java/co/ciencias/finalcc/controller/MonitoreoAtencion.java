package co.ciencias.finalcc.controller;

import co.ciencias.finalcc.model.SolicitudServicio;

public class MonitoreoAtencion {
    private final SolicitudServicio solicitud;
    private final long horaFinalizacion; // Timestamp real de la máquina

    public MonitoreoAtencion(SolicitudServicio solicitud, long horaFinalizacion) {
        this.solicitud = solicitud;
        this.horaFinalizacion = horaFinalizacion;
    }

    public SolicitudServicio getSolicitud() {
        return solicitud;
    }

    public long getHoraFinalizacion() {
        return horaFinalizacion;
    }

    /**
     * Calcula dinámicamente cuántos segundos reales le quedan al servicio
     */
    public int getSegundosRestantes() {
        long restantes = horaFinalizacion - System.currentTimeMillis();
        if (restantes <= 0) return 0;
        return (int) Math.ceil(restantes / 1000.0);
    }
}