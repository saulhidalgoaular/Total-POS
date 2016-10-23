package totalpos;

import java.sql.Time;

/**
 *
 * @author Saúl Hidalgo xD
 */
public class Turn {
    private String identificador;
    private String nombre;
    private Time inicio;
    private Time fin;

    public Turn(String identificador, String nombre, Time inicio, Time fin) {
        this.identificador = identificador;
        this.nombre = nombre;
        this.inicio = inicio;
        this.fin = fin;
    }

    public Time getFin() {
        return fin;
    }

    public String getIdentificador() {
        return identificador;
    }

    public Time getInicio() {
        return inicio;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Turn other = (Turn) obj;
        if ((this.identificador == null) ? (other.identificador != null) : !this.identificador.equals(other.identificador)) {
            return false;
        }
        return true;
    }

}
