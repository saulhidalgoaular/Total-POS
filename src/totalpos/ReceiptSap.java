
package totalpos;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import srvSap.ArrayOfZSDSPOSDEV;
import srvSap.ArrayOfZSDSPOSFACT;
import srvSap.ObjectFactory;
import srvSap.ZSDSCABDEV;
import srvSap.ZSDSCABFACT;
import srvSap.ZSDSPOSDEV;
import srvSap.ZSDSPOSFACT;

/**
 *
 * @author Saúl Hidalgo
 */
public class ReceiptSap implements Serializable {
    List<Receipt> receipts = new LinkedList<Receipt>();
    private String id = Shared.getConfig("maximunId"); // is it really INF??
    private String maxFiscalId = Shared.getConfig("minimunId");
    private String minFiscalId = Shared.getConfig("maximunId");
    private String z = "";
    private String kind = "";
    private String printerId;
    private String client = "";
    private String myDay = "";

    private static ObjectFactory of = Constants.of;

    public ReceiptSap(String day) {
        myDay = day.replace("-", "");
    }

    public String getId() {
        return id;
    }

    public String getClient() {
        return client;
    }

    public String getKind() {
        return kind;
    }

    public String getMyDay() {
        return myDay;
    }

    public String getPrinterId() {
        return printerId;
    }

    public String getZ() {
        return z;
    }

    public String getMaxFiscalId() {
        return maxFiscalId;
    }

    public String getMinFiscalId() {
        return minFiscalId;
    }
    
    public void add(Receipt r){
        receipts.add(r);
        if ( r.getInternId().compareTo(id) < 0 ){
            id = r.getInternId();
        }

        if ( Integer.parseInt( r.getFiscalNumber() ) > Integer.parseInt(maxFiscalId) ){
            maxFiscalId = r.getFiscalNumber();
        }

        if ( Integer.parseInt( r.getFiscalNumber() ) < Integer.parseInt(minFiscalId) ){
            minFiscalId = r.getFiscalNumber();
        }

        kind = r.getClientId().equals("Contado")?"1":"2";
        client = r.getClientId().equals("Contado")?"":r.getClientId();
        z = r.getzReportId();
        printerId = r.getFiscalPrinter();
    }

    public int getSize(){
        return receipts.size();
    }

    public ZSDSCABFACT getHeaderF(String myDay){
        ZSDSCABFACT ans = new ZSDSCABFACT();
        String idF = "F" + id;

        String range = minFiscalId + "-" + maxFiscalId;

        System.out.println("MANDT\tFKDAT\tVBELN\tZTIPV\tKUNNR\tRANGO\tREPOZ\tIMPRE\tWAERS\tWERKS");
        ans.setMANDT(of.createZSDSCABDEVMANDT(Shared.getConfig("mant")));
        System.out.print(Shared.getConfig("mant") + "\t");
        ans.setFKDAT(of.createZSDSPOSDEVFKDAT(myDay.replace("-", "")));
        System.out.print(myDay.replace("-", "") + "\t");
        ans.setVBELN(of.createZSDSCABDEVVBELN(idF));
        System.out.print(idF + "\t");
        ans.setZTIPV(of.createZSDSCABDEVZTIPV(kind));
        System.out.print(kind + "\t");
        ans.setKUNNR(of.createZSDSCABDEVKUNNR(client));
        System.out.print(client + "\t");
        ans.setRANGO(of.createZSDSCABDEVRANGO(range));
        System.out.print(range + "\t");
        ans.setREPOZ(of.createZSDSCABDEVREPOZ(z));
        System.out.print(z + "\t");
        ans.setIMPRE(of.createZSDSCABDEVIMPRE(printerId));
        System.out.print(printerId + "\t");
        ans.setWAERS(of.createZSDSCABDEVWAERS(Shared.getConfig("waerks")));
        System.out.print(Shared.getConfig("waerks") + "\t");
        ans.setWERKS(of.createZSDSCABDEVWERKS(Shared.getConfig("storePrefix")+Shared.getConfig("storeName")));
        System.out.print(Shared.getConfig("storePrefix")+Shared.getConfig("storeName") + "\n");
        return ans;
    }

