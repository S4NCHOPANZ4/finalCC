package co.ciencias.finalcc.model;

/**
 * Nodo del grafo de ciudad.
 *
 * <p>Cada nodo representa una intersección o punto de la ciudad.
 * Las conexiones se almacenan en una {@link ListaEnlazada} propia,
 * sin usar ninguna clase de {@code java.util}.</p>
 *
 * <p>El peso de cada arista representa la distancia (o tiempo)
 * entre dos nodos conectados.</p>
 */
public class NodoGrafo {

    /** Nombre descriptivo del nodo, p.ej. "Plaza Central". */
    private final String nombre;

    /** Lista de aristas salientes desde este nodo. */
    private final ListaEnlazada<AristaGrafo> adyacentes;

    public NodoGrafo(String nombre) {
        this.nombre     = nombre;
        this.adyacentes = new ListaEnlazada<>();
    }

    /** Añade una arista hacia {@code destino} con el peso indicado. */
    public void agregarAdyacente(NodoGrafo destino, int peso) {
        adyacentes.agregar(new AristaGrafo(destino, peso));
    }

    public String getNombre() { return nombre; }

    public ListaEnlazada<AristaGrafo> getAdyacentes() { return adyacentes; }

    @Override
    public String toString() { return nombre; }

    public static class AristaGrafo {
        private final NodoGrafo destino;
        private final int       peso;

        public AristaGrafo(NodoGrafo destino, int peso) {
            this.destino = destino;
            this.peso    = peso;
        }

        public NodoGrafo getDestino(){return destino; }
        public int getPeso(){ return peso;    }
    }
}