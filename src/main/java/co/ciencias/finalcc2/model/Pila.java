package co.ciencias.finalcc2.model;

public class Pila<T> {

    private Nodo<T> tope;
    private int tamanio;

    public Pila() {
        this.tope    = null;
        this.tamanio = 0;
    }

    public void push(T valor) {
        Nodo<T> nuevo = new Nodo<>(valor);
        nuevo.setSiguiente(tope);
        tope = nuevo;
        tamanio++;
    }

    public T pop() {
        if (esVacia()) return null;
        T dato = tope.getDato();
        tope   = tope.getSiguiente();
        tamanio--;
        return dato;
    }

    public T peek() {
        return esVacia() ? null : tope.getDato();
    }

    public boolean esVacia() { return tope == null; }
    public int getTamanio() { return tamanio; }
    public Nodo<T> getTope() { return tope; }
}