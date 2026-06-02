package co.ciencias.finalcc.model;

import co.ciencias.finalcc.model.enums.Especialidad;
import static co.ciencias.finalcc.model.enums.Especialidad.BRIGADISTA;
import static co.ciencias.finalcc.model.enums.Especialidad.HANDYMAN;
import static co.ciencias.finalcc.model.enums.Especialidad.SEGURIDAD_RUTA;
import co.ciencias.finalcc.model.enums.EstadoTecnico;
import co.ciencias.finalcc.model.enums.EstadoUnidad;
import co.ciencias.finalcc.model.enums.TipoUnidad;
import co.ciencias.finalcc.model.enums.EstadoSolicitud;

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
     * Toma la solicitud de mayor prioridad, le asigna un técnico Y una unidad
     * disponibles, y la mueve a la cola FIFO de ejecución.
     */
    public boolean atenderSiguiente() {
        if (solicitudesPendientes.esVacia()) return false;
        if (stockKitsManuales <= 0)          return false;

        // Busca la primera solicitud cuyo técnico Y unidad requeridos estén disponibles
        SolicitudServicio candidata  = null;
        Tecnico           tec        = null;
        UnidadServicio    unidad     = null;

        Nodo<SolicitudServicio> actual = solicitudesPendientes.getCabeza();
        while (actual != null) {
            SolicitudServicio sol      = actual.getDato();
            Especialidad      esp      = sol.getTipoEmergencia().getEspecialidad();
            TipoUnidad        tipoUni  = sol.getTipoEmergencia().getTipoUnidad();

            Tecnico        found    = buscarTecnico(esp);
            UnidadServicio foundUni = buscarUnidad(tipoUni);

            if (found != null && foundUni != null) {
                candidata = sol;
                tec       = found;
                unidad    = foundUni;
                break;
            }
            actual = actual.getSiguiente();
        }

        if (candidata == null) return false;

        solicitudesPendientes.eliminar(candidata);
        stockKitsManuales--;

        tec.setEstado(EstadoTecnico.OCUPADA);
        unidad.setEstado(EstadoUnidad.ASIGNADO);

        candidata.setTecnicoAsignado(tec);
        candidata.setUnidadAsignada(unidad);
        candidata.setEstado(EstadoSolicitud.EN_PROCESO);

        solicitudesEnEjecucion.encolar(candidata);
        return true;
    }

    /**
     * Finaliza la solicitud activa más antigua (FIFO), libera el técnico
     * y la unidad asignados, y coloca un kit gastado en la pila de mantenimiento.
     */
    public boolean terminarSolicitudActiva() {
        if (solicitudesEnEjecucion.esVacia()) return false;

        SolicitudServicio sol = solicitudesEnEjecucion.desencolar();
        sol.setEstado(EstadoSolicitud.FINALIZADA);

        if (sol.getTecnicoAsignado() != null) {
            sol.getTecnicoAsignado().setEstado(EstadoTecnico.DISPONIBLE);
        }

        // ← NUEVO: liberar la unidad asignada
        if (sol.getUnidadAsignada() != null) {
            sol.getUnidadAsignada().setEstado(EstadoUnidad.DISPONIBLE);
        }

        Kit kitGastado = new Kit("Gastado en: " + sol.getCliente().getNombre());
        kitGastado.setCompleto(false);
        pilaKitsDañados.push(kitGastado);

        return true;
    }

    public int getKitsReparadosHoy() { return kitsReparadosHoy; }
    public void resetKitsReparadosHoy() { kitsReparadosHoy = 0; }

    public boolean repararKitTope() {
        if (pilaKitsDañados.esVacia()) return false;
        pilaKitsDañados.pop();
        stockKitsManuales++;
        kitsReparadosHoy++;
        return true;
    }

    public void encolarSolicitud(SolicitudServicio solicitud) {
        solicitudesPendientes.insertar(solicitud);
    }

    public SolicitudServicio extraerSiguiente() { return solicitudesPendientes.extraer(); }
    public SolicitudServicio verSiguiente()      { return solicitudesPendientes.verFrente(); }

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

    public int    getIndice()       { return indice; }
    public String getNombre()       { return nombre; }
    public int    getContadorKits() { return stockKitsManuales; }

    public ColaPrioridad                 getSolicitudesPendientes()   { return solicitudesPendientes; }
    public Cola<SolicitudServicio>       getSolicitudesEnEjecucion()  { return solicitudesEnEjecucion; }
    public Pila<Kit>                     getPilaKitsDañados()         { return pilaKitsDañados; }
    public ListaEnlazada<UnidadServicio> getUnidades()                { return unidades; }
    public ListaEnlazada<Tecnico>        getTecnicos()                { return tecnicos; }

    // ------------------------------------------------------------------
    // Cierre de día
    // ------------------------------------------------------------------

    public void cerrarDia() {
        while (!solicitudesEnEjecucion.esVacia()) {
            SolicitudServicio sol = solicitudesEnEjecucion.desencolar();
            sol.setEstado(EstadoSolicitud.FINALIZADA);
            if (sol.getTecnicoAsignado() != null) {
                sol.getTecnicoAsignado().setEstado(EstadoTecnico.DISPONIBLE);
            }
            // ← NUEVO: liberar la unidad también al cerrar el día
            if (sol.getUnidadAsignada() != null) {
                sol.getUnidadAsignada().setEstado(EstadoUnidad.DISPONIBLE);
            }
            Kit kitGastado = new Kit("Cierre de día: " + sol.getCliente().getNombre());
            kitGastado.setCompleto(false);
            pilaKitsDañados.push(kitGastado);
        }
        kitsReparadosHoy = 0;
    }

    // ------------------------------------------------------------------
    // Inicialización de recursos
    // ------------------------------------------------------------------

    private void inicializarRecursos() {
        String p = nombre.substring(0, 1); // "N" o "S"

        for (int i = 1; i <= 3; i++)
            unidades.agregar(new UnidadServicio(TipoUnidad.GRUA,                EstadoUnidad.DISPONIBLE, indice, p + "-GRU-" + fmt(i)));
        for (int i = 1; i <= 5; i++)
            unidades.agregar(new UnidadServicio(TipoUnidad.MOTO,                EstadoUnidad.DISPONIBLE, indice, p + "-MOT-" + fmt(i)));
        for (int i = 1; i <= 3; i++)
            unidades.agregar(new UnidadServicio(TipoUnidad.CAMIONETA_ASISTENCIA, EstadoUnidad.DISPONIBLE, indice, p + "-CAM-" + fmt(i)));
        for (int i = 1; i <= 3; i++)
            unidades.agregar(new UnidadServicio(TipoUnidad.VEHICULO_LIVIANO,    EstadoUnidad.DISPONIBLE, indice, p + "-VLI-" + fmt(i)));

        for (int i = 1; i <= 3; i++)
            tecnicos.agregar(new Tecnico(p + "-BRI-" + fmt(i), BRIGADISTA,     EstadoTecnico.DISPONIBLE, indice));
        for (int i = 1; i <= 4; i++)
            tecnicos.agregar(new Tecnico(p + "-SEG-" + fmt(i), SEGURIDAD_RUTA, EstadoTecnico.DISPONIBLE, indice));
        for (int i = 1; i <= 7; i++)
            tecnicos.agregar(new Tecnico(p + "-HAN-" + fmt(i), HANDYMAN,       EstadoTecnico.DISPONIBLE, indice));
    }
    public boolean revertirUltimaEjecucion() {
        if (solicitudesEnEjecucion.esVacia()) return false;

        SolicitudServicio sol = solicitudesEnEjecucion.desencolarUltimo();
        if (sol == null) return false;

        // Liberar técnico
        if (sol.getTecnicoAsignado() != null) {
            sol.getTecnicoAsignado().setEstado(EstadoTecnico.DISPONIBLE);
            sol.setTecnicoAsignado(null);
        }
        // Liberar unidad
        if (sol.getUnidadAsignada() != null) {
            sol.getUnidadAsignada().setEstado(EstadoUnidad.DISPONIBLE);
            sol.setUnidadAsignada(null);
        }
        // Restaurar estado y kit
        sol.setEstado(EstadoSolicitud.PENDIENTE);
        stockKitsManuales++;   // el kit "vuelve" porque nunca se dañó

        // Reinsertar en cola de prioridad con su posición original
        solicitudesPendientes.insertar(sol);
        return true;
    }
    private static String fmt(int n) {
        return n < 10 ? "0" + n : String.valueOf(n);
    }
}