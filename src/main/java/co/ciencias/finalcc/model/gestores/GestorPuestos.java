package co.ciencias.finalcc.model.gestores;

import co.ciencias.finalcc.model.PuestoAtencion;
import co.ciencias.finalcc.model.ZonaCalculadora;


public class GestorPuestos {

    private static GestorPuestos instancia;

    private PuestoAtencion[] puestos;

    private GestorPuestos() {
        double r2 = Math.sqrt(40.0); // ≈ 6.3246
        puestos = new PuestoAtencion[4];
        puestos[0] = new PuestoAtencion(0, "Este",  0.0,              r2,   0.0);
        puestos[1] = new PuestoAtencion(1, "Norte", Math.PI / 2.0,    0.0,  r2 );
        puestos[2] = new PuestoAtencion(2, "Oeste", Math.PI,         -r2,   0.0);
        puestos[3] = new PuestoAtencion(3, "Sur",   3.0 * Math.PI / 2.0, 0.0, -r2);
    }


    public static GestorPuestos getInstance() {
        if (instancia == null) {
            instancia = new GestorPuestos();
        }
        return instancia;
    }


    public PuestoAtencion getPuesto(int zona) {
        if (zona < 0 || zona >= puestos.length) {
            throw new IllegalArgumentException("Zona inválida: " + zona);
        }
        return puestos[zona];
    }


    public PuestoAtencion[] getTodosPuestos() {
        return puestos;
    }


    public void imprimirEstado() {
        for (PuestoAtencion p : puestos) {
            System.out.println(p);
        }
    }
}
