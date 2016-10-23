/*
 * MainRetailWindows.java
 *
 * Created on 29-jul-2011, 15:41:13
 */

package totalpos;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JViewport;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Saúl Hidalgo
 */
public final class MainRetailWindows extends javax.swing.JFrame {

    private User user;
    protected int quant = 1;
    private List<Item2Receipt> items;
    public String actualId;
    public FiscalPrinter printer;
    public boolean isOk = false;
    public boolean closing = false;
    public Double globalDiscount = .0;
    private Client client = null;
    private Assign assign;
    public double subtotal;
    public boolean finishedFP = false;
    private int msgIndex = -1;
    public List<String> msg2pos;
    Login myParent;

    /** Creates new form MainRetailWindows
     * @param u
     * @param assign
     */
    public MainRetailWindows(User u, Assign assign, Login parent) {
        try {
            initComponents();
            System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Constructor invocado");
            myParent = parent;
            offlineLabel.setVisible(Shared.isOffline);
            Shared.setMyMainWindows(this);
            if ( this.getWidth() < 1100 ){
                descriptionLabel.setFont(new java.awt.Font("Courier New", 0, 9));
            }
            this.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);

            posID.setText("Caja #" + Shared.getFileConfig("myId"));

            user = u;
            try{
                System.out.println("Creando la libreria...");
                printer = new FiscalPrinter();
                Shared.printer = printer;
                System.out.println("Termino.");
            }catch( Exception ex ){
                MessageBox msb = new MessageBox(MessageBox.SGN_DANGER, "No se pudo cargar el controlador de la impresora. No se puede continuar",ex);
                msb.show(myParent);
                this.dispose();
                Shared.reload();
                return;
            }
            this.assign = assign;
            yourTurnIsFinishingLabel.setVisible(false);
            if ( !ConnectionDrivers.isAllowed(u.getPerfil(), "retail") ){
                MessageBox msb = new MessageBox(MessageBox.SGN_DANGER, "Esta usuario no tiene permisos para utilizar el punto de venta.");
                msb.show(myParent);
                this.dispose();
                Shared.reload();
                return;
            }

            try {
                printer.printerSerial = null;
                System.out.println("Check Fiscal Printer...");
                if ( !printer.checkPrinter() ){
                    MessageBox msb = new MessageBox(MessageBox.SGN_DANGER, "La impresora no coincide con la registrada en el sistema o la caja ha sido bloqueada.");
                    msb.show(myParent);
                    this.dispose();
                    Shared.reload();
                    myParent.dispose();
                    return;
                }
                System.out.println("Salio del checkFiscalPrinter!");
            }catch ( Exception ex ){
                MessageBox msb = new MessageBox(MessageBox.SGN_DANGER, Shared.getFileConfig("errWithPrinter"),ex);
                msb.show(myParent);
                this.dispose();
                Shared.reload();
                myParent.dispose();
                printer.forceClose();
                return;
            }
            
            try {
                ConnectionDrivers.updateReportZ(printer.getZ());
                //ConnectionDrivers.updateLastReceipt(printer.lastReceipt);
                printer.updateValues("curdate()");
            } catch (Exception ex) {
                MessageBox msb = new MessageBox(MessageBox.SGN_DANGER, Shared.getFileConfig("errWithPrinter"),ex);
                msb.show(myParent);
                this.dispose();
                Shared.reload();
                printer.forceClose();
                return;
            }

            if ( !Shared.isOffline ){
                msg2pos = ConnectionDrivers.getListMsg2Pos();
            }

            updateAll();
            
            isOk = true;
        } catch (SQLException ex) {
            MessageBox msb = new MessageBox(MessageBox.SGN_DANGER, "Problemas con la base de datos.",ex);
            msb.show(myParent);
            this.dispose();
            Shared.reload();
        } catch (Exception ex){
            MessageBox msb = new MessageBox(MessageBox.SGN_DANGER, "Error desconocido",ex);
            msb.show(myParent);
            this.dispose();
            Shared.reload();
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    protected void clearForm() throws SQLException{
        descriptionLabel.setText("Bievenido a Mundo Total");
        currentPrice.setText("");
        quantItemField.setText("0");
        ivaLabelResult.setText("0.00 Bs");
        TotalLabelResult.setText("0.00 Bs");
        subTotalLabelResult.setText("0.00 Bs");
        discountLabel.setVisible(false);
        discountResult.setVisible(false);
        items = new ArrayList<Item2Receipt>();
        imageLabel.setVisible(false);
        globalDiscount = .0;
        quant = 1;

        if ( msgIndex != -1 ){
            msg2user2.setText(msg2pos.get(msgIndex));
        }

        cleanTable();
        
        List<Receipt> idleReceipts = ConnectionDrivers.listIdleReceiptToday();
        if ( idleReceipts.isEmpty() ){
            msg2user.setVisible(false);
        }else if ( idleReceipts.size() == 1 ) {
            msg2user.setVisible(true);
            msg2user.setText("Tiene 1 pedido en espera.");
        }else{
            msg2user.setVisible(true);
            msg2user.setText("Tiene " + idleReceipts.size() + " pedidos en espera.");
        }
    }

    protected void updateAll() throws SQLException{

        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Actualizar todo");
        clearForm();

        ConnectionDrivers.fixWrongReceipts();
        List<Receipt> uncompletedReceipts = ConnectionDrivers.listUncompletedReceiptToday();
        
        if ( uncompletedReceipts.isEmpty() ){
            // For performance! 
            ConnectionDrivers.cancelAllReceipts();
            actualId = Shared.nextId(0);
            ConnectionDrivers.createReceipt(actualId, user.getLogin(), assign);
            if ( ConnectionDrivers.isNeededtoUpdate() ){
                try {
                    printer.updateValues("curdate()");
                } catch (Exception ex) {
                    MessageBox msb = new MessageBox(MessageBox.SGN_CAUTION, Shared.getFileConfig("errWithPrinter"),ex);
                    msb.show(this);
                    printer.forceClose();
                    this.dispose();
                    Shared.reload();
                    return;
                }
            }
            setClient(null);
            Shared.msgWithEffect("Bienvenido a    ", "    Mundo Total =D  ");
        }else{
            MessageBox msb = new MessageBox(MessageBox.SGN_IMPORTANT, "Existe un pedido pendiente!");
            msb.show(this);
            loadThisReceipt(uncompletedReceipts.get(0));
        }
    }

    protected void cleanTable(){
        DefaultTableModel model = (DefaultTableModel) gridTable.getModel();
        model.setRowCount(0);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Image ii;

        try{
            ii = ImageIO.read(new File("Fondo.jpg"));
        }catch(Exception ex){
            ii = (new ImageIcon(getClass().getResource("/totalpos/resources/Fondo-Inicio.jpg"))).getImage();
        }
        wallpaper = new Bottom(ii);
        jPanel2 = new Bottom((new ImageIcon(getClass().getResource("/totalpos/resources/Factura.jpg"))).getImage());
        jScrollPane1 = new javax.swing.JScrollPane(){
            private final TexturePaint texture = receiptBottom();
            @Override protected JViewport createViewport() {
                return new JViewport() {
                    @Override public void paintComponent(Graphics g) {
                        if(texture!=null) {
                            Graphics2D g2 = (Graphics2D)g;
                            g2.setPaint(texture);
                            g2.fillRect(0,0,getWidth(),getHeight());
                        }
                        super.paintComponent(g);
                    }
                };
            }
        };
        gridTable = new javax.swing.JTable(){
            @Override public Component prepareRenderer(TableCellRenderer renderer, int row, int column){
                Component comp = super.prepareRenderer(renderer, row, column);
                double discD = .0;
                try{
                    discD = Double.parseDouble((String)getValueAt(row, 2));
                }catch(Exception ex){
                    ;
                }
                if ( gridTable.getSelectedRow() == row ){
                    if ( discD != .0 ){
                        comp.setBackground(Constants.lightGreen);
                    }else{
                        comp.setBackground(Constants.lightBlue);
                    }
                } else if ( discD != .0 ){
                    comp.setBackground(Color.YELLOW);
                }else{
                    comp.setBackground(Constants.transparent);
                }
                return comp;
            }
        };
        barcodeField = new javax.swing.JTextField();
        subTotalLabelResult = new javax.swing.JLabel();
        subTotalLabel = new javax.swing.JLabel();
        discountLabel = new javax.swing.JLabel();
        discountResult = new javax.swing.JLabel();
        ivaLabelResult = new javax.swing.JLabel();
        ivaLabel = new javax.swing.JLabel();
        totalLabel = new javax.swing.JLabel();
        TotalLabelResult = new javax.swing.JLabel();
        quantItemsLabel = new javax.swing.JLabel();
        quantItemField = new javax.swing.JLabel();
        jPanel1 = new Bottom((new ImageIcon(getClass().getResource("/totalpos/resources/Area-descripcion-articulo.jpg"))).getImage());
        descriptionLabel = new javax.swing.JLabel();
        currentPrice = new javax.swing.JLabel();
        imagePanel = new Bottom((new ImageIcon(getClass().getResource("/totalpos/resources/area-foto-articulo.jpg")).getImage()));
        imageLabel = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jPanel3 = new Bottom((new ImageIcon(getClass().getResource("/totalpos/resources/fecha-y-hora.jpg")).getImage()));
        whatTimeIsIt = new javax.swing.JLabel();
        messageToTheClients = new Bottom((new ImageIcon(getClass().getResource("/totalpos/resources/Area-mensajes-al-cajero.jpg")).getImage()));
        msg2user = new javax.swing.JLabel();
        msg2user2 = new javax.swing.JLabel();
        yourTurnIsFinishingLabel = new javax.swing.JLabel();
        offlineLabel = new javax.swing.JLabel();
        posID = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle(Constants.appName);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        wallpaper.setName("wallpaper"); // NOI18N

        jPanel2.setName("jPanel2"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        gridTable.setAutoCreateRowSorter(true);
        gridTable.setOpaque(false);
        gridTable.setBackground(Constants.transparent); //THIS IS THE TRICK, ISN'T IT? xD
        gridTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Descripción", "Cantidad", "Descuento", "Precio", "P/Venta"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        gridTable.setFocusable(false);
        gridTable.setName("gridTable"); // NOI18N
        gridTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        gridTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(gridTable);
        gridTable.getColumnModel().getColumn(0).setPreferredWidth(600);

        barcodeField.setName("barcodeField"); // NOI18N
        barcodeField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                barcodeFieldKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                barcodeFieldKeyReleased(evt);
            }
        });

