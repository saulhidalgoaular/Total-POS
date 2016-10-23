package totalpos;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;

/**
 *
 * @author shidalgo
 */
public class Sticker {

    private static int offset = 440;
    private static int pixA[] = {160,170,215,160,180,20,20,240,235,235,30};
    private static int pixB[] = {0,15,40,45,-1,100,115,140,160,160,130};
    private static String header[] = {"A","A","A","A","A","A","A","A","A","A","B"};
    private static boolean inicialized = false;
    private static PrintService psZebra = null;
    private static String date;

    public static void inicialize(){
        if ( !inicialized ){
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date d = new Date();
            date = dateFormat.format(d);


            PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
            for (int i = 0; i < services.length; i++) {
                PrintService printService = services[i];
                if ( printService.getName().equals("Zebra") ){
                    psZebra = printService;
                    break;
                }
            }

            if ( psZebra == null  ){
                MessageBox msb = new MessageBox(MessageBox.SGN_DANGER, "No se ha conseguido la impresora llamada \"Zebra\"");
                msb.show(Shared.getMyMainWindows());
                return;
            }
        }

    }

    public static void configure(){
        inicialize();
        
        try{
            DocPrintJob job = psZebra.createPrintJob();

            String buff = "ZB\nR10,20\n";

            DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
            Doc doc = new SimpleDoc(buff.getBytes(), flavor, null);
            job.print(doc, null);
            MessageBox msb = new MessageBox(MessageBox.SGN_SUCCESS, "Se enviaron los comandos de configuración satisfactoriamente.");
            msb.show(Shared.getMyMainWindows());
        }catch (PrintException ex) {
            MessageBox msb = new MessageBox(MessageBox.SGN_DANGER, "Problemas con la impresora.",ex);
            msb.show(Shared.getMyMainWindows());
        }
    }

    public static void print(Item a_, int na){
        inicialize();
        try {
            if ( na <= 0 ) return;
            
            //System.out.println("Na = " + na);
            Item a = new Item(a_);
            int firstLot = (na - na%2)/2;

            String description = a.getDescription();
            String mark = a.getMark();
            String barCode = a.getMainBarcode();
            Double disc = (100.0-a.getDescuento())/100.0;
            
            String price = Shared.df.format(Math.round((new Price(null,a.getLastPrice().plusIva().getQuant()*disc)).getQuant()));
            
            String description2 = "";
            if (a.getDescription().length() > 34) {
                description2 = a.getDescription().substring(34);
                description = a.getDescription().substring(0, 34);
            }
            if (description2.length() > 34) {
                description2 = description2.substring(0, 34);
            }
            if (mark.length() > 15) {
                mark = mark.substring(0, 15);
            }
                        
            DocPrintJob job = psZebra.createPrintJob();
            String buff =
                "N\n" +
                header[0] + pixA[0] + "," + pixB[0] + ",0,1,1,1,N,\"Grupo Total 99 C.A.\"\n"+
                "A" + pixA[1] + ",15,0,1,1,1,N,\"RIF: J-31150187-8\"\n"+
                "A" + pixA[2] + ",40,0,4,1,2,N,\""+ price +"\"\n"+
                "A" + pixA[3] + ",45,0,4,1,1,N,\"Bs.\"\n"+
                //"A" + separations[4] + ",80,0,3,1,1,N,\""+mark+"\"\n"+
                "A" + pixA[5] + ",100,0,1,1,1,N,\""+description+"\"\n"+
                "A" + pixA[6] + ",115,0,1,1,1,N,\""+description2+"\"\n"+
                "A" + pixA[7] + ",140,0,1,1,1,N,\""+date + "\"\n"+
                "A" + pixA[8] + ",160,0,1,1,1,N,\""+barCode +"\"\n"+
                "A" + pixA[9] + ",180,0,1,1,1,N,\""+"AGENCIA " + Shared.getConfig("storeName") +"\"\n"+
                "B" + pixA[10] + ",130,0,1,1,2,100,N,\"" + barCode + "\"\n"+

                "A" + (offset+pixA[0]) +",0,0,1,1,1,N,\"Grupo Total 99 C.A.\"\n"+
                "A" + (offset+pixA[1]) + ",15,0,1,1,1,N,\"RIF: J-31150187-8\"\n"+
                "A" + (offset+pixA[2]) + ",40,0,4,1,2,N,\""+price +"\"\n"+
                "A" + (offset+pixA[3]) + ",45,0,4,1,1,N,\"Bs.\"\n"+
                //"A" + (offset+separations[4]) + ",80,0,3,1,1,N,\""+mark+"\"\n"+
                "A" + (offset+pixA[5]) + ",100,0,1,1,1,N,\""+description+"\"\n"+
                "A" + (offset+pixA[6]) + ",115,0,1,1,1,N,\""+description2+"\"\n"+
                "A" + (offset+pixA[7]) + ",140,0,1,1,1,N,\""+date + "\"\n"+
                "A" + (offset+pixA[8]) + ",160,0,1,1,1,N,\""+barCode +"\"\n"+
                "A" + (offset+pixA[9]) + ",180,0,1,1,1,N,\"" +"AGENCIA "  + Shared.getConfig("storeName") +"\"\n"+
                "B" + (offset+pixA[10]) + ",130,0,1,1,2,100,N,\"" + barCode + "\"\n"+
                "P" + firstLot + "\n";

            DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
            Doc doc = new SimpleDoc(buff.getBytes(), flavor, null);
            job.print(doc, null);
        } catch (PrintException ex) {
            MessageBox msb = new MessageBox(MessageBox.SGN_DANGER, "Problemas con la impresora.",ex);
            msb.show(Shared.getMyMainWindows());
        }
    }

