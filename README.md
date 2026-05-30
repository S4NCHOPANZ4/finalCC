# 🛠️ Arquitectura y Estructura del Sistema (MVC)

Este documento detalla la distribución completa de paquetes, clases, atributos y métodos que componen el núcleo del Sistema de Despacho Automático de Emergencias Viales. El diseño sigue estrictamente el patrón **Modelo-Vista-Controlador (MVC)** e implementa estructuras lineales dinámicas propias para prescindir por completo del framework *Java Collections*.

---

## 📦 1. Paquete: `co.ciencias.finalcc.model` (y `co.ciencias.finalcc2.model`)
Contiene las entidades de negocio y las estructuras de datos fundamentales desarrolladas a medida mediante nodos y punteros.

### 📄 `Nodo<T>`
Clase genérica que representa la unidad básica de almacenamiento secuencial en memoria lógica.
* **Atributos:**
  * `private T dato`: Objeto o valor referenciado dentro del nodo.
  * `private Nodo<T> siguiente`: Puntero o referencia al siguiente eslabón de la secuencia.
* **Métodos:**
  * `public Nodo(T dato)`: Constructor que inicializa el nodo con su información y define el puntero siguiente como nulo.
  * *(aquí getters y setters de: dato, siguiente)*

### 📄 `ListaEnlazada<T>`
Estructura lineal genérica simplemente enlazada que permite coleccionar dinámicamente elementos con inserción eficiente al final.
* **Atributos:**
  * `private Nodo<T> cabeza`: Referencia al primer elemento de la lista.
  * `private Nodo<T> cola`: Referencia al último elemento de la cadena (permite inserciones en tiempo constante $O(1)$).
  * `private int tamanio`: Contador incremental con la cantidad de nodos activos.
* **Métodos:**
  * `public ListaEnlazada()`: Inicializa una lista vacía.
  * `public void agregar(T valor)`: Inserta un nuevo elemento al final de la estructura.
  * `public T obtener(int indice)`: Recupera de forma secuencial el dato en la posición indicada.
  * `public boolean eliminar(T valor)`: Remueve un nodo reestructurando los punteros adyacentes si coincide mediante `.equals()`.
  * `public boolean contiene(T valor)`: Recorre los nodos verificando si el elemento existe en la lista.
  * `public int getTamanio()`: Retorna el tamaño.
  * `public boolean esVacia()`: Evalúa si la lista no tiene elementos.
  * *(aquí getters y setters de: cabeza)*

### 📄 `Cola<T>`
Estructura lineal genérica que opera bajo la política FIFO (*First-In, First-Out*), ideal para el procesamiento ordenado de flujos continuos como las colas de revisión de insumos.
* **Atributos:**
  * `private Nodo<T> frente`: Puntero al elemento listo para ser extraído.
  * `private Nodo<T> fin`: Puntero al último elemento ingresado.
  * `private int tamanio`: Cantidad de elementos actuales en el flujo.
* **Métodos:**
  * `public void encolar(T valor)`: Añade un elemento al final de la fila.
  * `public T desencolar()`: Extrae y retorna el elemento situado al frente de la cola.
  * `public T verFrente()`: Consulta el dato al frente de la cola sin removerlo.
  * `public boolean esVacia()`: Retorna verdadero si no contiene nodos.
  * *(aquí getters y setters de: tamanio, nodoFrente)*

### 📄 `Pila<T>`
Estructura lineal genérica de tipo LIFO (*Last-In, First-Out*) que modela el almacenamiento vertical apilable, utilizada para las estanterías de kits listos.
* **Atributos:**
  * `private Nodo<T> cima`: Puntero al elemento superior en la cima de la pila.
  * `private int tamanio`: Contador con la cantidad de objetos apilados.
