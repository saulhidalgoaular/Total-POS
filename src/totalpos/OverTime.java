package totalpos;

/**
 *
 * @author Saúl Hidalgo.
 */
public class OverTime {

    private Employ employ;
    private int hours;

    public OverTime(Employ employ, int hours) {
        this.employ = employ;
        this.hours = hours;
    }

    public Employ getEmploy() {
        return employ;
    }

    public void setEmploy(Employ employ) {
        this.employ = employ;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }
    
}
