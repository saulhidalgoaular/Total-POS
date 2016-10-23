package totalpos;

import net.sf.dynamicreports.report.constant.VerticalAlignment;
import net.sf.dynamicreports.report.constant.HorizontalAlignment;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import static net.sf.dynamicreports.report.builder.DynamicReports.*;

import srvSap.ObjectFactory;

/**
 *
 * @author Saul Hidalgo.
 */
public class Constants {
    protected static final String companyName = "Grupo Total 99";
    protected static final String appName = "Total POS";

    protected static final int numberConnection = 5;

    //protected static final String dbHost = "localhost";
    //protected static final String dbPassword = "123456789";
    protected static final String configPassword = "Admingt.99Admingt.99";

    protected static final ObjectFactory of = new ObjectFactory();
    
    //protected static final String[] var2check = {"Server","ServerMirror","myId","printerPort", "printerDriver"};
    protected static final String[] var2check = {"Server","ServerMirror","myId","printerPort"};

    protected static final boolean isPos = false;

    protected static final boolean withFiscalPrinter = true;

    protected static final Font font = new Font("Courier New", 0, 12);
    protected static final Font font12 = new Font("Courier New", 0, 12);
    protected static final Font font15 = new Font("Courier New", 0, 15);
    protected static final Font font24 = new Font("Courier New", 0, 24);
    protected static final Color transparent = new Color(0, true);
    protected static final Color lightBlue = new Color(184,207,229);
    protected static final Color lightGreen = new Color(150,255,150);

    protected static final StyleBuilder boldStyle = stl.style().bold();
    protected static final StyleBuilder boldCenteredStyle = stl.style(boldStyle)
	                                    .setHorizontalAlignment(HorizontalAlignment.CENTER);
    protected static final StyleBuilder columnTitleStyle  = stl.style(boldCenteredStyle)
	                                    .setBorder(stl.pen1Point())
	                                    .setBackgroundColor(Color.LIGHT_GRAY);

    protected static final StyleBuilder titleStyle = stl.style(boldCenteredStyle)
                             .setVerticalAlignment(VerticalAlignment.MIDDLE)
                             .setFontSize(15);

    protected static final String tmpDir = System.getenv("TMP") + File.separator;

    protected static final String rootDir = "./";
    protected static final String fileName4ConfigN = "config";
    protected static final String fileName4passwd = "passwd";
    protected static final String fileName4ConfigRar = "config.rar";
    protected static final String fileName4Config = "config.txt";
    protected static final String scriptConfig = "config.bat";

    protected static int dbTimeout = 60000;

    protected static int lockingPort = 54320;
    static boolean justEmail = false;
    static String passwdPassword = "Asdflolxd123.";

}
