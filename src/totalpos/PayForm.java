package totalpos;

/**
 *
 * @author Saúl Hidalgo
 */
public class PayForm {
    private String receiptId;
    private String formWay;
    private String bPos;
    private String lot;
    private Double quant;

    public PayForm(String receiptId, String formWay, String bPos, String lot, Double quant) {
        this.receiptId = receiptId;
        this.formWay = formWay;
        this.bPos = bPos;
        this.lot = lot;
        this.quant = quant;
    }

    public String getbPos() {
        return bPos;
    }

    public String getFormWay() {
        return formWay;
    }

    public String getLot() {
        return lot;
    }

    public Double getQuant() {
        return quant;
    }

    public String getReceiptId() {
        return receiptId;
    }

}