    static void print(Item a) {
        inicialize();
        
        String description = a.getDescription();
        String mark = a.getMark();
        String barCode = a.getMainBarcode();
        Double disc = (100.0-a.getDescuento())/100.0;
        String price = Shared.df.format(Math.round((new Price(null,a.getLastPrice().plusIva().getQuant()*disc)).getQuant()));

        String description2 = "";
        
        if (a.getDescription().length() > 34) {
            description2 = a.getDescription().substring(34);
            description = a.getDescription().substring(0, 34);
        }
        if (description2.length() > 34) {
            description2 = description2.substring(0, 34);
        }
        if (mark.length() > 15) {
            mark = mark.substring(0, 15);
        }

        try{
            DocPrintJob job = psZebra.createPrintJob();
            String buff =
                "N\n" +
                header[0] + pixA[0] + "," + pixB[0] + ",0,1,1,1,N,\"Grupo Total 99 C.A.\"\n"+
                "A" + pixA[1] + ",15,0,1,1,1,N,\"RIF: J-31150187-8\"\n"+
                "A" + pixA[2] + ",40,0,4,1,2,N,\""+ price +"\"\n"+
                "A" + pixA[3] + ",45,0,4,1,1,N,\"Bs.\"\n"+
                //"A" + separations[4] + ",80,0,3,1,1,N,\""+mark+"\"\n"+
                "A" + pixA[5] + ",100,0,1,1,1,N,\""+description+"\"\n"+
                "A" + pixA[6] + ",115,0,1,1,1,N,\""+description2+"\"\n"+
                "A" + pixA[7] + ",140,0,1,1,1,N,\""+date + "\"\n"+
                "A" + pixA[8] + ",160,0,1,1,1,N,\""+barCode +"\"\n"+
                "A" + pixA[9] + ",180,0,1,1,1,N,\"" +"AGENCIA "  + Shared.getConfig("storeName") +"\"\n"+
                "B" + pixA[10] + ",130,0,1,1,2,100,N,\"" + barCode + "\"\n"+
                "P1\n";

            DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
            Doc doc = new SimpleDoc(buff.getBytes(), flavor, null);
            job.print(doc, null);
        } catch (PrintException ex) {
            MessageBox msb = new MessageBox(MessageBox.SGN_DANGER, "Problemas con la impresora.",ex);
            msb.show(Shared.getMyMainWindows());
        }

    }

