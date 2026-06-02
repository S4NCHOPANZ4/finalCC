package co.ciencias.finalcc.model.enums;

import static co.ciencias.finalcc.model.enums.Especialidad.BRIGADISTA;
import static co.ciencias.finalcc.model.enums.Especialidad.HANDYMAN;
import static co.ciencias.finalcc.model.enums.Especialidad.SEGURIDAD_RUTA;

public enum TipoEmergencia {

    MEDICA (1, BRIGADISTA,     TipoUnidad.VEHICULO_LIVIANO),
    SEGURIDAD_PUBLICA(2, SEGURIDAD_RUTA, TipoUnidad.CAMIONETA_ASISTENCIA),
    PROTECCION_CIVIL (3, SEGURIDAD_RUTA, TipoUnidad.CAMIONETA_ASISTENCIA),
    SERVICIOS_PUBLICOS(4, HANDYMAN,      TipoUnidad.GRUA),
    SERVICIOS_DE_APOYO(5, HANDYMAN,      TipoUnidad.MOTO);

    private final int        valor;
    private final Especialidad especialidad;
    private final TipoUnidad   tipoUnidad;

    TipoEmergencia(int valor, Especialidad especialidad, TipoUnidad tipoUnidad) {
        this.valor        = valor;
        this.especialidad = especialidad;
        this.tipoUnidad   = tipoUnidad;
    }

    public int getValor() { return valor; }
    public Especialidad getEspecialidad() { return especialidad; }
    public TipoUnidad getTipoUnidad() { return tipoUnidad; }
}