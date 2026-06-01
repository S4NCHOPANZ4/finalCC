package co.ciencias.finalcc.model.gestores;

import co.ciencias.finalcc.model.enums.EstadoSolicitud;
import co.ciencias.finalcc.model.enums.TipoEmergencia;
import co.ciencias.finalcc.model.GrafoCiudad;
import co.ciencias.finalcc.model.PuestoAtencion;
import co.ciencias.finalcc.model.SolicitudServicio;
import co.ciencias.finalcc.model.ListaEnlazada;
import co.ciencias.finalcc.model.Cliente;
import co.ciencias.finalcc.model.Kit;
import co.ciencias.finalcc.model.Nodo;

/**
 * Gestor de solicitudes de servicio.
 *
 * <p>Usa {@link GrafoCiudad} para determinar el puesto más cercano al nodo
 * del cliente mediante BFS, eliminando por completo la dependencia de
 * {@code ZonaCalculadora} y la conversión ángulo/círculo → coordenada.</p>
 *
 * <p>Solo existen 2 puestos: Norte (0) y Sur (1).</p>
 */
public class GestorSolicitudes {

    private static GestorSolicitudes instancia;
    private final ListaEnlazada<SolicitudServicio> historicoGlobal;

    private GestorSolicitudes() {
        this.historicoGlobal = new ListaEnlazada<>();
    }

    public static GestorSolicitudes getInstancia() {
        if (instancia == null) instancia = new GestorSolicitudes();
        return instancia;
    }

    /**
     * Crea una solicitud y la encola en el puesto más cercano al nodo del cliente.
     *
     * @param cliente          cliente que reporta la emergencia
     * @param tipo             tipo de emergencia
     * @param descripcion      descripción del incidente
     * @param indiceNodoCliente índice del nodo del grafo donde está el cliente (0–9)
     * @return la solicitud creada, o {@code null} si los parámetros son inválidos
     */
    public SolicitudServicio crearSolicitud(Cliente cliente,
                                            TipoEmergencia tipo,
                                            String descripcion,
                                            int indiceNodoCliente) {
        if (cliente == null) return null;

        // BFS determina el puesto (0 = Norte, 1 = Sur)
        int indicePuesto = GrafoCiudad.getInstancia().calcularPuesto(indiceNodoCliente);

        SolicitudServicio solicitud = new SolicitudServicio(
                cliente, tipo, descripcion, indiceNodoCliente, indicePuesto);

        PuestoAtencion puesto = GestorRecursos.getInstancia().getPuesto(indicePuesto);
        puesto.encolarSolicitud(solicitud);

        System.out.printf("[GestorSolicitudes] Solicitud creada → cliente=%s | nodo=%s | puesto=%s | emergencia=%s%n",
                cliente.getNombre(),
                GrafoCiudad.getInstancia().nombreNodo(indiceNodoCliente),
                GrafoCiudad.NOMBRES_PUESTOS[indicePuesto],
                tipo);

        historicoGlobal.agregar(solicitud);
        return solicitud;
    }

    public void finalizarSolicitud(SolicitudServicio solicitud) {
        if (solicitud == null || solicitud.getEstado() != EstadoSolicitud.EN_PROCESO) return;
        solicitud.setEstado(EstadoSolicitud.FINALIZADA);
    }

    public ListaEnlazada<SolicitudServicio> getHistoricoGlobal() {
        return historicoGlobal;
    }
    
