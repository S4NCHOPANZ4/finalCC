package co.ciencias.finalcc2.controller;

import co.ciencias.finalcc2.model.*;
import co.ciencias.finalcc2.model.enums.*;
import co.ciencias.finalcc2.model.gestores.*;
import co.ciencias.finalcc2.view.VistaConsola;

/**
 * Controlador de Consola que implementa SimulacionListener.
 * Cuando hagas la GUI, tu clase 'VentanaPrincipal' o 'GuiController' implementará 
 * exactamente este mismo SimulacionListener.
 */
public class ConsoleController implements SimulacionListener {

    private final VistaConsola vista;
    private boolean ejecutando;
    private final MotorSimulacion motor;
    private Thread hiloMotor;

    // Almacenamiento local para consultas rápidas desde el menú de consola
    private int[] contadoresLocales = new int[4];
    private ListaEnlazada<MonitoreoAtencion> rutaLocal = new ListaEnlazada<>();
    private ListaEnlazada<MonitoreoMantenimiento> tallerLocal = new ListaEnlazada<>();

    public ConsoleController(VistaConsola vista) {
        this.vista = vista;
        this.ejecutando = true;
        
        // Instanciar el motor reutilizable
        this.motor = new MotorSimulacion();
        this.motor.setListener(this); // Escuchar los eventos del reloj aquí
    }

    public void iniciar() {
        vista.mostrarInformacion("=================================================");
        vista.mostrarInformacion("  SISTEMA DE DESPACHO CON MOTOR DE RELOJ MVC    ");
        vista.mostrarInformacion("=================================================");
        
        // Arrancar el motor en un hilo secundario
        this.hiloMotor = new Thread(motor, "Hilo-Reloj-MVC");
        this.hiloMotor.setDaemon(true);
        this.hiloMotor.start();

        while (ejecutando) {
            mostrarMenu();
            String opcion = vista.leerDato("\nSeleccione opción: ");
            procesarOpcion(opcion);
        }
    }

    private void mostrarMenu() {
        vista.mostrarInformacion("\n--- MENÚ DE MONITOREO ---");
        vista.mostrarInformacion("1. Registrar Nueva Emergencia");
        vista.mostrarInformacion("2. Ver Estado de Puestos y Temporizadores");
        vista.mostrarInformacion("3. Ver Servicios Activos en Ruta y Mantenimientos");
        vista.mostrarInformacion("4. Salir");
    }

    private void procesarOpcion(String opcion) {
        switch (opcion) {
            case "1": registrarSolicitud(); break;
            case "2": verEstadoPuestos(); break;
            case "3": verServiciosYMantenimientos(); break;
            case "4":
                vista.mostrarInformacion("Apagando módulos del controlador...");
                motor.detener();
                ejecutando = false;
                break;
            default:
                vista.mostrarInformacion("[SISTEMA] Opción inválida.");
        }
    }

    // =========================================================================
    // IMPLEMENTACIÓN DE LOS MÉTODOS DEL LISTENER (GUI READY)
    // =========================================================================
    
    @Override
    public void onTick(int[] countdowns, ListaEnlazada<MonitoreoAtencion> enRuta, ListaEnlazada<MonitoreoMantenimiento> enTaller) {
        // Guardamos las referencias actualizadas que el hilo nos envía cada segundo
        this.contadoresLocales = countdowns;
        this.rutaLocal = enRuta;
        this.tallerLocal = enTaller;
    }

    @Override
    public void onMensajeEmitido(String mensaje) {
        // En consola lo imprimimos directamente rompiendo el flujo de entrada de forma asíncrona
        System.out.println("\n\n" + mensaje + "\n[SISTEMA] Presione ENTER tras leer si estaba digitando.");
    }

    // =========================================================================
    // MÉTODOS DE CONSULTA DE LA VISTA
    // =========================================================================

    private void registrarSolicitud() {
        vista.mostrarInformacion("\n--- ENTRADA DE LLAMADA ---");
        String nombre = vista.leerDato("Nombre del Cliente: ");
        String telefono = vista.leerDato("Teléfono: ");
        
        vista.mostrarInformacion("1. MEDICA | 2. SEGURIDAD_PUBLICA | 3. PROTECCION_CIVIL | 4. SERVICIOS_PUBLICOS | 5. SERVICIOS_DE_APOYO");
        String tipoOpt = vista.leerDato("Seleccione Tipo (1-5): ");
        TipoEmergencia tipo = (tipoOpt.equals("1")) ? TipoEmergencia.MEDICA : TipoEmergencia.SEGURIDAD_PUBLICA; // Simplificación para prueba

        try {
            double x = Double.parseDouble(vista.leerDato("Coordenada X: "));
            double y = Double.parseDouble(vista.leerDato("Coordenada Y: "));
            PuntoVia punto = PuntoVia.desde(x, y);

            if (punto != null) {
                Cliente c = new Cliente(nombre, telefono);
                // Alterar el modelo de datos global
                SolicitudServicio s = GestorSolicitudes.getInstancia().crearSolicitud(c, tipo, "Emergencia Vial", punto);
                vista.mostrarInformacion("[OK] Registrada. Puesto: " + ZonaCalculadora.NOMBRES[s.getZonaPuesto()]);
            } else {
                vista.mostrarInformacion("[ERROR] Fuera de vía.");
            }
        } catch(Exception e) {
            vista.mostrarInformacion("[ERROR] Datos incorrectos.");
        }
    }

    private void verEstadoPuestos() {
        vista.mostrarInformacion("\n--- ESTADO DE CANALES DE DESPACHO ---");
        PuestoAtencion[] puestos = GestorRecursos.getInstancia().getPuestos();
        for (int i = 0; i < puestos.length; i++) {
            vista.mostrarInformacion(String.format("Puesto %s -> Próximo disparo en: %ss | En cola: %d",
                    puestos[i].getNombre(), contadoresLocales[i], puestos[i].getSolicitudesPendientes().getTamanio()));
        }
    }

    private void verServiciosYMantenimientos() {
        vista.mostrarInformacion("\n--- MONITOREO TEMPORAL EN VIVO ---");
        vista.mostrarInformacion("Unidades en ruta:");
        Nodo<MonitoreoAtencion> actRuta = rutaLocal.getCabeza();
        while(actRuta != null) {
            vista.mostrarInformacion(" * Caso: " + actRuta.getDato().getSolicitud().getCliente().getNombre() + " | Restan: " + actRuta.getDato().getSegundosRestantes() + "s");
            actRuta = actRuta.getSiguiente();
        }

        vista.mostrarInformacion("Unidades en mantenimiento taller:");
        Nodo<MonitoreoMantenimiento> actTal = tallerLocal.getCabeza();
        while(actTal != null) {
            vista.mostrarInformacion(" * Kit: " + actTal.getDato().getUnidad().getCodigo() + " | Listo en: " + actTal.getDato().getSegundosRestantes() + "s");
            actTal = actTal.getSiguiente();
        }
    }

    public static void main(String[] args) {
        VistaConsola vista = new VistaConsola();
        ConsoleController app = new ConsoleController(vista);
        app.iniciar();
    }
}