* **Métodos:**
  * `public void push(T valor)`: Apila un nuevo elemento en la cima de la estructura.
  * `public T pop()`: Remueve y retorna el elemento ubicado en la cima.
  * `public T peek()`: Inspecciona visualmente el elemento superior sin extraerlo.
  * `public boolean esVacia()`: Verifica si la pila carece de datos.
  * *(aquí getters y setters de: tamanio)*

### 📄 `ColaPrioridad`
Estructura especializada adaptada para objetos de tipo `SolicitudServicio`. Reubica e inserta los elementos de manera interna ordenándolos automáticamente por la gravedad de su tipo de emergencia (de mayor a menor jerarquía).
* **Atributos:**
  * `private Nodo<SolicitudServicio> cabeza`: Referencia a la emergencia de mayor urgencia en espera.
  * `private int tamanio`: Cantidad total de incidentes acumulados en esta cola sectorial.
* **Métodos:**
  * `public void insertar(SolicitudServicio solicitud)`: Inserta una emergencia recorriendo linealmente la estructura hasta hallar su posición correcta según su peso jerárquico.
  * `public SolicitudServicio extraer()`: Remueve y entrega el elemento prioritario que está al frente.
  * `public SolicitudServicio verFrente()`: Permite examinar la solicitud en la cima de prioridad sin despacharla.
  * `private boolean tieneMayorPrioridad(SolicitudServicio a, SolicitudServicio b)`: Algoritmo interno de comparación numérica de pesos entre enums.
  * *(aquí getters y setters de: tamanio, cabeza)*

### 📄 `Cliente`
Entidad inmutable que representa al usuario o afectado que reporta el incidente en la vía.
* **Atributos:**
  * `private String id`: Identificador único universal autogenerado mediante UUID.
  * `private String nombre`: Nombre completo del ciudadano.
  * `private String telefono`: Número o canal telefónico de contacto.
* **Métodos:**
  * `public Cliente(String nombre, String telefono)`: Constructor que inicializa los datos e inyecta el ID único.
  * `public String toString()`: Cadena formateada legible de la entidad.
  * *(aquí getters y setters de: nombre, telefono, id)*

### 📄 `Kit`
Clase que modela los kits de herramientas o insumos técnicos requeridos para solucionar percances viales.
* **Atributos:**
  * `private String nombre`: Descripción o código de identificación del kit.
  * `private boolean completo`: Estado de integridad física del kit (listo o incompleto).
* **Métodos:**
  * `public Kit(String nombre)`: Inicializa el kit con su nombre técnico y lo define con estado incompleto por defecto hasta pasar por revisión.
  * *(aquí getters y setters de: nombre, completo)*

### 📄 `UnidadServicio`
Entidad que representa a las unidades automotrices físicas (ambulancias, grúas, etc.) dispuestas en las estaciones.
* **Atributos:**
  * `private TipoUnidad tipo`: Categoría del vehículo.
  * `private EstadoUnidad estado`: Disponibilidad actual del recurso.
  * `private int zonaPuesto`: Identificador de la central a la que pertenece.
  * `private String codigo`: Placa o código de identificación vehicular interna.
* **Métodos:**
  * `public UnidadServicio(TipoUnidad tipo, EstadoUnidad estado, int zonaPuesto, String codigo)`: Instancia la unidad operativa.
  * *(aquí getters y setters de: tipo, estado, zonaPuesto, codigo)*

### 📄 `Tecnico`
Entidad encargada de representar a los operarios, médicos o brigadistas asignados a las misiones de rescate.
* **Atributos:**
  * `private String nombre`: Nombre o código identificador del especialista.
  * `private Especialidad especialidad`: Área técnica de formación del operario.
  * `private EstadoTecnico estado`: Disponibilidad operativa actual.
  * `private int zonaPuesto`: Puesto base de operaciones.
* **Métodos:**
  * `public Tecnico(String nombre, Especialidad especialidad, EstadoTecnico estado, int zonaPuesto)`: Registra al operario técnico en la base.
  * *(aquí getters y setters de: nombre, especialidad, estado, zonaPuesto)*