    static void print(Item a, Item b) {
        inicialize();

        String description = a.getDescription();
        String mark = a.getMark();
        String barCode = a.getMainBarcode();
        Double disc = (100.0-a.getDescuento())/100.0;
        String price = Shared.df.format(Math.round((new Price(null,a.getLastPrice().plusIva().getQuant()*disc)).getQuant()));

        String descriptionB = b.getDescription();
        String markB = b.getMark();
        String barCodeB = b.getMainBarcode();
        Double discB = (100.0-b.getDescuento())/100.0;
        String priceB = Shared.df.format(Math.round((new Price(null,b.getLastPrice().plusIva().getQuant()*discB)).getQuant()));

        String description2 = "";

        if (a.getDescription().length() > 34) {
            description2 = a.getDescription().substring(34);
            description = a.getDescription().substring(0, 34);
        }
        if (description2.length() > 34) {
            description2 = description2.substring(0, 34);
        }
        if (mark.length() > 15) {
            mark = mark.substring(0, 15);
        }

        String description2B = "";

        if (b.getDescription().length() > 34) {
            description2B = b.getDescription().substring(34);
            descriptionB = b.getDescription().substring(0, 34);
        }
        if (description2B.length() > 34) {
            description2B = description2B.substring(0, 34);
        }
        if (markB.length() > 15) {
            markB = markB.substring(0, 15);
        }

        try{
            DocPrintJob job = psZebra.createPrintJob();
            String buff =
                "N\n" +
                header[0] + pixA[0] + "," + pixB[0] + ",0,1,1,1,N,\"Grupo Total 99 C.A.\"\n"+
                "A" + pixA[1] + ",15,0,1,1,1,N,\"RIF: J-31150187-8\"\n"+
                "A" + pixA[2] + ",40,0,4,1,2,N,\""+ price +"\"\n"+
                "A" + pixA[3] + ",45,0,4,1,1,N,\"Bs.\"\n"+
                //"A" + separations[4] + ",80,0,3,1,1,N,\""+mark+"\"\n"+
                "A" + pixA[5] + ",100,0,1,1,1,N,\""+description+"\"\n"+
                "A" + pixA[6] + ",115,0,1,1,1,N,\""+description2+"\"\n"+
                "A" + pixA[7] + ",140,0,1,1,1,N,\""+date + "\"\n"+
                "A" + pixA[8] + ",160,0,1,1,1,N,\""+barCode +"\"\n"+
                "A" + pixA[9] + ",180,0,1,1,1,N,\"" +"AGENCIA "  +Shared.getConfig("storeName")  +"\"\n"+
                "B" + pixA[10] + ",130,0,1,1,2,100,N,\"" + barCode + "\"\n"+

                "A" + (offset+pixA[0]) +",0,0,1,1,1,N,\"Grupo Total 99 C.A.\"\n"+
                "A" + (offset+pixA[1]) + ",15,0,1,1,1,N,\"RIF: J-31150187-8\"\n"+
                "A" + (offset+pixA[2]) + ",40,0,4,1,2,N,\""+priceB +"\"\n"+
                "A" + (offset+pixA[3]) + ",45,0,4,1,1,N,\"Bs.\"\n"+
                //"A" + (offset+separations[4]) + ",80,0,3,1,1,N,\""+mark+"\"\n"+
                "A" + (offset+pixA[5]) + ",100,0,1,1,1,N,\""+descriptionB+"\"\n"+
                "A" + (offset+pixA[6]) + ",115,0,1,1,1,N,\""+description2B+"\"\n"+
                "A" + (offset+pixA[7]) + ",140,0,1,1,1,N,\""+date + "\"\n"+
                "A" + (offset+pixA[8]) + ",160,0,1,1,1,N,\""+barCodeB +"\"\n"+
                "A" + (offset+pixA[9]) + ",180,0,1,1,1,N,\""+"AGENCIA " +Shared.getConfig("storeName")  +"\"\n"+
                "B" + (offset+pixA[10]) + ",130,0,1,1,2,100,N,\"" + barCodeB + "\"\n"+
                "P1\n";

            DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
            Doc doc = new SimpleDoc(buff.getBytes(), flavor, null);
            job.print(doc, null);
        } catch (PrintException ex) {
            MessageBox msb = new MessageBox(MessageBox.SGN_DANGER, "Problemas con la impresora.",ex);
            msb.show(Shared.getMyMainWindows());
        }
    }

}
