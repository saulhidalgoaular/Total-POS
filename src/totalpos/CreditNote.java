package totalpos;

import java.util.Date;
import java.util.List;

/**
 *
 * @author Saúl Hidalgo
 */
public class CreditNote {
    private String receiptId;
    private Date creationDate;
    private Date printingDate;
    private Double totalWithoutIva;
    private Double totalWithIva;
    private Double iva;
    private String fiscalPrinter;
    private String fiscalNumber;
    private String zReportId;
    private String userCodeId;
    private List<Item> items;
    private Integer numberItems;
    private String turn;

    public CreditNote(String receiptId, Date creationDate, Date printingDate, Double totalWithoutIva, Double totalWithIva, Double iva, String fiscalPrinter, String fiscalNumber, String zReportId, String userCodeId, List<Item> items, Integer numberItems, String turn) {
        this.receiptId = receiptId;
        this.creationDate = creationDate;
        this.printingDate = printingDate;
        this.totalWithoutIva = totalWithoutIva;
        this.totalWithIva = totalWithIva;
        this.iva = iva;
        this.fiscalPrinter = fiscalPrinter;
        this.fiscalNumber = fiscalNumber;
        this.zReportId = zReportId;
        this.userCodeId = userCodeId;
        this.items = items;
        this.numberItems = numberItems;
        this.turn = turn;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public String getFiscalNumber() {
        return fiscalNumber;
    }

    public String getFiscalPrinter() {
        return fiscalPrinter;
    }

    public List<Item> getItems() {
        return items;
    }

    public Double getIva() {
        return iva;
    }

    public Integer getNumberItems() {
        return numberItems;
    }

    public Date getPrintingDate() {
        return printingDate;
    }

    public String getReceiptId() {
        return receiptId;
    }

    public Double getTotalWithIva() {
        return totalWithIva;
    }

    public Double getTotalWithoutIva() {
        return totalWithoutIva;
    }

    public String getTurn() {
        return turn;
    }

    public String getUserCodeId() {
        return userCodeId;
    }

    public String getzReportId() {
        return zReportId;
    }

}
