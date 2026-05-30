package co.ciencias.finalcc.controller;

import co.ciencias.finalcc.model.Kit;
import co.ciencias.finalcc.model.gestores.GestorKits;

public class KitsController {

    private GestorKits gestorKits;
    public KitsController() {
        this.gestorKits = GestorKits.getInstance();
    }

    public String ingresarKitARevision(String descripcion, boolean completo) {
        Kit kit = new Kit(descripcion, completo);
        gestorKits.ingresarARevision(kit);
        return "Kit '" + descripcion + "' ingresado a revisión.";
    }

    public Kit retirarKitDeRevision() {
        return gestorKits.retirarDeRevision();
    }

    public String agregarRepuesto(String descripcion) {
        Kit repuesto = new Kit(descripcion, true);
        gestorKits.agregarRepuesto(repuesto);
        return "Repuesto '" + descripcion + "' agregado a la pila.";
    }

    public Kit retirarRepuesto() {
        return gestorKits.retirarRepuesto();
    }

    public Kit verTopeRevision() {
        return gestorKits.verTopeRevision();
    }


    public Kit verTopeRepuestos() {
        return gestorKits.verTopeRepuestos();
    }

    public boolean revisionVacia() { return gestorKits.revisionEsVacia(); }
    public boolean repuestosVacia() { return gestorKits.repuestosEsVacia(); }
}
