package co.ciencias.finalcc2.model;

public class ListaEnlazada<T> {

    private Nodo<T> cabeza;
    private Nodo<T> cola;
    private int tamanio;
    
    public ListaEnlazada() {
        this.cabeza  = null;
        this.cola    = null;
        this.tamanio = 0;
    }

    public void agregar(T valor) {
        Nodo<T> nuevo = new Nodo<>(valor);
        if (cabeza == null) {
            cabeza = nuevo;
        } else {
            cola.setSiguiente(nuevo);
        }
        cola = nuevo;
        tamanio++;
    }

    public T obtener(int indice) {
        validarIndice(indice);
        Nodo<T> actual = cabeza;
        for (int i = 0; i < indice; i++) {
            actual = actual.getSiguiente();
        }
        return actual.getDato();
    }

    public boolean eliminar(T valor) {
        if (cabeza == null) return false;
        if (cabeza.getDato().equals(valor)) {
            cabeza = cabeza.getSiguiente();
            if (cabeza == null) cola = null;
            tamanio--;
            return true;
        }
        Nodo<T> actual = cabeza;
        while (actual.getSiguiente() != null) {
            if (actual.getSiguiente().getDato().equals(valor)) {
                if (actual.getSiguiente() == cola) cola = actual;
                actual.setSiguiente(actual.getSiguiente().getSiguiente());
                tamanio--;
                return true;
            }
            actual = actual.getSiguiente();
        }
        return false;
    }

    public boolean contiene(T valor) {
        Nodo<T> actual = cabeza;
        while (actual != null) {
            if (actual.getDato().equals(valor)) return true;
            actual = actual.getSiguiente();
        }
        return false;
    }

    public int getTamanio() { return tamanio; }
    public boolean esVacia() { return tamanio == 0; }
    public Nodo<T> getCabeza() { return cabeza; }
    private void validarIndice(int indice) {
        if (indice < 0 || indice >= tamanio) {
            throw new IndexOutOfBoundsException(
                "Índice " + indice + " fuera de rango [0," + (tamanio - 1) + "]");
        }
    }
}