package totalpos;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

/**
 *
 * @author Saúl Hidalgo xD
 */
public class Receipt implements Serializable{
    private String internId;
    private String status;
    private Timestamp creationDate;
    private Timestamp printingDate;
    private String clientId;
    private Double totalWithoutIva;
    private Double totalWithIva;
    private Double globalDiscount;
    private Double iva;
    private String fiscalPrinter;
    private String fiscalNumber;
    private String zReportId;
    private String userCodeId;
    private Integer numberItems;
    private List<Item2Receipt> items;
    private String turn;
    private String alternativeID;

    public Receipt(String internId, String status, Timestamp creationDate,
            Timestamp printingDate, String clientId, Double totalWithoutIva,
            Double totalWithIva, Double globalDiscount, Double iva,
            String fiscalPrinter, String fiscalNumber, String zReportId,
            String userCodeId, Integer numberItems, List<Item2Receipt> items,
            String turn, String alternativeID) {
        this.internId = internId;
        this.status = status;
        this.creationDate = creationDate;
        this.printingDate = printingDate;
        this.clientId = clientId;
        this.totalWithoutIva = totalWithoutIva;
        this.totalWithIva = totalWithIva;
        this.globalDiscount = globalDiscount;
        this.iva = iva;
        this.fiscalPrinter = fiscalPrinter;
        this.fiscalNumber = fiscalNumber;
        this.zReportId = zReportId;
        this.userCodeId = userCodeId;
        this.numberItems = numberItems;
        this.items = items;
        this.turn = turn;
        this.alternativeID = alternativeID;
    }

    public String getClientId() {
        return clientId;
    }

    public Timestamp getCreationDate() {
        return creationDate;
    }

    public String getFiscalNumber() {
        return fiscalNumber;
    }

    public String getFiscalPrinter() {
        return fiscalPrinter;
    }

    public Double getGlobalDiscount() {
        return globalDiscount;
    }

    public String getInternId() {
        return internId;
    }

    public List<Item2Receipt> getItems() {
        return items;
    }

    public Double getIva() {
        return iva;
    }

    public Integer getNumberItems() {
        return numberItems;
    }

    public Timestamp getPrintingDate() {
        return printingDate;
    }

    public String getStatus() {
        return status;
    }

    public Double getTotalWithIva() {
        return totalWithIva;
    }

    public Double getTotalWithoutIva() {
        return totalWithoutIva;
    }

    public String getUserCodeId() {
        return userCodeId;
    }

    public String getzReportId() {
        return zReportId;
    }

    public String getTurn() {
        return turn;
    }

    public String getAlternativeID() {
        return alternativeID;
    }
}
