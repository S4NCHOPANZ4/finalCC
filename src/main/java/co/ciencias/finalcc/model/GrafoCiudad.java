package co.ciencias.finalcc.model;

import co.ciencias.finalcc.model.NodoGrafo.AristaGrafo;

/**
 * Grafo de ciudad hardcoded para AutoRescate 24/7.
 *
 * <h2>Topología</h2>
 * <pre>
 *
 *   [Aeropuerto]──5──[Zona Norte]──3──[Hospital]
 *        |                |               |
 *        7               4               6
 *        |                |               |
 *   [Estadio]──3──[Plaza Central]──4──[Mercado]
 *        |                |               |
 *        5               3               5
 *        |                |               |
 *   [Terminal]──4──[Zona Sur]──3──[Parque Industrial]
 *
 * </pre>
 * <p>Los puestos de atención <b>Norte</b> (índice 0) y <b>Sur</b> (índice 1)
 * están fijos en los nodos "Zona Norte" y "Zona Sur" respectivamente.</p>
 *
 * <h2>Asignación de puesto</h2>
 * <p>Se usa BFS desde cada puesto para calcular la distancia mínima en saltos
 * (sin java.util — cola implementada con {@link Cola} propia).
 * La solicitud se asigna al puesto que alcanza el nodo del cliente
 * con menos saltos. En caso de empate, gana Norte (índice 0).</p>
 *
 * <p>No se usan {@code java.util.Collection}, {@code java.util.Map},
 * ni ninguna otra clase de {@code java.util} excepto las estructuras
 * de datos implementadas desde cero en este proyecto.</p>
 */
public class GrafoCiudad {

    // ------------------------------------------------------------------
    // Singleton
    // ------------------------------------------------------------------

    private static GrafoCiudad instancia;

    public static GrafoCiudad getInstancia() {
        if (instancia == null) instancia = new GrafoCiudad();
        return instancia;
    }

    // ------------------------------------------------------------------
    // Nodos de la ciudad (10 nodos)
    // ------------------------------------------------------------------

    /** Todos los nodos del grafo, en orden fijo para BFS. */
    private final NodoGrafo[] nodos;

    /** Índice dentro de {@link #nodos} donde están los puestos. */
    public static final int IDX_NORTE = 1;  // "Zona Norte"
    public static final int IDX_SUR   = 7;  // "Zona Sur"

    /** Nombres legibles de los puestos para la GUI. */
    public static final String[] NOMBRES_PUESTOS = { "Norte", "Sur" };

    // ------------------------------------------------------------------
    // Construcción del grafo
    // ------------------------------------------------------------------

    private GrafoCiudad() {
        // Crear los 10 nodos
        nodos = new NodoGrafo[10];
        nodos[0] = new NodoGrafo("Aeropuerto");
        nodos[1] = new NodoGrafo("Zona Norte");     // ← Puesto Norte (índice 0)
        nodos[2] = new NodoGrafo("Hospital");
        nodos[3] = new NodoGrafo("Estadio");
        nodos[4] = new NodoGrafo("Plaza Central");
        nodos[5] = new NodoGrafo("Mercado");
        nodos[6] = new NodoGrafo("Terminal");
        nodos[7] = new NodoGrafo("Zona Sur");       // ← Puesto Sur  (índice 1)
        nodos[8] = new NodoGrafo("Parque Industrial");
        nodos[9] = new NodoGrafo("Universidad");

        // Aristas (no dirigidas → agregar en ambas direcciones)
        conectar(0, 1, 5);   // Aeropuerto   ↔ Zona Norte
        conectar(0, 3, 7);   // Aeropuerto   ↔ Estadio
        conectar(1, 2, 3);   // Zona Norte   ↔ Hospital
        conectar(1, 4, 4);   // Zona Norte   ↔ Plaza Central
        conectar(2, 5, 6);   // Hospital     ↔ Mercado
        conectar(3, 4, 3);   // Estadio      ↔ Plaza Central
        conectar(3, 6, 5);   // Estadio      ↔ Terminal
        conectar(4, 5, 4);   // Plaza Central↔ Mercado
        conectar(4, 7, 3);   // Plaza Central↔ Zona Sur
        conectar(5, 8, 5);   // Mercado      ↔ Parque Industrial
        conectar(6, 7, 4);   // Terminal     ↔ Zona Sur
        conectar(7, 8, 3);   // Zona Sur     ↔ Parque Industrial
        conectar(2, 9, 4);   // Hospital     ↔ Universidad
        conectar(9, 5, 3);   // Universidad  ↔ Mercado
    }

