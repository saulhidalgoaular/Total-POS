package totalpos;

import java.io.Serializable;

/**
 *
 * @author Saúl Hidalgo
 */
public class Item2Receipt implements Serializable{
    private Item item;
    private Integer quant;
    private Integer antiQuant;
    private Double sellPrice;
    private Double sellDiscount;

    public Item2Receipt(Item item, Integer quant, Integer antiQuant, Double sellPrice, Double sellDiscount) {
        this.item = item;
        this.quant = quant;
        this.antiQuant = antiQuant;
        this.sellPrice = sellPrice;
        this.sellDiscount = sellDiscount;
    }

    public Integer getAntiQuant() {
        return antiQuant;
    }

    public Item getItem() {
        return item;
    }

    public Integer getQuant() {
        return quant;
    }

    public Double getSellDiscount() {
        return sellDiscount;
    }

    public Double getSellPrice() {
        return sellPrice;
    }

    

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Item2Receipt other = (Item2Receipt) obj;
        if (this.item != other.item && (this.item == null || !this.item.equals(other.item))) {
            return false;
        }
        return true;
    }
}