### 📄 `PuntoVia`
Componente geométrico tridimensional simplificado que valida si las coordenadas $(X, Y)$ ingresadas pertenecen espacialmente a la red vial (anillos concéntricos o rectas radiales).
* **Atributos:**
  * `public static final double RADIO_1, RADIO_2, RADIO_3`: Radios de la autopista circular.
  * `public static final double EPSILON`: Margen de tolerancia admisible para aproximación flotante ($0.20$).
  * `public static final double[] ANGULOS_RECTAS`: Conjunto de rectas radiales en radianes.
  * `private final double x, y`: Coordenadas cartesianas planas.
  * `private final TipoVia tipoVia`: Clasificación de la vía donde se encuentra (Círculo o Recta).
  * `private final int indiceVia`: Identificador de la pista o tramo específico de la red.
* **Métodos:**
  * `public static PuntoVia desde(double x, double y)`: Fábrica estática que calcula mediante distancias si el punto ingresado pertenece a las carreteras. Retorna `null` si la ubicación está fuera de la vía.
  * *(aquí getters y setters de: x, y, tipoVia, indiceVia)*

### 📄 `SolicitudServicio`
Entidad central que modela el ciclo de vida completo de un incidente vial, consolidando la información geográfica, de usuario, de prioridad y los recursos asignados.
* **Atributos:**
  * `private final String id`: Código único autogenerado.
  * `private final Cliente cliente`: Usuario asociado al reporte.
  * `private final TipoEmergencia tipoEmergencia`: Nivel de clasificación y prioridad nativa del incidente.
  * `private String descripcion`: Notas o detalles del suceso.
  * `private final LocalDateTime timestamp`: Fecha y hora exacta de creación del ticket.
  * `private EstadoSolicitud estado`: Estado operativo del ciclo (PENDIENTE, DESPACHADA, CERRADA).
  * `private final PuntoVia ubicacion`: Coordenadas cartesianas del suceso.
  * `private int zonaPuesto`: Índice numérico de la estación asignada (0=Este, 1=Norte, 2=Oeste, 3=Sur).
  * `private UnidadServicio unidadAsignada`: Kit vehicular de emergencia despachado.
  * `private Tecnico tecnicoAsignado`: Operario o especialista enviado en ruta.
* **Métodos:**
  * `public String getTimestampFormateado()`: Convierte la marca temporal interna a formato legible para auditoría (`HH:mm:ss dd/MM/yyyy`).
  * *(aquí getters y setters de: descripcion, estado, zonaPuesto, unidadAsignada, tecnicoAsignado, id, cliente, tipoEmergencia, timestamp, ubicacion)*

### 📄 `PuestoAtencion`
Representa una de las 4 centrales físicas instaladas estratégicamente en la periferia de la autopista para administrar inventarios y colas locales.
* **Atributos:**
  * `private final int indice`: Código numérico identificador de la zona (0 a 3).
  * `private final String nombre`: Nombre textual de la estación (Este, Norte, Oeste, Sur).
  * `private final ColaPrioridad solicitudesPendientes`: Cola de prioridad dedicada exclusivamente a este cuadrante.
  * `private final ListaEnlazada<UnidadServicio> unidades`: Stock local de vehículos asignados a la base.
  * `private final ListaEnlazada<Tecnico> tecnicos`: Nómina local de especialistas adscritos a la estación.
* **Métodos:**
  * `public void encolarSolicitud(SolicitudServicio solicitud)`: Agrega ordenadamente un nuevo reporte al flujo de prioridad local.
  * `private void inicializarRecursos()`: Carga el inventario inicial de grúas, ambulancias y brigadistas con codificación propia del puesto.
  * `public SolicitudServicio verSiguiente()`: Inspecciona visualmente la cabeza de la cola sin extraerla.
  * *(aquí getters y setters de: indice, nombre, solicitudesPendientes, unidades, tecnicos)*

