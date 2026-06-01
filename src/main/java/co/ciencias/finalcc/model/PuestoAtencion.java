package co.ciencias.finalcc.model;

import co.ciencias.finalcc.model.enums.Especialidad;
import static co.ciencias.finalcc.model.enums.Especialidad.BRIGADISTA;
import static co.ciencias.finalcc.model.enums.Especialidad.HANDYMAN;
import static co.ciencias.finalcc.model.enums.Especialidad.SEGURIDAD_RUTA;
import co.ciencias.finalcc.model.enums.EstadoTecnico;
import co.ciencias.finalcc.model.enums.EstadoUnidad;
import co.ciencias.finalcc.model.enums.TipoUnidad;
import co.ciencias.finalcc.model.enums.EstadoSolicitud;

/**
 * Puesto de atención de AutoRescate 24/7.
 *
 * <p>Solo existen dos instancias: índice 0 → <b>Norte</b>, índice 1 → <b>Sur</b>.
 * El nombre se deriva del array {@link GrafoCiudad#NOMBRES_PUESTOS} para que
 * no haya magia en el código.</p>
 *
 * <p>Mantiene:</p>
 * <ul>
 *   <li>{@link ColaPrioridad} de solicitudes pendientes.</li>
 *   <li>{@link Cola} FIFO de solicitudes en ejecución.</li>
 *   <li>{@link Pila} LIFO de kits dañados.</li>
 *   <li>{@link ListaEnlazada} de técnicos y unidades de servicio.</li>
 * </ul>
 */
public class PuestoAtencion {

    public static final int NORTE = 0;
    public static final int SUR   = 1;
    private final int    indice;
    private final String nombre;
    private int kitsReparadosHoy = 0;
    private final ColaPrioridad                  solicitudesPendientes;
    private final Cola<SolicitudServicio>         solicitudesEnEjecucion;
    private final Pila<Kit>                      pilaKitsDañados;
    private final ListaEnlazada<UnidadServicio>  unidades;
    private final ListaEnlazada<Tecnico>         tecnicos;

    private int stockKitsManuales;

    public PuestoAtencion(int indice) {
        this.indice = indice;
        this.nombre = GrafoCiudad.NOMBRES_PUESTOS[indice];

        this.solicitudesPendientes  = new ColaPrioridad();
        this.solicitudesEnEjecucion = new Cola<>();
        this.pilaKitsDañados        = new Pila<>();
        this.unidades               = new ListaEnlazada<>();
        this.tecnicos               = new ListaEnlazada<>();
        this.stockKitsManuales      = 10;

        inicializarRecursos();
    }

    // ------------------------------------------------------------------
    // Operaciones principales
    // ------------------------------------------------------------------

    /**
     * Toma la solicitud de mayor prioridad, le asigna un técnico disponible
     * y la mueve a la cola FIFO de ejecución.
     *
     * @return {@code true} si se pudo atender, {@code false} si no hay
     *         solicitudes, no hay kits o no hay técnico disponible
     */
    public boolean atenderSiguiente() {
        if (solicitudesPendientes.esVacia()) return false;
        if (stockKitsManuales <= 0)          return false;

        // Recorre la cola en orden de prioridad buscando la primera solicitud
        // cuyo técnico requerido tenga al menos uno disponible en este puesto
        SolicitudServicio candidata = null;
        Tecnico           tec       = null;

        Nodo<SolicitudServicio> actual = solicitudesPendientes.getCabeza();
        while (actual != null) {
            SolicitudServicio sol     = actual.getDato();
            Especialidad      esp     = sol.getTipoEmergencia().getEspecialidad();
            Tecnico           found   = buscarTecnico(esp);
            if (found != null) {
                candidata = sol;
                tec       = found;
                break;
            }
            actual = actual.getSiguiente();
        }

        if (candidata == null) return false; // todos los técnicos requeridos están ocupados

        solicitudesPendientes.eliminar(candidata);
        stockKitsManuales--;
        tec.setEstado(EstadoTecnico.OCUPADA);

        candidata.setTecnicoAsignado(tec);
        candidata.setEstado(EstadoSolicitud.EN_PROCESO);

        solicitudesEnEjecucion.encolar(candidata);
        return true;
    }
    /**
     * Finaliza la solicitud activa más antigua (FIFO), libera el técnico
     * y coloca un kit gastado en la pila de mantenimiento.
     *
     * @return {@code true} si había algo en ejecución
     */
    public boolean terminarSolicitudActiva() {
        if (solicitudesEnEjecucion.esVacia()) return false;

        SolicitudServicio sol = solicitudesEnEjecucion.desencolar();
        sol.setEstado(EstadoSolicitud.FINALIZADA);

        if (sol.getTecnicoAsignado() != null) {
            sol.getTecnicoAsignado().setEstado(EstadoTecnico.DISPONIBLE);
        }

        Kit kitGastado = new Kit("Gastado en: " + sol.getCliente().getNombre());
        kitGastado.setCompleto(false);
        pilaKitsDañados.push(kitGastado);

        return true;
    }

    /**
     * Repara el kit del tope de la pila de mantenimiento y lo devuelve
     * al stock disponible.
     *
     * @return {@code true} si había un kit que reparar
     */
    
    public int getKitsReparadosHoy() { return kitsReparadosHoy; }
    public void resetKitsReparadosHoy() { kitsReparadosHoy = 0; }
    public boolean repararKitTope() {
        if (pilaKitsDañados.esVacia()) return false;
        pilaKitsDañados.pop();
        stockKitsManuales++;
        kitsReparadosHoy++;   // ← línea nueva
        return true;
    }

