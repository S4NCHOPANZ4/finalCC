package co.ciencias.finalcc2.model;


public class Cola<T> {

    private Nodo<T> frente;
    private Nodo<T> fin;
    private int tamanio;
    public Cola() {
        this.frente  = null;
        this.fin     = null;
        this.tamanio = 0;
    }

    public void encolar(T valor) {
        Nodo<T> nuevo = new Nodo<>(valor);
        if (esVacia()) {
            frente = nuevo;
        } else {
            fin.setSiguiente(nuevo);
        }
        fin = nuevo;
        tamanio++;
    }

    public T desencolar() {
        if (esVacia()) return null;
        T dato = frente.getDato();
        frente = frente.getSiguiente();
        if (frente == null) fin = null;
        tamanio--;
        return dato;
    }

    public T verFrente() {
        return esVacia() ? null : frente.getDato();
    }

    public boolean esVacia() { return frente == null; }
    public int getTamanio() { return tamanio; }
    public Nodo<T> getNodoFrente() { return frente; }
}