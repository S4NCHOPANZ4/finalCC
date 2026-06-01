package co.ciencias.finalcc.model.enums;

// Importamos las especialidades estáticamente para mantener un código limpio
import static co.ciencias.finalcc.model.enums.Especialidad.BRIGADISTA;
import static co.ciencias.finalcc.model.enums.Especialidad.HANDYMAN;
import static co.ciencias.finalcc.model.enums.Especialidad.SEGURIDAD_RUTA;

public enum TipoEmergencia {
    // Vinculamos cada emergencia con su nivel de prioridad (valor) y su Especialidad técnica asignada
    MEDICA(1, BRIGADISTA),
    SEGURIDAD_PUBLICA(2, SEGURIDAD_RUTA),
    PROTECCION_CIVIL(3, SEGURIDAD_RUTA),
    SERVICIOS_PUBLICOS(4, HANDYMAN),
    SERVICIOS_DE_APOYO(5, HANDYMAN);

    private final int valor;
    private final Especialidad especialidad;

    TipoEmergencia(int valor, Especialidad especialidad) {
        this.valor = valor;
        this.especialidad = especialidad;
    }

    public int getValor() {
        return valor;
    }

    /**
     * Retorna la especialidad del técnico requerida para atender 
     * este tipo de emergencia.
     */
    public Especialidad getEspecialidad() {
        return especialidad;
    }
}