package co.ciencias.finalcc2.model;

import co.ciencias.finalcc2.model.enums.TipoVia;


public class PuntoVia {

    public static final double RADIO_1 = Math.sqrt(10.0);
    public static final double RADIO_2 = Math.sqrt(40.0);
    public static final double RADIO_3 = Math.sqrt(90.0);
    public static final double EPSILON = 0.20;

    public static final double[] ANGULOS_RECTAS = {
        0,
        Math.PI / 6,
        Math.PI / 3,
        Math.PI / 2,
        2.0 * Math.PI / 3,
        5.0 * Math.PI / 6,
        Math.PI
    };

    private final double x;
    private final double y;
    private final TipoVia tipoVia;
    private final int indiceVia;


    private PuntoVia(double x, double y, TipoVia tipoVia, int indiceVia) {
        this.x         = x;
        this.y         = y;
        this.tipoVia   = tipoVia;
        this.indiceVia = indiceVia;
    }


    public static PuntoVia desde(double x, double y) {
        double dist = Math.sqrt(x * x + y * y);

        double[] radios = { RADIO_1, RADIO_2, RADIO_3 };
        for (int i = 0; i < radios.length; i++) {
            if (Math.abs(dist - radios[i]) <= EPSILON) {
                return new PuntoVia(x, y, TipoVia.CIRCULO, i);
            }
        }

        for (int i = 0; i < ANGULOS_RECTAS.length; i++) {
            double ang = ANGULOS_RECTAS[i];
            double residuo;
            if (Math.abs(ang - Math.PI / 2) < 1e-9) {
                residuo = Math.abs(x);
            } else {
                residuo = Math.abs(y * Math.cos(ang) - x * Math.sin(ang));
            }
            double normalizado = (dist > 1e-9) ? residuo / dist : residuo;
            if (normalizado <= EPSILON) {
                return new PuntoVia(x, y, TipoVia.RECTA, i);
            }
        }

        return null;
    }

    public static boolean esValido(double x, double y) {
        return desde(x, y) != null;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public TipoVia getTipoVia() { return tipoVia; }
    public int getIndiceVia() { return indiceVia; }

    @Override
    public String toString() {
        return String.format("(%.2f, %.2f) [%s %d]", x, y, tipoVia, indiceVia);
    }
}