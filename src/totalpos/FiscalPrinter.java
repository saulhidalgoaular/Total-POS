package totalpos;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.ptr.IntByReference;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;

/**
 *
 * @author Saúl Hidalgo.
 */
public class FiscalPrinter {
    private FiscalDriver printer;
    public boolean isOk = false;
    public String printerSerial = null;
    private String z = null;
    public String lastReceipt = null;
    public boolean isReceipt = false;

    public FiscalPrinter() {
        if ( Constants.withFiscalPrinter ){
            printer = (FiscalDriver) Native.loadLibrary(Shared.getFileConfig("printerDriver"), FiscalDriver.class);
        }
    }

    public boolean checkPrinter() throws SQLException, FileNotFoundException, Exception {
        if ( !Constants.withFiscalPrinter ){
            return true;
        }
        return isTheSame(ConnectionDrivers.getMyPrinter());
    }

    private void calculateSerial() throws Exception{

        if ( !Constants.withFiscalPrinter ){
            return;
        }
        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " calculateSerial");

        if ( Shared.getFileConfig("printerDriver").equals("PrnFiscalDLL32") ){
            // TODO CODE HERE =D!

            //printer.ClosePort();


            System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Probando Timeout");
            printer.SetTimeOuts((byte)6);
            System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Termino de probar");

            int ans = printer.OpenPort(Byte.parseByte(Shared.getFileConfig("printerPort")), (byte)2);
            //printer.CancelTransaction();

            System.out.println("Ams = " + ans);
            if ( ans != 0 ){
                throw new Exception(Shared.ncrErrMapping.get(ans));
            }

            TQueryPrnStatus tqps = new TQueryPrnStatus();
            ans = printer.QueryPrnStatus(tqps);

            if ( ans != 0 ){
                throw new Exception(Shared.ncrErrMapping.get(ans));
            }
            
            printerSerial = Shared.b2s(tqps.PrnID);
            z = (tqps.UltZ+1) + "";

            TQueryPrnTransaction tqpt = new TQueryPrnTransaction();
            ans = printer.QueryPrnTransaction((byte)1, tqpt);

            if ( isReceipt ){
                lastReceipt = tqpt.VoucherVta + "";
            }else{
                lastReceipt = tqpt.VoucherDev + "";
            }
            System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " LastReceipt = " + lastReceipt);

            //ans = printer.ClosePort();