### 📄 `ZonaCalculadora`
Componente matemático puro estructurado bajo el patrón **Singleton**. Determina la estación vial óptima minimizando la distancia euclidiana espacial.
* **Atributos:**
  * `private static ZonaCalculadora instancia`: Instancia única de acceso global.
  * `public static final double[][] COORDS`: Ubicaciones fijas cartesianas de las 4 sedes centrales.
  * `public static final String[] NOMBRES`: Etiquetas textuales de localización (`"Este", "Norte", "Oeste", "Sur"`).
* **Métodos:**
  * `public static ZonaCalculadora getInstancia()`: Recupera la instancia única global.
  * `public int calcularPuesto(PuntoVia punto)`: Aplica el cálculo de distancia euclidiana tradicional $\sqrt{(x_2-x_1)^2 + (y_2-y_1)^2}$ contra las sedes fijas y retorna el índice del puesto más cercano.

### 📄 `Operacion`
Entidad destinada a guardar registros cronológicos e inmutables de auditoría interna de acciones operativas.
* **Atributos:**
  * `private String descripcion`: Detalle textual de la acción ejecutada.
  * `private String idObjetoAfectado`: Identificador de la entidad alterada.
  * `private String estadoAnterior`: Datos de trazabilidad previa.
  * `private LocalDateTime timestamp`: Instante preciso del suceso.
* **Métodos:**
  * *(aquí getters y setters de: descripcion, idObjetoAfectado, estadoAnterior, timestamp)*

---

## 🗂️ 2. Paquete: `co.ciencias.finalcc.model.enums`
Tipifica de manera robusta los estados lógicos y clasificaciones estáticas del modelo de datos.

* **`TipoEmergencia`**: Define incidentes y pesos numéricos de prioridad: `MEDICA` (Máxima), `SEGURIDAD_PUBLICA`, `PROTECCION_CIVIL`, `SERVICIOS_PUBLICOS`, `SERVICIOS_DE_APOYO` (Mínima).
* **`EstadoSolicitud`**: Flujo del ticket (`PENDIENTE`, `DESPACHADA`, `CERRADA`).
* **`EstadoUnidad` / `EstadoTecnico`**: Ciclo de recursos (`DISPONIBLE`, `ASIGNADO`, `EN_MANTENIMIENTO`).
* **`TipoVia`**: Geometría plana (`CIRCULO`, `RECTA`).
* **`TipoUnidad` / `Especialidad`**: Tipificación física de asistencia en carretera (`AMBULANCIA`, `GRUA`, `BRIGADISTA`, etc.).

---

## ⚙️ 3. Paquete: `co.ciencias.finalcc.model.gestores` (y `co.ciencias.finalcc2.model.gestores`)
Módulos funcionales de control operativo interno que manipulan el estado global del modelo a través del patrón **Singleton**.

### 📄 `GestorSolicitudes`
* **Descripción:** Concentra la creación global de emergencias, vincula a `ZonaCalculadora` para asignar zonas e instruye el despacho inmediato. Mantiene además una lista histórica de servicios concluidos.
* **Atributos, métodos y getters de:** Instancia global y lista de solicitudes.

### 📄 `GestorRecursos`
* **Descripción:** Aloja y expone la matriz fija de las cuatro centrales (`PuestoAtencion[4]`). Centraliza búsquedas, filtrados de stock de grúas/ambulancias y cambios de estado de operarios.
* **Atributos, métodos y getters de:** Estructura de puestos de atención.

### 📄 `GestorOperaciones`
* **Descripción:** Administra una estructura de tipo Pila personalizada para almacenar los logs cronológicos continuos del sistema (*Caja Negra*).
* **Atributos, métodos y getters de:** Historial de operaciones.

