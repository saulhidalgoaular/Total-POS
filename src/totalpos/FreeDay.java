package totalpos;

/**
 *
 * @author Saúl Hidalgo.
 */
public class FreeDay {

    private Employ employ;
    private String concept;
    private String extraHours;

    public FreeDay(Employ employ, String concept, String extraHours) {
        this.employ = employ;
        this.concept = concept;
        this.extraHours = extraHours;
    }

    public String getConcept() {
        return concept;
    }

    public Employ getEmploy() {
        return employ;
    }

    public String getExtraHours() {
        return extraHours;
    }

}
