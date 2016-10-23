package totalpos;

/**
 *
 * @author Saúl Hidalgo
 */
public class Employ {
    private String code;
    private String name;
    private String department;

    public Employ(String code, String name, String department) {
        this.code = code;
        this.name = name;
        this.department = department;
    }
    
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public String getName4Menu(){
        return this.getCode() + " - " + this.getName().split(",")[0];
    }

}