### 📄 `GestorKits`
* **Descripción:** Gestiona el inventario centralizado de herramientas mediante un flujo combinado de estructuras. Los kits en reparación entran a una `Cola` de revisión técnica y, al quedar completos, se apilan en una `Pila` de estantería listos para ser retirados de inmediato por las patrullas. Opera bajo el patrón **Singleton**.
* **Atributos:**
  * `private static GestorKits instancia`: Instancia única universal para acceso global seguro.
  * `private final Cola<Kit> zonaRevision`: Cola estructurada FIFO para controlar el orden de reparación técnica de los kits.
  * `private final Pila<Kit> estanteriaListos`: Estructura LIFO que almacena de forma vertical los kits aptos para despacho inmediato.
* **Métodos:**
  * `public static GestorKits getInstancia()`: Devuelve o crea la instancia única global del gestor.
  * `private void inicializarInventario()`: Inicializa el sistema inyectando kits mecánicos, hidráulicos y eléctricos base.
  * `public void registrarKitEnRevision(Kit kit)`: Recibe un kit gastado en servicio, invalida su bandera de integridad y lo encola en talleres.
  * `public Kit liberarKitDeRevision()`: Desencola el primer kit reparado en cola, cambia su estado a completo y lo apila en la estantería de listos.
  * `public Kit retirarKitParaServicio()`: Desapila el kit de la cima de la estantería para asignarlo a una patrulla de auxilio vial.
  * *(aquí getters y setters de: zonaRevision, estanteriaListos)*

---

## 🎮 4. Paquete: `co.ciencias.finalcc2.controller`
Contiene los controladores dinámicos de flujo del sistema, el motor asíncrono y los mecanismos de comunicación desacoplados basados en eventos.

### 📄 `SimulacionListener` *(Interfaz)*
Contrato de desacoplamiento crítico (Patrón *Observer/Listener*). Define los métodos de salida para que cualquier Vista (Consola o GUI futura) capture las actualizaciones lógicas en tiempo real sin interferir en los hilos del motor.
* **Métodos:**
  * `void onTick(int[] countdowns, ListaEnlazada<MonitoreoAtencion> enRuta, ListaEnlazada<MonitoreoMantenimiento> enTaller)`: Notificación que se dispara de forma síncrona cada 1 segundo del reloj simulado. Transmite los vectores de cuenta regresiva y las colecciones vivas de operaciones en carretera y talleres.
  * `void onMensajeEmitido(String mensaje)`: Emite alertas operativas inmediatas (despachos automáticos, retornos de unidades) ideales para consolas o componentes gráficos como logs de texto (`JTextArea`).

### 📄 `MonitoreoAtencion`
Contenedor temporal que simula el tránsito y conteo regresivo de un vehículo de emergencia desplegado en carretera atendiendo una solicitud.
* **Atributos:**
  * `private final SolicitudServicio solicitud`: El caso asignado en ruta.
  * `private int segundosRestantes`: Temporizador de viaje aleatorio calculado al salir (entre 15 y 30 segundos).
* **Métodos:**
  * `public void miembro decrementarSegundos()`: Resta un segundo al temporizador por cada ciclo del reloj.
  * *(aquí getters y setters de: solicitud, segundosRestantes)*

### 📄 `MonitoreoMantenimiento`
Encapsula el ciclo de enfriamiento técnico, reabastecimiento de insumos médicos/mecánicos y desinfección obligatoria de los recursos al retornar a la base.
* **Atributos:**
  * `private final UnidadServicio unidad`: Vehículo en fosa técnica.
  * `private final Tecnico tecnico`: Especialista retenido en la estación.
  * `private int segundosRestantes`: Temporizador fijo de mantenimiento configurado en 15 segundos.
* **Métodos:**
  * `public void decrementarSegundos()`: Reduce el tiempo de permanencia en el taller mecánico.
  * *(aquí getters y setters de: unidad, tecnico, segundosRestantes)*

