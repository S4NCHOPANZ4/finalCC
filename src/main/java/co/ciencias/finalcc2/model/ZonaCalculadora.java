package co.ciencias.finalcc2.model;

/**
 * Calcula qué puesto de atención debe atender una solicitud según la
 * distancia euclidiana entre la ubicación del cliente y los cuatro puestos.
 *
 * <p>Los puestos están fijos en el segundo círculo (radio √40) en los
 * ángulos 0°, 90°, 180° y 270°:</p>
 * <pre>
 *   Índice 0 — Este  : ( √40,    0)
 *   Índice 1 — Norte : (   0,  √40)
 *   Índice 2 — Oeste : (-√40,    0)
 *   Índice 3 — Sur   : (   0, -√40)
 * </pre>
 *
 * @author AutoRescate 24/7
 */
public class ZonaCalculadora {

    private static ZonaCalculadora instancia;
    public static final double[][] COORDS = {
        {  PuntoVia.RADIO_2,  0               },  
        {  0,                  PuntoVia.RADIO_2 },
        { -PuntoVia.RADIO_2,  0               }, 
        {  0,                 -PuntoVia.RADIO_2 }  
    };

    public static final String[] NOMBRES = { "Este", "Norte", "Oeste", "Sur" };
    private ZonaCalculadora() {}
    public static ZonaCalculadora getInstancia() {
        if (instancia == null) instancia = new ZonaCalculadora();
        return instancia;
    }


    public int calcularPuesto(PuntoVia punto) {
        double minDist2 = Double.MAX_VALUE;
        int    cercano  = 0;

        for (int i = 0; i < COORDS.length; i++) {
            double dx    = punto.getX() - COORDS[i][0];
            double dy    = punto.getY() - COORDS[i][1];
            double dist2 = dx * dx + dy * dy;
            if (dist2 < minDist2) {
                minDist2 = dist2;
                cercano  = i;
            }
        }
        return cercano;
    }

    public double distanciaA(PuntoVia punto, int indicePuesto) {
        double dx = punto.getX() - COORDS[indicePuesto][0];
        double dy = punto.getY() - COORDS[indicePuesto][1];
        return Math.sqrt(dx * dx + dy * dy);
    }
}