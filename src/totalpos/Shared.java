package totalpos;

import com.sun.jna.Native;
import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.comm.CommPortIdentifier;
import javax.comm.SerialPort;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import net.n3.nanoxml.XMLElement;
import srvEntidades.BNKA;
import srvEntidades.DD07T;
import srvEntidades.IsrvEntidades;
import srvEntidades.SrvEntidades;

/**
 *
 * @author Saul Hidalgo
 */
public class Shared {

    private static TreeMap<String,String> config = new TreeMap<String, String>();
    private static TreeMap<String,String> fileConfig = new TreeMap<String, String>();
    private static Component myMainWindows = null;
    private static TreeMap<String, Integer> tries = new TreeMap<String, Integer>();
    private static User user;
    private static UpdateClock screenSaver;
    private static TreeMap<Integer, String> errMapping = new TreeMap<Integer, String>();
    protected static TreeMap<Integer, String> ncrErrMapping = new TreeMap<Integer, String>();
    protected static boolean isOffline = false;
    private static TreeMap<String , Item > newItemMapping;
    private static boolean hadMovements;
    protected static List< XMLElement > itemsNeeded;
    private static int processingWindows = 0;
    public static FiscalPrinter printer;
    public static int isFingerOpened = 0;
    public static int numberClosingDayOpened = 0;
    protected static Set<String> holidays = new TreeSet<String>();
    protected static String storeIp = null;
    private static Enumeration portList;
    private static CommPortIdentifier portId;
    private static SerialPort serialPort;
    private static OutputStream outputNCRDisplay;
    private static User32 user32;
    protected static SimpleDateFormat sdfHour = null;
    protected static SimpleDateFormat sdfHour2BK = null;
    protected static SimpleDateFormat sdfDay = null;
    protected static SimpleDateFormat sdfDay2DB = null;
    protected static SimpleDateFormat sdfDateHour = null;
    protected static SimpleDateFormat sdfDay2SAP = null;
    protected static SimpleDateFormat sdf4backup = null;
    protected static SimpleDateFormat sdf4ncr = null;
    protected static DateFormat dateFormatter = null;
    protected static DecimalFormat df = null;
    protected static DecimalFormat df2z = null;
    protected static DecimalFormat df2int = null;
    protected static DecimalFormat df2intSAP = null;
    protected static DecimalFormat df2int2p = null;
    protected static DecimalFormat df2intnonfiscalcopy = null;

