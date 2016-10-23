package totalpos;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Saul Hidalgo.
 */
public class Item implements Serializable{
    private String code;
    private String description;
    private Date registerDate;
    private String mark;
    private String sector;
    private String sublineCode;
    private String mainBarcode;
    private String model;
    private String sellUnits;
    private String buyUnits;
    private int currentStock;
    private List<Price> price;
    private List<String> barcodes;
    private boolean status;
    private String imageAddr;
    private String descuento;

    public Item(String code, String description, Date registerDate, String mark, String sector, String sublineCode, String mainBarcode, String model, String sellUnits, String buyUnits, int currentStock, List<Price> price, List<String> barcodes, boolean status, String imageAddr, String descuento) {
        this.code = code;
        this.description = description;
        this.registerDate = registerDate;
        this.mark = mark;
        this.sector = sector;
        this.sublineCode = sublineCode;
        this.mainBarcode = mainBarcode;
        this.model = model;
        this.sellUnits = sellUnits;
        this.buyUnits = buyUnits;
        this.currentStock = currentStock;
        this.price = price;
        this.barcodes = barcodes;
        this.status = status;
        this.imageAddr = imageAddr;
        this.descuento = descuento;
    }

    public Item(Item o){
        this.code = o.getCode();
        this.description = o.getDescription();
        this.registerDate = o.getRegisterDate();
        this.mark = o.getMark();
        this.sector = o.getSector();
        this.sublineCode = o.getSublineCode();
        this.mainBarcode = o.getMainBarcode();
        this.model = o.getModel();
        this.sellUnits = o.getSellUnits();
        this.buyUnits = o.getBuyUnits();
        this.currentStock = o.getCurrentStock();
        this.price = o.getPrice();
        this.barcodes = o.getBarcodes();
        this.status = o.isStatus();
        this.imageAddr = o.getImageAddr();
        this.descuento = o.getDescuento()+"";
    }

    public List<String> getBarcodes() {
        return Collections.unmodifiableList(barcodes);
    }

    public String getBuyUnits() {
        return buyUnits;
    }

    public String getCode() {
        return code;
    }

    public int getCurrentStock() {
        return currentStock;
    }

    public String getDescription() {
        return description;
    }

    public String getImageAddr() {
        return imageAddr;
    }

    public String getMainBarcode() {
        return mainBarcode;
    }

    public String getMark() {
        return mark;
    }

    public String getModel() {
        return model;
    }

    public List<Price> getPrice() {
        return Collections.unmodifiableList(price);
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public String getSector() {
        return sector;
    }

    public String getSellUnits() {
        return sellUnits;
    }

    public boolean isStatus() {
        return status;
    }

    public String getSublineCode() {
        return sublineCode;
    }

    public Price getLastPrice(){
        assert(price.size() > 0);
        Price ans = new Price(price.get(0));

        for (Price p : price) {
            ans = p.newest(ans);
        }

        return ans;
    }

    public Double getDescuento() {
        try{
            return Double.parseDouble(descuento.replace(',', '.'));
        }catch (Exception ex){
            return .0;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Item other = (Item) obj;
        if ((this.code == null) ? (other.code != null) : !this.code.equals(other.code)) {
            return false;
        }
        return true;
    }

}
