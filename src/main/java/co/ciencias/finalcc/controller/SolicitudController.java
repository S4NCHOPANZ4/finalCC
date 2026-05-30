package co.ciencias.finalcc.controller;

import co.ciencias.finalcc.model.Cliente;
import co.ciencias.finalcc.model.ListaEnlazada;
import co.ciencias.finalcc.model.Nodo;
import co.ciencias.finalcc.model.PuestoAtencion;
import co.ciencias.finalcc.model.PuntoVia;
import co.ciencias.finalcc.model.SolicitudServicio;
import co.ciencias.finalcc.model.enums.TipoEmergencia;
import co.ciencias.finalcc.model.gestores.GestorClientes;
import co.ciencias.finalcc.model.gestores.GestorSolicitudes;

public class SolicitudController {

    private GestorSolicitudes gestorSolicitudes;
    private GestorClientes gestorClientes;

    public SolicitudController() {
        this.gestorSolicitudes = GestorSolicitudes.getInstance();
        this.gestorClientes = GestorClientes.getInstance();
    }

    public String crearSolicitud(String clienteId, TipoEmergencia tipoEmergencia, String descripcion, double x, double y) {
        Cliente cliente = gestorClientes.buscarPorId(clienteId);
        if (cliente == null) {
            return "ERROR: Cliente no encontrado con ID: " + clienteId;
        }
        if (!PuntoVia.esPuntoValido(x, y)) {
            return String.format(
                "ERROR: El punto (%.3f, %.3f) no está sobre ninguna vía de la red.", x, y);
        }
        try {
            PuntoVia punto = new PuntoVia(x, y);
            SolicitudServicio solicitud = new SolicitudServicio(
                cliente, tipoEmergencia, descripcion, punto);

            PuestoAtencion puesto = gestorSolicitudes.registrarSolicitud(solicitud);

            String msg = "Solicitud registrada en puesto " + puesto.getNombre()
                + " (zona " + puesto.getId() + ").";
            if (solicitud.getUnidadAsignada() != null) {
                msg += " Asignada: " + solicitud.getUnidadAsignada().getCodigo()
                    + " + " + solicitud.getTecnicoAsignado().getNombre() + ".";
            } else {
                msg += " En cola de espera (sin recursos disponibles).";
            }
            return msg;

        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    public String cerrarSolicitud(String solicitudId) {
        SolicitudServicio s = buscarSolicitudEnHistorial(solicitudId);
        if (s == null) {
            return "ERROR: Solicitud no encontrada: " + solicitudId;
        }
        try {
            gestorSolicitudes.cerrarSolicitud(s);
            return "Solicitud cerrada exitosamente.";
        } catch (IllegalStateException e) {
            return "ERROR: " + e.getMessage();
        }
    }

    public Cliente registrarCliente(String nombre, String telefono) {
        return gestorClientes.registrarCliente(nombre, telefono);
    }

    public ListaEnlazada<SolicitudServicio> getHistorial() {
        return gestorSolicitudes.getHistorial();
    }

    public SolicitudServicio buscarSolicitudEnHistorial(String id) {
        Nodo<SolicitudServicio> actual = gestorSolicitudes.getHistorial().getCabeza();
        while (actual != null) {
            if (actual.getDato().getId().equals(id)) return actual.getDato();
            actual = actual.getSiguiente();
        }
        return null;
    }
}