    protected static void initialize(){
        errMapping.put(new Integer(0), "No hay error");
        errMapping.put(new Integer(1), "Fin de entrega de papel");
        errMapping.put(new Integer(2), "Error mecánico con el papel");
        errMapping.put(new Integer(3), "Fin en la entrega de papel");
        errMapping.put(new Integer(80), "Comando Inválido");
        errMapping.put(new Integer(84), "Tasa Inválida");
        errMapping.put(new Integer(88), "Directivas Inválidas");
        errMapping.put(new Integer(92), "Comando Inválido");
        errMapping.put(new Integer(96), "Error fiscal");
        errMapping.put(new Integer(100), "Error de la memoria fiscal");
        errMapping.put(new Integer(108), "Memoria fiscal llena");
        errMapping.put(new Integer(112), "Buffer completo");
        errMapping.put(new Integer(128), "Error en la comunicación");
        errMapping.put(new Integer(137), "No hay respuesta");
        errMapping.put(new Integer(144), "Error LRC");
        errMapping.put(new Integer(145), "Error con el API");
        errMapping.put(new Integer(153), "Error al crear el archivo");
        ncrErrMapping.put(new Integer(1), "La fecha enviada en el comando es anterior a la ultima utilizada");
        ncrErrMapping.put(new Integer(2), "No hay transacciones por cancelar");
        ncrErrMapping.put(new Integer(3), "Error en operación aritmética");
        ncrErrMapping.put(new Integer(4), "Error en los parámetros del comando");
        ncrErrMapping.put(new Integer(5), "El parámetro límite de la configuración ha sido alcanzado");
        ncrErrMapping.put(new Integer(6), "Falta papel");
        ncrErrMapping.put(new Integer(7), "El comando no puede ser ejecutado, hay una transaccion pendiente");
        ncrErrMapping.put(new Integer(8), "Memoria fiscal vacía");
        ncrErrMapping.put(new Integer(9), "Memoria fiscal llena");
        ncrErrMapping.put(new Integer(10), "Error de comunicación con la impresora fiscal");
        ncrErrMapping.put(new Integer(11), "Impresora fiscal no autenticada");
        ncrErrMapping.put(new Integer(12), "El cheque no ha sido colocado o la información para imprimirse no ha sido enviada");
        ncrErrMapping.put(new Integer(13), "Comando previo en curso");
        ncrErrMapping.put(new Integer(14), "Hay un cheque en curso");
        ncrErrMapping.put(new Integer(16), "Error leyendo o escribiendo memoria fiscal (CRC error)");
        ncrErrMapping.put(new Integer(17), "Error leyendo o escribiendo memoria de auditoría (CRC error)");
        ncrErrMapping.put(new Integer(18), "Memoria de Auditoría no autenticada");
        ncrErrMapping.put(new Integer(19), "Memoria de Auditoría llena");
        ncrErrMapping.put(new Integer(249), "Memoria fiscal procesando transacciones");
        ncrErrMapping.put(new Integer(250), "Error en secuencia de paquetes");
        ncrErrMapping.put(new Integer(251), "Error en el contador de secuencia de paquetes");
        ncrErrMapping.put(new Integer(252), "Error en la cabecera de paquetes");
        ncrErrMapping.put(new Integer(253), "Error de paquetes CRC");
        ncrErrMapping.put(new Integer(254), "Time out de paquetes");
        ncrErrMapping.put(new Integer(255), "Número de comando inválido");
        ncrErrMapping.put(new Integer(301), "Error en Recepción");
        ncrErrMapping.put(new Integer(302), "Error en transmisión");
        ncrErrMapping.put(new Integer(303), "Comando Inválido");
        ncrErrMapping.put(new Integer(304), "Paquete repetido");
        ncrErrMapping.put(new Integer(305), "Memoria fiscal no responde");
        ncrErrMapping.put(new Integer(306), "Error abriendo el registro histórico de transacciones.");
        ncrErrMapping.put(new Integer(307), "Error escribiendo el registro histórico de transacciones.");
        ncrErrMapping.put(new Integer(308), "Tipo inválido de reporte");
        ncrErrMapping.put(new Integer(309), "TimeOut en comunicación");
        ncrErrMapping.put(new Integer(310), "Formato de fecha inválido");
        ncrErrMapping.put(new Integer(311), "Formato de hora inválido");
        ncrErrMapping.put(new Integer(312), "Error en el cierre del registro histórico de transacciones.");
        ncrErrMapping.put(new Integer(313), "Cantidad no puede ser cero.");
        ncrErrMapping.put(new Integer(314), "El precio no puede ser cero.");
        ncrErrMapping.put(new Integer(315), "La cantidad no puede ser mayor a 12 dígitos.");
        ncrErrMapping.put(new Integer(316), "El precio no puede ser mayor a 12 dígitos");
        ncrErrMapping.put(new Integer(317), "La descripción del artículo no puede ser vacío.");
        ncrErrMapping.put(new Integer(318), "Carga global , tipos de pago y documento de descuento no puede ser mayor a 14 dígitos.");
        ncrErrMapping.put(new Integer(319), "El nombre del cliente no puede tener mas de 106 carácteres.");
        ncrErrMapping.put(new Integer(320), "Tipo de Operación Inválido");
        ncrErrMapping.put(new Integer(321), "Tipo de Iva inválido");
        ncrErrMapping.put(new Integer(322), "Cantidad de puede ser cero");
        ncrErrMapping.put(new Integer(323), "Cantidad no puede ser mayor a 12 dígitos.");
        ncrErrMapping.put(new Integer(324), "Longitud del código de barras no puede ser mayor a 12 dígitos.");
        ncrErrMapping.put(new Integer(325), "La descrición de la operación no puede ser vacía.");
        ncrErrMapping.put(new Integer(326), "La longitud del identificador del cliente no puede ser mayor a 30 carácteres.");
        ncrErrMapping.put(new Integer(327), "La impresora no está en linea");
        ncrErrMapping.put(new Integer(328), "El monto del cheque no puede ser cero.");
        ncrErrMapping.put(new Integer(329), "La longitud maxima de un texto no fiscal es 38 carácteres.");
        ncrErrMapping.put(new Integer(330), "La cantidad del cheque no puede ser mayor a 12 digitos.");
        ncrErrMapping.put(new Integer(331), "La cantidad del cheque en letras del cheque no puede ser mayor a 120 carácteres");
        ncrErrMapping.put(new Integer(332), "El parametro de validación del año is opcional y solo acepta valor 1");
        ncrErrMapping.put(new Integer(333), "El tipo de reporte Z por intervalos de Z debe ser solamente de mes a mes ( opcion 0 ) o dia a dia ( opcion 1 )");
        ncrErrMapping.put(new Integer(334), "Numero de linea inválido");
        ncrErrMapping.put(new Integer(335), "La longitud maxima de cabecera es 38 carácteres.");
        ncrErrMapping.put(new Integer(336), "La longitud maxima del tipo de pago es 15 carácteres.");
        ncrErrMapping.put(new Integer(337), "La longitud maxima de los productos es 190 carácteres.");
        ncrErrMapping.put(new Integer(338), "Longitud invalida de IVA (Maximo 2 enteros y 2 decimales)");
        ncrErrMapping.put(new Integer(339), "El nombre del cliente no puede ser vacío.");
        ncrErrMapping.put(new Integer(340), "El identificador de la memoria fiscal no puede ser vacío.");
        ncrErrMapping.put(new Integer(341), "El número de documento no puede ser cero o vacío");
        ncrErrMapping.put(new Integer(342), "La fecha inicial no puede ser mas grande que la fecha final");
        ncrErrMapping.put(new Integer(343), "La descripción de los cargos o descuentos debe tener una descripción de máximo 190 carácteres.");
        ncrErrMapping.put(new Integer(344), "Tipo inválido en procesar el cheque.");
        ncrErrMapping.put(new Integer(345), "La cantidad iva no puede ser vacía");
        ncrErrMapping.put(new Integer(346), "Hay una transacción en proceso en la memoria fiscal");
        ncrErrMapping.put(new Integer(347), "Pago a orden no puede ser vacío");
        ncrErrMapping.put(new Integer(348), "Número de cuenta no puede ser vacío");
        ncrErrMapping.put(new Integer(349), "Número de cuenta no puede ser alfanumérico");
        ncrErrMapping.put(new Integer(350), "El número de cuenta debe ser entre 10 y 72 carácteres.");
        ncrErrMapping.put(new Integer(351), "El título de la cuenta no puede ser vacío");
        ncrErrMapping.put(new Integer(352), "El banco de la cuenta no puede ser vacío");
        ncrErrMapping.put(new Integer(353), "El registro de transacciones fue activado");
        ncrErrMapping.put(new Integer(354), "El registro histórico de transacciones fue desactivado");
        ncrErrMapping.put(new Integer(355), "Error al crear/eliminar registro de transacciones");
        ncrErrMapping.put(new Integer(356), "Registro histórico de transacciones borrado.");
        ncrErrMapping.put(new Integer(357), "Registro Histórico no existe en el PATH de la DLL");
        ncrErrMapping.put(new Integer(358), "Parámetro para imprimir texto no fiscal sin buffer es 0 , con buffer es 1");
        ncrErrMapping.put(new Integer(359), "Tipo de documento inválido");
        ncrErrMapping.put(new Integer(360), "El rango permitido para consultar reportes Z en la memoria es desde 1 a 1825");
        ncrErrMapping.put(new Integer(361), "El identificador de la memoria fiscal debe tener 10 carácteres.");
        ncrErrMapping.put(new Integer(362), "Tipo de fuente inválido");
        ncrErrMapping.put(new Integer(363), "Longitud del identificador de la memoria no puede ser mayor a 10 carácteres.");
        ncrErrMapping.put(new Integer(364), "Longitud de la orden de pago no puede ser mayor a 70 carácteres");
        ncrErrMapping.put(new Integer(365), "La longitud del Voucher no puede ser mas grande a 6 carácteres.");
        ncrErrMapping.put(new Integer(366), "La longitud del nombre del cajero no puede ser mas grande a 20 carácteres.");
        ncrErrMapping.put(new Integer(367), "La longitud del título de la cuenta no puede ser mayor a 73 carácteres.");
        ncrErrMapping.put(new Integer(368), "La longitud de la cuenta del banco no puede ser mayor a 70 carácteres.");
        ncrErrMapping.put(new Integer(369), "El puerto serial de la impresora no está disponible.");
        ncrErrMapping.put(new Integer(370), "En la configuración SetMsgErr solo se permite 0=deshabilitado, 1=habilitado");
        ncrErrMapping.put(new Integer(371), "Error en el API de la función Win32");
        ncrErrMapping.put(new Integer(372), "Valor de la configuración de Bps inválido. Los posibles son 0=9600 Bps, 1=19200 Bps");
        ncrErrMapping.put(new Integer(373), "Puerto serial inválido");
        ncrErrMapping.put(new Integer(374), "Error en la configuración de los Bps");
        ncrErrMapping.put(new Integer(375), "Falla de la memoria fiscal a nivel interno o falla de energía.");
        ncrErrMapping.put(new Integer(376), "Licencia invalida. Comuníquese con su Proveedor");
        ncrErrMapping.put(new Integer(377), "Número de Documento Invalido");
        ncrErrMapping.put(new Integer(378), "Tipo de Documento Invalido");
        ncrErrMapping.put(new Integer(379), "No se encuentra el Journal o no está Asociado con la Memoria Fiscal");
        ncrErrMapping.put(new Integer(380), "Documento No Encontrado en el Journal en base a los Parámetros de Búsqueda");
        ncrErrMapping.put(new Integer(381), "Tipo de Código de Barra Inválido");
        ncrErrMapping.put(new Integer(382), "Longitud de Código de Barra Inválido");
        ncrErrMapping.put(new Integer(383), "El Archivo LicenciaPrnFiscal.txt no esta en el Directorio del DLL");
        ncrErrMapping.put(new Integer(384), "Error de I/O en Apertura de Archivo LicenciaPrnFiscal.txt");
        ncrErrMapping.put(new Integer(385), "Nombre de Archivo de Salida del Journal no Puede Estar en Blanco");
        ncrErrMapping.put(new Integer(386), "La Sintaxis de la Ruta y/o Nombre de Archivo de Salida No Es Valida");
        ncrErrMapping.put(new Integer(387), "Error de I/O en Archivo de Salida del Journal");

        sdfHour = new SimpleDateFormat(Shared.getConfig("sdfHour"));
        sdfHour2BK = new SimpleDateFormat(Shared.getConfig("sdfHour2BK"));
        sdfDay = new SimpleDateFormat(Shared.getConfig("sdfDay"));
        sdfDay2DB = new SimpleDateFormat(Shared.getConfig("sdfDay2DB"));
        sdfDateHour = new SimpleDateFormat(Shared.getConfig("sdfDateHour"));
        sdfDay2SAP = new SimpleDateFormat(Shared.getConfig("sdfDay2SAP"));
        sdf4backup = new SimpleDateFormat(Shared.getConfig("sdf4backup"));
        sdf4ncr =  new SimpleDateFormat(Shared.getConfig("sdf4ncr"));
        dateFormatter =  new SimpleDateFormat(Shared.getConfig("dateFormatter"));
        df = new DecimalFormat(Shared.getConfig("df"));
        df2z = new DecimalFormat(Shared.getConfig("df2z"));
        df2int = new DecimalFormat(Shared.getConfig("df2int"));
        df2intSAP = new DecimalFormat(Shared.getConfig("df2intSAP"));
        df2int2p = new DecimalFormat(Shared.getConfig("df2int2p"));
        df2intnonfiscalcopy = new DecimalFormat(Shared.getConfig("df2intnonfiscalcopy"));
    }

