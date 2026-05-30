package co.ciencias.finalcc.model;

import co.ciencias.finalcc.model.enums.TipoVia;


public class PuntoVia {

    public static final double R1_CUADRADO = 10.0;
    public static final double R2_CUADRADO = 40.0;
    public static final double R3_CUADRADO = 90.0;
    public static final double EPSILON = 0.15;
    private static final double[] ANGULOS_RECTAS = {
        0.0,
        Math.PI / 6.0,
        Math.PI / 3.0,
        Math.PI / 2.0,
        2.0 * Math.PI / 3.0,
        5.0 * Math.PI / 6.0,
        Math.PI
    };

    private double x;
    private double y;
    private TipoVia tipoVia;

    public PuntoVia(double x, double y) {
        if (!esPuntoValido(x, y)) {
            throw new IllegalArgumentException(
                String.format("El punto (%.4f, %.4f) no está sobre ninguna vía de la red vial.", x, y)
            );
        }
        this.x = x;
        this.y = y;
        this.tipoVia = determinarTipoVia(x, y);
    }

    public static boolean esPuntoValido(double x, double y) {
        return estaEnCirculo(x, y) || estaEnRecta(x, y);
    }

    private static boolean estaEnCirculo(double x, double y) {
        double r2 = x * x + y * y;
        return Math.abs(r2 - R1_CUADRADO) < EPSILON * 10
            || Math.abs(r2 - R2_CUADRADO) < EPSILON * 10
            || Math.abs(r2 - R3_CUADRADO) < EPSILON * 10;
    }

    private static boolean estaEnRecta(double x, double y) {
        for (double angulo : ANGULOS_RECTAS) {
            if (estaEnRectaAngulo(x, y, angulo)) return true;
        }
        return false;
    }

    private static boolean estaEnRectaAngulo(double x, double y, double angulo) {
        double distancia = Math.abs(y * Math.cos(angulo) - x * Math.sin(angulo));
        return distancia < EPSILON;
    }

    private static TipoVia determinarTipoVia(double x, double y) {
        if (estaEnCirculo(x, y)) return TipoVia.CIRCULO;
        return TipoVia.RECTA;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public TipoVia getTipoVia() { return tipoVia; }

    @Override
    public String toString() {
        return String.format("PuntoVia{x=%.3f, y=%.3f, via=%s}", x, y, tipoVia);
    }
}
