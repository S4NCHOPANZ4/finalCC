package co.ciencias.finalcc.model.enums;

public enum TipoEmergencia {

    MEDICA(1),
    SEGURIDAD_PUBLICA(2),
    PROTECCION_CIVIL(3),
    SERVICIOS_PUBLICOS(4),
    SERVICIOS_APOYO(5);

    private int valor;

    TipoEmergencia(int valor) {
        this.valor = valor;
    }

    public int getValor() {
        return valor;
    }
}