    protected static void initializaUser32(){
        System.out.println("[" + Shared.now() + "] Shared " + Shared.lineNumber() +  " Cargando libreria User32...");
        user32 = (User32)Native.loadLibrary( "User32" , User32.class ) ;
        System.out.println("[" + Shared.now() + "] Shared " + Shared.lineNumber() +  " Cargada libreria User32!");
    }

    protected static void lockUser32(){ 
        user32.BlockInput(true);
        //System.out.println("hWnd = " + (int)Native.getWindowID((Window)Shared.getMyMainWindows()));
        //user32.EnableWindow((int)Native.getWindowID((Window)Shared.getMyMainWindows()), true);
    }

    protected static void unlockUser32(){
        user32.BlockInput(false);
        //user32.EnableWindow((int)Native.getWindowID((Window)Shared.getMyMainWindows()), true);
    }

    public static int lineNumber() {
        return Thread.currentThread().getStackTrace()[2].getLineNumber();
    }
    
    protected static boolean initializeDisplay(){
        if( !fileConfig.containsKey("displayPort") ){
            System.out.println("No contiene configuracion para display");
            return true;
        }
        portList = CommPortIdentifier.getPortIdentifiers();
        while (portList.hasMoreElements()) {
            portId = (CommPortIdentifier) portList.nextElement();
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL && portId.getName().equals(Shared.getFileConfig("displayPort")))  {
                try{ 
                    serialPort = (SerialPort) portId.open("SimpleWriteApp", 9600);
                    outputNCRDisplay = serialPort.getOutputStream();
                    outputNCRDisplay.write(new byte[]{27,14});
                    outputNCRDisplay.write(new byte[]{27,5});
                    outputNCRDisplay.write(new byte[]{27,2});
                    //outputNCRDisplay.write(new byte[]{27,11});
                    //outputNCRDisplay.write(new byte[]{27,27});
                    msgWithEffect("Total Pos =D", "Version 1.04.30");
                    return true;
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }
        return false;
    }

    public static boolean msgWithEffect(String line1, String line2){

        if ( !fileConfig.containsKey("displayPort") ){
            return true;
        }
        //line1 = String.format("%" + Constants.displaySize*2 + "s", line1);
        //line2 = String.format("%" + Constants.displaySize*2 + "s", line2);
        line1 = String.format("%" + Shared.getConfig("displaySize") + "s", line1);
        line2 = String.format("%" + Shared.getConfig("displaySize") + "s", line2);

        System.out.println("Line1 " + line1);
        System.out.println("Line2 " + line2);
        try{
            outputNCRDisplay.write(new byte[]{27,5});
            outputNCRDisplay.write(new byte[]{27,2});
            /*for ( int i = 0 ; i <= Constants.displaySize ; i+=4 ){
                outputNCRDisplay.write((line1.substring(i, i+Constants.displaySize) + line2.substring(i, i+Constants.displaySize)).getBytes());
            }*/
            outputNCRDisplay.write( (line1 + line2).getBytes() );
            return true;
        }catch(Exception ex){
            return false;
        }
    }

    protected static void centerFrame(javax.swing.JFrame frame){
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

        // Determine the new location of the window
        int w = frame.getSize().width;
        int h = frame.getSize().height;
        int x = (dim.width-w)/2;
        int y = (dim.height-h)/2;
        frame.setLocation(x, y);
    }

    protected static void maximize(JFrame frame){
        frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
    }

    protected static String hashPassword(String x){
        return x.hashCode() + "";
    }

    public static void centerFrame(JDialog dialog) {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

        // Determine the new location of the window
        int w = dialog.getSize().width;
        int h = dialog.getSize().height;
        int x = (dim.width-w)/2;
        int y = (dim.height-h)/2;
        dialog.setLocation(x, y);
    }

    public static String booleanToAllowed(boolean b){
        return b?"Habilitado":"Deshabilitado";
    }

    public static User giveUser(List<User> l, String u){
        for (User user : l)
            if ( user.getLogin().equals(u.toLowerCase()) )
                return user;

        return null;
        
    }

    public static List<Profile> updateProfiles(JComboBox rCombo, boolean withEmpty) throws SQLException, Exception {

        List<Profile> profiles = ConnectionDrivers.listProfile("");

        DefaultComboBoxModel dfcbm = (DefaultComboBoxModel) rCombo.getModel();
        dfcbm.removeAllElements();

        if (withEmpty){
            profiles.add(0, new Profile("", ""));
        }

        for (Profile profile : profiles) {
            rCombo.addItem(profile.getId());
        }

        return profiles;
    }

    public static String nextId(int offset){
        try {
            return Shared.getConfig("storeName") + Shared.getFileConfig("myId")
                    + String.format((Shared.isOffline?"9%05d":"%06d"), ConnectionDrivers.lastReceipt()-offset+1);
        } catch (SQLException ex) {
            MessageBox msb = new MessageBox(MessageBox.SGN_DANGER, "Problemas con la base de datos.",ex);
            msb.show(Shared.getMyMainWindows());
            Shared.reload();
            return "";
        } catch (Exception ex) {
            MessageBox msb = new MessageBox(MessageBox.SGN_DANGER, "Problemas al listar calcular el siguiente código de factura.",ex);
            msb.show(Shared.getMyMainWindows());
            Shared.reload();
            return "";
        }

    }

    public static String nextIdCN(int offset) throws SQLException{
        return Shared.getConfig("storeName") + Shared.getFileConfig("myId")
                + String.format((Shared.isOffline?"9%05d":"%06d"), ConnectionDrivers.lastCreditNote()-offset+1);
    }

    public static void userTrying(String l) throws Exception{
        if ( !tries.containsKey(l) ){
            getTries().put(l, new Integer(1));
        }else if ( getTries().get(l).compareTo(new Integer(Integer.parseInt(Shared.getConfig("triesWithPassword"))-2)) > 0 ){
            try {
                ConnectionDrivers.lockUser(l);
            } catch (SQLException ex1) {
                MessageBox msg = new MessageBox(MessageBox.SGN_DANGER, "Problemas con la base de datos.", ex1);
                msg.show(Shared.getMyMainWindows());
            }
            throw new Exception(Shared.getConfig("userLocked"));
        }else{
            getTries().put(l, new Integer(getTries().get(l) + 1));
        }
    }

    public static void userInsertedPasswordOk(String username){
        getTries().put(username, 0);
    }

    protected static void reload(){
        Login login = new Login();
        Shared.centerFrame(login);
        login.setExtendedState(login.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        if ( getMyMainWindows() instanceof MainWindows ){
            ((MainWindows)getMyMainWindows()).dispose();
        }else if ( getMyMainWindows() instanceof MainRetailWindows ) {
            ((MainRetailWindows)getMyMainWindows()).dispose();
        }
        login.setVisible(true);
        setUser(null);
    }

    protected static void loadFileConfig(String fileName, String pass) throws FileNotFoundException, IOException{
        System.out.println("[" + Shared.now() + "] Shared " + Shared.lineNumber() +  " Cargando archivo de configuracion " + fileName);
        prepareFile(new File(Constants.rootDir + fileName), Constants.fileName4ConfigRar, pass, Constants.scriptConfig);
        File f = new File(Constants.tmpDir + Constants.fileName4Config);
        Scanner sc = new Scanner(f);
        int lineNumber = 1;
        while(sc.hasNextLine()){
            String line = sc.nextLine();
            String[] toks = line.split("==");
            if ( toks.length != 2 ){
                throw new FileNotFoundException("Error al leer la línea " + lineNumber);
            }
            fileConfig.put(toks[0], toks[1]);
            ++lineNumber;
        }
        sc.close();
        f.delete();

        // Backward compatible D=
        if ( !fileConfig.containsKey("printerDriver") ){
            fileConfig.put("printerDriver", "tfhkaif");
        }
    }

    public static String getFileConfig(String k){
        return fileConfig.get(k);
    }

    public static String getConfig(String k){
        return getConfig().get(k);
    }

    public static void loadPhoto(JLabel imageLabel , String addr, int x, int y){
        if ( addr != null ){
            System.out.println("[" + Shared.now() + "] Shared " + Shared.lineNumber() +  " Cargando Imagen: " + addr);
            ImageIcon image = new ImageIcon(addr);
            ImageIcon imageIcon = new ImageIcon(image.getImage().getScaledInstance( x, y, Image.SCALE_AREA_AVERAGING));
            imageLabel.setIcon(imageIcon);
            imageLabel.setVisible(true);
        }
    }

    public static double round(double value, int decimalPlace)
    {
      /*double power_of_ten = 1;
      double fudge_factor = 0.05;
      while (decimalPlace-- > 0) {
         power_of_ten *= 10.0d;
         fudge_factor /= 10.0d;
      }
      return Math.round((value + fudge_factor)* power_of_ten)  / power_of_ten;*/

      int pd = (int) Math.pow(10, decimalPlace);
      int t = (int) ( pd * value);
      return (double)t / (double)pd;
    }

    /**
     * @return the config
     */
    public static TreeMap<String, String> getConfig() {
        return config;
    }

    /**
     * @param aConfig the config to set
     */
    public static void setConfig(TreeMap<String, String> aConfig) {
        config = aConfig;
    }

    /**
     * @return the myMainWindows
     */
    protected static Component getMyMainWindows() {
        return myMainWindows;
    }

    /**
     * @param aMyMainWindows the myMainWindows to set
     */
    protected static void setMyMainWindows(Component aMyMainWindows) {
        myMainWindows = aMyMainWindows;
    }

    /**
     * @return the tries
     */
    protected static TreeMap<String, Integer> getTries() {
        return tries;
    }

    /**
     * @param aTries the tries to set
     */
    protected static void setTries(TreeMap<String, Integer> aTries) {
        tries = aTries;
    }

    /**
     * @return the user
     */
    protected static User getUser() {
        return user;
    }

    /**
     * @param aUser the user to set
     */
    protected static void setUser(User aUser) {
        user = aUser;
    }

    public static UpdateClock getScreenSaver() {
        return screenSaver;
    }

    public static void setScreenSaver(UpdateClock screenSaver) {
        Shared.screenSaver = screenSaver;
    }

    public static Turn getTurn(List<Turn> l ,String turnId){
        for (Turn turn : l) {
            if ( turn.getIdentificador().equals(turnId))
                return turn;
        }
        return null;
    }

    /**
     * @return the errMapping
     */
    public static TreeMap<Integer, String> getErrMapping() {
        return errMapping;
    }

    /**
     * @param errMapping the errMapping to set
     */
    public static void setErrMapping(TreeMap<Integer, String> errMapping_) {
        errMapping = errMapping_;
    }

    public static String formatDoubleToPrint(Double d){
        DecimalFormat df = new DecimalFormat("00000000.00");
        return df.format(d).replaceAll(",", "");
    }

    public static String formatDoubleToSpecifyMoneyInPrinter(Double d){
        DecimalFormat df = new DecimalFormat("0000000000.00");
        return df.format(d).replaceAll(",", "");
    }

    public static String formatDoubleToPrintDiscount(Double d){
        DecimalFormat df = new DecimalFormat("00.00");
        return df.format(d*100.0).replaceAll(",", "");
    }

    public static String formatQuantToPrint(Double d){
        DecimalFormat df = new DecimalFormat("00000.000");
        return df.format(d).replaceAll(",", "");
    }

    public static String format4Display(Double d){
        DecimalFormat df = new DecimalFormat("#0.00");
        return df.format(d).replaceAll(",", "");
    }

    public static Double getIva(){
        try{
            return Double.parseDouble(getConfig("iva"))*100.0;
        }catch (NumberFormatException nfe){
            return .0;
        }
    }

    public static void what2DoWithReceipt(MainRetailWindows myParent , Exception msg){
        try{
            MessageBox msb = new MessageBox(MessageBox.SGN_CAUTION, Shared.getConfig("errWithPrinter") , msg);
            msb.show(null);

            myParent.toWait();
            myParent.updateAll();
        } catch (SQLException ex1) {
            MessageBox msb = new MessageBox(MessageBox.SGN_DANGER, "Problemas con la base de datos.",ex1);
            msb.show(null);
        }
    }

    public static void parseDiscounts(String fileAdr) throws FileNotFoundException, SQLException{
        Scanner sc = new Scanner(new File(fileAdr));
        while (sc.hasNextLine()) {
            String[] toks = sc.nextLine().split("\t");
            ConnectionDrivers.updateDiscount(myTrim(toks[0]) , toks[8]);
        }
        sc.close();
    }

    public static String myTrim(String str){
        return str.substring(1, str.length()-1);
    }

    public static List<Item> parseItems(String fileAddr) throws FileNotFoundException, ParseException, IOException{
        newItemMapping = new TreeMap<String, Item>();
        List<Item> ans = new LinkedList<Item>();
        DataInputStream in = new DataInputStream(new FileInputStream(fileAddr));
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line = "";
        while ( (line = br.readLine()) != null ) {
            String[] toks = line.split("\t");
            
            Price p = new Price( null , Double.parseDouble(toks[35])/(getIva()/100.0+1.0));
            List<Price> lp = new LinkedList<Price>();
            lp.add(p);
            List<String> barcodes = new LinkedList<String>();
            barcodes.add(myTrim(toks[9]));
            Item i = new Item(myTrim(toks[0]),
                    myTrim(toks[1]) , Shared.dateFormatter.parse(toks[2].split(" ")[0]) , myTrim(toks[4]),
                    "",  myTrim(toks[6]), myTrim(toks[9]), myTrim(toks[10]), myTrim(toks[14]),
                    myTrim(toks[15]), Integer.parseInt(toks[19].split("\\.")[0]), lp, barcodes,
                    toks[85].equals("T"), Shared.getConfig("photoDir") + myTrim(toks[0]) + ".JPG", "0");
            ans.add(i);
            newItemMapping.put(i.getCode(), i);
        }
        in.close();
        return ans;
    }

    public static List<Movement> parseMovements(String fileAddrMain, String fileAddrDetails) throws FileNotFoundException, ParseException, IOException{
        List<Movement> ans = new LinkedList<Movement>();

        DataInputStream inM = new DataInputStream(new FileInputStream(fileAddrMain));
        DataInputStream inD = new DataInputStream(new FileInputStream(fileAddrDetails));
        BufferedReader brM = new BufferedReader(new InputStreamReader(inM));
        BufferedReader brD = new BufferedReader(new InputStreamReader(inD));

        TreeMap<String , List<ItemQuant> > t = new TreeMap<String, List<ItemQuant>>();

        String line;
        while ( (line = brD.readLine()) != null ) {
            String[] toks = line.split("\t");
            if ( !t.containsKey(toks[0]) ){
                t.put(toks[0], new LinkedList<ItemQuant>());
            }
            int mul = 1;
            if ( toks[2].equals("\"TSAL\"") ){
                mul = -1;
            }
            t.get(toks[0]).add(new ItemQuant(myTrim(toks[3]), ((int) Double.parseDouble(toks[4])) * mul ));
        }

        while( (line = brM.readLine()) != null ){
            String[] toks = line.split("\t");
            if ( myTrim(toks[25]).equals(Shared.getConfig("storeName")) ){
                hadMovements = true;
                Date dd = Shared.dateFormatter.parse(toks[1].split(" ")[0]);
                java.sql.Date ddsql = new java.sql.Date(dd.getYear(), dd.getMonth(), dd.getDate());
                Movement m = new Movement(toks[0], ddsql
                        , myTrim(toks[2]) , myTrim(toks[16]), myTrim(toks[28]), t.get(toks[0]));
                ans.add(m);
            }
        }

        inM.close();
        inD.close();
        return ans;
    }

    public static void updateExpensesAndBanks(){
        try {
            SrvEntidades srvEnt = new SrvEntidades();
            IsrvEntidades bHBIE = srvEnt.getBasicHttpBindingIsrvEntidades();
            List<BNKA> lbnka = bHBIE.obtenerBancosSap(Shared.getConfig("mant")).getBNKA();
            String banks = "";
            for (BNKA bnka : lbnka) {
                banks += "{" + bnka.getBANKL().getValue() + " - " + bnka.getBANKA().getValue() + "}";
            }
            ConnectionDrivers.updateConfig("banks", banks);
            List<DD07T> dD07T = bHBIE.obtenerTiposSap().getDD07T();
            String expenses = "";
            for (DD07T dd07t : dD07T) {
                try{
                    Integer.parseInt(dd07t.getDOMVALUEL().getValue());
                    expenses += "{" + dd07t.getDOMVALUEL().getValue() + " - " + dd07t.getDDTEXT().getValue() + "}";
                }catch(NumberFormatException e){
                    ;// Nothing to do. That is not a number
                }
            }
            ConnectionDrivers.updateConfig("expenses", expenses);
            
        } catch (SQLException ex) {
            System.err.println("Problemas actualizando a los bancos.");
        }
    }

    public static void updateMovements() throws FileNotFoundException, SQLException, ParseException, IOException{
        hadMovements = false;
        System.out.println("[" + Shared.now() + "] Shared " + Shared.lineNumber() +  " Parse Items");

        String addrForIncome = Shared.getConfig("addrForIncome");
        List<Item> items = parseItems(addrForIncome + "art.txt");
        System.out.println("[" + Shared.now() + "] Shared " + Shared.lineNumber() +  " Listo\nActualizar Items");
        ConnectionDrivers.updateItems(items);
        System.out.println("[" + Shared.now() + "] Shared " + Shared.lineNumber() +  " Actualizar Movimientos");
        List<Movement> movements = parseMovements(addrForIncome + "ajuste.txt", addrForIncome + "reng_aju.txt");
        ConnectionDrivers.updateMovements(movements, newItemMapping);
        parseDiscounts(addrForIncome + "descuen.txt");
    }

    public static boolean isHadMovements() {
        return hadMovements;
    }
    
    public static String formatIt(String msg1, String msg2){
        char[] spaces = new char[Integer.parseInt(Shared.getConfig("longReportTotals")) - msg1.length() - msg2.length()];
        Arrays.fill(spaces, ' ');
        return msg1 + new String(spaces) + msg2;
    }

    @Deprecated
    static void prepareMovements(File myRar) throws IOException {
        System.out.println("[" + Shared.now() + "] Shared " + Shared.lineNumber() +  " Preparando movimientos");
        String addrForIncome = Shared.getConfig("addrForIncome");
        String fileName4Income = Shared.getConfig("fileName4Income");
        String cmd = "copy \"" + myRar.getAbsolutePath() + "\" \"" + addrForIncome + fileName4Income + "\"\n"+
                "cd \"" + addrForIncome + "\"\n" +
                "erase *.txt\n" +
                "\"C:\\Archivos de programa\\WinRAR\\unrar.exe\" e " + fileName4Income + "\n"+
                "erase " + fileName4Income + "\n";

        String scriptMovementsName = Shared.getConfig("scriptMovementsName");
        FileWriter fstream = new FileWriter(Constants.rootDir + scriptMovementsName);
        BufferedWriter out = new BufferedWriter(fstream);

        out.write(cmd);
        out.close();

        Process process = Runtime.getRuntime().exec(Constants.rootDir + scriptMovementsName);
        InputStream is = process.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        while ( br.readLine() != null) {
            ;
        }
        File f = new File(Constants.rootDir + scriptMovementsName);
        f.delete();

    }

    public static void createBackup(String table) throws IOException, SQLException{

        System.out.println("[" + Shared.now() + "] Shared " + Shared.lineNumber() +  " Creando respaldo");
        String backupDir = Shared.getConfig("backupDir");
        String cmd = "mysqldump -u " + Shared.getFileConfig("dbUser") + " -p" + Shared.getFileConfig("dbPassword") + " " + Shared.getFileConfig("dbName") + " " + table + " > " +
                backupDir + Shared.sdfDay2DB.format(Calendar.getInstance().getTime()) + "-" +
                Shared.sdfHour2BK.format((Calendar.getInstance().getTime()));

        String scriptMovementsName = Shared.getConfig("scriptMovementsName");
        FileWriter fstream = new FileWriter(Constants.tmpDir + scriptMovementsName);
        BufferedWriter out = new BufferedWriter(fstream);

        out.write(cmd);
        out.close();

        Process process = Runtime.getRuntime().exec(Constants.tmpDir + scriptMovementsName);
        InputStream is = process.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        while ( br.readLine() != null) {
            ;
        }
        File f = new File(Constants.tmpDir + scriptMovementsName);
        f.delete();
    }

    protected static void prepareFile(File myRar, String fileName, String configKey, String scriptFile) throws IOException {
        System.out.println("[" + Shared.now() + "] Shared " + Shared.lineNumber() +  " Preparando archivo " + fileName);
        String pass = Shared.getConfig(configKey);
        if ( configKey.equals("password4config") ){
            pass = Constants.configPassword;
        }else if ( configKey.equals("password4passwd") ){
            pass = Constants.passwdPassword;
        }
        String cmd = "copy \"" + myRar.getAbsolutePath() + "\" \"" + Constants.tmpDir + fileName + "\"\n"+
                "cd \"" + Constants.tmpDir + "\"\n" +
                "\"C:\\Archivos de programa\\WinRAR\\unrar.exe\" -p" + pass + " e -y " + fileName + "\n"+
                "erase " + Constants.tmpDir + fileName + "\n";

        FileWriter fstream = new FileWriter(Constants.tmpDir + scriptFile);
        BufferedWriter out = new BufferedWriter(fstream);

        out.write(cmd);
        out.close();

        Process process = Runtime.getRuntime().exec(Constants.tmpDir + scriptFile);
        InputStream is = process.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        while ( br.readLine() != null) {
            ;
        }
        File f = new File(Constants.tmpDir + scriptFile);
        f.delete();

    }

    protected static void checkVisibility(JTable table) {
        Rectangle rect = table.getCellRect(table.getSelectedRow(), 0, true);
        table.scrollRectToVisible(rect);
    }

    public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";

    public static String now() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        return sdf.format(cal.getTime());
    }

    public static int calculateReason(String bwart, String shkzg){
        System.out.println("[" + Shared.now() + "] Shared " + Shared.lineNumber() +  " Calculando razon para " + bwart + " " + shkzg);
        int reason = 0;
        for (String ii : Shared.getConfig("incomingItems").split(",")) {
            if ( bwart.equals(ii) ){
                reason = 1;
            }
        }

        for (String oi : Shared.getConfig("outcomingItems").split(",")) {
            if ( bwart.equals(oi) ){
                reason = -1;
            }
        }

        boolean isEqual = false;
        for (String bMovement : Shared.getConfig("bwartMovement").split(",")) {
            if ( bwart.equals(bMovement) ){
                isEqual = true;
            }
        }
        if ( isEqual ){
            if ( shkzg.equals("H") ){
                reason = -1;
            }else if ( shkzg.equals("S") ){
                reason = 1;
            }
        }
        return reason;
    }

    public static String b2s(byte b[]) {
        // Converts C string to Java String
        int len = 0;
        while (len < b.length && b[len] != 0)
        ++len;
        return new String(b, 0, len);
    }

    protected static String now4backup(){
        return Shared.sdf4backup.format(Calendar.getInstance().getTime()) + ".rar";
    }

    protected static void createBackup() throws IOException, IllegalStateException, FTPIllegalReplyException, FTPException, FileNotFoundException, FTPDataTransferException, FTPAbortedException{
        System.out.println("[" + Shared.now() + "] Shared " + Shared.lineNumber() +  " Creando respaldo");
        String cmd = "mysqldump -u " + Shared.getFileConfig("dbUser") + " -p" + Shared.getFileConfig("dbPassword") + " " + Shared.getFileConfig("dbName")  +
                " > " + Constants.tmpDir + "Backup.sql";

        String tmpScript = Shared.getConfig("tmpScript");

        FileWriter fstream = new FileWriter(Constants.tmpDir + tmpScript);
        BufferedWriter out = new BufferedWriter(fstream);

        out.write(cmd);
        out.close();

        Process process = Runtime.getRuntime().exec(Constants.tmpDir + tmpScript);
        InputStream is = process.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        while ( br.readLine() != null) {
            ;
        }

        String fileName = now4backup();
        cmd = "\"C:\\Archivos de programa\\WinRAR\\Rar.exe\" a -m5 -ed \"" + Constants.tmpDir
                + fileName + "\" " + Constants.tmpDir + "Backup.sql";
        process = Runtime.getRuntime().exec(cmd);
        is = process.getInputStream();
        isr = new InputStreamReader(is);
        br = new BufferedReader(isr);

        while ( br.readLine() != null) {
            ;
        }

        FTPClient client = new FTPClient();
        client.connect(Shared.getConfig("ftpBackupAddr"), Integer.parseInt(Shared.getConfig("ftpPort")));
        client.login(Shared.getConfig("ftpBackupUser"), Shared.getConfig("ftpBackupPassword"));
        client.changeDirectory("/" + Shared.getConfig("storeName"));
        File f = new File(Constants.tmpDir + fileName);
        client.upload(f);
        client.disconnect(false);

    }

    static void sendSells(String myDay, ClosingDay cd , String ansMoney ) throws SQLException, IOException {

        // CN

        /*if ( receipts.isEmpty() ){
            MessageBox msg = new MessageBox(MessageBox.SGN_SUCCESS, "No se puede continuar, debe existir al menos una nota de crédito.");
            msg.show(this);
            return;
        }*/
        // TODO UNCOMMENT THIS
        

        



        /*String ansTP = "OK";

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLWriter xmlw = new XMLWriter(baos);
        xmlw.write(xmlCN);
        System.out.println("Notas de Credito = " + baos.toString());
/*
        /*

        System.out.println("Comienzo de envio");
        WS ws = new WSService().getWSPort();
        String ansI = ws.initialize(myDay, Shared.getConfig("storeName"));
        System.out.println("Inicializar = " + ansI);
        if ( !ansI.isEmpty() ) {
            ansTP = ansI;
        }
        ansI = ws.deleteDataFrom();
        System.out.println("Eliminar = " + ansI);
        if ( !ansI.isEmpty() ) {
            ansTP = ansI;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLWriter xmlw = new XMLWriter(baos);
        xmlw.write(xmlCN);
        ansI = ws.sendCreditNotes(baos.toString());
        System.out.println("Nota de Credito = " + ansI );
        if ( !ansI.isEmpty() ) {
            ansTP = ansI;
        }
        ByteArrayOutputStream baosF = new ByteArrayOutputStream();
        XMLWriter xmlwF = new XMLWriter(baosF);
        xmlwF.write(xmlRe);
        ansI =  ws.sendReceipts(baosF.toString());
        System.out.println("Facturas = " + ansI );
        if ( !ansI.isEmpty() ) {
            ansTP = ansI;
        }
        ByteArrayOutputStream baosC = new ByteArrayOutputStream();
        XMLWriter xmlwC = new XMLWriter(baosC);
        xmlwC.write(clienXML);
        ansI = ws.sendClients(baosC.toString());
        System.out.println("Clientes = " + ansI);
        if ( !ansI.isEmpty() ) {
            ansTP = ansI;
        }
        ansI = ws.createDummySeller();
        System.out.println("Vend = " + ansI );

        if ( !ansI.isEmpty() ) {
            ansTP = ansI;
        }*/

        //String ansTP = "";
        // UNCOMMENT THIS
        /*try{
            Shared.createBackup();
        }catch( Exception ex ){
            ansTP = "File Error";
        }*/

        String msgT = "<html><br>Cobranzas: " + ansMoney + "<br>Ventas: " + "" + " </html>" ;
        MessageBox msg = new MessageBox(MessageBox.SGN_SUCCESS, msgT);
        msg.show(cd);
    }

    public static int getProcessingWindows() {
        return processingWindows;
    }

    public static void setProcessingWindows(int processingWindows) {
        Shared.processingWindows = processingWindows;
    }

    public static void createLockFile(){
        FileWriter fstream = null;
        try {
            fstream = new FileWriter(".lock");
            BufferedWriter out = new BufferedWriter(fstream);
            out.write("");
            out.close();
        } catch (IOException ex) {
            //Don't do anything!
        } finally {
            try {
                fstream.close();
            } catch (IOException ex) {
                //Don't do anything!
            }
        }
    }

    public static void removeLockFile(){
        File f = new File(".lock");
        f.delete();
    }

    public static boolean isLockFile(){
        File f = new File(".lock");
        return f.canRead();
    }

    public static void sendMail(String to , String msg, String subject) throws MessagingException, UnsupportedEncodingException{
        System.out.println("[" + Shared.now() + "] Shared " + Shared.lineNumber() +  " Enviando correo");
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        Session session = Session.getDefaultInstance(props,
        new javax.mail.Authenticator() {
        protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(Shared.getConfig("email"),Shared.getConfig("passEmail"));
        }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("ventasdiariasgt99@gmail.com", "Agencia " + Shared.getConfig("storeName") + " ( " + Shared.getConfig("storeDescription") + " )"));

        String addrs[] = to.split(",");
        InternetAddress[] addressTo = new InternetAddress[addrs.length];
        /*addressTo[0] = new InternetAddress("yrondon@gt99.com.ve");
        addressTo[1] = new InternetAddress("nmiuc@gt99.com.ve");*/
        for ( int i = 0 ; i < addrs.length ; i++ ){
            addressTo[i] = new InternetAddress(addrs[i]);
        }
        /*addressTo[2] = new InternetAddress("nmiuc@gt99.com.ve");*/
        message.setRecipients(Message.RecipientType.TO,
                    addressTo);
        message.setSubject(subject);
        message.setContent(msg, "text/html");

        Transport.send(message);

        System.out.println("Correo enviado");
    }

    protected static boolean isHoliday(String day){
        return holidays.contains(day);
    }

    /*protected static boolean didItCome(String c){
        return !(c == null  ||
                        (!c.equals("S") && !c.equals("L")
                          && !c.equals("R") && !c.equals("ML")
                            && !c.equals("V")));
    }*/
    protected static boolean didItCome(String c){
        return (c != null  &&
                        (c.equals("S") || c.equals("L")
                          || c.equals("R")
                            || c.equals("V")));
    }

    public static boolean isSunday(JTable presenceTable, int j) {
        return presenceTable.getModel().getColumnName(j).split(" ")[0].equals("Dom");
    }

    public static boolean isSaturday(JTable presenceTable, int j) {
        return presenceTable.getModel().getColumnName(j).split(" ")[0].equals("Sab");
    }

    public static boolean isHoliday(JTable presenceTable, int j) {
        return isHoliday(presenceTable.getModel().getColumnName(j).split(" ")[1]);
    }

}
