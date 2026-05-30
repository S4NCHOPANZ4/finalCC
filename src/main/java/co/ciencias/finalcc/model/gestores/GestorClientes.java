package co.ciencias.finalcc.model.gestores;

import co.ciencias.finalcc.model.Cliente;
import co.ciencias.finalcc.model.ListaEnlazada;
import co.ciencias.finalcc.model.Nodo;


public class GestorClientes {

    private static GestorClientes instancia;
    private ListaEnlazada<Cliente> clientes;

    private GestorClientes() {
        clientes = new ListaEnlazada<>();
    }

    public static GestorClientes getInstance() {
        if (instancia == null) {
            instancia = new GestorClientes();
        }
        return instancia;
    }

    public Cliente registrarCliente(String nombre, String telefono) {
        Cliente c = new Cliente(nombre, telefono);
        clientes.agregar(c);
        return c;
    }


    public void agregarCliente(Cliente cliente) {
        clientes.agregar(cliente);
    }

    public Cliente buscarPorId(String id) {
        Nodo<Cliente> actual = clientes.getCabeza();
        while (actual != null) {
            if (actual.getDato().getId().equals(id)) return actual.getDato();
            actual = actual.getSiguiente();
        }
        return null;
    }

    public Cliente buscarPorNombre(String nombre) {
        Nodo<Cliente> actual = clientes.getCabeza();
        while (actual != null) {
            if (actual.getDato().getNombre().equalsIgnoreCase(nombre))
                return actual.getDato();
            actual = actual.getSiguiente();
        }
        return null;
    }

    public ListaEnlazada<Cliente> getTodosClientes() {
        return clientes;
    }

    public int getTotalClientes() {
        return clientes.getTamanio();
    }
}
