package totalpos;

/**
 *
 * @author Saul Hidalgo.
 */
public class User {
    private String login;
    private String password;
    private String perfil;
    private String nombre;
    private String apellido;
    private int bloqueado;
    private int puedeCambiarPassword;
    private int debeCambiarPassword;

    public User(String login, String password, String perfil) {
        this.login = login;
        this.password = password;
        this.perfil = perfil;
    }

    public User(String login, String password, String perfil, String nombre, String apellido, int bloqueado, int debeCambiarPassword, int puedeCambiarPassword) {
        this.login = login;
        this.password = password;
        this.perfil = perfil;
        this.nombre = nombre;
        this.apellido = apellido;
        this.bloqueado = bloqueado;
        this.puedeCambiarPassword = puedeCambiarPassword;
        this.debeCambiarPassword = debeCambiarPassword;
    }

    public String getApellido() {
        return apellido;
    }

    public String getLogin() {
        return login;
    }

    public String getNombre() {
        return nombre;
    }

    public String getPassword() {
        return password;
    }

    public String getPerfil() {
        return perfil;
    }

    public boolean getBloqueado(){
        return bloqueado != 0;
    }

    public boolean getDebeCambiarPassword() {
        return debeCambiarPassword != 0;
    }

    public boolean getPuedeCambiarPassword() {
        return puedeCambiarPassword != 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final User other = (User) obj;
        if ((this.login == null) ? (other.login != null) : !this.login.equals(other.login)) {
            return false;
        }
        return true;
    }

}