        subTotalLabelResult.setFont(new java.awt.Font("Courier New", 0, 14));
        subTotalLabelResult.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        subTotalLabelResult.setFocusable(false);
        subTotalLabelResult.setName("subTotalLabelResult"); // NOI18N

        subTotalLabel.setFont(new java.awt.Font("Courier New", 0, 12));
        subTotalLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        subTotalLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/totalpos/resources/Etiquetas.jpg"))); // NOI18N
        subTotalLabel.setText("SubTotal");
        subTotalLabel.setFocusable(false);
        subTotalLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        subTotalLabel.setName("subTotalLabel"); // NOI18N

        discountLabel.setFont(new java.awt.Font("Courier New", 0, 12));
        discountLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        discountLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/totalpos/resources/Etiquetas.jpg"))); // NOI18N
        discountLabel.setText("Descuento");
        discountLabel.setFocusable(false);
        discountLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        discountLabel.setName("discountLabel"); // NOI18N

        discountResult.setFont(new java.awt.Font("Courier New", 0, 14));
        discountResult.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        discountResult.setFocusable(false);
        discountResult.setName("discountResult"); // NOI18N

        ivaLabelResult.setFont(new java.awt.Font("Courier New", 0, 14));
        ivaLabelResult.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        ivaLabelResult.setFocusable(false);
        ivaLabelResult.setName("ivaLabelResult"); // NOI18N

        ivaLabel.setFont(new java.awt.Font("Courier New", 0, 12));
        ivaLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        ivaLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/totalpos/resources/Etiquetas.jpg"))); // NOI18N
        ivaLabel.setText("I.V.A.(" + (Shared.getIva()+"").split("\\.")[0] + "%)");
        ivaLabel.setFocusable(false);
        ivaLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ivaLabel.setName("ivaLabel"); // NOI18N

