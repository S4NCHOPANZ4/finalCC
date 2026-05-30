package co.ciencias.finalcc2.model.enums;

public enum TipoEmergencia {
    MEDICA(1),
    SEGURIDAD_PUBLICA(2),
    PROTECCION_CIVIL(3),
    SERVICIOS_PUBLICOS(4),
    SERVICIOS_DE_APOYO(5);

    private final int valor;

    TipoEmergencia(int valor) {
        this.valor = valor;
    }

    public int getValor() {
        return valor;
    }
}