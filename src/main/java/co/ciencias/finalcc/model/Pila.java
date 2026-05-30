package co.ciencias.finalcc.model;

public class Pila<T> {
    private Nodo<T> top;
    private int tamanio;
    
    public Pila(){
        this.top = null;
        this.tamanio = 0;
    }
    public void push(T valor){
        Nodo<T> nodoNuevo = new Nodo(valor);
        nodoNuevo.setSiguiente(top);
        this.top = nodoNuevo;
        tamanio++;
    }
    public Nodo<T> pop(){
        if(esVacia()){
            return null;
        }
        Nodo<T> borrado = this.top;
        this.top = this.top.getSiguiente();
        tamanio--;
        return borrado;
    }
    public Nodo<T> ver(){return top;}
    
    public Boolean esVacia(){
        if(top == null){
            return true;
        }
        return false;
    }
}