### 📄 `MotorSimulacion` *(Implements Runnable)*
El motor autónomo y corazón del backend temporal. Se ejecuta de forma paralela e indefinida en un hilo independiente (`Thread`), procesando reglas de negocio abstractas sin conocer la existencia de interfaces visuales.
* **Atributos:**
  * `private final int[] countdownsDespacho`: Arreglo que mide los relojes regresivos de 10 segundos de cada puesto con llamadas pendientes.
  * `private final ListaEnlazada<MonitoreoAtencion> listaAtencion`: Lista enlazada nativa de incidentes activos navegando la autopista.
  * `private final ListaEnlazada<MonitoreoMantenimiento> listaMantenimiento`: Lista enlazada nativa de recursos en fosa técnica.
  * `private volatile boolean activo`: Bandera lógica de control seguro para encendido o apagado ordenado del bucle del hilo.
  * `private SimulacionListener listener`: Vista o componente registrado para escuchar y repintar los eventos dinámicos.
* **Métodos:**
  * `public void setListener(SimulacionListener listener)`: Vincula la interfaz interesada en consumir el flujo del reloj.
  * `public void detener()`: Cambia la bandera a falso para concluir limpiamente la ejecución asíncrona.
  * `public void run()`: Bucle que duerme cíclicamente 1000ms (`Thread.sleep`) e invoca las políticas lógicas en `procesarSegundo()`.
  * `private synchronized void procesarSegundo()`: Ejecuta las 3 reglas deterministas del negocio:
    1. Descuenta los contadores de los puestos que tienen llamadas en espera; al llegar a cero, despacha de forma asíncrona la cabeza de la `ColaPrioridad` asignándole recursos y un tiempo en ruta de 15 a 30s.
    2. Decrementa los tiempos de viaje en carretera y transfiere las unidades concluidas a la zona de talleres.
    3. Monitorea los talleres liberando y regresando a estado `DISPONIBLE` los recursos tras cumplirse sus 15 segundos reglamentarios de cooldown técnico.
  * *(aquí getters y setters de: countdownsDespacho, listaAtencion, listaMantenimiento)*

### 📄 `ConsoleController` *(Implements SimulacionListener)*
Controlador orquestador que captura comandos de la terminal y los mapea hacia el Modelo, actuando al mismo tiempo como el observador oficial del reloj.
* **Atributos:**
  * `private final VistaConsola vista`: Enlace directo con los flujos de texto de entrada/salida.
  * `private boolean ejecutando`: Control del bucle del menú principal.
  * `private final MotorSimulacion motor`: Instancia dedicada del motor asíncrono.
  * `private Thread hiloMotor`: Hilo secundario configurado en modo Daemon para correr el reloj en segundo plano.
  * `private int[] contadoresLocales`, `rutaLocal`, `tallerLocal`: Estructuras caché locales sincronizadas segundo a segundo que permiten a la vista consultar datos de forma instantánea sin bloquear o pausar el hilo del motor simulador.
* **Métodos:**
  * `public void iniciar()`: Enciende el hilo del motor e inicia el menú interactivo.
  * `public void onTick(...)`: Guarda de forma instantánea las referencias del backend enviadas por el hilo del reloj cada segundo.
  * `public void onMensajeEmitido(String mensaje)`: Imprime alertas operativas directas rompiendo limpiamente el flujo textual de la terminal.

---

## 🖥️ 5. Paquete: `co.ciencias.finalcc2.view`
Aloja los componentes encargados puramente de la presentación e interacción visual con el usuario operativo.

### 📄 `VistaConsola`
Abstracción de entrada y salida estándar encargada de aislar al controlador de comandos nativos de la consola.
* **Atributos:**
  * `private final java.util.Scanner scanner`: Lector nativo del flujo de entrada de bytes del sistema.
* **Métodos:**
  * `public void mostrarInformacion(String mensaje)`: Canaliza la salida de cadenas a la pantalla.
  * `public String leerDato(String etiqueta)`: Imprime un prompt indicador y detiene el flujo para capturar la línea digitada por el usuario.