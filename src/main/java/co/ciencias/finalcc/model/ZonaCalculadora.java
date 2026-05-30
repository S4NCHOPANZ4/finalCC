package co.ciencias.finalcc.model;

public class ZonaCalculadora {

    private static final double R2 = Math.sqrt(PuntoVia.R2_CUADRADO);
    private static final double[] PUESTOS_X = { R2,  0.0, -R2,  0.0 };
    private static final double[] PUESTOS_Y = { 0.0, R2,  0.0, -R2 };
    public static final String[] NOMBRES_PUESTOS = { "Este", "Norte", "Oeste", "Sur" };

    private ZonaCalculadora() {}

    public static int calcularZona(PuntoVia punto) {
        return calcularZona(punto.getX(), punto.getY());
    }

    public static int calcularZona(double x, double y) {
        int zonaMasCercana = 0;
        double menorDistancia = distanciaCuadrada(x, y, PUESTOS_X[0], PUESTOS_Y[0]);

        for (int i = 1; i < PUESTOS_X.length; i++) {
            double d = distanciaCuadrada(x, y, PUESTOS_X[i], PUESTOS_Y[i]);
            if (d < menorDistancia) {
                menorDistancia = d;
                zonaMasCercana = i;
            }
        }
        return zonaMasCercana;
    }

    public static double[] coordenadasPuesto(int zona) {
        if (zona < 0 || zona >= PUESTOS_X.length) {
            throw new IllegalArgumentException("Zona inválida: " + zona);
        }
        return new double[]{ PUESTOS_X[zona], PUESTOS_Y[zona] };
    }

    private static double distanciaCuadrada(double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        return dx * dx + dy * dy;
    }
}
