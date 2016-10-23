
package totalpos;


/**
 *
 * @author Saúl Hidalgo.
 */
public class Presence4Print {
    private Employ e;
    private String mark1;
    private String mark2;
    private String mark3;
    private String mark4;

    public Presence4Print(Employ e, String mark1, String mark2, String mark3, String mark4) {
        this.e = e;
        this.mark1 = mark1;
        this.mark2 = mark2;
        this.mark3 = mark3;
        this.mark4 = mark4;
    }

    public Employ getE() {
        return e;
    }

    public String getMark1() {
        return mark1;
    }

    public String getMark2() {
        return mark2;
    }

    public String getMark3() {
        return mark3;
    }

    public String getMark4() {
        return mark4;
    }
}