        totalLabel.setFont(new java.awt.Font("Courier New", 0, 12));
        totalLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        totalLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/totalpos/resources/Etiquetas.jpg"))); // NOI18N
        totalLabel.setText("Total");
        totalLabel.setFocusable(false);
        totalLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        totalLabel.setName("totalLabel"); // NOI18N

        TotalLabelResult.setFont(new java.awt.Font("Courier New", 0, 14));
        TotalLabelResult.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        TotalLabelResult.setFocusable(false);
        TotalLabelResult.setName("TotalLabelResult"); // NOI18N

        quantItemsLabel.setFont(new java.awt.Font("Courier New", 0, 12));
        quantItemsLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        quantItemsLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/totalpos/resources/Etiquetas.jpg"))); // NOI18N
        quantItemsLabel.setText("Cantidad");
        quantItemsLabel.setFocusable(false);
        quantItemsLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        quantItemsLabel.setName("quantItemsLabel"); // NOI18N

        quantItemField.setFont(new java.awt.Font("Courier New", 0, 14));
        quantItemField.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        quantItemField.setFocusable(false);
        quantItemField.setName("quantItemField"); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(185, 185, 185)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(discountLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(subTotalLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(quantItemsLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGap(10, 10, 10)
                                        .addComponent(discountResult, javax.swing.GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(subTotalLabelResult, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
                                            .addComponent(quantItemField, javax.swing.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)))))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(totalLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)
                                .addComponent(TotalLabelResult, javax.swing.GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(ivaLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)
                                .addComponent(ivaLabelResult, javax.swing.GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE))))
                    .addComponent(barcodeField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 494, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 494, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(barcodeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(quantItemField, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(quantItemsLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(subTotalLabel)
                    .addComponent(subTotalLabelResult, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(discountResult, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(discountLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ivaLabelResult, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ivaLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(TotalLabelResult, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(totalLabel))
                .addContainerGap())
        );

        jScrollPane1.getViewport().setOpaque(false);

        jPanel1.setName("jPanel1"); // NOI18N

        descriptionLabel.setFont(new java.awt.Font("Courier New", 0, 12));
        descriptionLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        descriptionLabel.setName("descriptionLabel"); // NOI18N

        currentPrice.setFont(new java.awt.Font("Courier New", 1, 12));
        currentPrice.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        currentPrice.setName("currentPrice"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(descriptionLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE)
                    .addComponent(currentPrice, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(15, Short.MAX_VALUE)
                .addComponent(descriptionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(currentPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        imagePanel.setName("imagePanel"); // NOI18N

        imageLabel.setName("imageLabel"); // NOI18N

        javax.swing.GroupLayout imagePanelLayout = new javax.swing.GroupLayout(imagePanel);
        imagePanel.setLayout(imagePanelLayout);
        imagePanelLayout.setHorizontalGroup(
            imagePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(imagePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(imageLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE)
                .addContainerGap())
        );
        imagePanelLayout.setVerticalGroup(
            imagePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, imagePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(imageLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 307, Short.MAX_VALUE))
        );

        jPanel6.setName("jPanel6"); // NOI18N
        jPanel6.setOpaque(false);
        jPanel6.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                jPanel6MouseMoved(evt);
            }
        });
        jPanel6.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jPanel6KeyPressed(evt);
            }
        });

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/totalpos/resources/Etiquetas.jpg"))); // NOI18N
        jLabel1.setText("Fin / Reporte Z");
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/totalpos/resources/Etiquetas.jpg"))); // NOI18N
        jLabel2.setText("F2 / Borrar");
        jLabel2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel2.setName("jLabel2"); // NOI18N

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/totalpos/resources/Etiquetas.jpg"))); // NOI18N
        jLabel3.setText("F5 / Cobrar");
        jLabel3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel3.setName("jLabel3"); // NOI18N

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/totalpos/resources/Etiquetas.jpg"))); // NOI18N
        jLabel4.setText("F8 / Clientes");
        jLabel4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel4.setName("jLabel4"); // NOI18N

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/totalpos/resources/Etiquetas.jpg"))); // NOI18N
        jLabel5.setText("Esc / Salir");
        jLabel5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel5.setName("jLabel5"); // NOI18N

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/totalpos/resources/Etiquetas.jpg"))); // NOI18N
        jLabel6.setText("F9 / Desc Global");
        jLabel6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel6.setName("jLabel6"); // NOI18N

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/totalpos/resources/Etiquetas.jpg"))); // NOI18N
        jLabel7.setText("F10 / Borrar Pedido");
        jLabel7.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel7.setName("jLabel7"); // NOI18N

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/totalpos/resources/Etiquetas.jpg"))); // NOI18N
        jLabel8.setText("F11 / A espera");
        jLabel8.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel8.setName("jLabel8"); // NOI18N

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/totalpos/resources/Etiquetas.jpg"))); // NOI18N
        jLabel9.setText("F12 / Ver  espera");
        jLabel9.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel9.setName("jLabel9"); // NOI18N

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/totalpos/resources/Etiquetas.jpg"))); // NOI18N
        jLabel10.setText("F6 / Extraer Dinero");
        jLabel10.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel10.setName("jLabel10"); // NOI18N

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/totalpos/resources/Etiquetas.jpg"))); // NOI18N
        jLabel11.setText("F3 Nota de Crédito");
        jLabel11.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel11.setName("jLabel11"); // NOI18N

        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/totalpos/resources/Etiquetas.jpg"))); // NOI18N
        jLabel12.setText("F7 / Reporte X");
        jLabel12.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel12.setName("jLabel12"); // NOI18N

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(88, 88, 88)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel11)))
                .addContainerGap(483, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel5)
                    .addComponent(jLabel4)
                    .addComponent(jLabel3)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jLabel10)
                    .addComponent(jLabel9)
                    .addComponent(jLabel8)
                    .addComponent(jLabel7)
                    .addComponent(jLabel12))
                .addContainerGap())
        );

        jPanel3.setName("jPanel3"); // NOI18N

        whatTimeIsIt.setName("whatTimeIsIt"); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(whatTimeIsIt, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(whatTimeIsIt, javax.swing.GroupLayout.DEFAULT_SIZE, 14, Short.MAX_VALUE)
                .addContainerGap())
        );

        messageToTheClients.setFocusable(false);
        messageToTheClients.setName("messageToTheClients"); // NOI18N

        msg2user.setFont(new java.awt.Font("Courier New", 1, 14));
        msg2user.setForeground(new java.awt.Color(255, 0, 0));
        msg2user.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        msg2user.setText("Acá van los mensajes xD");
        msg2user.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        msg2user.setName("msg2user"); // NOI18N

        msg2user2.setFont(new java.awt.Font("Courier New", 1, 14));
        msg2user2.setForeground(new java.awt.Color(255, 0, 0));
        msg2user2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        msg2user2.setText("Sonríale al cliente así =D ");
        msg2user2.setName("msg2user2"); // NOI18N

        javax.swing.GroupLayout messageToTheClientsLayout = new javax.swing.GroupLayout(messageToTheClients);
        messageToTheClients.setLayout(messageToTheClientsLayout);
        messageToTheClientsLayout.setHorizontalGroup(
            messageToTheClientsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, messageToTheClientsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(messageToTheClientsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(msg2user2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE)
                    .addComponent(msg2user, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE))
                .addContainerGap())
        );
        messageToTheClientsLayout.setVerticalGroup(
            messageToTheClientsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, messageToTheClientsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(msg2user, javax.swing.GroupLayout.DEFAULT_SIZE, 22, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(msg2user2, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        yourTurnIsFinishingLabel.setFont(new java.awt.Font("Courier New", 1, 18));
        yourTurnIsFinishingLabel.setForeground(new java.awt.Color(255, 0, 0));
        yourTurnIsFinishingLabel.setText("Acá va lo del turno!! =D");
        yourTurnIsFinishingLabel.setName("yourTurnIsFinishingLabel"); // NOI18N

        offlineLabel.setFont(new java.awt.Font("Courier New", 1, 32));
        offlineLabel.setForeground(new java.awt.Color(255, 0, 0));
        offlineLabel.setText("Fuera de línea");
        offlineLabel.setName("offlineLabel"); // NOI18N

        posID.setFont(new java.awt.Font("Courier New", 1, 14));
        posID.setForeground(new java.awt.Color(51, 51, 255));
        posID.setText("Acá va lo de la caja");
        posID.setName("posID"); // NOI18N

        javax.swing.GroupLayout wallpaperLayout = new javax.swing.GroupLayout(wallpaper);
        wallpaper.setLayout(wallpaperLayout);
        wallpaperLayout.setHorizontalGroup(
            wallpaperLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, wallpaperLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(yourTurnIsFinishingLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 491, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(posID, javax.swing.GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(offlineLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 299, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(wallpaperLayout.createSequentialGroup()
                .addGap(84, 84, 84)
                .addGroup(wallpaperLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(messageToTheClients, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(imagePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(83, 83, 83)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(217, 217, 217))
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        wallpaperLayout.setVerticalGroup(
            wallpaperLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, wallpaperLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(wallpaperLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(offlineLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(wallpaperLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(posID, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(yourTurnIsFinishingLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)))
                .addGap(10, 10, 10)
                .addGroup(wallpaperLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(wallpaperLayout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(imagePanel, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(messageToTheClients, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(wallpaper, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(wallpaper, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    protected void loadItem(String myBarcode){
        try{
            System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Se introdujo el texto " + myBarcode);
            if ( myBarcode.equals("about") ){
                About ab = new About(this, true);
                Shared.centerFrame(ab);
                ab.setVisible(true);
                return;
            }else if ( myBarcode.equals("version") ){
                MessageBox msb = new MessageBox(MessageBox.SGN_IMPORTANT, "Total Pos Version " + Shared.getConfig("version"));
                msb.show(this);
                return;
            }else if ( myBarcode.split(" ")[0].equals("reimprimir") ){
                SellWithoutStock cqi = new SellWithoutStock(this, true, "Reimprimir factura " + myBarcode.split(" ")[1], Shared.getConfig("reprint"));
                Shared.centerFrame(cqi);
                cqi.setVisible(true);
                if ( cqi.authorized ){
                    List<Receipt> receiptMatched = ConnectionDrivers.listThisReceipt(myBarcode.split(" ")[1]);
                    if ( receiptMatched.isEmpty() ){
                        MessageBox msb = new MessageBox(MessageBox.SGN_IMPORTANT, "No se ha conseguido la factura!");
                        msb.show(this);
                    }else if ( receiptMatched.size() == 1){
                        deleteCurrent();
                        clearForm();
                        loadThisReceipt(receiptMatched.get(0));
                        ConnectionDrivers.putToNormal(receiptMatched.get(0).getInternId());
                    }
                }
                return;
            }else if ( myBarcode.split(" ")[0].equals("copiafactura") ) {
                System.out.println("Imprimiendo copia de factura...");
                SellWithoutStock cqi = new SellWithoutStock(this, true, "Imprimir Copia de factura " + myBarcode.split(" ")[1], Shared.getConfig("printnonfiscalcopy"));
                Shared.centerFrame(cqi);
                cqi.setVisible(true);
                if ( cqi.authorized ){
                    List<Receipt> receiptMatched = ConnectionDrivers.listThisReceipt(myBarcode.split(" ")[1]);
                    if ( receiptMatched.isEmpty() ){
                        MessageBox msb = new MessageBox(MessageBox.SGN_IMPORTANT, "No se ha conseguido la factura!");
                        msb.show(this);
                    }else if ( receiptMatched.size() == 1){
                        Receipt r = receiptMatched.get(0);
                        try{
                            printer.printNonFiscalCopyReceipt(r.getFiscalNumber(), r.getPrintingDate());
                        }catch(Exception ex){
                            MessageBox msb = new MessageBox(MessageBox.SGN_IMPORTANT, "Error al imprimir copia", ex);
                            msb.show(this);
                        }
                    }
                }
                return;
            }else if ( myBarcode.split(" ")[0].equals("copianotadecredito") ) {
                SellWithoutStock cqi = new SellWithoutStock(this, true, "Imprimir Copia de devolucion " + myBarcode.split(" ")[1], Shared.getConfig("printnonfiscalcopy"));
                Shared.centerFrame(cqi);
                cqi.setVisible(true);
                if ( cqi.authorized ){
                    List<Receipt> receiptMatched = ConnectionDrivers.listThisCreditNote(myBarcode.split(" ")[1]);
                    if ( receiptMatched.isEmpty() ){
                        MessageBox msb = new MessageBox(MessageBox.SGN_IMPORTANT, "No se ha conseguido la devolucion!");
                        msb.show(this);
                    }else if ( receiptMatched.size() == 1){
                        Receipt r = receiptMatched.get(0);
                        try{
                            printer.printNonFiscalCopyCreditNote(r.getFiscalNumber(), r.getPrintingDate());
                        }catch(Exception ex){
                            MessageBox msb = new MessageBox(MessageBox.SGN_IMPORTANT, "Error al imprimir copia", ex);
                            msb.show(this);
                        }
                    }
                }
                return;
            }else if ( myBarcode.split(" ")[0].equals("getConfig") ){
                MessageBox msb = new MessageBox(MessageBox.SGN_SUCCESS, Shared.getConfig(myBarcode.split(" ")[1]) );
                msb.show(this);
                return;
            }else if ( myBarcode.split(" ")[0].equals("getFileConfig") ){
                MessageBox msb = new MessageBox(MessageBox.SGN_SUCCESS, Shared.getFileConfig(myBarcode.split(" ")[1]) );
                msb.show(this);
                return;
            }
            if ( myBarcode.isEmpty() ){
                MessageBox msb = new MessageBox(MessageBox.SGN_IMPORTANT, "Debe introducir el producto!");
                msb.show(this);
                return;
            }
            List<Item> itemC = ConnectionDrivers.listFastItems(myBarcode);
            if ( itemC.isEmpty() ){
                MessageBox msb = new MessageBox(MessageBox.SGN_IMPORTANT, "Artículo no existe!");
                msb.show(this);
                cleanForNewItem();
                return;
            }
            //TODO FIX IT!!!!!!
            assert( itemC.size() >= 1 );
            if ( itemC.get(0).isStatus() ){
                MessageBox msb = new MessageBox(MessageBox.SGN_IMPORTANT, "Artículo bloqueado. No puede ser facturado!");
                msb.show(this);
                cleanForNewItem();
                return;
            }

            if ( quant != 1 ){
                SellWithoutStock cqi = new SellWithoutStock(this, true, "Cantidad de Producto: ", Shared.getConfig("changeQuant"));
                Shared.centerFrame(cqi);
                cqi.setVisible(true);
                if ( !cqi.authorized ){
                    quant = 1;
                    return;
                }
            }
            if ( itemC.get(0).getCurrentStock() - quant < 0 ){
                if ( Shared.getConfig("sellWithoutStock").equals("0") ){
                    MessageBox msb = new MessageBox(MessageBox.SGN_IMPORTANT, "Este artículo no tiene stock. No se puede agregar.");
                    msb.show(this);
                    cleanForNewItem();
                    return;
                }else{
                    SellWithoutStock sws = new SellWithoutStock(this, true, "Artículo sin stock.","sellWithoutStock");
                    Shared.centerFrame(sws);
                    sws.setVisible(true);
                    if ( !sws.authorized ){
                        return;
                    }
                }
            }
            addItem(itemC.get(0));
            updateCurrentItem();
            cleanForNewItem();
            updateSubTotal();

        } catch (SQLException ex) {
            MessageBox msb = new MessageBox(MessageBox.SGN_IMPORTANT, "Problemas con la base de datos.",ex);
            msb.show(this);
        }
    }

    private void barcodeFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_barcodeFieldKeyPressed

        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Se ha pisado la tecla " + evt.getKeyCode());
        Shared.getScreenSaver().actioned();
        if ( evt.getKeyCode() == KeyEvent.VK_ESCAPE ){
            logout();
            return;
        }else if ( evt.getKeyCode() == KeyEvent.VK_ENTER ){
            String myBarcode = barcodeField.getText();
            barcodeField.setText("");
            loadItem(myBarcode);
        } else if ( evt.getKeyCode() == KeyEvent.VK_DOWN ){
            if ( gridTable.getSelectedRow() == gridTable.getModel().getRowCount() - 1 ){
                return;
            }
            gridTable.setRowSelectionInterval(gridTable.getSelectedRow()+1, gridTable.getSelectedRow()+1);
            Shared.checkVisibility(gridTable);
        } else if ( evt.getKeyCode() == KeyEvent.VK_UP ){
            if ( gridTable.getSelectedRow() <= 0 ){
                return;
            }
            gridTable.setRowSelectionInterval(gridTable.getSelectedRow()-1, gridTable.getSelectedRow()-1);
            Shared.checkVisibility(gridTable);
        }else if ( evt.getKeyCode() == KeyEvent.VK_F2 ){
            if ( items.isEmpty() ){
                return;
            }
            Object[] options = {"Si",
                    "No"};
            int n = JOptionPane.showOptionDialog(this,
                "¿Desea eliminar el artículo?",
                Constants.appName,
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

            if ( n == 0 ){
                deleteItem();
                if ( items.isEmpty() ){
                    try{
                        updateAll();
                    } catch (SQLException ex) {
                        MessageBox msb = new MessageBox(MessageBox.SGN_IMPORTANT, "Problemas con la base de datos.",ex);
                        msb.show(this);
                        this.dispose();
                        Shared.reload();
                    } catch (Exception ex) {
                        MessageBox msb = new MessageBox(MessageBox.SGN_IMPORTANT, "Problemas con la base de datos.",ex);
                        msb.show(this);
                        this.dispose();
                        Shared.reload();
                    }
                }
            }
        } else if ( evt.getKeyCode() == KeyEvent.VK_F10 ){
            if ( items.isEmpty() ){
                return;
            }
            Object[] options = {"Si",
                    "No"};
            int n = JOptionPane.showOptionDialog(this,
                "¿Desea eliminar el pedido?",
                Constants.appName,
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                null);
            if ( n == 0 ){
                deleteCurrent();
                try{
                    updateAll();
                } catch (SQLException ex) {
                    MessageBox msb = new MessageBox(MessageBox.SGN_IMPORTANT, "Problemas con la base de datos.",ex);
                    msb.show(this);
                    this.dispose();
                    Shared.reload();
                } catch (Exception ex) {
                    MessageBox msb = new MessageBox(MessageBox.SGN_IMPORTANT, "Problemas al creando el pedido.",ex);
                    msb.show(this);
                    this.dispose();
                    Shared.reload();
                }
            }
        } else if ( evt.getKeyCode() == KeyEvent.VK_F11 ){
            toWait();
            try{
                updateAll();
            } catch (SQLException ex) {
                MessageBox msb = new MessageBox(MessageBox.SGN_IMPORTANT, "Problemas con la base de datos.",ex);
                msb.show(this);
                this.dispose();
                Shared.reload();
            } catch (Exception ex) {
                MessageBox msb = new MessageBox(MessageBox.SGN_IMPORTANT, "Problemas con la base de datos.",ex);
                msb.show(this);
                this.dispose();
                Shared.reload();
            } 
        } else if ( evt.getKeyCode() == KeyEvent.VK_F12 ){
            ListIdleReceipts lir = new ListIdleReceipts(this, true);
            if ( lir.isOk ){
                try {
                    Shared.centerFrame(lir);
                    lir.setVisible(true);
                    List<Receipt> idleReceipts = ConnectionDrivers.listIdleReceiptToday();
                    if (idleReceipts.isEmpty()) {
                        msg2user.setVisible(false);
                    } else if (idleReceipts.size() == 1) {
                        msg2user.setVisible(true);
                        msg2user.setText("Tiene 1 pedido en espera.");
                    } else {
                        msg2user.setVisible(true);
                        msg2user.setText("Tiene " + idleReceipts.size() + " pedidos en espera.");
                    }
                } catch (SQLException ex) {
                    MessageBox msb = new MessageBox(MessageBox.SGN_DANGER, "Problemas con la base de datos",ex);
                    msb.show(this);
                    this.dispose();
                    Shared.reload();
                }
            }
        } else if ( evt.getKeyCode() == KeyEvent.VK_F8 ){
            ManageClient mc = new ManageClient(this, true, client);
            if ( mc.isOk ){
                Shared.centerFrame(mc);
                mc.setVisible(true);
            }
        } else if ( evt.getKeyCode() == KeyEvent.VK_F9 ){
            if ( items.isEmpty() ){
                return;
            }
            GlobalDiscount gd = new GlobalDiscount(this, true,subtotal);
            Shared.centerFrame(gd);
            gd.setVisible(true);
        } else if ( evt.getKeyCode() == KeyEvent.VK_F5 ){
            if ( !items.isEmpty() ){
                SpecifyPaymentForm sfpf = new SpecifyPaymentForm(this, true, subtotal*(1.0-globalDiscount), actualId);
                Shared.centerFrame(sfpf);
                sfpf.setVisible(true);
            }
        } else if ( evt.getKeyCode() == KeyEvent.VK_F6 ){
            ExtractMoney em = new ExtractMoney(this, true, printer);
            Shared.centerFrame(em);
            em.setVisible(true);
        } else if ( evt.getKeyCode() == KeyEvent.VK_F3 ){
            String id = JOptionPane.showInputDialog(this, "Factura a Devolver", "");
            if ( id != null ){
                try {
                    Receipt r = ConnectionDrivers.getReceiptToDev(id.substring(0, 12));
                    if (r == null) {
                        MessageBox msb = new MessageBox(MessageBox.SGN_CAUTION, "La factura no existe!");
                        msb.show(this);
                    } else if ( r.getGlobalDiscount() != .0 ) {
                        MessageBox msb = new MessageBox(MessageBox.SGN_CAUTION, "La factura tiene descuento global. No puede ser devuelta!");
                        msb.show(this);
                    }else {
                        SellWithoutStock cqi = new SellWithoutStock(this, true, "Crear Nota de Crédito", Shared.getConfig("createCN"));
                        Shared.centerFrame(cqi);
                        cqi.setVisible(true);
                        if ( cqi.authorized ){
                            CreditNoteForm cnf = new CreditNoteForm(this, true, r);
                            Shared.centerFrame(cnf);
                            cnf.setVisible(true);
                        }
                    }
                } catch (SQLException ex) {
                    MessageBox msb = new MessageBox(MessageBox.SGN_DANGER, "Problemas con la base de datos",ex);
                    msb.show(this);
                    this.dispose();
                    Shared.reload();
                }
            }
        } else if ( evt.getKeyCode() == KeyEvent.VK_F1 ){
            /*MessageBox msg = new MessageBox(MessageBox.SGN_IMPORTANT, "Escriba la cantidad, luego el signo \'*\' y finalmente introduzca el código de barras.");
            msg.show(this);*/
            if ( Shared.getConfig("searchByModel").equals("1") ){
                SearchItem si = new SearchItem(this, true);
                if ( si.isOk ){
                    Shared.centerFrame(si);
                    si.setVisible(true);
                }
            }
        } else if ( evt.getKeyCode() == KeyEvent.VK_END ){
            try {
                for (Receipt receipt : ConnectionDrivers.listIdleReceiptToday()) {
                    loadThisReceipt(receipt);
                    deleteCurrent();
                }
                ReportZ rz = new ReportZ(this, true, "Z", "curdate()");
                Shared.centerFrame(rz);
                rz.setVisible(true);
            } catch (SQLException ex) {
                MessageBox msb = new MessageBox(MessageBox.SGN_DANGER, "Problemas con la base de datos",ex);
                msb.show(this);
                this.dispose();
                Shared.reload();
            }
        } else if ( evt.getKeyCode() == KeyEvent.VK_F7 ){
            ReportZ rz = new ReportZ(this, true, "X", "curdate()");
            Shared.centerFrame(rz);
            rz.setVisible(true);
        }
    }//GEN-LAST:event_barcodeFieldKeyPressed

    public void print(List<PayForm> l) throws SQLException, FileNotFoundException, Exception{
        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Imprimir");
        printer.printTicket(items, client, globalDiscount, actualId, user , l);
        ConnectionDrivers.setAllFiscalData(actualId, printer.getSerial() , printer.getZ() , printer.getLastFiscalNumber(), client);
        updateAll();
    }

    private void jPanel6MouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel6MouseMoved
        Shared.getScreenSaver().actioned();
}//GEN-LAST:event_jPanel6MouseMoved

    private void jPanel6KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPanel6KeyPressed
        if ( evt.getKeyCode() == KeyEvent.VK_ESCAPE ){
            logout();
        }
}//GEN-LAST:event_jPanel6KeyPressed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        deleteCurrent();
    }//GEN-LAST:event_formWindowClosing

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        deleteCurrent();
    }//GEN-LAST:event_formWindowClosed

    private void barcodeFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_barcodeFieldKeyReleased
        if ( evt.getKeyCode() == KeyEvent.VK_DOWN ){
            updateCurrentItem();
        } else if ( evt.getKeyCode() == KeyEvent.VK_UP ){
            updateCurrentItem();
        } else if ( evt.getKeyChar() == '*' ) {
            String code = barcodeField.getText();
            code = code.substring(0, code.length()-1);
            try{
                quant = Integer.parseInt(code);
                if ( quant < 1 ){
                    throw new NumberFormatException();
                }
            }catch ( NumberFormatException ex){
                MessageBox msb = new MessageBox(MessageBox.SGN_IMPORTANT, "Cantidad incorrecta!!");
                msb.show(this);
                quant = 1;
            }
            barcodeField.setText("");
        }
    }//GEN-LAST:event_barcodeFieldKeyReleased

    private void updateCurrentItem(){
        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Actualizar item actual");
        if ( gridTable.getSelectedRow() != -1 ){
            Item i = items.get(gridTable.getSelectedRow()).getItem();
            descriptionLabel.setText(i.getDescription());
            currentPrice.setText(i.getLastPrice().toString());
            Shared.loadPhoto(imageLabel, i.getImageAddr(),imagePanel.getWidth()-27,imagePanel.getHeight()-30);
            Shared.msgWithEffect(i.getDescription().substring(0,Math.min(i.getDescription().length(), Integer.parseInt(Shared.getConfig("displaySize")))), quant + " x " + i.getLastPrice().withDiscount(i.getDescuento()).toString() + " = " + Shared.format4Display(i.getLastPrice().withDiscount(i.getDescuento()).getQuant()*(double)quant) );
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel TotalLabelResult;
    private javax.swing.JTextField barcodeField;
    private javax.swing.JLabel currentPrice;
    private javax.swing.JLabel descriptionLabel;
    private javax.swing.JLabel discountLabel;
    private javax.swing.JLabel discountResult;
    private javax.swing.JTable gridTable;
    private javax.swing.JLabel imageLabel;
    public javax.swing.JPanel imagePanel;
    private javax.swing.JLabel ivaLabel;
    private javax.swing.JLabel ivaLabelResult;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel messageToTheClients;
    private javax.swing.JLabel msg2user;
    private javax.swing.JLabel msg2user2;
    private javax.swing.JLabel offlineLabel;
    private javax.swing.JLabel posID;
    private javax.swing.JLabel quantItemField;
    private javax.swing.JLabel quantItemsLabel;
    private javax.swing.JLabel subTotalLabel;
    private javax.swing.JLabel subTotalLabelResult;
    private javax.swing.JLabel totalLabel;
    private javax.swing.JPanel wallpaper;
    public javax.swing.JLabel whatTimeIsIt;
    public javax.swing.JLabel yourTurnIsFinishingLabel;
    // End of variables declaration//GEN-END:variables

    private void logout(){
        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Salir");
        String msg = "Se procederá a cerrar sesión";
        if ( !items.isEmpty() ){
            msg = " El pedido que está cargado será ANULADO!";
        }
        msg += " ¿Está seguro que desea continuar?";

        Object[] options = {"Si","No"};
        int n = JOptionPane.showOptionDialog(this,msg,
                Constants.appName,
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                null);

        if ( n == 0 ){
            deleteCurrent();
            Login l = new Login();
            Shared.centerFrame(l);
            Shared.maximize(l);
            l.setVisible(true);
            Shared.setUser(null);

            setVisible(false);
            dispose();
        }
    }

    private void addItem(Item get) {
        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Agregar articulo " + get.getCode());
        try {
            ConnectionDrivers.addItem2Receipt(actualId, get, quant);
            DefaultTableModel model = (DefaultTableModel) gridTable.getModel();

            String[] s = {get.getDescription(), quant+"", get.getDescuento()+"", Shared.df.format(get.getLastPrice().plusIva().getQuant()*quant), Shared.df.format(get.getLastPrice().plusIva().withDiscount(get.getDescuento()).getQuant()*quant)};
            model.addRow(s);
            gridTable.setRowSelectionInterval(model.getRowCount() - 1, model.getRowCount() - 1);
            gridTable.scrollRectToVisible(gridTable.getCellRect(model.getRowCount()-1, 0, true));
            items.add(new Item2Receipt(get, quant,0,get.getLastPrice().getQuant(),get.getDescuento()));
        } catch (SQLException ex) {
            MessageBox msb = new MessageBox(MessageBox.SGN_IMPORTANT, "Problemas con la base de datos.",ex);
            msb.show(this);
            this.dispose();
            Shared.reload();
        } catch (Exception ex) {
            MessageBox msb = new MessageBox(MessageBox.SGN_IMPORTANT, "Problemas al agregar el artículo",ex);
            msb.show(this);
            this.dispose();
            Shared.reload();
        }
        
    }

    private void cleanForNewItem(){
        quant = 1;
        barcodeField.setText("");
    }

    public void updateSubTotal() {
        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Actualizar subtotal");
        double subT = .0 , ivaT = .0 , total = .0 , subTwithoutD = .0;
        for (Item2Receipt item2r : items) {
            Item item = item2r.getItem();
            subTwithoutD += item.getLastPrice().withDiscount(item.getDescuento()).getQuant()*item2r.getQuant();
            System.out.println("Articulo vale " + item.getLastPrice().withDiscount(item.getDescuento()).getQuant());
            System.out.println("Articulo Precio " + item.getLastPrice().getQuant());
            System.out.println("Articulo Descuento " + item.getDescuento());
            subT += Shared.round( item.getLastPrice().withDiscount(item.getDescuento()).getQuant()*(1.0-globalDiscount) , 2 )*item2r.getQuant();
        }

        int quantItems = 0;
        for (Item2Receipt item2Receipt : items) {
            quantItems += item2Receipt.getQuant();
        }
        quantItemField.setText(quantItems+"");
        subTotalLabelResult.setText(Shared.df.format(subTwithoutD) + " Bs");
        if ( !globalDiscount.equals(.0) ){
            discountLabel.setText("Desc (" + Shared.df.format(globalDiscount*100.0) + "%):");
            discountLabel.setVisible(true);
            discountResult.setText(Shared.df.format(subT) + " Bs");
            discountResult.setVisible(true);
        }else{
            discountLabel.setVisible(false);
            discountResult.setVisible(false);
        }
        subtotal = subTwithoutD;

        ivaT = new Price(null, subT).getIva().getQuant();
        total = Math.round(subT + ivaT)+.0;

        ivaLabelResult.setText(Shared.df.format(ivaT) + " Bs");
        TotalLabelResult.setText(Shared.df.format(total) + " Bs");
    }

    private void deleteItem() {
        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Eliminar articulo");
        if ( gridTable.getSelectedRow() != -1){
            try {
                ConnectionDrivers.deleteItem2Receipt(actualId, items.get(gridTable.getSelectedRow()).getItem(), items.get(gridTable.getSelectedRow()).getQuant());

                items.remove(gridTable.getSelectedRow());
                DefaultTableModel model = (DefaultTableModel) gridTable.getModel();
                model.removeRow(gridTable.getSelectedRow());
                if (items.isEmpty()) {
                    try {
                        ConnectionDrivers.cancelReceipt(actualId);
                        //MessageBox msb = new MessageBox(MessageBox.SGN_SUCCESS, "Pedido anulado.");
                        //msb.show(this);
                        //That msg might be annoying...
                    } catch (SQLException ex) {
                        MessageBox msb = new MessageBox(MessageBox.SGN_DANGER, "Problemas con la base de datos.",ex);
                        msb.show(this);
                        this.dispose();
                        Shared.reload();
                    }
                } else {
                    gridTable.setRowSelectionInterval(model.getRowCount() - 1, model.getRowCount() - 1);
                    updateCurrentItem();
                    updateSubTotal();
                    cleanForNewItem();
                }
            } catch (SQLException ex) {
                MessageBox msb = new MessageBox(MessageBox.SGN_DANGER, "Problemas con la base de datos.",ex);
                msb.show(this);
                this.dispose();
                Shared.reload();
            } catch (Exception ex) {
                MessageBox msb = new MessageBox(MessageBox.SGN_DANGER, "Problemas al eliminar artículo de la factura.",ex);
                msb.show(this);
                this.dispose();
                Shared.reload();
            }
        }else{
            MessageBox msb = new MessageBox(MessageBox.SGN_IMPORTANT, "Debe seleccionar un artículo.");
            msb.show(this);
        }
    }

    public void toWait() {
        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Pedido puesto en espera");
        try {
            if ( !items.isEmpty() ){
                ConnectionDrivers.putToIdle(actualId);
                MessageBox msb = new MessageBox(MessageBox.SGN_SUCCESS, "Pedido puesto en espera.");
                msb.show(this);
            }
        } catch (SQLException ex) {
            MessageBox msb = new MessageBox(MessageBox.SGN_DANGER, "Problemas con la base de datos.",ex);
            msb.show(this);
            this.dispose();
            Shared.reload();
        }
    }

    public void loadThisReceipt(Receipt r) throws SQLException{
        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Cargar el recibo " + r.getInternId());
        if ( !r.getItems().isEmpty() ){
            if ( r.getInternId().isEmpty() ){
                actualId = r.getAlternativeID();
            }else{
                actualId = r.getInternId();
            }
            DefaultTableModel model = (DefaultTableModel) gridTable.getModel();

            for (Item2Receipt item2r : r.getItems()) {
                Item item = item2r.getItem();
                String[] s = {item.getDescription(), item2r.getQuant()+"", item.getDescuento()+"", item.getLastPrice().toString(), item.getLastPrice().getIva().toString(), item.getLastPrice().plusIva().toString()};
                model.addRow(s);
                items.add(item2r);
            }
            gridTable.setRowSelectionInterval(model.getRowCount() - 1, model.getRowCount() - 1);
            this.globalDiscount = r.getGlobalDiscount();
            updateCurrentItem();
            updateSubTotal();
        }
    }

    public void deleteCurrent(){
        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Eliminar recibo actual");
        try {
            ConnectionDrivers.cancelReceipt(actualId);
            if ( items != null ){
                for( int i = 0 ; i < items.size() ; i++ ){
                    ConnectionDrivers.deleteItem2Receipt(actualId, items.get(i).getItem(), items.get(i).getQuant());
                }
            }
        } catch (Exception ex) {
            // Nothing to do... We are fucked. =( 
        }
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setGlobalDiscount(Double d) throws SQLException{
        this.globalDiscount = d;
        updateSubTotal();
        ConnectionDrivers.setGlobalDiscount(actualId, d);
    }

    private TexturePaint receiptBottom() {
        BufferedImage bi = null;
        try{
            bi = ImageIO.read(getClass().getResource("/totalpos/resources/Factura.jpg"));
        }catch(java.io.IOException ioe) {
            assert(false);
        }
        return new TexturePaint(bi, new Rectangle(bi.getWidth(),bi.getHeight()));
    }

    public Assign getAssign() {
        return assign;
    }

    public int getMsgIndex() {
        return msgIndex;
    }

    public void setMsgIndex(int msgIndex) {
        this.msgIndex = msgIndex;
    }

    public void increaseShiftValue(){
        if ( !msg2user2.getText().isEmpty() ){
            String s = msg2user2.getText();
            msg2user2.setText(s.substring(1) + s.substring(0,1));
        }
    }

    void updateMsg() {
        msg2user2.setText(msg2pos.get(msgIndex));
    }

}
