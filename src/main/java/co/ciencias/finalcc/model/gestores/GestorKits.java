package co.ciencias.finalcc.model.gestores;

import co.ciencias.finalcc.model.Cola;
import co.ciencias.finalcc.model.Pila;
import co.ciencias.finalcc.model.Kit;

public class GestorKits {
    private static GestorKits instancia;
    private final Cola<Kit> zonaRevision;       
    private final Pila<Kit> estanteriaListos;  

    private GestorKits() {
        this.zonaRevision = new Cola<>();
        this.estanteriaListos = new Pila<>();
        inicializarInventario();
    }
    public static GestorKits getInstancia() {
        if (instancia == null) {
            instancia = new GestorKits();
        }
        return instancia;
    }
    private void inicializarInventario() {
        estanteriaListos.push(new Kit("Kit Mecánico Básico K01"));
        estanteriaListos.push(new Kit("Kit Hidráulico Pesado K02"));
        estanteriaListos.push(new Kit("Kit Eléctrico Avanzado K03"));
    }

    public void registrarKitEnRevision(Kit kit) {
        if (kit == null) return;
        kit.setCompleto(false);
        zonaRevision.encolar(kit);
    }
    public Kit liberarKitDeRevision() {
        Kit kit = zonaRevision.desencolar();
        if (kit != null) {
            kit.setCompleto(true);
            estanteriaListos.push(kit);
        }
        return kit;
    }

    public Kit retirarKitParaServicio() {
        return estanteriaListos.pop(); 
    }

    public Cola<Kit> getZonaRevision() { return zonaRevision; }
    public Pila<Kit> getEstanteriaListos() { return estanteriaListos; }
}