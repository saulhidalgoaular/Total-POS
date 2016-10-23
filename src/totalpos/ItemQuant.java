package totalpos;

/**
 *
 * @author Saúl Hidalgo
 */
public class ItemQuant {
    private String itemId;
    private int quant;

    public ItemQuant(String itemId, int quant) {
        this.itemId = itemId;
        this.quant = quant;
    }

    public String getItemId() {
        return itemId;
    }

    public int getQuant() {
        return quant;
    }

}