            /*if ( ans != 0 ){
                System.out.println(Shared.ncrErrMapping.get(ans));
            }*/
        }else if ( Shared.getFileConfig("printerDriver").equals("tfhkaif") ){
            boolean ansT;
            isOk = false;
            IntByReference a = new IntByReference();
            IntByReference b = new IntByReference();
            ansT = printer.OpenFpctrl(Shared.getFileConfig("printerPort"));
            if ( !ansT ){
                throw new Exception(Shared.getErrMapping().get(128));
            }

            ansT = printer.UploadStatusCmd(a, b, "S1", Constants.tmpDir + Shared.getConfig("tmpFileName"));
            if ( b.getValue() != 0 ){
                throw new Exception(Shared.getErrMapping().get(b.getValue()));
            }
            assert (ansT);

            File file = new File(Constants.tmpDir + Shared.getConfig("tmpFileName"));

            Scanner sc = new Scanner(file);

            ansT = sc.hasNext();
            assert(ansT);
            String line = sc.next();


            printerSerial = line.substring(66, 76);
            z = Shared.df2z.format(Integer.parseInt(line.substring(47, 51))+1);
            lastReceipt = line.substring(21,21+8);
            sc.close();
            file.delete();
            printer.CloseFpctrl();
        }else{
            throw new Exception("Driver de impresora desconocido!");
        }
    }

    public boolean isTheSame(String serial) throws Exception{
        if ( !Constants.withFiscalPrinter ){
            return true;
        }
        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Tiene " + getSerial());
        return getSerial().equals(serial);
    }

    private void restartCommunicationNCR(){
        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Cancelar transaccion...");
        printer.CancelTransaction();
        try {
            Thread.currentThread().sleep(500);
        } catch (InterruptedException ex) {
            ;
        }
        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Cerrar Puerto...");
        //printer.ClosePort();
        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Esperar 5 segundos...");

        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Abrir puerto... ");
        //int a = printer.OpenPort(Byte.parseByte(Shared.getFileConfig("printerPort")), (byte)2);
        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Listo =D ");
        /*try {
            Thread.currentThread().sleep(2000);
        } catch (InterruptedException ex) {
            ;
        }*/
    }

    // TODO Check difference between predicted and everythingCool
    public void printTicket(List<Item2Receipt> items , Client client, Double globalDiscount, String ticketId, User u , List<PayForm> pfs) throws Exception{
        if ( !Constants.withFiscalPrinter ){
            return;
        }
        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " PrintTicket " + ticketId);
        boolean everythingCool = true;
        boolean predicted = false;
        
        if ( Shared.getFileConfig("printerDriver").equals("PrnFiscalDLL32") ){
            isOk = false;

            //System.out.println("Cerrando puerto...");
            //printer.ClosePort();
            //System.out.println("Abriendo puerto...");
            //int ans = printer.OpenPort(Byte.parseByte(Shared.getFileConfig("printerPort")), (byte)2);

            TQueryPrnStatus tqps = new TQueryPrnStatus();
            int ans = printer.QueryPrnStatus(tqps);
            byte b = tqps.PrnStatusHdw[0];
            byte c = tqps.PrnStatusHdw[1];
            System.out.println("b = " + b);
            System.out.println("c = " + c);

            if ( tqps.PrnStatusApp != 0 ){
                printer.CancelTransaction();
            }
            /*if ( (4 & c) == 4 ){
                throw new Exception("Gaveta abierta");
            }*/

            printer.OpenBox();
            //System.out.println("Cancelando Transaccion...");
            //printer.CancelTransaction();

            if ( ans != 0 ){
                throw new Exception(Shared.ncrErrMapping.get(ans));
            }
            
            Calendar calendar = GregorianCalendar.getInstance();
            Date dd = Shared.sdf4ncr.parse(ConnectionDrivers.getDate4NCR());
            calendar.setTime(dd);

            if ( client == null || client.getId() == null || client.getId().isEmpty() ){
                client = new Client("", "", "", "");
            }

            int hour = calendar.get(Calendar.HOUR) == 0?12:calendar.get(Calendar.HOUR);

            System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Anyo = " +  calendar.get(Calendar.YEAR)%100);
            System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Fecha enviada: " + calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH)+1) + "/"
                    + (byte)(calendar.get(Calendar.YEAR)%100)+ " " + hour + ":" + calendar.get(Calendar.MINUTE)
                    + ":" + calendar.get(Calendar.SECOND) + " " + ((calendar.get(Calendar.AM_PM) == Calendar.AM)?0:1));
            System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Abriendo documentos...");
            ans = printer.NewDoc(Integer.parseInt(Shared.getConfig("receipt")), client.getName(), client.getId(),
                    Shared.getUser().getLogin() + " " + ticketId, "ABC", new NativeLong(0), (calendar.get(Calendar.DAY_OF_MONTH)),
                    //(calendar.get(Calendar.MONTH)+1), calendar.get(Calendar.YEAR)%100+Constants.ncrYearOffset, (calendar.get(Calendar.HOUR)+12)%13,
                    (calendar.get(Calendar.MONTH)+1), calendar.get(Calendar.YEAR)%100, hour,
                    calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND),
                    ((calendar.get(Calendar.AM_PM) == Calendar.AM)?0:1) ,(calendar.get(Calendar.DAY_OF_MONTH)),
                    (calendar.get(Calendar.MONTH)+1), calendar.get(Calendar.YEAR)%100, hour,
                    calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND),
                    ((calendar.get(Calendar.AM_PM) == Calendar.AM)?0:1));

            System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Ans = " + ans);

            if ( ans != 0 ){
                throw new Exception(Shared.ncrErrMapping.get(ans));
            }


            TQueryPrnTransaction tqpt = new TQueryPrnTransaction();

            ans = printer.QueryPrnTransaction((byte)1, tqpt);

            if ( ans != 0 ){
                throw new Exception(Shared.ncrErrMapping.get(ans));
            }
            String nextReceipt = tqpt.VoucherVta + "";

            if ( ans != 0 || tqpt.VoucherVta <= 0 || tqpt.VoucherVta > Integer.parseInt(Shared.getConfig("maximumFiscalNumber")) ){
                throw new Exception(Shared.ncrErrMapping.get(ans));
            }
            Double subtotal = .0;
            int itemsQuant = 0;
            for (Item2Receipt item2r : items) {
                System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Enviando articulo...." + item2r.getItem().getCode());
                String d = item2r.getItem().getDescription();
                subtotal += item2r.getSellPrice()*(1.0 - item2r.getSellDiscount()/100.0)*item2r.getQuant();
                ans = printer.NewItem((byte)0, (byte)0, item2r.getQuant()+.0, Shared.round(item2r.getSellPrice(),2) , (item2r.getItem().getModel()+"-"+d).substring(0, Math.min(Integer.parseInt(Shared.getConfig("maxNcrDescription")), d.length())));
                if ( ans != 0 ){
                    throw new Exception(Shared.ncrErrMapping.get(ans));
                }
                if ( Math.abs(item2r.getItem().getDescuento()) > Double.parseDouble(Shared.getConfig("exilon")) ){
                    ans = printer.OprDoc((byte)0, (byte)0, Shared.round((item2r.getQuant()+.0)*item2r.getSellPrice()*(item2r.getSellDiscount()/100.0),2), ((item2r.getSellDiscount())+"%").replace(',', '.'));
                }
                if ( ans != 0 ){
                    throw new Exception(Shared.ncrErrMapping.get(ans));
                }
                itemsQuant += item2r.getQuant();
            }

            TreeMap<String,Double> buff = new TreeMap<String , Double>();
            buff.put(Shared.getConfig("cashPaymentName"), .0);
            buff.put(Shared.getConfig("CNPaymentName"), .0);
            buff.put(Shared.getConfig("creditPaymentName"), .0);
            buff.put(Shared.getConfig("debitPaymentName"), .0);
            buff.put(Shared.getConfig("americanExpressPaymentName"), .0);
            for (PayForm payForm : pfs) {
                buff.put( payForm.getFormWay() , buff.get(payForm.getFormWay()) + payForm.getQuant());
            }
            System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Enviando cantidad de articulos...");
            ans = printer.PrintTextNoFiscal(Integer.parseInt(Shared.getConfig("normalFont")),"                                   ",1,(byte)0);
            ans = printer.PrintTextNoFiscal(Integer.parseInt(Shared.getConfig("normalFont")),"===================================",2,(byte)0);
            ans = printer.PrintTextNoFiscal(Integer.parseInt(Shared.getConfig("normalFont")),"      CANTIDAD DE ARTICULOS " + itemsQuant,3,(byte)0);
            ans = printer.PrintTextNoFiscal(Integer.parseInt(Shared.getConfig("normalFont")),"===================================",4,(byte)0);
            
            ans = printer.PrintTextNoFiscal(Integer.parseInt(Shared.getConfig("normalFont")),"CAMBIOS SOLO LUNES, MIERCOLES Y SABADO",5,(byte)1);
            ans = printer.PrintTextNoFiscal(Integer.parseInt(Shared.getConfig("normalFont")),"HASTA 5PM. NO SE PUEDEN HACER CAMBIOS",6,(byte)1);
            ans = printer.PrintTextNoFiscal(Integer.parseInt(Shared.getConfig("normalFont")),"CON MAS DE 15 DIAS DE ANTIGUEDAD =D.",7,(byte)1);

            System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Código de barras 70");
            subtotal = Math.round(subtotal * 100)/100.0;
            System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " SubTotal = " + subtotal);

            int ansOfPrinting = 0;
            ansOfPrinting = printer.CloseDoc(buff.get(Shared.getConfig("cashPaymentName")), buff.get(Shared.getConfig("CNPaymentName")), buff.get(Shared.getConfig("creditPaymentName")), buff.get(Shared.getConfig("debitPaymentName")),
                    buff.get(Shared.getConfig("americanExpressPaymentName")), .0, subtotal*globalDiscount, .0, (byte)12 , (byte)70, ticketId);
            /*ansOfPrinting = printer.CloseDoc(1, 0, 0, 0,
                    0, .0, 10, .0, (byte)12 , (byte)70, ticketId);*/

            System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " ans Close Doc = " + ansOfPrinting);
            System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Numero supuesto siguiente fiscal " + nextReceipt);

            tqpt = new TQueryPrnTransaction();

            int tries = 1;
            boolean isOk = false;
            do{
                System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Intento " + tries);
                int cancelT = printer.CancelTransaction();
                System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Respuesta de Cancelar = " + cancelT);
                ans = printer.QueryPrnTransaction((byte)2, tqpt);
                System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Respuesta del Primer Intento = " + ans);

                predicted = false;
                if ( ans != 0 || tqpt.VoucherVta <= 0 || tqpt.VoucherVta > Integer.parseInt(Shared.getConfig("maximumFiscalNumber")) ){
                    restartCommunicationNCR();
                }else{
                    lastReceipt = tqpt.VoucherVta + "";
                    System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Numero Fiscal detectado = " + lastReceipt);
                    isOk = true;
                }
                ++tries;
            }
            while( tries <= Integer.parseInt(Shared.getConfig("triesWithPrinter")) && !isOk );

            if ( !isOk ){
                lastReceipt = nextReceipt;

                ConnectionDrivers.flipEnabledPointOfSale(new PointOfSale(Shared.getFileConfig("myId"), "", "", true));
                MessageBox msb = new MessageBox(MessageBox.SGN_WARNING, "La impresora fiscal no esta respondiendo correctamente. Se ha guardado la factura y se ha bloqueado esta caja.");
                msb.show(null);
                System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Caja bloqueada");
            }else if ( nextReceipt.equals(lastReceipt) ){
                // so far so good
            }else{
                throw new Exception(Shared.ncrErrMapping.get(ansOfPrinting));
            }

            //ans = printer.ClosePort();

            try{
                updateValues("curdate()");
            }catch(Exception exx){
                // =(
            }
            isOk = true;
        }else if ( Shared.getFileConfig("printerDriver").equals("tfhkaif") ){
            isOk = false;
            IntByReference a = new IntByReference();
            IntByReference b = new IntByReference();
            printer.OpenFpctrl(Shared.getFileConfig("printerPort"));

            List<String> buffer = new ArrayList<String>();

            if ( !items.isEmpty() ){
                int line = 1;
                if ( client != null && !client.getId().isEmpty() ){
                    buffer.add("i0" + ( line++ ) + "RIF: " + client.getId());
                    if ( !client.getName().trim().isEmpty() ) {
                        buffer.add("i0" + (line++) + "Nombre: " + client.getName());
                    }
                    if ( !client.getPhone().trim().isEmpty() ) {
                        buffer.add("i0" + (line++) + "Telefono: " + client.getPhone());
                    }
                    if ( !client.getAddress().trim().isEmpty() ) {
                        buffer.add("i0" + (line++) + "Direccion: " + client.getAddress());
                    }
                }
                buffer.add("i0" + ( line++ ) + "Correlativo: " + ticketId);
                buffer.add("i0" + ( line++ ) + "Caja: " + Shared.getFileConfig("myId"));
                buffer.add("i0" + ( line++ ) + "SIN DERECHO A CREDITO FISCAL");

                for (String bu : buffer) {
                    printer.SendCmd(a, b, bu);
                    if ( b.getValue() != 0 ){
                        throw new Exception(Shared.getErrMapping().get(b.getValue()));
                    }
                }

                for (Item2Receipt item2r : items) {
                    Item item = item2r.getItem();
                    // TODO OJO!
                    // " " EXENTO!
                    // "!" Tasa de Iva 1
                    printer.SendCmd(a, b, Shared.getConfig("tfhkaifKindTaxReceipt") + ( Shared.formatDoubleToPrint(item.getLastPrice().getQuant()) ) +
                            Shared.formatQuantToPrint(item2r.getQuant()+.0) + item.getDescription().substring(0, Math.min(item.getDescription().length(), 37)));
                    if ( b.getValue() != 0 ){
                        throw new Exception(Shared.getErrMapping().get(b.getValue()));
                    }
                    if ( item.getDescuento() != .0 ){
                        Double finalDiscount = item.getDescuento();
                        if ( finalDiscount > .0 ){
                            printer.SendCmd(a, b, "p-"+Shared.formatDoubleToPrintDiscount(finalDiscount/100.0));
                            if ( b.getValue() != 0 ){
                                throw new Exception(Shared.getErrMapping().get(b.getValue()));
                            }
                        }
                    }
                }
                try{
                    printer.SendCmd(a, b, "3");
                    if ( b.getValue() != 0 ){
                        // Fiscal Number has been generated.
                        //System.out.println("RECUPERANDO NUMERO FISCAL!!");
                        lastReceipt = Integer.parseInt(ConnectionDrivers.getLastReceipt())+1+"";
                        everythingCool = false;
                        predicted = true;
                    }

                    if ( everythingCool ){
                        printer.SendCmd(a, b, "y" + ticketId);// + Shared.formatDoubleToPrintDiscount(globalDiscount));
                        if ( b.getValue() != 0 ){
                            lastReceipt = Integer.parseInt(ConnectionDrivers.getLastReceipt())+1+"";
                            everythingCool = false;
                            predicted = true;
                        }

                        if ( globalDiscount != null && globalDiscount > 0 ){
                            printer.SendCmd(a, b, "p-" + Shared.formatDoubleToPrintDiscount(globalDiscount));
                            if ( b.getValue() != 0 ){
                                lastReceipt = Integer.parseInt(ConnectionDrivers.getLastReceipt())+1+"";
                                everythingCool = false;
                                predicted = true;
                            }
                        }
                        for (PayForm pf : pfs) {
                            //TODO Put it in the configuracion database.
                            /**
                             * Put it in the configuracion file.
                             */
                            String cmd = "01";
                            if ( pf.getFormWay().equals(Shared.getConfig("cashPaymentName")) ){
                                cmd = "01";
                            }else if ( pf.getFormWay().equals(Shared.getConfig("CNPaymentName")) ){
                                cmd = "02";
                            }else if ( pf.getFormWay().equals(Shared.getConfig("creditPaymentName")) ){
                                cmd = "09";
                            }else if ( pf.getFormWay().equals(Shared.getConfig("debitPaymentName")) ){
                                cmd = "10";
                            }else if ( pf.getFormWay().equals(Shared.getConfig("americanExpressPaymentName")) ){
                                cmd = "11";
                            }
                            printer.SendCmd(a, b, "2" + cmd + Shared.formatDoubleToSpecifyMoneyInPrinter(pf.getQuant()));
                            /*if ( b.getValue() != 0 ){
                                lastReceipt = Integer.parseInt(ConnectionDrivers.getLastReceipt())+1+"";
                                everythingCool = false;
                                predicted = true;
                            }*/
                        }
                        printer.SendCmd(a, b, "2" + "01" + Shared.formatDoubleToSpecifyMoneyInPrinter(Double.parseDouble(Shared.getConfig("add2PayForm"))));
                    }
                }catch(Exception ex){
                    lastReceipt = Integer.parseInt(ConnectionDrivers.getLastReceipt())+1+"";
                    everythingCool = false;
                    predicted = true;
                }

            }
            if ( everythingCool ){
                printer.UploadStatusCmd(a, b, "S1", Constants.tmpDir + Shared.getConfig("tmpFileName"));
                if ( b.getValue() != 0 ){
                    lastReceipt = Integer.parseInt(ConnectionDrivers.getLastReceipt())+1+"";
                    everythingCool = false;
                    predicted = true;
                }
                if ( everythingCool ){
                    File file = new File(Constants.tmpDir + Shared.getConfig("tmpFileName"));

                    Scanner sc = new Scanner(file);

                    boolean ansT = sc.hasNext();
                    assert(ansT);
                    String s = sc.next().substring(21, 29);

                    lastReceipt = s;
                    sc.close();
                    file.delete();
                }
            }
            printer.CloseFpctrl();
            isOk = true;

            if ( predicted ){
                MessageBox msb = new MessageBox(MessageBox.SGN_WARNING, "La factura fue guardada satisfactoriamente!!");
                msb.show(null);
            }
        } else{
            throw new Exception("Driver de impresora desconocido!");
        }
    }

    public void extractMoney(User u, String boss, Double quant) throws Exception{
        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Extraer dinero");
        if ( !Constants.withFiscalPrinter ){
            return;
        }

        if ( Shared.getFileConfig("printerDriver").equals("PrnFiscalDLL32") ){
            isOk = false;
            //printer.ClosePort();
            //int ans = printer.OpenPort(Byte.parseByte(Shared.getFileConfig("printerPort")), (byte)2);
            //printer.CancelTransaction();

            /*if ( ans != 0 ){
                throw new Exception(Shared.ncrErrMapping.get(ans));
            }*/
            
            Calendar calendar = GregorianCalendar.getInstance();
            Date dd = Shared.sdf4ncr.parse(ConnectionDrivers.getDate4NCR());
            calendar.setTime(dd);

            int hour = calendar.get(Calendar.HOUR) == 0?12:calendar.get(Calendar.HOUR);

            int ans = printer.NewDoc(Integer.parseInt(Shared.getConfig("nonfiscalDoc")), "SAUL", "123",
                    Shared.getUser().getLogin(), "", new NativeLong(0), (calendar.get(Calendar.DAY_OF_MONTH)),
                    //(calendar.get(Calendar.MONTH)+1), calendar.get(Calendar.YEAR)%100+Constants.ncrYearOffset, (calendar.get(Calendar.HOUR)+12)%13,
                    (calendar.get(Calendar.MONTH)+1), calendar.get(Calendar.YEAR)%100, hour,
                    calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND),
                    ((calendar.get(Calendar.AM_PM) == Calendar.AM)?0:1) ,(calendar.get(Calendar.DAY_OF_MONTH)),
                    (calendar.get(Calendar.MONTH)+1), calendar.get(Calendar.YEAR)%100, hour,
                    calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND),
                    ((calendar.get(Calendar.AM_PM) == Calendar.AM)?0:1));

            if ( ans != 0 ){
                throw new Exception(Shared.ncrErrMapping.get(ans));
            }

            ans = printer.PrintTextNoFiscal(Integer.parseInt(Shared.getConfig("normalFont")),"",1,(byte)0);
            ans = printer.PrintTextNoFiscal(Integer.parseInt(Shared.getConfig("normalFont")),"         Extraer dinero de la Caja " + Shared.getFileConfig("myId"),2,(byte)0);
            ans = printer.PrintTextNoFiscal(Integer.parseInt(Shared.getConfig("bigFont")),"          " + Shared.df.format(quant) + " Bs",1,(byte)0);
            ans = printer.PrintTextNoFiscal(Integer.parseInt(Shared.getConfig("normalFont")),"",3,(byte)0);
            ans = printer.PrintTextNoFiscal(Integer.parseInt(Shared.getConfig("normalFont")),"               Firma Cajera",4,(byte)0);
            ans = printer.PrintTextNoFiscal(Integer.parseInt(Shared.getConfig("normalFont")),"",5,(byte)0);
            ans = printer.PrintTextNoFiscal(Integer.parseInt(Shared.getConfig("normalFont")),"",6,(byte)0);
            ans = printer.PrintTextNoFiscal(Integer.parseInt(Shared.getConfig("normalFont")),"            _____________________",7,(byte)0);
            ans = printer.PrintTextNoFiscal(Integer.parseInt(Shared.getConfig("normalFont")),"",8,(byte)0);
            ans = printer.PrintTextNoFiscal(Integer.parseInt(Shared.getConfig("normalFont")),"               Firma Encargado",9,(byte)0);
            ans = printer.PrintTextNoFiscal(Integer.parseInt(Shared.getConfig("normalFont")),"",10,(byte)0);
            ans = printer.PrintTextNoFiscal(Integer.parseInt(Shared.getConfig("normalFont")),"",11,(byte)0);
            ans = printer.PrintTextNoFiscal(Integer.parseInt(Shared.getConfig("normalFont")),"            _____________________",12,(byte)0);
            ans = printer.PrintTextNoFiscal(Integer.parseInt(Shared.getConfig("normalFont")),"",13,(byte)0);
            ans = printer.PrintTextNoFiscal(Integer.parseInt(Shared.getConfig("normalFont")),"",14,(byte)0);

            if ( ans != 0 ){
                throw new Exception(Shared.ncrErrMapping.get(ans));
            }

            ans = printer.CloseDoc(.0, .0, .0, .0, .0, .0, .0, .0, (byte)0, (byte)0, "");

            if ( ans != 0 ){
                throw new Exception(Shared.ncrErrMapping.get(ans));
            }

            ans = printer.OpenBox();

            //printer.ClosePort();
            isOk = true;
        }else if ( Shared.getFileConfig("printerDriver").equals("tfhkaif") ){

            isOk = false;
            IntByReference a = new IntByReference();
            IntByReference b = new IntByReference();
            printer.OpenFpctrl(Shared.getFileConfig("printerPort"));

            // TODO Bug in the printer?? Sometimes It prints 10K instead of Shared.formatDoubleToPrint(quant);
            /*printer.SendCmd(a, b, "9001" + Shared.formatDoubleToPrint(quant) );
            if ( b.getValue() != 0 ){
                throw new Exception(Shared.getErrMapping().get(b.getValue()));
            }*/

            List<String> buffer = new ArrayList<String>();
            buffer.add("800 Retiro de Efectivo  " +  Shared.df.format(quant));
            buffer.add("800 Caja " + Shared.getFileConfig("myId"));
            buffer.add("800 ");
            buffer.add("800                 _______________________");
            buffer.add("800                   Firma del encargado");
            buffer.add("800 ");
            buffer.add("800                 _______________________");
            buffer.add("810                     Firma del cajero");

             for (String bu : buffer) {
                printer.SendCmd(a, b, bu);
                if ( b.getValue() != 0 ){
                    throw new Exception(Shared.getErrMapping().get(b.getValue()));
                }
            }
            printer.OpenBox();

            printer.CloseFpctrl();
            isOk = true;

        }else{
            throw new Exception("Driver de impresora desconocido!");
        }
    }

    public void reportExtraction() throws Exception{
        if ( !Constants.withFiscalPrinter ){
            return;
        }

        if ( Shared.getFileConfig("printerDriver").equals("PrnFiscalDLL32") ){
            // TODO CODE HERE =D!
        }else if ( Shared.getFileConfig("printerDriver").equals("tfhkaif") ){
            isOk = false;
            IntByReference a = new IntByReference();
            IntByReference b = new IntByReference();
            printer.OpenFpctrl(Shared.getFileConfig("printerPort"));

            printer.SendCmd(a, b, "t");
            if ( b.getValue() != 0 ){
                throw new Exception(Shared.getErrMapping().get(b.getValue()));
            }
            printer.OpenBox();

            printer.CloseFpctrl();
            isOk = true;
        }else{
            throw new Exception("Driver de impresora desconocido!");
        }
    }

    public String getZ() throws Exception{
        if ( !Constants.withFiscalPrinter ){
            return "";
        }

        if ( z == null  ){
            if ( Shared.getFileConfig("printerDriver").equals("PrnFiscalDLL32") ){
                //printer.ClosePort();
                //int ans = printer.OpenPort(Byte.parseByte(Shared.getFileConfig("printerPort")), (byte)2);
                //printer.CancelTransaction();

                /*if ( ans != 0 ){
                    throw new Exception(Shared.ncrErrMapping.get(ans));
                }*/

                TQueryPrnStatus tqps = new TQueryPrnStatus();
                int ans = printer.QueryPrnStatus(tqps);

                if ( ans != 0 ){
                    throw new Exception(Shared.ncrErrMapping.get(ans));
                }

                z = (tqps.UltZ+1) + "";
                
                //printer.ClosePort();

            }else if ( Shared.getFileConfig("printerDriver").equals("tfhkaif") ){

                isOk = false;
                IntByReference a = new IntByReference();
                IntByReference b = new IntByReference();
                printer.OpenFpctrl(Shared.getFileConfig("printerPort"));

                printer.UploadReportCmd(a, b, "U0X", Constants.tmpDir + Shared.getConfig("tmpFileName"));
                if ( b.getValue() != 0 ){
                    throw new Exception(Shared.getErrMapping().get(b.getValue()));
                }

                File file = new File(Constants.tmpDir + Shared.getConfig("tmpFileName"));

                Scanner sc = new Scanner(file);

                sc.hasNext();
                String s = sc.next().substring(0, 4);


                sc.close();
                file.delete();
                printer.CloseFpctrl();
                isOk = true;
                z = s;
            }else{
                throw new Exception("Driver de impresora desconocido!");
            }
        }
        return z;
    }

    public String getSerial() throws Exception{
        if ( !Constants.withFiscalPrinter ){
            return "";
        }

        if ( printerSerial == null ){
            System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Calculando el serial...");
            calculateSerial();
        }
        return printerSerial;
    }

    public String getLastFiscalNumber(){
        if ( !Constants.withFiscalPrinter ){
            return "";
        }

        return lastReceipt;
    }

    // TODO Check difference between predicted and everythingCool
    public void printCreditNote(List<Item2Receipt> items, String ticketId, String myId, User u , Client client, String alternativeId, String fiscalTicketId, String receiptPrinter , Timestamp printingHour) throws Exception{
        if ( !Constants.withFiscalPrinter ){
            return;
        }

        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Imprimir nota de credito");

        if ( Shared.getFileConfig("printerDriver").equals("PrnFiscalDLL32") ){
            isOk = false;

            System.out.println(printingHour);
            Date printingHourD = new Date(printingHour.getTime());
            /*printer.ClosePort();
            int ans = printer.OpenPort(Byte.parseByte(Shared.getFileConfig("printerPort")), (byte)2);
            printer.CancelTransaction();

            if ( ans != 0 ){
                throw new Exception(Shared.ncrErrMapping.get(ans));
            }*/
            int ans = printer.OpenBox();

            Calendar calendar = GregorianCalendar.getInstance();
            Date dd = Shared.sdf4ncr.parse(ConnectionDrivers.getDate4NCR());
            calendar.setTime(dd);

            Calendar calendarCN = new GregorianCalendar();
            calendarCN.setTime(printingHourD);

            int hour = calendar.get(Calendar.HOUR) == 0?12:calendar.get(Calendar.HOUR);
            int hourCN = calendarCN.get(Calendar.HOUR) == 0?12:calendarCN.get(Calendar.HOUR);

            if ( client == null || client.getId() == null || client.getId().isEmpty() || client.getId().equals("Contado") ){
                client = new Client("", "", "", "");
            }

            System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Hora = " + hour + " _ " + hour);
            ans = printer.NewDoc(Integer.parseInt(Shared.getConfig("creditNote")), client.getName(), client.getId(),
                    Shared.getUser().getLogin() + " " + ticketId, receiptPrinter, new NativeLong(Long.parseLong(fiscalTicketId)), (calendar.get(Calendar.DAY_OF_MONTH)),
                    //(calendar.get(Calendar.MONTH)+1), calendar.get(Calendar.YEAR)%100+Constants.ncrYearOffset, (calendar.get(Calendar.HOUR)+12)%13,
                    (calendar.get(Calendar.MONTH)+1), calendar.get(Calendar.YEAR)%100, hour,
                    calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND),
                    ((calendar.get(Calendar.AM_PM) == Calendar.AM)?0:1) ,(calendarCN.get(Calendar.DAY_OF_MONTH)),
                    (calendarCN.get(Calendar.MONTH)+1), calendar.get(Calendar.YEAR)%100, hourCN,
                    calendarCN.get(Calendar.MINUTE), calendarCN.get(Calendar.SECOND),
                    ((calendarCN.get(Calendar.AM_PM) == Calendar.AM)?0:1));

            if ( ans != 0 ){
                throw new Exception(Shared.ncrErrMapping.get(ans));
            }


            TQueryPrnTransaction tqpt = new TQueryPrnTransaction();

            ans = printer.QueryPrnTransaction((byte)1, tqpt);

            if ( ans != 0 || tqpt.VoucherDev <= 0 || tqpt.VoucherDev > Integer.parseInt(Shared.getConfig("maximumFiscalNumber")) ){
                throw new Exception(Shared.ncrErrMapping.get(ans));
            }
            String nextReceipt = tqpt.VoucherDev + "";

            
            Double subtotal = .0;
            for (Item2Receipt item2r : items) {
                String d = item2r.getItem().getDescription();
                subtotal += item2r.getSellPrice()*(1.0 - item2r.getSellDiscount()/100.0)*item2r.getQuant();
                ans = printer.NewItem((byte)0, (byte)0, item2r.getQuant()+.0, Shared.round(item2r.getSellPrice(),2) , (item2r.getItem().getModel()+"-"+d).substring(0, Math.min(Integer.parseInt(Shared.getConfig("maxNcrDescription")), d.length())));
                if ( ans != 0 ){
                    throw new Exception(Shared.ncrErrMapping.get(ans));
                }
                if ( Math.abs(item2r.getSellDiscount()) > Double.parseDouble(Shared.getConfig("exilon")) ){
                    ans = printer.OprDoc((byte)0, (byte)0, Shared.round((item2r.getQuant()+.0)*item2r.getSellPrice()*(item2r.getSellDiscount()/100.0),2), ((item2r.getSellDiscount())+"%").replace(',', '.'));
                }

                if ( ans != 0 ){
                    throw new Exception(Shared.ncrErrMapping.get(ans));
                }
            }

            TreeMap<String,Double> buff = new TreeMap<String , Double>();
            buff.put(Shared.getConfig("cashPaymentName"), .0);
            buff.put(Shared.getConfig("CNPaymentName"), .0);
            buff.put(Shared.getConfig("creditPaymentName"), .0);
            buff.put(Shared.getConfig("debitPaymentName"), .0);
            buff.put(Shared.getConfig("americanExpressPaymentName"), .0);

            int ansOfPrinting = printer.CloseDoc(.0, new Price(null, subtotal).plusIva().getQuant(), .0, .0,
                    .0, .0, .0, .0, (byte)12 , (byte)70, ticketId);

            ans = printer.QueryPrnTransaction((byte)2, tqpt);

            int tries = 0;
            boolean isOkT = false;
            do{
                printer.CancelTransaction();
                ans = printer.QueryPrnTransaction((byte)2, tqpt);

                if ( ans != 0 || tqpt.VoucherDev <= 0 || tqpt.VoucherDev > Integer.parseInt(Shared.getConfig("maximumFiscalNumber")) ){
                    restartCommunicationNCR();
                }else{
                    lastReceipt = tqpt.VoucherDev + "";
                    isOkT = true;
                }
                ++tries;
                System.out.println("Intento " + tries);
            }
            while( tries < Integer.parseInt(Shared.getConfig("triesWithPrinter")) && !isOkT );


            if ( !isOkT ){
                lastReceipt = nextReceipt;
                ConnectionDrivers.flipEnabledPointOfSale(new PointOfSale(Shared.getFileConfig("myId"), "", "", true));
                MessageBox msb = new MessageBox(MessageBox.SGN_WARNING, "La impresora fiscal no esta respondiendo correctamente. Se ha guardado la factura y se ha bloqueado esta caja.");
                msb.show(null);
            }else if ( nextReceipt.equals(lastReceipt) ){
                //Everything ok =D
            }else{
                throw new Exception(Shared.ncrErrMapping.get(ansOfPrinting));
            }

            //ans = printer.ClosePort();

            try{
                updateValues("curdate()");
            }catch(Exception exx){
                // =(
            }
            isOk = true;
        }else if ( Shared.getFileConfig("printerDriver").equals("tfhkaif") ){

            boolean everythingCool = true;
            boolean predicted = false;

            isOk = false;
            IntByReference a = new IntByReference();
            IntByReference b = new IntByReference();
            printer.OpenFpctrl(Shared.getFileConfig("printerPort"));

            List<String> buffer = new ArrayList<String>();

            if ( !items.isEmpty() ){
                int line = 1;

                if ( client != null && !client.getId().isEmpty() && !client.getId().equals("Contado") ){
                    buffer.add("i0" + ( line++ ) + "RIF: " + client.getId());
                    if ( !client.getName().trim().isEmpty() ) {
                        buffer.add("i0" + (line++) + "Nombre: " + client.getName());
                    }
                    if ( !client.getPhone().trim().isEmpty() ) {
                        buffer.add("i0" + (line++) + "Telefono: " + client.getPhone());
                    }
                    if ( !client.getAddress().trim().isEmpty() ) {
                        buffer.add("i0" + (line++) + "Direccion: " + client.getAddress());
                    }
                }
                buffer.add("i0" + ( line++ ) + "Correlativo: " + myId);
                buffer.add("i0" + ( line++ ) + "Factura: " + ticketId);
                if ( !alternativeId.isEmpty() ){
                    buffer.add("i0" + ( line++ ) + "Factura ID Temporal: " + alternativeId);
                }
                buffer.add("i0" + ( line++ ) + "Caja: " + Shared.getFileConfig("myId"));

                for (String bu : buffer) {
                    printer.SendCmd(a, b, bu);
                    if ( b.getValue() != 0 ){
                        throw new Exception(Shared.getErrMapping().get(b.getValue()));
                    }
                }

                for (Item2Receipt item2r : items) {
                    Item item = item2r.getItem();
                    // TODO OJO!!!!!!!!!!!!!!!!!!!
                    // "d0 -> exento.
                    // "d1 -> Tasa de iva 1
                    printer.SendCmd(a, b, "d" + Shared.getConfig("tfhkaifKindTaxCN") + ( Shared.formatDoubleToPrint( item2r.getSellPrice() ) ) +
                            Shared.formatQuantToPrint(item2r.getQuant()+.0) + item.getDescription().substring(0, Math.min(item.getDescription().length(), 37)));
                    if ( b.getValue() != 0 ){
                        throw new Exception(Shared.getErrMapping().get(b.getValue()));
                    }
                    if ( item2r.getSellDiscount() != .0 ){
                        Double finalDiscount = ConnectionDrivers.getDiscount( item.getCode(),ticketId );
                        if ( finalDiscount > .0 ){
                            printer.SendCmd(a, b, "p-"+Shared.formatDoubleToPrintDiscount(finalDiscount/100.0));
                            if ( b.getValue() != 0 ){
                                throw new Exception(Shared.getErrMapping().get(b.getValue()));
                            }
                        }
                    }
                }
                try{
                    printer.SendCmd(a, b, "3");
                    if ( b.getValue() != 0 ){
                        // Fiscal Number has been generated.
                        //System.out.println("RECUPERANDO NUMERO FISCAL!!");
                        lastReceipt = Integer.parseInt(ConnectionDrivers.getLastCN())+1+"";
                        everythingCool = false;
                        predicted = true;
                    }

                    if ( everythingCool ){
                        printer.SendCmd(a, b, "y" + ticketId);// + Shared.formatDoubleToPrintDiscount(globalDiscount));
                        if ( b.getValue() != 0 ){
                            lastReceipt = Integer.parseInt(ConnectionDrivers.getLastCN())+1+"";
                            everythingCool = false;
                            predicted = true;
                        }
                        printer.SendCmd(a, b, "f11000000000000");
                    }
                }catch(Exception ex){
                    lastReceipt = Integer.parseInt(ConnectionDrivers.getLastCN())+1+"";
                    everythingCool = false;
                    predicted = true;
                }
            }

            try{
                if ( everythingCool ){
                    printer.UploadReportCmd(a, b, "U0X", Constants.tmpDir + Shared.getConfig("tmpFileName"));
                    if ( b.getValue() != 0 ){
                        lastReceipt = Integer.parseInt(ConnectionDrivers.getLastCN())+1+"";
                        everythingCool = false;
                        predicted = true;
                    }

                    if ( everythingCool ){
                        File file = new File(Constants.tmpDir + Shared.getConfig("tmpFileName"));

                        Scanner sc = new Scanner(file);

                        boolean ansT = sc.hasNext();
                        assert(ansT);
                        String s = sc.next().substring(168);

                        lastReceipt = s;
                        sc.close();
                        file.delete();
                    }
                }
            }catch(Exception ex){
                lastReceipt = Integer.parseInt(ConnectionDrivers.getLastCN())+1+"";
                everythingCool = false;
                predicted = true;
            }
            printer.CloseFpctrl();
            isOk = true;

            if ( predicted ){
                MessageBox msb = new MessageBox(MessageBox.SGN_WARNING, "La nota de crédito fue guardada satisfactoriamente!!");
                msb.show(null);
            }
        }
    }

    void report(String r) throws Exception{
        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Reporte " + r);
        if ( !Constants.withFiscalPrinter ){
            return;
        }

        //System.out.println("Imprimir reporte " + r);
        if ( Shared.getFileConfig("printerDriver").equals("PrnFiscalDLL32") ){

            isOk = false;
            /*printer.ClosePort();
            int ans = printer.OpenPort(Byte.parseByte(Shared.getFileConfig("printerPort")), (byte)2);
            printer.CancelTransaction();*/

            int ans = 0;
            Calendar calendar = GregorianCalendar.getInstance();
            Date dd = Shared.sdf4ncr.parse(ConnectionDrivers.getDate4NCR());
            calendar.setTime(dd);
            int hour = calendar.get(Calendar.HOUR) == 0?12:calendar.get(Calendar.HOUR);

            System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  calendar.get(Calendar.DAY_OF_MONTH) + " " + (calendar.get(Calendar.MONTH)+1) + " " + calendar.get(Calendar.YEAR)%100 + " " + hour + " " + calendar.get(Calendar.MINUTE) + " " + calendar.get(Calendar.SECOND) + " " + ((calendar.get(Calendar.AM_PM) == Calendar.AM)?0:1));
            System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  (byte)calendar.get(Calendar.DAY_OF_MONTH) + " " + (byte)(calendar.get(Calendar.MONTH)+1) + " " + (byte)calendar.get(Calendar.YEAR)%100 + " " + (byte)hour + " " + (byte)calendar.get(Calendar.MINUTE) + " " + (byte)calendar.get(Calendar.SECOND) + " " + (byte)((calendar.get(Calendar.AM_PM) == Calendar.AM)?0:1));

            if ( r.equals("X") ){
                //ans = printer.RptX((byte)(calendar.get(Calendar.DAY_OF_MONTH)), (byte)(calendar.get(Calendar.MONTH)+1), (byte)(calendar.get(Calendar.YEAR)%100+Constants.ncrYearOffset), (byte)((calendar.get(Calendar.HOUR)+12)%13), (byte)calendar.get(Calendar.MINUTE), (byte)calendar.get(Calendar.SECOND), (byte)((calendar.get(Calendar.AM_PM) == Calendar.AM)?0:1), "Caja" + Shared.getFileConfig("myId"));
                ans = printer.RptX((byte)(calendar.get(Calendar.DAY_OF_MONTH)), (byte)(calendar.get(Calendar.MONTH)+1), (byte)(calendar.get(Calendar.YEAR)%100), (byte)(hour), (byte)calendar.get(Calendar.MINUTE), (byte)calendar.get(Calendar.SECOND), (byte)((calendar.get(Calendar.AM_PM) == Calendar.AM)?0:1), "Caja" + Shared.getFileConfig("myId"));
            }else{
                //ans = printer.GenRptZ((byte)calendar.get(Calendar.DAY_OF_MONTH), (byte)(calendar.get(Calendar.MONTH)+1), (byte)(calendar.get(Calendar.YEAR)%100+Constants.ncrYearOffset), (byte)((calendar.get(Calendar.HOUR)+12)%13), (byte)calendar.get(Calendar.MINUTE), (byte)calendar.get(Calendar.SECOND), (byte)((calendar.get(Calendar.AM_PM) == Calendar.AM)?0:1));
                ans = printer.GenRptZ((byte)calendar.get(Calendar.DAY_OF_MONTH), (byte)(calendar.get(Calendar.MONTH)+1), (byte)(calendar.get(Calendar.YEAR)%100), (byte)(hour), (byte)calendar.get(Calendar.MINUTE), (byte)calendar.get(Calendar.SECOND), (byte)((calendar.get(Calendar.AM_PM) == Calendar.AM)?0:1));
            }

            //printer.ClosePort();

            if ( ans != 0 ){
                System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " ans = " + ans);
                throw new Exception(Shared.ncrErrMapping.get(ans));
            }
            isOk = true;

            return;
        }else if ( Shared.getFileConfig("printerDriver").equals("tfhkaif") ){
            isOk = false;
            IntByReference a = new IntByReference();
            IntByReference b = new IntByReference();
            printer.OpenFpctrl(Shared.getFileConfig("printerPort"));
            printer.SendCmd(a, b, "I0"+r);
            printer.CloseFpctrl();
            isOk = true;
        }else{
            throw new Exception("Driver de impresora desconocido!");
        }
        
    }

    public void forceClose(){
        printer.CloseFpctrl();
    }

    void updateValues(String day) throws Exception {
        if ( !Constants.withFiscalPrinter ){
            return;
        }
        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Actualizar valores");

        if ( Shared.getFileConfig("printerDriver").equals("PrnFiscalDLL32") ){
            isOk = false;
            //printer.ClosePort();
            int ans = 0;//= printer.OpenPort(Byte.parseByte(Shared.getFileConfig("printerPort")), (byte)2);
            //printer.CancelTransaction();

            if ( ans != 0 ){
                System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  Shared.ncrErrMapping.get(ans));
            }

            TQueryPrnStatus tqps = new TQueryPrnStatus();
            ans = printer.QueryPrnStatus(tqps);

            if ( ans != 0 ){
                throw new Exception(Shared.ncrErrMapping.get(ans));
            }

            z = (tqps.UltZ+1) + "";
            System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " El Reporte Z es " + z);

            TQueryPrnTransaction tqpr = new TQueryPrnTransaction();
            ans = printer.QueryPrnTransaction((byte)2, tqpr);

            if ( ans != 0 ){
                throw new Exception(Shared.ncrErrMapping.get(ans));
            }

            System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Pago 1 = " + Double.parseDouble(Shared.b2s(tqpr.FPago1))/100.0);

            ConnectionDrivers.updateFiscalNumbers(Double.parseDouble(Shared.b2s(tqpr.FPago1))/100.0,
                    Double.parseDouble(Shared.b2s(tqpr.FPago2))/100.0,
                    Double.parseDouble(Shared.b2s(tqpr.FPago4))/100.0,
                    Double.parseDouble(Shared.b2s(tqpr.FPago3))/100.0);

            System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Venta A = " + Double.parseDouble(Shared.b2s(tqpr.VtaA))/100.0);
            System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Dev A = " + Double.parseDouble(Shared.b2s(tqpr.DevA))/100.0);

            Double total = Double.parseDouble(Shared.b2s(tqpr.VtaA))/100.0 - Double.parseDouble(Shared.b2s(tqpr.DevA))/100.0;

            TQueryPrnMemory tqpm = new TQueryPrnMemory();
            int lastZ = Integer.parseInt(z)-1;
            if ( lastZ != 0 ){
                ans = printer.QueryPrnMemory(lastZ, tqpm);
                if ( ans != 0 ){
                    throw new Exception(Shared.ncrErrMapping.get(ans));
                }

                System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Anterior ultima venta = " + Shared.b2s(tqpm.CounterLastVta) + " " + Shared.b2s(tqpm.CounterLastDev) );

                System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Todos los valores = ");
                System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " tqpm.CounterDev ="+ Shared.b2s(tqpm.CounterDev));
                System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " tqpm.CounterLastDev ="+ Shared.b2s(tqpm.CounterLastDev));
                System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " tqpm.CounterLastMemRptZ ="+ Shared.b2s(tqpm.CounterLastMemRptZ));
                System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " tqpm.CounterLastVta ="+ Shared.b2s(tqpm.CounterLastVta));
                System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " tqpm.DateTimeLastDev ="+ Shared.b2s(tqpm.DateTimeLastDev));
            }
            
            /*System.out.println(" tqpm.CounterDev ="+ Shared.b2s(tqpm.));
            System.out.println(" tqpm.CounterDev ="+ Shared.b2s(tqpm.CounterDev));*/

            int myCounterLastVta = 0;
            int myCounterLastDev = 0;

            try{
                if ( lastZ != 0 ){
                    myCounterLastVta = Integer.parseInt(Shared.b2s(tqpm.CounterLastVta));
                    myCounterLastDev = Integer.parseInt(Shared.b2s(tqpm.CounterLastDev));
                }
                
            }catch(Exception ex){
                
            }


            ConnectionDrivers.updateTotalFromPrinter(total, z ,tqpr.VoucherVta+"",tqpr.VoucherVta-myCounterLastVta,tqpr.VoucherDev+"",tqpr.VoucherDev-myCounterLastDev, day);

            //System.out.println("Total = " + Double.parseDouble(Shared.b2s(tqpr.VtaA))/100.0 + " - " + Double.parseDouble(Shared.b2s(tqpr.DevA))/100.0);
            //printer.ClosePort();
            System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Termino de actualizar valores. Ultima venta = " + tqpr.VoucherVta + " Ultima Devolucion = " + tqpr.VoucherDev);
            isOk = true;
        }else if ( Shared.getFileConfig("printerDriver").equals("tfhkaif") ){
            printer.OpenFpctrl(Shared.getFileConfig("printerPort"));
            IntByReference a = new IntByReference();
            IntByReference b = new IntByReference();
            printer.UploadStatusCmd(a, b, "S4", Constants.tmpDir + Shared.getConfig("tmpFileName"));
            if ( b.getValue() != 0 ){
                throw new Exception(Shared.getErrMapping().get(b.getValue()));
            }

            File file = new File(Constants.tmpDir + Shared.getConfig("tmpFileName"));

            Scanner sc = new Scanner(file);

            boolean ansT = sc.hasNext();
            assert(ansT);

            String line = sc.next();
            //System.out.println("Linea = " + line.substring(2, 2+10));
            Double cash = Double.parseDouble(line.substring(2+10*0, 2+10*1))/100.0;
            Double cn = Double.parseDouble(line.substring(2+10*1, 2+10*2))/100.0;
            Double credit = Double.parseDouble(line.substring(2+10*8, 2+10*9))/100.0;
            Double debit = Double.parseDouble(line.substring(2+10*9, 2+10*10))/100.0;
            ConnectionDrivers.updateFiscalNumbers(cash, cn, debit, credit);

            sc.close();
            file.delete();
            printer.UploadStatusCmd(a, b, "S1", Constants.tmpDir + Shared.getConfig("tmpFileName"));
            if ( b.getValue() != 0 ){
                throw new Exception(Shared.getErrMapping().get(b.getValue()));
            }
            file = new File(Constants.tmpDir + Shared.getConfig("tmpFileName"));
            sc = new Scanner(file);
            line = sc.next();
            //Double total = Double.parseDouble(line.substring(2+10*1,2+10*2))/100.0;
            //String lReceipt = line.substring(2+10*2, 2+10*2+8);
            //TODO IS IT A BUG?????????????
            String lReceipt = line.substring(2+10*2-1, 2+10*2+8-1);
            int quantReceiptsToday = Integer.parseInt(line.substring(2+10*2+8 , 2+10*2+8+4));

            sc.close();
            file.delete();

            String pZ = Shared.df2z.format(Integer.parseInt(z) - 1);
            printer.UploadReportCmd(a, b, "U0X", Constants.tmpDir + Shared.getConfig("tmpFileName"));
            if ( b.getValue() != 0 ){
                throw new Exception(Shared.getErrMapping().get(b.getValue()));
            }

            file = new File(Constants.tmpDir + Shared.getConfig("tmpFileName"));
            sc = new Scanner(file);

            line = sc.next();

            String lastCN = line.substring(168);
            //System.out.println("line = " + line );

            Double total = .0;
            if ( Double.parseDouble(Shared.getConfig("iva") ) <= .0 ){
                total = Double.parseDouble(line.substring(29,29+9))/100.0 - Double.parseDouble(line.substring(99, 99+9))/100.0;
            }else{
                total = Double.parseDouble(line.substring(39,39+9))/100.0 - Double.parseDouble(line.substring(109, 109+9))/100.0;
            }

            sc.close();
            file.delete();

            printer.UploadReportCmd(a, b, "U3A00" + pZ + "00" + z, Constants.tmpDir + Shared.getConfig("tmpFileName"));
            if ( b.getValue() != 0 ){
                throw new Exception(Shared.getErrMapping().get(b.getValue()));
            }

            file = new File(Constants.tmpDir + Shared.getConfig("tmpFileName"));
            sc = new Scanner(file);

            line = sc.next();

            String pLastCN = line.substring(168);
            int nNC = (Integer.parseInt(lastCN)-Integer.parseInt(pLastCN));
            ConnectionDrivers.updateTotalFromPrinter(total, z ,lReceipt,quantReceiptsToday,lastCN,nNC, day);

            printer.CloseFpctrl();
        }else{
            throw new Exception("Driver de impresora desconocido!");
        }
    }

    // Pre: updateValues()
    void printResumeZ(String day, String xoz) throws Exception{
        if ( !Constants.withFiscalPrinter ){
            return;
        }

        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " printResumeZ");
        if ( Shared.getFileConfig("printerDriver").equals("PrnFiscalDLL32") ){
            isOk = false;
            //printer.ClosePort();
            int ans = 0 ; //= printer.OpenPort(Byte.parseByte(Shared.getFileConfig("printerPort")), (byte)2);
            //printer.CancelTransaction();

            if ( ans != 0 ){
                throw new Exception(Shared.ncrErrMapping.get(ans));
            }

            Calendar calendar = GregorianCalendar.getInstance();
            Date dd = Shared.sdf4ncr.parse(ConnectionDrivers.getDate4NCR());
            calendar.setTime(dd);
            int hour = calendar.get(Calendar.HOUR) == 0?12:calendar.get(Calendar.HOUR);

            System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Comenzo el new doc");
            ans = printer.NewDoc(Integer.parseInt(Shared.getConfig("nonfiscalDoc")), "SAUL", "123",
                    Shared.getUser().getLogin(), "", new NativeLong(0), (calendar.get(Calendar.DAY_OF_MONTH)),
                    //(calendar.get(Calendar.MONTH)+1), calendar.get(Calendar.YEAR)%100+Constants.ncrYearOffset, (calendar.get(Calendar.HOUR)+12)%13,
                    (calendar.get(Calendar.MONTH)+1), calendar.get(Calendar.YEAR)%100, hour,
                    calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND),
                    ((calendar.get(Calendar.AM_PM) == Calendar.AM)?0:1) ,(calendar.get(Calendar.DAY_OF_MONTH)),
                    (calendar.get(Calendar.MONTH)+1), calendar.get(Calendar.YEAR)%100, hour,
                    calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND),
                    ((calendar.get(Calendar.AM_PM) == Calendar.AM)?0:1));

            System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Termino new doc");
            if ( ans != 0 ){
                throw new Exception(Shared.ncrErrMapping.get(ans));
            }

            System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Mandando texto no fiscal");
            int nf = Integer.parseInt(Shared.getConfig("normalFont"));
            ans = printer.PrintTextNoFiscal(nf,"",1,(byte)0);
            ans = printer.PrintTextNoFiscal(nf," Resumen del Reporte " + xoz + "  Nro " + z ,2,(byte)0);
            ans = printer.PrintTextNoFiscal(nf," Impresora Fiscal Serial Nro " + printerSerial,1,(byte)0);
            ans = printer.PrintTextNoFiscal(nf," ",3,(byte)0);
            ans = printer.PrintTextNoFiscal(nf," Sucursal: " + Shared.getConfig("storeName"),4,(byte)0);
            ans = printer.PrintTextNoFiscal(nf," Caja Nro: " + Shared.getFileConfig("myId"),5,(byte)0);
            ans = printer.PrintTextNoFiscal(nf," Ult Factura:        " + ConnectionDrivers.getLastReceipt(),6,(byte)0);
            ans = printer.PrintTextNoFiscal(nf," Ult Nota de Credito " + ConnectionDrivers.getLastCN(),7,(byte)0);
            ans = printer.PrintTextNoFiscal(nf," Nro de Facturas:      " + ConnectionDrivers.getQuant(Shared.getFileConfig("myId"),"num_facturas", day),8,(byte)0);
            ans = printer.PrintTextNoFiscal(nf," Nro de N/C:         " + ConnectionDrivers.getQuant( Shared.getFileConfig("myId"),"numero_notas_credito", day),9,(byte)0);
            ans = printer.PrintTextNoFiscal(nf," Neto Ventas:         " + Shared.df.format(ConnectionDrivers.getTotalDeclaredPos(Shared.getFileConfig("myId"), day)),11,(byte)0);
            ans = printer.PrintTextNoFiscal(nf," ",12,(byte)0);
            ans = printer.PrintTextNoFiscal(nf," Fin de Resumen del Reporte Z Nro " + z,13,(byte)0);
            ans = printer.PrintTextNoFiscal(nf,"",14,(byte)0);
            System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Termino de mandar texto");

            if ( ans != 0 ){
                throw new Exception(Shared.ncrErrMapping.get(ans));
            }

            ans = printer.CloseDoc(.0, .0, .0, .0, .0, .0, .0, .0, (byte)0, (byte)0, "");

            if ( ans != 0 ){
                throw new Exception(Shared.ncrErrMapping.get(ans));
            }

            System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Termino todo!");
            //printer.ClosePort();
            isOk = true;
        }else if ( Shared.getFileConfig("printerDriver").equals("tfhkaif") ){

            isOk = false;
            IntByReference a = new IntByReference();
            IntByReference b = new IntByReference();
            printer.OpenFpctrl(Shared.getFileConfig("printerPort"));

            List<String> buffer = new ArrayList<String>();
            buffer.add("800 ");
            buffer.add("800 Resumen del Reporte Z Nro " + z);
            buffer.add("800 Impresora Fiscal Serial Nro " + printerSerial );
            buffer.add("800 ");
            buffer.add("800 Sucursal: " + Shared.getConfig("storeName"));
            buffer.add("800 Caja Nro: " + Shared.getFileConfig("myId"));
            buffer.add("800 Ult Factura:        " + ConnectionDrivers.getLastReceipt());
            buffer.add("800 Ult Nota de Credito " + ConnectionDrivers.getLastCN());
            buffer.add("800 Nro de Facturas:      " + ConnectionDrivers.getQuant(Shared.getFileConfig("myId"),"num_facturas", day));
            buffer.add("800 Nro de N/C:         " + ConnectionDrivers.getQuant( Shared.getFileConfig("myId"),"numero_notas_credito", day));
            buffer.add("800 ");
            buffer.add("800 Neto Ventas:         " + Shared.df.format(ConnectionDrivers.getTotalDeclaredPos(Shared.getFileConfig("myId"), day)));
            buffer.add("800 ");
            buffer.add("810 Fin de Resumen del Reporte Z Nro " + z);

             for (String bu : buffer) {
                printer.SendCmd(a, b, bu);
                if ( b.getValue() != 0 ){
                    throw new Exception(Shared.getErrMapping().get(b.getValue()));
                }
            }

            printer.CloseFpctrl();
            isOk = true;
        }else{
            throw new Exception("Driver de impresora desconocido!");
        }
    }

    private int compareComputerPrinter(double currentReceipt) throws SQLException, Exception {
        double computer = ConnectionDrivers.getSumTotalWithIva("curdate()","factura","Facturada", true , Shared.getFileConfig("myId"))
                - ConnectionDrivers.getSumTotalWithIva("curdate()","nota_de_credito","Nota",false, Shared.getFileConfig("myId") );

        updateValues("curdate()");

        double printer = ConnectionDrivers.getTotalPrinter("curdate()", Shared.getFileConfig("myId"));

        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Computer = " + (computer + currentReceipt));
        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Printer = " + printer );
        printer = ConnectionDrivers.getTotalPrinter("curdate()", Shared.getFileConfig("myId"));
        if ( Math.abs( computer + currentReceipt - printer ) < Double.parseDouble(Shared.getConfig("printerExilon")) ){
            return 0;
        }else if ( computer < printer ){
            return -1;
        }else{
            return 1;
        }
    }

    void printNonFiscalCopyReceipt(String fiscalNumber, Timestamp printingDate) throws SQLException, ParseException {
        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Imprimir copia no fiscal de " + fiscalNumber);
        if ( Shared.getFileConfig("printerDriver").equals("PrnFiscalDLL32") ){
            isOk = false;

            Calendar calendar = GregorianCalendar.getInstance();
            Date dd = Shared.sdf4ncr.parse(ConnectionDrivers.getDate4NCR());
            calendar.setTime(dd);
            int hour = calendar.get(Calendar.HOUR) == 0?12:calendar.get(Calendar.HOUR);

            
            int ans = printer.PrintDocument(new NativeLong(Long.parseLong(fiscalNumber)), (byte)printingDate.getDate(), (byte)(printingDate.getMonth()+1), (byte)(printingDate.getYear()%100), (byte)0, (byte)calendar.get(Calendar.DAY_OF_MONTH), (byte)(calendar.get(Calendar.MONTH)+1), (byte)(calendar.get(Calendar.YEAR)%100), (byte)hour, (byte)calendar.get(Calendar.MINUTE), (byte)calendar.get(Calendar.SECOND), (byte)((calendar.get(Calendar.AM_PM) == Calendar.AM)?0:1));

            System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " ans = " + ans);
            isOk = true;
        }else if ( Shared.getFileConfig("printerDriver").equals("tfhkaif") ){
            isOk = false;
            IntByReference a = new IntByReference();
            IntByReference b = new IntByReference();
            printer.OpenFpctrl(Shared.getFileConfig("printerPort"));

            String tmp = Shared.df2intnonfiscalcopy.format(Integer.parseInt(fiscalNumber));
            printer.SendCmd(a, b, "RF" + tmp + tmp);
            printer.CloseFpctrl();
            isOk = true;
        }
    }

    void printNonFiscalCopyCreditNote(String fiscalNumber, Timestamp printingDate) throws ParseException, SQLException {
        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Imprimir copia no fiscal de nc " + fiscalNumber);
        if ( Shared.getFileConfig("printerDriver").equals("PrnFiscalDLL32") ){
            isOk = false;

            Calendar calendar = GregorianCalendar.getInstance();
            Date dd = Shared.sdf4ncr.parse(ConnectionDrivers.getDate4NCR());
            calendar.setTime(dd);
            int hour = calendar.get(Calendar.HOUR) == 0?12:calendar.get(Calendar.HOUR);


            int ans = printer.PrintDocument(new NativeLong(Long.parseLong(fiscalNumber)), (byte)printingDate.getDate(), (byte)(printingDate.getMonth()+1), (byte)(printingDate.getYear()%100), (byte)1, (byte)calendar.get(Calendar.DAY_OF_MONTH), (byte)(calendar.get(Calendar.MONTH)+1), (byte)(calendar.get(Calendar.YEAR)%100), (byte)hour, (byte)calendar.get(Calendar.MINUTE), (byte)calendar.get(Calendar.SECOND), (byte)((calendar.get(Calendar.AM_PM) == Calendar.AM)?0:1));

            System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " ans = " + ans);
            isOk = true;
        }else if ( Shared.getFileConfig("printerDriver").equals("tfhkaif") ){
            isOk = false;
            IntByReference a = new IntByReference();
            IntByReference b = new IntByReference();
            printer.OpenFpctrl(Shared.getFileConfig("printerPort"));

            String tmp = Shared.df2intnonfiscalcopy.format(Integer.parseInt(fiscalNumber));
            printer.SendCmd(a, b, "RC" + tmp + tmp);
            printer.CloseFpctrl();
            isOk = true;
        }
    }
    
}
