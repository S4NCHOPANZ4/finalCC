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
    private final int indice;
    private final String nombre;
    private int kitsReparadosHoy = 0;
    private final ColaPrioridad solicitudesPendientes;
    private final Cola<SolicitudServicio> solicitudesEnEjecucion;
    private final Pila<Kit> pilaKitsDañados;
    private final ListaEnlazada<UnidadServicio> unidades;
    private final ListaEnlazada<Tecnico> tecnicos;

    private int stockKitsManuales;

    public PuestoAtencion(int indice) {
        this.indice = indice;
        this.nombre = GrafoCiudad.NOMBRES_PUESTOS[indice];

        this.solicitudesPendientes  = new ColaPrioridad();
        this.solicitudesEnEjecucion = new Cola<>();
        this.pilaKitsDañados = new Pila<>();
        this.unidades = new ListaEnlazada<>();
        this.tecnicos = new ListaEnlazada<>();
        this.stockKitsManuales = 10;

        inicializarRecursos();
    }

    public boolean atenderSiguiente() {
        if (solicitudesPendientes.esVacia()) return false;
        if (stockKitsManuales <= 0) return false;

        SolicitudServicio candidata  = null;
        Tecnico tec = null;
        UnidadServicio unidad = null;

        Nodo<SolicitudServicio> actual = solicitudesPendientes.getCabeza();
        while (actual != null) {
            SolicitudServicio sol = actual.getDato();
            Especialidad esp = sol.getTipoEmergencia().getEspecialidad();
            TipoUnidad tipoUni  = sol.getTipoEmergencia().getTipoUnidad();

            Tecnico found = buscarTecnico(esp);
            UnidadServicio foundUni = buscarUnidad(tipoUni);

            if (found != null && foundUni != null) {
                candidata = sol;
                tec = found;
                unidad = foundUni;
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

    public boolean terminarSolicitudActiva() {
        if (solicitudesEnEjecucion.esVacia()) return false;

        SolicitudServicio sol = solicitudesEnEjecucion.desencolar();
        sol.setEstado(EstadoSolicitud.FINALIZADA);

        if (sol.getTecnicoAsignado() != null) {
            sol.getTecnicoAsignado().setEstado(EstadoTecnico.DISPONIBLE);
        }
        if (sol.getUnidadAsignada() != null) {
            // 50% de probabilidad de que el vehiculo entre en mantenimiento
            boolean entraMantenimiento = Math.random() < 0.5;
            if (entraMantenimiento) {
                sol.getUnidadAsignada().setEstado(EstadoUnidad.EN_MANTENIMIENTO);
            } else {
                sol.getUnidadAsignada().setEstado(EstadoUnidad.DISPONIBLE);
            }
        }

        Kit kitGastado = new Kit("Gastado en: " + sol.getCliente().getNombre());
        kitGastado.setCompleto(false);
        pilaKitsDañados.push(kitGastado);

        return true;
    }

    /**
     * Repara el vehiculo con el id dado si está en EN_MANTENIMIENTO
     * y lo devuelve a DISPONIBLE.
     * @param id UUID del vehículo a reparar
     * @return true si se reparó, false si no se encontró o no estaba en mantenimiento
     */
    public boolean repararVehiculo(String id) {
        if (id == null) return false;
        Nodo<UnidadServicio> actual = unidades.getCabeza();
        while (actual != null) {
            UnidadServicio u = actual.getDato();
            if (u.getId().equals(id) && u.getEstado() == EstadoUnidad.EN_MANTENIMIENTO) {
                u.setEstado(EstadoUnidad.DISPONIBLE);
                return true;
            }
            actual = actual.getSiguiente();
        }
        return false;
    }

    public boolean revertirUltimaEjecucion() {
        if (solicitudesEnEjecucion.esVacia()) return false;

        SolicitudServicio sol = solicitudesEnEjecucion.desencolarUltimo();
        if (sol == null) return false;

        if (sol.getTecnicoAsignado() != null) {
            sol.getTecnicoAsignado().setEstado(EstadoTecnico.DISPONIBLE);
            sol.setTecnicoAsignado(null);
        }
        if (sol.getUnidadAsignada() != null) {
            sol.getUnidadAsignada().setEstado(EstadoUnidad.DISPONIBLE);
            sol.setUnidadAsignada(null);
        }

        sol.setEstado(EstadoSolicitud.PENDIENTE);
        stockKitsManuales++;

        solicitudesPendientes.insertar(sol);
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

    public Tecnico agregarTecnico(String nombre, Especialidad especialidad) {
        Tecnico t = new Tecnico(nombre, especialidad, EstadoTecnico.DISPONIBLE, indice);
        tecnicos.agregar(t);
        return t;
    }

    public boolean editarTecnico(String id, String nuevoNombre, Especialidad nuevaEsp) {
        if (id == null) return false;
        Nodo<Tecnico> actual = tecnicos.getCabeza();
        while (actual != null) {
            Tecnico t = actual.getDato();
            if (t.getId().equals(id)) {
                if (t.getEstado() == EstadoTecnico.OCUPADA) return false; 
                if (nuevoNombre != null && !nuevoNombre.trim().isEmpty()) {
                    Tecnico editado = new Tecnico(
                        nuevoNombre.trim(),
                        nuevaEsp != null ? nuevaEsp : t.getEspecialidad(),
                        t.getEstado(),
                        t.getZona()
                    );
                    actual.setDato(editado);
                } else if (nuevaEsp != null) {
                    Tecnico editado = new Tecnico(
                        t.getNombre(),
                        nuevaEsp,
                        t.getEstado(),
                        t.getZona()
                    );
                    actual.setDato(editado);
                }
                return true;
            }
            actual = actual.getSiguiente();
        }
        return false;
    }

    public UnidadServicio agregarUnidad(TipoUnidad tipo, String codigo) {
        UnidadServicio u = new UnidadServicio(tipo, EstadoUnidad.DISPONIBLE, indice, codigo);
        unidades.agregar(u);
        return u;
    }


    public boolean editarUnidad(String id, String nuevoCod, TipoUnidad nuevoTipo) {
        if (id == null) return false;
        Nodo<UnidadServicio> actual = unidades.getCabeza();
        while (actual != null) {
            UnidadServicio u = actual.getDato();
            if (u.getId().equals(id)) {
                if (u.getEstado() == EstadoUnidad.ASIGNADO) return false; 
                String  cod  = (nuevoCod  != null && !nuevoCod.trim().isEmpty())  ? nuevoCod.trim()  : u.getCodigo();
                TipoUnidad tp = nuevoTipo != null ? nuevoTipo : u.getTipo();
                UnidadServicio editada = new UnidadServicio(tp, u.getEstado(), u.getZona(), cod);
                actual.setDato(editada);
                return true;
            }
            actual = actual.getSiguiente();
        }
        return false;
    }

    public boolean eliminarTecnico(String id) {
        if (id == null) return false;
        Nodo<Tecnico> actual = tecnicos.getCabeza();
        while (actual != null) {
            Tecnico t = actual.getDato();
            if (t.getId().equals(id)) {
                if (t.getEstado() == EstadoTecnico.OCUPADA) return false; 
                tecnicos.eliminar(t);
                return true;
            }
            actual = actual.getSiguiente();
        }
        return false;
    }

    public boolean eliminarUnidad(String id) {
        if (id == null) return false;
        Nodo<UnidadServicio> actual = unidades.getCabeza();
        while (actual != null) {
            UnidadServicio u = actual.getDato();
            if (u.getId().equals(id)) {
                if (u.getEstado() == EstadoUnidad.ASIGNADO) return false;   
                unidades.eliminar(u);
                return true;
            }
            actual = actual.getSiguiente();
        }
        return false;
    }


    public int getIndice(){ return indice; }
    public String getNombre(){ return nombre; }
    public int getContadorKits(){ return stockKitsManuales; }

    public ColaPrioridad getSolicitudesPendientes() { return solicitudesPendientes; }
    public Cola<SolicitudServicio> getSolicitudesEnEjecucion()  { return solicitudesEnEjecucion; }
    public Pila<Kit> getPilaKitsDañados() { return pilaKitsDañados; }
    public ListaEnlazada<UnidadServicio> getUnidades() { return unidades; }
    public ListaEnlazada<Tecnico> getTecnicos() { return tecnicos; }


    public void cerrarDia() {
        while (!solicitudesEnEjecucion.esVacia()) {
            SolicitudServicio sol = solicitudesEnEjecucion.desencolar();
            sol.setEstado(EstadoSolicitud.FINALIZADA);
            if (sol.getTecnicoAsignado() != null) {
                sol.getTecnicoAsignado().setEstado(EstadoTecnico.DISPONIBLE);
            }
            if (sol.getUnidadAsignada() != null) {
                sol.getUnidadAsignada().setEstado(EstadoUnidad.DISPONIBLE);
            }
            Kit kitGastado = new Kit("Cierre de día: " + sol.getCliente().getNombre());
            kitGastado.setCompleto(false);
            pilaKitsDañados.push(kitGastado);
        }
        kitsReparadosHoy = 0;
    }


    private void inicializarRecursos() {
        String p = nombre.substring(0, 1); 

        for (int i = 1; i <= 3; i++)
            unidades.agregar(new UnidadServicio(TipoUnidad.GRUA, EstadoUnidad.DISPONIBLE, indice, p + "-GRU-" + fmt(i)));
        for (int i = 1; i <= 5; i++)
            unidades.agregar(new UnidadServicio(TipoUnidad.MOTO, EstadoUnidad.DISPONIBLE, indice, p + "-MOT-" + fmt(i)));
        for (int i = 1; i <= 3; i++)
            unidades.agregar(new UnidadServicio(TipoUnidad.CAMIONETA_ASISTENCIA, EstadoUnidad.DISPONIBLE, indice, p + "-CAM-" + fmt(i)));
        for (int i = 1; i <= 3; i++)
            unidades.agregar(new UnidadServicio(TipoUnidad.VEHICULO_LIVIANO, EstadoUnidad.DISPONIBLE, indice, p + "-VLI-" + fmt(i)));
        for (int i = 1; i <= 3; i++)
            tecnicos.agregar(new Tecnico(p + "-BRI-" + fmt(i), BRIGADISTA,EstadoTecnico.DISPONIBLE, indice));
        for (int i = 1; i <= 4; i++)
            tecnicos.agregar(new Tecnico(p + "-SEG-" + fmt(i), SEGURIDAD_RUTA, EstadoTecnico.DISPONIBLE, indice));
        for (int i = 1; i <= 7; i++)
            tecnicos.agregar(new Tecnico(p + "-HAN-" + fmt(i), HANDYMAN, EstadoTecnico.DISPONIBLE, indice));
    }

    private static String fmt(int n) {
        return n < 10 ? "0" + n : String.valueOf(n);
    }
}