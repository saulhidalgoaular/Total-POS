package totalpos;

/**
 *
 * @author Saúl Hidalgo
 */
public class Expense {
    private String concept;
    private Double quant;
    private String description;

    public Expense(String concept, Double quant, String description) {
        this.concept = concept;
        this.quant = quant;
        this.description = description;
    }

    public String getConcept() {
        return concept;
    }

    public String getDescription() {
        return description;
    }

    public Double getQuant() {
        return quant;
    }
}
