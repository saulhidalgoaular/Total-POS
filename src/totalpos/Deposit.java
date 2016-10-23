package totalpos;

import java.sql.Date;

/**
 *
 * @author Saúl Hidalgo
 */
public class Deposit {
    private String bank;
    private String formId;
    private Double quant;

    public Deposit(String bank, String formId, Double quant) {
        this.bank = bank;
        this.formId = formId;
        this.quant = quant;
    }

    public String getBank() {
        return bank;
    }

    public String getFormId() {
        return formId;
    }

    public Double getQuant() {
        return quant;
    }

}
