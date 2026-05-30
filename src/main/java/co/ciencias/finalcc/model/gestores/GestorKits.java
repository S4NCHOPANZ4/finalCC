
package co.ciencias.finalcc.model.gestores;

import co.ciencias.finalcc.model.Kit;
import co.ciencias.finalcc.model.Nodo;
import co.ciencias.finalcc.model.Pila;

public class GestorKits {

    private static GestorKits instancia;
    private Pila<Kit> pilaRevision;
    private Pila<Kit> pilaRepuestos;

    private GestorKits() {
        pilaRevision = new Pila<>();
        pilaRepuestos = new Pila<>();
    }
    public static GestorKits getInstance() {
        if (instancia == null) {
            instancia = new GestorKits();
        }
        return instancia;
    }
    public void ingresarARevision(Kit kit) {
        pilaRevision.push(kit);
    }
    public Kit retirarDeRevision() {
        Nodo<Kit> nodo = pilaRevision.pop();
        return nodo != null ? nodo.getDato() : null;
    }
    public Kit verTopeRevision() {
        Nodo<Kit> nodo = pilaRevision.ver();
        return nodo != null ? nodo.getDato() : null;
    }
    public boolean revisionEsVacia() {
        return pilaRevision.esVacia();
    }
    public void agregarRepuesto(Kit kit) {
        pilaRepuestos.push(kit);
    }
    public Kit retirarRepuesto() {
        Nodo<Kit> nodo = pilaRepuestos.pop();
        return nodo != null ? nodo.getDato() : null;
    }
    public Kit verTopeRepuestos() {
        Nodo<Kit> nodo = pilaRepuestos.ver();
        return nodo != null ? nodo.getDato() : null;
    }
    public boolean repuestosEsVacia() {
        return pilaRepuestos.esVacia();
    }
    public Pila<Kit> getPilaRevision() { return pilaRevision; }
    public Pila<Kit> getPilaRepuestos() { return pilaRepuestos; }
}
