package co.ciencias.finalcc2.model;


public class ColaPrioridad {

    private Nodo<SolicitudServicio> cabeza;
    private int tamanio;
    
    public ColaPrioridad() {
        this.cabeza  = null;
        this.tamanio = 0;
    }

    public void insertar(SolicitudServicio solicitud) {
        Nodo<SolicitudServicio> nuevo = new Nodo<>(solicitud);
        if (cabeza == null || tieneMayorPrioridad(solicitud, cabeza.getDato())) {
            nuevo.setSiguiente(cabeza);
            cabeza = nuevo;
        } else {
            Nodo<SolicitudServicio> actual = cabeza;
            while (actual.getSiguiente() != null
                    && !tieneMayorPrioridad(solicitud, actual.getSiguiente().getDato())) {
                actual = actual.getSiguiente();
            }
            nuevo.setSiguiente(actual.getSiguiente());
            actual.setSiguiente(nuevo);
        }
        tamanio++;
    }


    public SolicitudServicio extraer() {
        if (esVacia()) return null;
        SolicitudServicio dato = cabeza.getDato();
        cabeza = cabeza.getSiguiente();
        tamanio--;
        return dato;
    }

    public SolicitudServicio verFrente() {
        return esVacia() ? null : cabeza.getDato();
    }

    public boolean esVacia() { return cabeza == null; }
    public int getTamanio() { return tamanio; }
    public Nodo<SolicitudServicio> getCabeza() { return cabeza; }
    private boolean tieneMayorPrioridad(SolicitudServicio a, SolicitudServicio b) {
        int valorA = a.getTipoEmergencia().getValor();
        int valorB = b.getTipoEmergencia().getValor();
        if (valorA != valorB) {
            return valorA < valorB; 
        }
        return a.getTimestamp().isBefore(b.getTimestamp());
    }
}