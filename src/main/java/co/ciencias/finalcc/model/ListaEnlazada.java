package co.ciencias.finalcc.model;

public class ListaEnlazada<T> {

    private Nodo<T> cabeza;
    private int tamanio;

    public ListaEnlazada() {
        this.cabeza = null;
        this.tamanio = 0;
    }

    public void agregar(T valor) {
        Nodo<T> nuevo = new Nodo<>(valor);
        if (cabeza == null) {
            cabeza = nuevo;
        } else {
            Nodo<T> actual = cabeza;
            while (actual.getSiguiente() != null) {
                actual = actual.getSiguiente();
            }
            actual.setSiguiente(nuevo);
        }
        tamanio++;
    }

    public T obtener(int indice) {
        if (indice < 0 || indice >= tamanio) {
            throw new IndexOutOfBoundsException("Indice fuera de rango: " + indice);
        }
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
            tamanio--;
            return true;
        }
        Nodo<T> actual = cabeza;
        while (actual.getSiguiente() != null) {
            if (actual.getSiguiente().getDato().equals(valor)) {
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

    public boolean esVacia() {return tamanio == 0;}
    public int getTamanio() {return tamanio;}
    public Nodo<T> getCabeza() {return cabeza;}

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Lista[");
        Nodo<T> actual = cabeza;
        while (actual != null) {
            sb.append(actual.getDato());
            if (actual.getSiguiente() != null) sb.append(", ");
            actual = actual.getSiguiente();
        }
        sb.append("]");
        return sb.toString();
    }
}