    public ZSDSCABDEV getHeader(String myDay){
        ZSDSCABDEV ans = new ZSDSCABDEV();
        String idF = "D" + id;

        String range = minFiscalId + "-" + maxFiscalId;

        System.out.println("MANDT\tFKDAT\tVBELN\tZTIPV\tKUNNR\tRANGO\tREPOZ\tIMPRE\tWAERS\tWERKS");
        ans.setMANDT(of.createZSDSCABDEVMANDT(Shared.getConfig("mant")));
        System.out.print(Shared.getConfig("mant") + "\t");
        ans.setFKDAT(of.createZSDSPOSDEVFKDAT(myDay.replace("-", "")));
        System.out.print(myDay.replace("-", "") + "\t");
        ans.setVBELN(of.createZSDSCABDEVVBELN(idF));
        System.out.print(idF + "\t");
        ans.setZTIPV(of.createZSDSCABDEVZTIPV(kind));
        System.out.print(kind + "\t");
        ans.setKUNNR(of.createZSDSCABDEVKUNNR(client));
        System.out.print(client + "\t");
        ans.setRANGO(of.createZSDSCABDEVRANGO(range));
        System.out.print(range + "\t");
        ans.setREPOZ(of.createZSDSCABDEVREPOZ(z));
        System.out.print(z + "\t");
        ans.setIMPRE(of.createZSDSCABDEVIMPRE(printerId));
        System.out.print(printerId + "\t");
        ans.setWAERS(of.createZSDSCABDEVWAERS(Shared.getConfig("waerks")));
        System.out.print(Shared.getConfig("waerks") + "\t");
        ans.setWERKS(of.createZSDSCABDEVWERKS(Shared.getConfig("storePrefix")+Shared.getConfig("storeName")));
        System.out.print(Shared.getConfig("storePrefix")+Shared.getConfig("storeName") + "\n");
        return ans;
    }

    public ArrayOfZSDSPOSDEV getDetails(String myDay){
        ArrayOfZSDSPOSDEV ansP = new ArrayOfZSDSPOSDEV();
        List<ZSDSPOSDEV> ans = ansP.getZSDSPOSDEV();

        int position = 1;
        for (Receipt receipt : receipts) {
            for (Item2Receipt item2Receipt : receipt.getItems()) {
                if ( item2Receipt.getQuant() > 0 ){
                    String idd = item2Receipt.getItem().getCode();
                    ZSDSPOSDEV zdpd = new ZSDSPOSDEV();
                    zdpd.setMANDT(of.createZSDSPOSDEVMANDT(Shared.getConfig("mant")));
                    System.out.print(Shared.getConfig("mant") + "\t");
                    zdpd.setFKDAT(of.createZSDSPOSDEVFKDAT(myDay.replace("-", "")));
                    System.out.print(myDay.replace("-", "") + "\t");
                    zdpd.setVBELN(of.createZSDSPOSDEVVBELN("D" + id ));
                    System.out.print("D" + id + "\t");
                    zdpd.setPOSNR(of.createZSDSPOSDEVPOSNR(Shared.df2intSAP.format(position++)));
                    System.out.print(Shared.df2intSAP.format(position-1) + "\t");
                    zdpd.setEAN11(of.createZSDSPOSDEVEAN11(idd));
                    System.out.print(idd + "\t");
                    zdpd.setKWMENG(new BigDecimal(item2Receipt.getQuant()));
                    System.out.print(item2Receipt.getQuant() + "\t");
                    zdpd.setVRKME(of.createZSDSPOSDEVVRKME(item2Receipt.getItem().getSellUnits()));
                    System.out.print(item2Receipt.getItem().getSellUnits() + "\t");
                    zdpd.setCHARG(of.createZSDSPOSDEVCHARG(""));
                    System.out.print("" + "\t");
                    zdpd.setKBETP(new BigDecimal(item2Receipt.getSellPrice()));
                    System.out.print(item2Receipt.getSellPrice() + "\t");

                    zdpd.setKBETD(new BigDecimal((item2Receipt.getSellDiscount()/100.0)*item2Receipt.getSellPrice()));
                    System.out.print((item2Receipt.getSellDiscount()/100.0)*item2Receipt.getSellPrice() + "\t");
                    zdpd.setPERNR(of.createZSDSPOSDEVPERNR("999999"));
                    System.out.print("999999" + "\t");
                    zdpd.setWERKS(of.createZSDSPOSDEVWERKS(Shared.getConfig("storePrefix")+Shared.getConfig("storeName")));
                    System.out.print(Shared.getConfig("storePrefix")+Shared.getConfig("storeName") + "\n");
                    ans.add(zdpd);
                }
            }
        }
        return ansP;
    }

