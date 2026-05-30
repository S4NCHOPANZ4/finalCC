package co.ciencias.finalcc2.controller;

import co.ciencias.finalcc2.model.ListaEnlazada;

/**
 * Interfaz que cualquier Vista (Consola o futura GUI) implementará
 * para reaccionar a los cambios del motor de simulación en tiempo real.
 */
public interface SimulacionListener {
    
    /**
     * Se ejecuta cada segundo. Ideal para actualizar etiquetas de cuenta regresiva
     * o refrescar tablas y listas en la interfaz gráfica.
     * * @param countdowns Arreglo con los segundos restantes de los 4 puestos [Este, Norte, Oeste, Sur].
     * @param enRuta Lista de servicios actualmente navegando en carretera.
     * @param enTaller Lista de recursos en mantenimiento técnico.
     */
    void onTick(int[] countdowns, ListaEnlazada<MonitoreoAtencion> enRuta, ListaEnlazada<MonitoreoMantenimiento> enTaller);
    
    /**
     * Se ejecuta cuando ocurre una acción importante (Despacho, Retorno, Taller Listo).
     * Ideal para imprimir en consola o añadir a un JTextArea de logs en la GUI.
     */
    void onMensajeEmitido(String mensaje);
}