package totalpos;

/**
 *
 * @author Saul Hidalgo
 */
public class Edge implements Comparable{
    private String id , nombre, predecesor, icono, funcion;

    public Edge(String id, String nombre, String predecesor, String funcion) {
        this.id = id;
        this.nombre = nombre;
        this.predecesor = predecesor;
        this.funcion = funcion;
    }

    public Edge(String id, String nombre, String predecesor, String icono, String funcion) {
        this.id = id;
        this.nombre = nombre;
        this.predecesor = predecesor;
        this.icono = icono;
        this.funcion = funcion;
    }

    public String getFuncion() {
        return funcion;
    }

    public String getIcono() {
        return icono;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getPredecesor() {
        return predecesor;
    }

    @Override
    public int compareTo(Object ob){
        Edge o = (Edge)ob;
        return o.getNombre().compareTo(this.getNombre());
    }

}
