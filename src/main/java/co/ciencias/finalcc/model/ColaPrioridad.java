package co.ciencias.finalcc.model;

/**
 * Cola de prioridad implementada sobre una lista enlazada ordenada.
 *
 * <p>Los elementos se mantienen ordenados de mayor a menor prioridad.
 * El criterio de ordenación es <b>total y estricto</b>:</p>
 * <ol>
 *   <li>Menor {@code TipoEmergencia.getValor()} → mayor prioridad.</li>
 *   <li>Si el valor es igual, menor {@code secuencia} (llegó primero) → mayor prioridad.</li>
 * </ol>
 *
 * <p>El uso del número de secuencia (contador atómico en {@link SolicitudServicio})
 * garantiza que {@code tieneMayorPrioridad(a,b)} y {@code tieneMayorPrioridad(b,a)}
 * nunca retornen {@code false} simultáneamente para a≠b, lo que causaba que el
 * bucle de {@link #insertar} nunca encontrara la posición correcta y corrompiera
 * la estructura a partir del 6.º elemento con igual tipo de emergencia.</p>
 */
public class ColaPrioridad {

    private Nodo<SolicitudServicio> cabeza;
    private int tamanio;

    /** Construye una cola de prioridad vacía. */
    public ColaPrioridad() {
        this.cabeza  = null;
        this.tamanio = 0;
    }

    /**
     * Devuelve una vista de la cola como {@link ListaEnlazada} sin modificarla.
     * Usado por la GUI para renderizar la cola de pendientes.
     *
     * @return lista con las solicitudes en orden de prioridad (mayor primero)
     */
    public ListaEnlazada<SolicitudServicio> getLista() {
        ListaEnlazada<SolicitudServicio> lista = new ListaEnlazada<>();
        Nodo<SolicitudServicio> actual = cabeza;
        while (actual != null) {
            lista.agregar(actual.getDato());
            actual = actual.getSiguiente();
        }
        return lista;
    }

    /**
     * Inserta una solicitud en la posición que le corresponde según prioridad.
     *
     * <p>Gracias al orden total estricto (tipo + secuencia), el bucle siempre
     * avanza en O(n) sin importar cuántos elementos compartan tipo de emergencia.</p>
     *
     * @param solicitud la solicitud a insertar; ignorada si es {@code null}
     */
    public void insertar(SolicitudServicio solicitud) {
        if (solicitud == null) return;

        Nodo<SolicitudServicio> nuevo = new Nodo<>(solicitud);

        if (cabeza == null || tieneMayorPrioridad(solicitud, cabeza.getDato())) {
            nuevo.setSiguiente(cabeza);
            cabeza = nuevo;
        } else {
            Nodo<SolicitudServicio> actual = cabeza;
            while (actual.getSiguiente() != null && !tieneMayorPrioridad(solicitud, actual.getSiguiente().getDato())) {
                actual = actual.getSiguiente();
            }
            nuevo.setSiguiente(actual.getSiguiente());
            actual.setSiguiente(nuevo);
        }
        tamanio++;
    }

    /**
     * Extrae y retorna la solicitud de mayor prioridad (frente de la cola).
     *
     * @return solicitud extraída, o {@code null} si la cola está vacía
     */
    public SolicitudServicio extraer() {
        if (esVacia()) return null;
        SolicitudServicio dato = cabeza.getDato();
        cabeza = cabeza.getSiguiente();
        tamanio--;
        return dato;
    }

    /**
     * Consulta la solicitud de mayor prioridad sin extraerla.
     *
     * @return solicitud al frente, o {@code null} si la cola está vacía
     */
    public SolicitudServicio verFrente() {
        return esVacia() ? null : cabeza.getDato();
    }

    /** @return {@code true} si la cola no tiene elementos */
    public boolean esVacia() { return cabeza == null; }

    /** @return número de solicitudes en la cola */
    public int getTamanio() { return tamanio; }

    /** @return nodo cabeza de la lista interna (uso interno y GUI) */
    public Nodo<SolicitudServicio> getCabeza() { return cabeza; }


        public boolean eliminar(SolicitudServicio solicitud) {
        if (solicitud == null || esVacia()) return false;

        if (cabeza.getDato() == solicitud) {
            cabeza = cabeza.getSiguiente();
            tamanio--;
            return true;
        }

        Nodo<SolicitudServicio> actual = cabeza;
        while (actual.getSiguiente() != null) {
            if (actual.getSiguiente().getDato() == solicitud) {
                actual.setSiguiente(actual.getSiguiente().getSiguiente());
                tamanio--;
                return true;
            }
            actual = actual.getSiguiente();
        }
        return false;
    }
    // ------------------------------------------------------------------
    // Comparación con orden total estricto
    // ------------------------------------------------------------------

    /**
     * Determina si {@code a} debe ubicarse antes que {@code b} en la cola.
     *
     * <p>Para cualquier par (a,b) con a≠b, exactamente uno retorna {@code true}.
     * Nunca hay empate porque la secuencia de cada solicitud es única.</p>
     *
     * @param a primer candidato
     * @param b segundo candidato
     * @return {@code true} si {@code a} tiene mayor prioridad que {@code b}
     */
    private boolean tieneMayorPrioridad(SolicitudServicio a, SolicitudServicio b) {
        // 1. Menor valor de emergencia = mayor prioridad
        int valorA = a.getTipoEmergencia().getValor();
        int valorB = b.getTipoEmergencia().getValor();
        if (valorA != valorB) {
            return valorA < valorB;
        }

        // 2. Menor número de secuencia = llegó antes = mayor prioridad.
        //    La secuencia es un AtomicLong único por instancia, garantiza
        //    orden total estricto sin depender de la resolución del reloj.
        return a.getSecuencia() < b.getSecuencia();
    }
}