    public ArrayOfZSDSPOSFACT getDetailsF(String myDay){
        ArrayOfZSDSPOSFACT ansP = new ArrayOfZSDSPOSFACT();
        List<ZSDSPOSFACT> ans = ansP.getZSDSPOSFACT();

        /**
         * I am not proud about this code xDDD
         * But, it is simply fast!
         */

        int position = 1;
        for (Receipt receipt : receipts) {
            for (Item2Receipt item2Receipt : receipt.getItems()) {
                if ( item2Receipt.getQuant() > 0 ){
                    String idd = item2Receipt.getItem().getCode();
                    ZSDSPOSFACT zdpd = new ZSDSPOSFACT();
                    zdpd.setMANDT(of.createZSDSPOSDEVMANDT(Shared.getConfig("mant")));
                    System.out.print(Shared.getConfig("mant") + "\t");
                    zdpd.setFKDAT(of.createZSDSPOSDEVFKDAT(myDay.replace("-", "")));
                    System.out.print(myDay.replace("-", "") + "\t");
                    zdpd.setVBELN(of.createZSDSPOSDEVVBELN("F" + id ));
                    System.out.print("F" + id + "\t");
                    zdpd.setPOSNR(of.createZSDSPOSDEVPOSNR(Shared.df2intSAP.format(position++)));
                    System.out.print(Shared.df2intSAP.format(position-1) + "\t");
                    zdpd.setEAN11(of.createZSDSPOSDEVEAN11(idd));
                    System.out.print(idd + "\t");
                    zdpd.setKWMENG(new BigDecimal(item2Receipt.getQuant()));
                    System.out.print(item2Receipt.getQuant() + "\t");
                    zdpd.setVRKME(of.createZSDSPOSDEVVRKME(item2Receipt.getItem().getSellUnits()));
                    System.out.print(item2Receipt.getItem().getSellUnits() + "\t");
                    zdpd.setCHARG(of.createZSDSPOSDEVCHARG(""));
                    System.out.print("" + "\t");
                    zdpd.setKBETP(new BigDecimal(item2Receipt.getSellPrice()));
                    System.out.print(item2Receipt.getSellPrice() + "\t");

                    zdpd.setKBETD(new BigDecimal((item2Receipt.getSellDiscount()/100.0)*item2Receipt.getSellPrice()));
                    System.out.print((item2Receipt.getSellDiscount()/100.0)*item2Receipt.getSellPrice() + "\t");
                    zdpd.setPERNR(of.createZSDSPOSDEVPERNR("999999"));
                    System.out.print("999999" + "\t");
                    zdpd.setWERKS(of.createZSDSPOSDEVWERKS(Shared.getConfig("storePrefix")+Shared.getConfig("storeName")));
                    System.out.print(Shared.getConfig("storePrefix")+Shared.getConfig("storeName") + "\n");
                    ans.add(zdpd);
                }
            }
        }
        return ansP;
    }

}
