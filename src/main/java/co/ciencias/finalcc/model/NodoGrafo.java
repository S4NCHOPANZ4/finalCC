package co.ciencias.finalcc.model;

public class NodoGrafo {

    private final String nombre;

    private final ListaEnlazada<AristaGrafo> adyacentes;

    public NodoGrafo(String nombre) {
        this.nombre     = nombre;
        this.adyacentes = new ListaEnlazada<>();
    }

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