    /** Encola una nueva solicitud en la cola de prioridad. */
    public void encolarSolicitud(SolicitudServicio solicitud) {
        solicitudesPendientes.insertar(solicitud);
    }

    /** Extrae la solicitud de mayor prioridad sin ningún efecto secundario. */
    public SolicitudServicio extraerSiguiente() { return solicitudesPendientes.extraer(); }

    /** Consulta la solicitud de mayor prioridad sin extraerla. */
    public SolicitudServicio verSiguiente() { return solicitudesPendientes.verFrente(); }

    // ------------------------------------------------------------------
    // Búsqueda de recursos
    // ------------------------------------------------------------------

    public UnidadServicio buscarUnidad(TipoUnidad tipo) {
        Nodo<UnidadServicio> actual = unidades.getCabeza();
        while (actual != null) {
            UnidadServicio u = actual.getDato();
            if (u.getTipo() == tipo && u.getEstado() == EstadoUnidad.DISPONIBLE) return u;
            actual = actual.getSiguiente();
        }
        return null;
    }

    public Tecnico buscarTecnico(Especialidad esp) {
        Nodo<Tecnico> actual = tecnicos.getCabeza();
        while (actual != null) {
            Tecnico t = actual.getDato();
            if (t.getEspecialidad() == esp && t.getEstado() == EstadoTecnico.DISPONIBLE) return t;
            actual = actual.getSiguiente();
        }
        return null;
    }

    // ------------------------------------------------------------------
    // Getters
    // ------------------------------------------------------------------

    public int    getIndice()                { return indice; }
    public String getNombre()                { return nombre; }
    public int    getContadorKits()          { return stockKitsManuales; }

    public ColaPrioridad                 getSolicitudesPendientes()   { return solicitudesPendientes; }
    public Cola<SolicitudServicio>       getSolicitudesEnEjecucion()  { return solicitudesEnEjecucion; }
    public Pila<Kit>                     getPilaKitsDañados()         { return pilaKitsDañados; }
    public ListaEnlazada<UnidadServicio> getUnidades()                { return unidades; }
    public ListaEnlazada<Tecnico>        getTecnicos()                { return tecnicos; }

    // ------------------------------------------------------------------
    // Inicialización de recursos
    // ------------------------------------------------------------------

    private void inicializarRecursos() {
        String p = nombre.substring(0, 1); // "N" o "S"

        for (int i = 1; i <= 3; i++)
            unidades.agregar(new UnidadServicio(TipoUnidad.GRUA,               EstadoUnidad.DISPONIBLE, indice, p + "-GRU-" + fmt(i)));
        for (int i = 1; i <= 5; i++)
            unidades.agregar(new UnidadServicio(TipoUnidad.MOTO,               EstadoUnidad.DISPONIBLE, indice, p + "-MOT-" + fmt(i)));
        for (int i = 1; i <= 3; i++)
            unidades.agregar(new UnidadServicio(TipoUnidad.CAMIONETA_ASISTENCIA, EstadoUnidad.DISPONIBLE, indice, p + "-CAM-" + fmt(i)));
        for (int i = 1; i <= 3; i++)
            unidades.agregar(new UnidadServicio(TipoUnidad.VEHICULO_LIVIANO,   EstadoUnidad.DISPONIBLE, indice, p + "-VLI-" + fmt(i)));

        for (int i = 1; i <= 3; i++)
            tecnicos.agregar(new Tecnico(p + "-BRI-" + fmt(i), BRIGADISTA,     EstadoTecnico.DISPONIBLE, indice));
        for (int i = 1; i <= 4; i++)
            tecnicos.agregar(new Tecnico(p + "-SEG-" + fmt(i), SEGURIDAD_RUTA, EstadoTecnico.DISPONIBLE, indice));
        for (int i = 1; i <= 7; i++)
            tecnicos.agregar(new Tecnico(p + "-HAN-" + fmt(i), HANDYMAN,       EstadoTecnico.DISPONIBLE, indice));
    }
/**
 * Cierra el día operativo del puesto:
 * - Las solicitudes EN_PROCESO se marcan FINALIZADA y se vacía la cola de ejecución.
 * - Las solicitudes PENDIENTE en la cola de prioridad se conservan para el día siguiente.
 * - Los kits en la pila de mantenimiento se conservan.
 * - Los técnicos ocupados quedan DISPONIBLE.
 * - El contador de kits reparados se reinicia para el nuevo día.
 */
    public void cerrarDia() {
        // Finalizar todo lo que estaba EN_PROCESO
        while (!solicitudesEnEjecucion.esVacia()) {
            SolicitudServicio sol = solicitudesEnEjecucion.desencolar();
            sol.setEstado(EstadoSolicitud.FINALIZADA);
            if (sol.getTecnicoAsignado() != null) {
                sol.getTecnicoAsignado().setEstado(EstadoTecnico.DISPONIBLE);
            }
            // Kit gastado va a la pila de mantenimiento (igual que terminarSolicitudActiva)
            Kit kitGastado = new Kit("Cierre de día: " + sol.getCliente().getNombre());
            kitGastado.setCompleto(false);
            pilaKitsDañados.push(kitGastado);
        }
        // Resetear contador diario de reparaciones
        kitsReparadosHoy = 0;
    }
    private static String fmt(int n) {
        return n < 10 ? "0" + n : String.valueOf(n);
    }
}