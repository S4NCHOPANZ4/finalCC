package co.ciencias.finalcc2.controller;

import co.ciencias.finalcc2.model.*;
import co.ciencias.finalcc2.model.enums.*;
import co.ciencias.finalcc2.model.gestores.*;

public class MotorSimulacion implements Runnable {

    private final int[] countdownsDespacho;
    private final ListaEnlazada<MonitoreoAtencion> listaAtencion;
    private final ListaEnlazada<MonitoreoMantenimiento> listaMantenimiento;
    
    private volatile boolean activo;
    private SimulacionListener listener;

    public MotorSimulacion() {
        this.countdownsDespacho = new int[]{10, 10, 10, 10};
        this.listaAtencion = new ListaEnlazada<>();
        this.listaMantenimiento = new ListaEnlazada<>();
        this.activo = false;
    }

    /**
     * Vincula la vista interesada en escuchar los cambios (Consola o GUI).
     */
    public void setListener(SimulacionListener listener) {
        this.listener = listener;
    }

    public void detener() {
        this.activo = false;
    }

    @Override
    public void run() {
        this.activo = true;
        while (activo) {
            try {
                Thread.sleep(1000); // 1 Segundo de simulación
                procesarSegundo();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private synchronized void procesarSegundo() {
        PuestoAtencion[] puestos = GestorRecursos.getInstancia().getPuestos();

        // 1. REGLA DE DESPACHO (Cada 10 segundos)
        for (int i = 0; i < puestos.length; i++) {
            PuestoAtencion puesto = puestos[i];
            
            if (!puesto.getSolicitudesPendientes().esVacia()) {
                countdownsDespacho[i]--;
                
                if (countdownsDespacho[i] <= 0) {
                    // Justo en el segundo 0 extrae la cabeza de la cola de prioridad.
                    // Si entró una médica hace 1 segundo, la prioridad nativa la puso al frente.
                    SolicitudServicio proxima = puesto.verSiguiente();
                    boolean despachado = GestorSolicitudes.getInstancia().despacharSiguiente(i);
                    
                    if (despachado) {
                        int tiempoRuta = 15 + (int)(Math.random() * 16); // 15 a 30s
                        listaAtencion.agregar(new MonitoreoAtencion(proxima, tiempoRuta));
                        
                        emitirMensaje(String.format("[DESPACHO AUTOMÁTICO] Canal %s despachó caso de: %s (%s). Tiempo en ruta: %ss.",
                                puesto.getNombre(), proxima.getCliente().getNombre(), proxima.getTipoEmergencia(), tiempoRuta));
                        
                        countdownsDespacho[i] = 10; // Reset
                    } else {
                        // Si no hay recursos, se queda esperando en 1 segundo para reintentar de inmediato
                        countdownsDespacho[i] = 1;
                    }
                }
            } else {
                countdownsDespacho[i] = 10; // Reloj en espera estático si no hay llamadas
            }
        }

        // 2. REGLA DE TRÁNSITO Y RETORNO (15s a 30s)
        Nodo<MonitoreoAtencion> actAtn = listaAtencion.getCabeza();
        ListaEnlazada<MonitoreoAtencion> terminadosAtn = new ListaEnlazada<>();
        
        while (actAtn != null) {
            MonitoreoAtencion mat = actAtn.getDato();
            mat.decrementarSegundos();
            if (mat.getSegundosRestantes() <= 0) {
                terminadosAtn.agregar(mat);
            }
            actAtn = actAtn.getSiguiente();
        }

        Nodo<MonitoreoAtencion> finAtn = terminadosAtn.getCabeza();
        while (finAtn != null) {
            MonitoreoAtencion mat = finAtn.getDato();
            listaAtencion.eliminar(mat);
            
            SolicitudServicio sol = mat.getSolicitud();
            sol.setEstado(EstadoSolicitud.CERRADA);
            
            UnidadServicio u = sol.getUnidadAsignada();
            Tecnico t = sol.getTecnicoAsignado();
            
            if (u != null) u.setEstado(EstadoUnidad.EN_MANTENIMIENTO);
            if (t != null) t.setEstado(EstadoTecnico.ASIGNADO);
            
            // Entra a enfriamiento / reabastecimiento técnico por 15s
            listaMantenimiento.agregar(new MonitoreoMantenimiento(u, t, 15));
            
            emitirMensaje(String.format("[RETORNO] Unidad %s terminó auxilio de %s. Kit ingresa a taller (15s).",
                    u.getCodigo(), sol.getCliente().getNombre()));
            
            finAtn = finAtn.getSiguiente();
        }

        // 3. REGLA DE REPARACIÓN EN TALLER (15s)
        Nodo<MonitoreoMantenimiento> actMnt = listaMantenimiento.getCabeza();
        ListaEnlazada<MonitoreoMantenimiento> terminadosMnt = new ListaEnlazada<>();
        
        while (actMnt != null) {
            MonitoreoMantenimiento mmnt = actMnt.getDato();
            mmnt.decrementarSegundos();
            if (mmnt.getSegundosRestantes() <= 0) {
                terminadosMnt.agregar(mmnt);
            }
            actMnt = actMnt.getSiguiente();
        }

        Nodo<MonitoreoMantenimiento> finMnt = terminadosMnt.getCabeza();
        while (finMnt != null) {
            MonitoreoMantenimiento mmnt = finMnt.getDato();
            listaMantenimiento.eliminar(mmnt);
            
            if (mmnt.getUnidad() != null) mmnt.getUnidad().setEstado(EstadoUnidad.DISPONIBLE);
            if (mmnt.getTecnico() != null) mmnt.getTecnico().setEstado(EstadoTecnico.DISPONIBLE);
            
            emitirMensaje(String.format("[TALLER LIBRE] Kit [%s + %s] reparado y DISPONIBLE.",
                    mmnt.getUnidad().getCodigo(), mmnt.getTecnico().getNombre()));
            
            finMnt = finMnt.getSiguiente();
        }

        // 4. NOTIFICAR CAMBIOS A LA INTERFAZ REGISTRADA
        if (listener != null) {
            listener.onTick(countdownsDespacho, listaAtencion, listaMantenimiento);
        }
    }

    private void emitirMensaje(String msg) {
        if (listener != null) {
            listener.onMensajeEmitido(msg);
        }
    }

    // Getters limpios útiles para mapear modelos de tablas en la GUI externa
    public int[] getCountdownsDespacho() { return countdownsDespacho; }
    public ListaEnlazada<MonitoreoAtencion> getListaAtencion() { return listaAtencion; }
    public ListaEnlazada<MonitoreoMantenimiento> getListaMantenimiento() { return listaMantenimiento; }
}