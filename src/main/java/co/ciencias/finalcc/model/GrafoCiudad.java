package co.ciencias.finalcc.model;

public class GrafoCiudad {

    private static GrafoCiudad instancia;
    public static GrafoCiudad getInstancia() {
        if (instancia == null) instancia = new GrafoCiudad();
        return instancia;
    }
    private final NodoGrafo[] nodos;

    public static final int IDX_NORTE = 1;  
    public static final int IDX_SUR = 7;  

    public static final String[] NOMBRES_PUESTOS = { "Norte", "Sur" };

    private GrafoCiudad() {
        nodos = new NodoGrafo[10];
        nodos[0] = new NodoGrafo("Aeropuerto");
        nodos[1] = new NodoGrafo("Zona Norte");     
        nodos[2] = new NodoGrafo("Hospital");
        nodos[3] = new NodoGrafo("Estadio");
        nodos[4] = new NodoGrafo("Plaza Central");
        nodos[5] = new NodoGrafo("Mercado");
        nodos[6] = new NodoGrafo("Terminal");
        nodos[7] = new NodoGrafo("Zona Sur");
        nodos[8] = new NodoGrafo("Parque Industrial");
        nodos[9] = new NodoGrafo("Universidad");
        conectar(0, 1, 5);   
        conectar(0, 3, 7);   
        conectar(1, 2, 3);   
        conectar(1, 4, 4);   
        conectar(2, 5, 6);   
        conectar(3, 4, 3);   
        conectar(3, 6, 5);   
        conectar(4, 5, 4);   
        conectar(4, 7, 3);   
        conectar(5, 8, 5);   
        conectar(6, 7, 4);   
        conectar(7, 8, 3);   
        conectar(2, 9, 4);   
        conectar(9, 5, 3);   
    }

    private void conectar(int a, int b, int peso) {
        nodos[a].agregarAdyacente(nodos[b], peso);
        nodos[b].agregarAdyacente(nodos[a], peso);
    }


    public NodoGrafo[] getNodos() { return nodos; }
    public NodoGrafo getNodo(int indice) { return nodos[indice]; }
    public int getCantidadNodos() { return nodos.length; }
    public int calcularPuesto(int indiceNodoCliente) {
        int saltoNorte = bfsSaltos(IDX_NORTE, indiceNodoCliente);
        int saltosSur  = bfsSaltos(IDX_SUR,   indiceNodoCliente);
        return (saltoNorte <= saltosSur) ? 0 : 1;
    }

    public String nombreNodo(int indice) {
        if (indice < 0 || indice >= nodos.length) return "Desconocido";
        return nodos[indice].getNombre();
    }


    private int bfsSaltos(int origenIdx, int destinoIdx) {
        if (origenIdx == destinoIdx) return 0;

        boolean[] visitado = new boolean[nodos.length];
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
                int idxVecino = indexOf(vecino);
                if (idxVecino == destinoIdx) return saltos + 1;
                if (!visitado[idxVecino]) {
                    visitado[idxVecino] = true;
                    cola.encolar(new int[]{idxVecino, saltos + 1});
                }
                aristaNode = aristaNode.getSiguiente();
            }
        }
        return Integer.MAX_VALUE; 
    }

    private int indexOf(NodoGrafo nodo) {
        for (int i = 0; i < nodos.length; i++) {
            if (nodos[i] == nodo) return i;
        }
        return -1;
    }
}