    private void conectar(int a, int b, int peso) {
        nodos[a].agregarAdyacente(nodos[b], peso);
        nodos[b].agregarAdyacente(nodos[a], peso);
    }

    // ------------------------------------------------------------------
    // API pública
    // ------------------------------------------------------------------

    /** @return array de todos los nodos (para poblar el selector en la GUI). */
    public NodoGrafo[] getNodos() { return nodos; }

    /** @return nodo por índice (0–9). */
    public NodoGrafo getNodo(int indice) { return nodos[indice]; }

    /** @return cantidad total de nodos. */
    public int getCantidadNodos() { return nodos.length; }

    /**
     * Determina el índice de puesto (0 = Norte, 1 = Sur) más cercano
     * al nodo indicado, usando BFS por cantidad de saltos.
     *
     * <p>Se implementa con {@link Cola} propia y un arreglo de visitados,
     * sin ninguna clase de {@code java.util}.</p>
     *
     * @param indiceNodoCliente índice en {@link #nodos} donde está el cliente
     * @return 0 (Norte) o 1 (Sur)
     */
    public int calcularPuesto(int indiceNodoCliente) {
        int saltoNorte = bfsSaltos(IDX_NORTE, indiceNodoCliente);
        int saltosSur  = bfsSaltos(IDX_SUR,   indiceNodoCliente);

        // Empate → Norte
        return (saltoNorte <= saltosSur) ? 0 : 1;
    }

    /**
     * Retorna el nombre del nodo por índice (para logging y GUI).
     *
     * @param indice índice del nodo (0–9)
     * @return nombre del nodo, o "Desconocido" si el índice es inválido
     */
    public String nombreNodo(int indice) {
        if (indice < 0 || indice >= nodos.length) return "Desconocido";
        return nodos[indice].getNombre();
    }

    // ------------------------------------------------------------------
    // BFS con estructuras propias — sin java.util
    // ------------------------------------------------------------------

    /**
     * BFS desde {@code origen} hacia {@code destino}.
     * Retorna la cantidad de saltos (aristas) del camino más corto.
     * Si no hay camino, retorna {@code Integer.MAX_VALUE}.
     *
     * <p>Usa {@link Cola} propia como cola BFS y un arreglo {@code boolean[]}
     * como tabla de visitados; no usa ninguna colección de java.util.</p>
     */
    private int bfsSaltos(int origenIdx, int destinoIdx) {
        if (origenIdx == destinoIdx) return 0;

        boolean[] visitado = new boolean[nodos.length];
        // Cola de pares [índiceNodo, saltos] — codificados como int[2]
        Cola<int[]> cola = new Cola<>();

        visitado[origenIdx] = true;
        cola.encolar(new int[]{origenIdx, 0});

        while (!cola.esVacia()) {
            int[] actual  = cola.desencolar();
            int   idxAct  = actual[0];
            int   saltos  = actual[1];

            NodoGrafo nodoAct = nodos[idxAct];
            Nodo<NodoGrafo.AristaGrafo> aristaNode = nodoAct.getAdyacentes().getCabeza();

            while (aristaNode != null) {
                NodoGrafo vecino    = aristaNode.getDato().getDestino();
                int       idxVecino = indexOf(vecino);

                if (idxVecino == destinoIdx) return saltos + 1;

                if (!visitado[idxVecino]) {
                    visitado[idxVecino] = true;
                    cola.encolar(new int[]{idxVecino, saltos + 1});
                }
                aristaNode = aristaNode.getSiguiente();
            }
        }
        return Integer.MAX_VALUE; // sin camino
    }

    /**
     * Busca el índice de un nodo en el arreglo {@link #nodos}.
     * Comparación por referencia (cada nodo es único).
     */
    private int indexOf(NodoGrafo nodo) {
        for (int i = 0; i < nodos.length; i++) {
            if (nodos[i] == nodo) return i;
        }
        return -1;
    }
}