    /**
 * Genera un CSV con el resumen del día:
 * - Todas las solicitudes del histórico global con su estado final.
 * - Resumen de kits por puesto.
 *
 * Después de generar el reporte, limpia el histórico para el día siguiente
 * (las solicitudes PENDIENTE siguen vivas en los puestos, solo se borra
 * la referencia del histórico global).
 */
/**
 * Genera el CSV del día para UN puesto específico y lo escribe en disco.
 * Retorna la ruta absoluta del archivo generado, o cadena vacía si falla.
 *
 * Reglas de cierre:
 * - Solicitudes FINALIZADA y EN_PROCESO (ya cerradas por cerrarDia()) → aparecen en el CSV.
 * - Solicitudes PENDIENTE → aparecen en el CSV pero NO se eliminan del puesto.
 * - El histórico global se limpia SOLO de las solicitudes del puesto procesado.
 */
public String generarCsvDia(int indicePuesto) {

    System.out.println("[CSV] Iniciando generarCsvDia para puesto " + indicePuesto);

    // ── Ruta de destino: carpeta Documentos del usuario ───────────────
    java.nio.file.Path carpeta = java.nio.file.Paths.get(
            System.getProperty("user.home"), "Downloads", "reportes_autorescate");

    try {
        java.nio.file.Files.createDirectories(carpeta);
        System.out.println("[CSV] Carpeta destino: " + carpeta.toAbsolutePath());
    } catch (Exception e) {
        System.err.println("[CSV] No se pudo crear carpeta: " + e.getMessage());
        return "ERROR: no se pudo crear carpeta: " + e.getMessage();
    }

    String nombrePuesto = GrafoCiudad.NOMBRES_PUESTOS[indicePuesto];
    PuestoAtencion p    = GestorRecursos.getInstancia().getPuesto(indicePuesto);

    // ── Construir contenido ───────────────────────────────────────────
    StringBuilder sb = new StringBuilder();
    sb.append("\uFEFF"); // BOM UTF-8 para Excel

    // Sección 1 — Solicitudes
    sb.append("SECCION,UUID,PUESTO,ESTADO,CLIENTE,TELEFONO,TIPO_EMERGENCIA,")
      .append("NODO_UBICACION,TECNICO_ASIGNADO,DESCRIPCION,TIMESTAMP\n");

    int totalSolicitudes = 0;
    Nodo<SolicitudServicio> nodo = historicoGlobal.getCabeza();
    while (nodo != null) {
        SolicitudServicio s = nodo.getDato();
        if (s.getIndicePuesto() == indicePuesto) {
            sb.append("SOLICITUD,")
              .append(s.getId()).append(",")
              .append(esc(nombrePuesto)).append(",")
              .append(s.getEstado()).append(",")
              .append(esc(s.getCliente().getNombre())).append(",")
              .append(esc(s.getCliente().getTelefono())).append(",")
              .append(s.getTipoEmergencia()).append(",")
              .append(esc(GrafoCiudad.getInstancia().nombreNodo(s.getIndiceNodo()))).append(",")
              .append(s.getTecnicoAsignado() != null
                      ? esc(s.getTecnicoAsignado().getNombre()) : "Sin asignar").append(",")
              .append(esc(s.getDescripcion())).append(",")
              .append(s.getTimestampFormateado()).append("\n");
            totalSolicitudes++;
        }
        nodo = nodo.getSiguiente();
    }
    System.out.println("[CSV] Solicitudes escritas: " + totalSolicitudes);

    // Sección 2 — Resumen kits
    sb.append("\nSECCION,PUESTO,KITS_DISPONIBLES,KITS_REPARADOS_HOY,KITS_EN_PILA_MANTENIMIENTO\n");
    int enPila = 0;
    Nodo<Kit> nKit = p.getPilaKitsDañados().getTope();
    while (nKit != null) { enPila++; nKit = nKit.getSiguiente(); }

    sb.append("KITS,")
      .append(esc(nombrePuesto)).append(",")
      .append(p.getContadorKits()).append(",")
      .append(p.getKitsReparadosHoy()).append(",")
      .append(enPila).append("\n");

    // Sección 3 — Detalle kits dañados
    sb.append("\nSECCION,UUID_KIT,PUESTO,DESCRIPCION,COMPLETO\n");
    Nodo<Kit> nKitDetalle = p.getPilaKitsDañados().getTope();
    while (nKitDetalle != null) {
        Kit k = nKitDetalle.getDato();
        sb.append("KIT_DAÑADO,")
          .append(k.getId()).append(",")
          .append(esc(nombrePuesto)).append(",")
          .append(esc(k.getDescripcion())).append(",")
          .append(k.isCompleto()).append("\n");
        nKitDetalle = nKitDetalle.getSiguiente();
    }
    System.out.println("[CSV] Kits en pila: " + enPila);

    // ── Escribir en disco ─────────────────────────────────────────────
    try {
        String fecha = java.time.LocalDate.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String nombreArchivo = "reporte_" + nombrePuesto.toLowerCase()
                             + "_" + fecha + ".csv";
        java.nio.file.Path archivo = carpeta.resolve(nombreArchivo);

        java.nio.file.Files.writeString(
                archivo,
                sb.toString(),
                java.nio.charset.StandardCharsets.UTF_8,
                java.nio.file.StandardOpenOption.CREATE,
                java.nio.file.StandardOpenOption.TRUNCATE_EXISTING);

        System.out.println("[CSV] Archivo escrito exitosamente: " + archivo.toAbsolutePath());

        // Limpiar histórico (solo finalizadas de este puesto)
        eliminarDelHistorico(indicePuesto);
        p.resetKitsReparadosHoy();

        return archivo.toAbsolutePath().toString();

    } catch (Exception e) {
        System.err.println("[CSV] FALLO al escribir archivo: "
                + e.getClass().getSimpleName() + ": " + e.getMessage());
        e.printStackTrace();
        return "ERROR: " + e.getMessage();
    }
}

private void eliminarDelHistorico(int indicePuesto) {
    // Reconstruye la lista excluyendo las solicitudes del puesto cerrado
    // que ya fueron finalizadas (PENDIENTE se conservan para el día siguiente)
    Nodo<SolicitudServicio> actual = historicoGlobal.getCabeza();
    while (actual != null) {
        SolicitudServicio s = actual.getDato();
        Nodo<SolicitudServicio> siguiente = actual.getSiguiente();
        if (s.getIndicePuesto() == indicePuesto
                && s.getEstado() != co.ciencias.finalcc.model.enums.EstadoSolicitud.PENDIENTE) {
            historicoGlobal.eliminar(s);
        }
        actual = siguiente;
    }
}

private String esc(String s) {
    if (s == null) return "";
    if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
        return "\"" + s.replace("\"", "\"\"") + "\"";
    }
    return s;
}
}