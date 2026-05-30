package co.ciencias.finalcc.model;

public class ColaPrioridad<T extends Comparable<T>> {

    private Nodo<T> cabeza;
    private int tamanio;
    public ColaPrioridad() {
        this.cabeza = null;
        this.tamanio = 0;
    }
    public void insertar(T valor) {
        Nodo<T> nuevo = new Nodo<>(valor);
        if (cabeza == null || valor.compareTo(cabeza.getDato()) < 0) {
            nuevo.setSiguiente(cabeza);
            cabeza = nuevo;
        } else {
            Nodo<T> actual = cabeza;
            while (actual.getSiguiente() != null
                    && valor.compareTo(actual.getSiguiente().getDato()) >= 0) {
                actual = actual.getSiguiente();
            }
            nuevo.setSiguiente(actual.getSiguiente());
            actual.setSiguiente(nuevo);
        }
        tamanio++;
    }

    public T extraerMinimo() {
        if (esVacia()) return null;
        T dato = cabeza.getDato();
        cabeza = cabeza.getSiguiente();
        tamanio--;
        return dato;
    }

    public T verMinimo() {
        if (esVacia()) return null;
        return cabeza.getDato();
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

    public boolean esVacia() {return cabeza == null;}
    public int getTamanio() {return tamanio;}
    public Nodo<T> getCabeza() {return cabeza;}

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ColaPrioridad[");
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
