/*
 * ClosingDay.java
 *
 * Created on 26-ago-2011, 15:37:34
 */

package totalpos;

import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import net.n3.nanoxml.IXMLElement;
import net.n3.nanoxml.XMLElement;
import net.n3.nanoxml.XMLWriter;
import srvSap.ObjectFactory;
import srvSap.ZFISCOBRANZA;
import webservice.TotalPosWebService;
import webservice.TotalPosWebServiceService;

/**
 *
 * @author Saúl Hidalgo
 */
public class ClosingDay extends javax.swing.JInternalFrame implements Doer{

    List<Expense> expenses;
    List<Deposit> deposits;
    Double totalInCard = .0;
    Double totalInCash = .0;
    Double totalExpenses = .0;
    Double totalCN = .0;
    private ObjectFactory of = Constants.of;
    protected Working workingFrame;
    private String myDay = "";
    protected boolean isOk = false;
    private String date4sap = "";
    private Double receiptTotal;
    List<String> clients = new LinkedList<String>();

    private String getExpensesTable() {
        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Creando tabla de gastos para email");
        String ans = "";
        for( int i = 0 ; i < expenseTable.getRowCount() ; i++ ){
            ans += "<tr>";
            if ( i%2 == 0 ){
                ans += "<td bordercolor=\"#C1F2FF\" style=\"background-color:#C1F2FF\">";
            }else{
                ans += "<td>";
            }
            ans += ((String)expenseTable.getValueAt(i, 0)).split("-")[1].trim();
            if ( i%2 == 0 ){
                ans += "<td bordercolor=\"#C1F2FF\" style=\"background-color:#C1F2FF\">";
            }else{
                ans += "<td>";
            }
            ans += ((String)expenseTable.getValueAt(i, 1));
            ans += "</td>";
            if ( i%2 == 0 ){
                ans += "<td bordercolor=\"#C1F2FF\" style=\"background-color:#C1F2FF\">";
            }else{
                ans += "<td>";
            }
            ans += ((String)expenseTable.getValueAt(i, 2));
            ans += "</td>";
            ans += "</tr>";
        }
        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Tabla creada satisfactoriamente");
        return ans;
    }

    private String getNegativeStock(List<Item> l) throws SQLException {
        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Creando tabla para articulos en negativo en email");
        String ans = "";
        for( int i = 0 ; i < l.size() ; i++ ){
            Item item = l.get(i);
            ans += "<tr>";
            if ( i%2 == 0 ){
                ans += "<td bordercolor=\"#C1F2FF\" style=\"background-color:#C1F2FF\">";
            }else{
                ans += "<td>";
            }
            ans += item.getCode();
            if ( i%2 == 0 ){
                ans += "<td bordercolor=\"#C1F2FF\" style=\"background-color:#C1F2FF\">";
            }else{
                ans += "<td>";
            }
            ans += item.getDescription();
            ans += "</td>";
            if ( i%2 == 0 ){
                ans += "<td bordercolor=\"#C1F2FF\" style=\"background-color:#C1F2FF\">";
            }else{
                ans += "<td>";
            }
            ans += item.getCurrentStock();
            ans += "</td>";
            ans += "</tr>";
        }
        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Tabla creada satisfactoriamente");
        return ans;
    }

    private String getPaymentsTable() {
        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Creando tabla de formas de pago para email");
        String ans = "";
        for( int i = 0 ; i < bankTable.getRowCount() ; i++ ){
            ans += "<tr>";
            if ( i%2 == 0 ){
                ans += "<td bordercolor=\"#C1F2FF\" style=\"background-color:#C1F2FF\">";
            }else{
                ans += "<td>";
            }
            ans += ((String)bankTable.getValueAt(i, 2));
            ans += "</td>";
            if ( i%2 == 0 ){
                ans += "<td bordercolor=\"#C1F2FF\" style=\"background-color:#C1F2FF\">";
            }else{
                ans += "<td>";
            }
            ans += ((String)bankTable.getValueAt(i, 0)).split("-")[1].trim();
            ans += "</td>";
            if ( i%2 == 0 ){
                ans += "<td bordercolor=\"#C1F2FF\" style=\"background-color:#C1F2FF\">";
            }else{
                ans += "<td>";
            }
            ans += ((String)bankTable.getValueAt(i, 1));
            ans += "</td>";
            if ( i%2 == 0 ){
                ans += "<td bordercolor=\"#C1F2FF\" style=\"background-color:#C1F2FF\">";
            }else{
                ans += "<td>";
            }
            ans += (bankTable.getValueAt(i, 4))+"";
            ans += "</td>";
            ans += "</tr>";
        }

        int j = bankTable.getRowCount();
        for( int i = 0 ; i < depositTable.getRowCount() ; i++ ){
            ans += "<tr>";
            if ( (j+i)%2 == 0 ){
                ans += "<td bordercolor=\"#C1F2FF\" style=\"background-color:#C1F2FF\">";
            }else{
                ans += "<td>";
            }
            ans += "Efectivo";
            ans += "</td>";
            if ( (j+i)%2 == 0 ){
                ans += "<td bordercolor=\"#C1F2FF\" style=\"background-color:#C1F2FF\">";
            }else{
                ans += "<td>";
            }
            ans += ((String)depositTable.getValueAt(i, 0)).split("-")[1].trim();
            ans += "</td>";
            if ( (j+i)%2 == 0 ){
                ans += "<td bordercolor=\"#C1F2FF\" style=\"background-color:#C1F2FF\">";
            }else{
                ans += "<td>";
            }
            ans += ((String)depositTable.getValueAt(i, 1));
            ans += "</td>";
            if ( (j+i)%2 == 0 ){
                ans += "<td bordercolor=\"#C1F2FF\" style=\"background-color:#C1F2FF\">";
            }else{
                ans += "<td>";
            }
            ans += ((String)depositTable.getValueAt(i, 2));
            ans += "</td>";
            ans += "</tr>";
        }
        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Tabla creada satisfactoriamente");
        return ans;
    }

    private String getZTable() throws SQLException {
        return ConnectionDrivers.getOperativeDaysHtml(myDay);
    }

    static class DecimalFormatRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if ( column == 4 ){
                if ( value instanceof String ){
                    System.out.println(value);
                    System.out.println(row + " _ " + column);
                }
                if ( value == null ){
                    value = Shared.df.format(.0);
                }else{
                    value = Shared.df.format((Double)value);
                }
            }

            return super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column );
        }
    }

    /** Creates new form ClosingDay
     * @param day
     * @param sr
     */
    protected ClosingDay(String day , boolean sr) {
        try {

            /*if ( Shared.numberClosingDayOpened > 0 ){
                MessageBox msg = new MessageBox(MessageBox.SGN_CAUTION, "Solo debe haber 1 ventana de cierre administrativo abierta!");
                msg.show(Shared.getMyMainWindows());
                return;
            }*/

            System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Invocando constructor de cierre administrativo");
            initComponents();
            myDay = day;
            System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Dia Elegido " + myDay);
            date4sap = myDay.replace("-", "");
            String[] dayA = myDay.split("-");
            this.setTitle("Cierre Administrativo - Fecha " + dayA[2] + "/" + dayA[1] + "/" + dayA[0]);

            if ( sr && !ConnectionDrivers.previousClosed(myDay) ){
                System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " No se han realizado cierres anteriores");
                MessageBox msg = new MessageBox(MessageBox.SGN_CAUTION, "No se le ha realizado el cierre administrativo a días anteriores. No se puede continuar");
                msg.show(Shared.getMyMainWindows());
                return;
            }

            DefaultTableModel model = (DefaultTableModel) bankTable.getModel();
            model.setRowCount(0);

            JComboBox jcb = new JComboBox();
            jcb.addItem(Shared.getConfig("creditPaymentName"));
            jcb.addItem(Shared.getConfig("debitPaymentName"));
            jcb.addItem(Shared.getConfig("americanExpressPaymentName"));

            DefaultTableCellRenderer renderer =
                    new DefaultTableCellRenderer();
            renderer.setToolTipText("Click para ver las opciones");
            TableColumn conceptColumn = bankTable.getColumnModel().getColumn(2);
            conceptColumn.setCellRenderer(renderer);
            conceptColumn.setCellEditor(new DefaultCellEditor(jcb));
            bankTable.getColumnModel().getColumn(4).setCellRenderer(new DecimalFormatRenderer() );

            jcb = new JComboBox();
            for (BankPOS bankPOS : ConnectionDrivers.listBPos()) {
                jcb.addItem(bankPOS.getId() + " - " + bankPOS.getDescripcion());
            }
            conceptColumn = bankTable.getColumnModel().getColumn(0);
            conceptColumn.setCellRenderer(renderer);
            conceptColumn.setCellEditor(new DefaultCellEditor(jcb));
            updateAll();

            if ( ConnectionDrivers.wasFlagC(day) ){
                System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Detectado que este dia fue compensado. Deshabilitando opciones para guardar");
                saveDeposit.setEnabled(false);
                saveExpense.setEnabled(false);
                saveNewBanks.setEnabled(false);
                printAndSendButton.setEnabled(false);
                updateButton.setEnabled(false);
                addDeposit.setEnabled(false);
                deleteDeposit.setEnabled(false);
                addExpense.setEnabled(false);
                deleteExpense.setEnabled(false);
                addDeposit1.setEnabled(false);
                deleteNewBanks.setEnabled(false);
                MessageBox msg = new MessageBox(MessageBox.SGN_IMPORTANT, "Este día ha sido compensado!");
                msg.show(this);
            }else if ( sr &&  ConnectionDrivers.wasClosed(day) ){
                System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Dia cerrado anteriormente");
                MessageBox msg = new MessageBox(MessageBox.SGN_IMPORTANT, "Este día ha sido cerrado anteriormente!");
                msg.show(this);
            }

            //ConnectionDrivers.lockClosingDay(c);
            ++Shared.numberClosingDayOpened;
            isOk = true;
        } catch (SQLException ex) {
            MessageBox msg = new MessageBox(MessageBox.SGN_DANGER, "Problemas con la base de datos.", ex);
            msg.show(null);
            this.dispose();
            Shared.reload();
        }
    }

    private void updateDeposits() throws SQLException{
        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Actualizando depositos");
        deposits = ConnectionDrivers.listDeposits(myDay);
        DefaultTableModel model = (DefaultTableModel) depositTable.getModel();
        model.setRowCount(0);

        String ex = Shared.getConfig("banks");
        String allConcepts = ex.substring(1, ex.length()-1);
        Scanner sc = new Scanner(allConcepts);
        sc.useDelimiter("\\}\\{");

        JComboBox jcb = new JComboBox();
        while(sc.hasNext()){
            jcb.addItem(sc.next());
        }

        DefaultTableCellRenderer renderer =
                new DefaultTableCellRenderer();
        renderer.setToolTipText("Click para ver las opciones");
        TableColumn conceptColumn = depositTable.getColumnModel().getColumn(0);
        conceptColumn.setCellRenderer(renderer);
        conceptColumn.setCellEditor(new DefaultCellEditor(jcb));

        for (Deposit e : deposits) {
            String[] s = {e.getBank(),e.getFormId(),Shared.df.format(e.getQuant())};
            model.addRow(s);
        }
        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Depositos actualizados");
    }

    private void updateExpense() throws SQLException{
        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Actualizando gastos");
        expenses = ConnectionDrivers.listExpenses(myDay);
        DefaultTableModel model = (DefaultTableModel) expenseTable.getModel();
        model.setRowCount(0);

        String ex = Shared.getConfig("expenses");
        String allConcepts = ex.substring(1, ex.length()-1);
        Scanner sc = new Scanner(allConcepts);
        sc.useDelimiter("\\}\\{");

        JComboBox jcb = new JComboBox();
        while(sc.hasNext()){
            jcb.addItem(sc.next());
        }

        DefaultTableCellRenderer renderer =
                new DefaultTableCellRenderer();
        renderer.setToolTipText("Click para ver las opciones");
        TableColumn conceptColumn = expenseTable.getColumnModel().getColumn(0);
        conceptColumn.setCellRenderer(renderer);
        conceptColumn.setCellEditor(new DefaultCellEditor(jcb));

        for (Expense e : expenses) {
            String[] s = {e.getConcept(),Shared.df.format(e.getQuant()),e.getDescription()};
            model.addRow(s);
        }
        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Gastos actualizados satisfactoriamente");
    }

    protected void updatePayFormWaysxPoses() throws SQLException{
        ConnectionDrivers.listFormWayXPos((DefaultTableModel) formWayxPoses.getModel(), myDay);
    }

    protected void updateFiscalZ() throws SQLException{
        ConnectionDrivers.listFiscalZ((DefaultTableModel) fiscalZ.getModel(), myDay);
    }

    protected void updatePayWayxPosesDetails() throws SQLException{
        ConnectionDrivers.listFormWayXPosesDetail((DefaultTableModel) payWayxPosTable.getModel(), myDay);
    }

    protected void updateBankTable() throws SQLException{
        ConnectionDrivers.listBankTable((DefaultTableModel) bankTable.getModel(),myDay);
    }

    private void updateAll() throws SQLException{
        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Actualizando todo...");
        Shared.getScreenSaver().actioned();
        updateBankTable();
        receiptTotal = ConnectionDrivers.getSumTotalWithIva(myDay,"factura","Facturada", true, null) - ConnectionDrivers.getSumTotalWithIva(myDay,"nota_de_credito","Nota",false,null);
        Double totalDeclared = ConnectionDrivers.getTotalDeclared(myDay);
        updateDeposits();
        updateExpense();
        updatePayFormWaysxPoses();
        updateFiscalZ();
        updatePayWayxPosesDetails();
        Double expensesD = ConnectionDrivers.getExpenses(myDay);

        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Calculando Totales");
        totalCardsField.setText(Shared.df.format(totalInCard = ConnectionDrivers.getTotalCards(myDay)));
        totalCashField.setText(Shared.df.format(totalInCash = ConnectionDrivers.getTotalCash(myDay)));
        payWithCreditNoteField.setText( Shared.df.format( ConnectionDrivers.getTotalPCN(myDay) ));
        creditNoteField.setText(Shared.df.format(totalCN = (ConnectionDrivers.getTotalCN(myDay)*(Shared.getIva()+100.0)/100.0)));
        totalTotalField.setText(Shared.df.format(totalInCard + totalInCash));
        expensesTodayField.setText( Shared.df.format(totalExpenses = expensesD) );

        double emdf = receiptTotal*(Shared.getIva()+100.0)/100.0;
        double tdf = totalDeclared*(Shared.getIva()+100.0)/100.0;

        if ( Math.abs(emdf - tdf) > Double.parseDouble(Shared.getConfig("moneyExilon")) ){
            expensesMinusDeclaredField.setForeground(new Color(255, 51, 51));
            totalDeclaredField.setForeground(new Color(255, 51, 51));
        }

        expensesMinusDeclaredField.setText(Shared.df.format(emdf));
        totalDeclaredField.setText(Shared.df.format(tdf));

        double n = (receiptTotal*(Shared.getIva()+100.0)/100.0 - totalInCard - totalInCash-expensesD);
        if ( Math.abs(n) < Double.parseDouble(Shared.getConfig("moneyExilon") ) ){
            totalField.setForeground(new Color(51, 255, 51));
        }else if ( n > 0 ){
            totalField.setForeground(new Color(255, 51, 51));
        }else{
            totalField.setForeground(new Color(51, 51, 255));
        }

        totalField.setText(Shared.df.format(n));
        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Actualizado todo satisfactoriamente");
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFileChooser1 = new javax.swing.JFileChooser();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        fiscalZ = new javax.swing.JTable(){
            @Override public Component prepareRenderer(TableCellRenderer renderer, int row, int column){
                Component comp = super.prepareRenderer(renderer, row, column);
                boolean closed = false;
                try{
                    closed = (Boolean)getValueAt(row, 4);
                }catch(Exception ex){
                    ;
                }
                if ( fiscalZ.getSelectedRow() == row ){
                    if ( closed ){
                        comp.setBackground(Constants.lightGreen);
                    }else{
                        comp.setBackground(Constants.lightBlue);
                    }
                } else if ( closed ){
                    comp.setBackground(Color.GREEN);
                }else{
                    comp.setBackground(Constants.transparent);
                }
                return comp;
            }
        };
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        depositTable = new javax.swing.JTable();
        saveDeposit = new javax.swing.JButton();
        deleteDeposit = new javax.swing.JButton();
        addDeposit = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        bankTable = new javax.swing.JTable();
        addDeposit1 = new javax.swing.JButton();
        deleteNewBanks = new javax.swing.JButton();
        saveNewBanks = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        expenseTable = new javax.swing.JTable();
        saveExpense = new javax.swing.JButton();
        addExpense = new javax.swing.JButton();
        deleteExpense = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        formWayxPoses = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        payWayxPosTable = new javax.swing.JTable();
        jPanel7 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        totalCardsField = new javax.swing.JTextField();
        totalCashField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        totalTotalField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        expensesMinusDeclaredField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        totalDeclaredField = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        expensesTodayField = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        cnLabel = new javax.swing.JLabel();
        creditNoteField = new javax.swing.JTextField();
        cancelButton = new javax.swing.JButton();
        printAndSendButton = new javax.swing.JButton();
        totalField = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        updateButton = new javax.swing.JButton();
        payWithCreditNoteField = new javax.swing.JTextField();
        payWithCN = new javax.swing.JLabel();
        noteField = new javax.swing.JTextField();
        noteLabel = new javax.swing.JLabel();

        jFileChooser1.setName("jFileChooser1"); // NOI18N

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Día Operativo");
        setVisible(true);
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosing(evt);
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
        });
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                formMouseMoved(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Zetas Fiscales"));
        jPanel2.setName("jPanel2"); // NOI18N

        jScrollPane6.setName("jScrollPane6"); // NOI18N

        fiscalZ.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Caja", "Serial", "Neto Ventas", "Neto Fiscal", "Cerrado"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        fiscalZ.setName("fiscalZ"); // NOI18N
        fiscalZ.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        fiscalZ.getTableHeader().setReorderingAllowed(false);
        fiscalZ.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                fiscalZMouseMoved(evt);
            }
        });
        fiscalZ.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fiscalZFocusGained(evt);
            }
        });
        jScrollPane6.setViewportView(fiscalZ);
        fiscalZ.getColumnModel().getColumn(0).setPreferredWidth(40);
        fiscalZ.getColumnModel().getColumn(2).setPreferredWidth(120);
        fiscalZ.getColumnModel().getColumn(3).setPreferredWidth(120);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Depósito Efectivo"));
        jPanel3.setName("jPanel3"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        depositTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Banco", "Numero", "Monto"
            }
        ));
        depositTable.setName("depositTable"); // NOI18N
        depositTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        depositTable.getTableHeader().setReorderingAllowed(false);
        depositTable.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                depositTableMouseMoved(evt);
            }
        });
        jScrollPane1.setViewportView(depositTable);

        saveDeposit.setText("Guardar");
        saveDeposit.setName("saveDeposit"); // NOI18N
        saveDeposit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveDepositActionPerformed(evt);
            }
        });

        deleteDeposit.setText("Eliminar");
        deleteDeposit.setName("deleteDeposit"); // NOI18N
        deleteDeposit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteDepositActionPerformed(evt);
            }
        });

        addDeposit.setText("Agregar");
        addDeposit.setName("addDeposit"); // NOI18N
        addDeposit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addDepositActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 287, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(addDeposit, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteDeposit, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(saveDeposit, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(saveDeposit)
                    .addComponent(deleteDeposit)
                    .addComponent(addDeposit))
                .addContainerGap())
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Depositos de Punto de venta de Bancos"));
        jPanel4.setName("jPanel4"); // NOI18N

        jScrollPane5.setName("jScrollPane5"); // NOI18N

        bankTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Banco", "Lote", "Medio", "Declarado", "Monto Real"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                true, true, true, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        bankTable.setName("bankTable"); // NOI18N
        bankTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        bankTable.getTableHeader().setReorderingAllowed(false);
        bankTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                bankTableMouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                bankTableMouseReleased(evt);
            }
        });
        bankTable.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                bankTableMouseMoved(evt);
            }
        });
        bankTable.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                bankTableFocusGained(evt);
            }
        });
        jScrollPane5.setViewportView(bankTable);

        addDeposit1.setText("Agregar");
        addDeposit1.setName("addDeposit1"); // NOI18N
        addDeposit1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addDeposit1ActionPerformed(evt);
            }
        });

        deleteNewBanks.setText("Eliminar");
        deleteNewBanks.setName("deleteNewBanks"); // NOI18N
        deleteNewBanks.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteNewBanksActionPerformed(evt);
            }
        });

        saveNewBanks.setText("Guardar");
        saveNewBanks.setName("saveNewBanks"); // NOI18N
        saveNewBanks.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveNewBanksActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 533, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(addDeposit1, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteNewBanks, javax.swing.GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(saveNewBanks, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addDeposit1)
                    .addComponent(deleteNewBanks)
                    .addComponent(saveNewBanks))
                .addContainerGap())
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Gastos"));
        jPanel5.setName("jPanel5"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        expenseTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Concepto", "Monto", "Descripción"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        expenseTable.setName("expenseTable"); // NOI18N
        expenseTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        expenseTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(expenseTable);
        expenseTable.getColumnModel().getColumn(0).setPreferredWidth(90);
        expenseTable.getColumnModel().getColumn(1).setPreferredWidth(10);

        saveExpense.setText("Guardar");
        saveExpense.setName("saveExpense"); // NOI18N
        saveExpense.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveExpenseActionPerformed(evt);
            }
        });

        addExpense.setText("Agregar");
        addExpense.setName("addExpense"); // NOI18N
        addExpense.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addExpenseActionPerformed(evt);
            }
        });

        deleteExpense.setText("Eliminar");
        deleteExpense.setName("deleteExpense"); // NOI18N
        deleteExpense.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteExpenseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(addExpense)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteExpense, javax.swing.GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(saveExpense)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(saveExpense)
                    .addComponent(addExpense)
                    .addComponent(deleteExpense))
                .addContainerGap())
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Resumen de Medios de Pago x Cajero"));
        jPanel6.setName("jPanel6"); // NOI18N

        jScrollPane4.setName("jScrollPane4"); // NOI18N

        formWayxPoses.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Cajero", "Efectivo", "Tarjeta", "NC", "Mov", "Mov+Retiro"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        formWayxPoses.setName("formWayxPoses"); // NOI18N
        formWayxPoses.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        formWayxPoses.getTableHeader().setReorderingAllowed(false);
        formWayxPoses.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                formWayxPosesMouseMoved(evt);
            }
        });
        formWayxPoses.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                formWayxPosesFocusGained(evt);
            }
        });
        jScrollPane4.setViewportView(formWayxPoses);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 365, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Medios de Pago x Cajero"));
        jPanel1.setName("jPanel1"); // NOI18N

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        payWayxPosTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Cajero", "Medio", "Registrado"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        payWayxPosTable.setName("payWayxPosTable"); // NOI18N
        payWayxPosTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        payWayxPosTable.getTableHeader().setReorderingAllowed(false);
        payWayxPosTable.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                payWayxPosTableFocusGained(evt);
            }
        });
        jScrollPane3.setViewportView(payWayxPosTable);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("Resumen"));
        jPanel7.setName("jPanel7"); // NOI18N

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/totalpos/resources/Etiquetas.jpg"))); // NOI18N
        jLabel1.setText("Pagos Tarjeta");
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/totalpos/resources/Etiquetas.jpg"))); // NOI18N
        jLabel2.setText("Depósitos");
        jLabel2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel2.setName("jLabel2"); // NOI18N

        totalCardsField.setText("Falta");
        totalCardsField.setFocusable(false);
        totalCardsField.setName("totalCardsField"); // NOI18N

        totalCashField.setText("Falta");
        totalCashField.setFocusable(false);
        totalCashField.setName("totalCashField"); // NOI18N

        jLabel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel3.setName("jLabel3"); // NOI18N

        totalTotalField.setText("Falta");
        totalTotalField.setFocusable(false);
        totalTotalField.setName("totalTotalField"); // NOI18N

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/totalpos/resources/Etiquetas.jpg"))); // NOI18N
        jLabel4.setText("Total Cobro");
        jLabel4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel4.setName("jLabel4"); // NOI18N

        expensesMinusDeclaredField.setText("Falta");
        expensesMinusDeclaredField.setFocusable(false);
        expensesMinusDeclaredField.setName("expensesMinusDeclaredField"); // NOI18N

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/totalpos/resources/Etiquetas.jpg"))); // NOI18N
        jLabel5.setText("Facturado");
        jLabel5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel5.setName("jLabel5"); // NOI18N

        totalDeclaredField.setText("Falta");
        totalDeclaredField.setFocusable(false);
        totalDeclaredField.setName("totalDeclaredField"); // NOI18N

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/totalpos/resources/Etiquetas.jpg"))); // NOI18N
        jLabel6.setText("Declarado");
        jLabel6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel6.setName("jLabel6"); // NOI18N

        expensesTodayField.setText("Falta");
        expensesTodayField.setFocusable(false);
        expensesTodayField.setName("expensesTodayField"); // NOI18N

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/totalpos/resources/Etiquetas.jpg"))); // NOI18N
        jLabel7.setText("Gastos");
        jLabel7.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel7.setName("jLabel7"); // NOI18N

        jLabel10.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel10.setName("jLabel10"); // NOI18N

        cnLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/totalpos/resources/Etiquetas.jpg"))); // NOI18N
        cnLabel.setText("Monto NC");
        cnLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cnLabel.setName("cnLabel"); // NOI18N

        creditNoteField.setText("Falta");
        creditNoteField.setFocusable(false);
        creditNoteField.setName("creditNoteField"); // NOI18N

        cancelButton.setText("Cancelar");
        cancelButton.setFocusable(false);
        cancelButton.setName("cancelButton"); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        printAndSendButton.setText("Imprimir y Enviar");
        printAndSendButton.setFocusable(false);
        printAndSendButton.setName("printAndSendButton"); // NOI18N
        printAndSendButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printAndSendButtonActionPerformed(evt);
            }
        });

        totalField.setText("Falta");
        totalField.setFocusable(false);
        totalField.setName("totalField"); // NOI18N

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/totalpos/resources/Etiquetas.jpg"))); // NOI18N
        jLabel11.setText("Falta(+)/Sobra(-)");
        jLabel11.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel11.setName("jLabel11"); // NOI18N

        updateButton.setText("Recalcular Todo");
        updateButton.setName("updateButton"); // NOI18N
        updateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateButtonActionPerformed(evt);
            }
        });

        payWithCreditNoteField.setText("Falta");
        payWithCreditNoteField.setFocusable(false);
        payWithCreditNoteField.setName("payWithCreditNoteField"); // NOI18N

        payWithCN.setIcon(new javax.swing.ImageIcon(getClass().getResource("/totalpos/resources/Etiquetas.jpg"))); // NOI18N
        payWithCN.setText("Pagos con NC");
        payWithCN.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        payWithCN.setName("payWithCN"); // NOI18N

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                                .addComponent(totalCardsField, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(totalCashField, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addComponent(printAndSendButton, javax.swing.GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addComponent(updateButton, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(payWithCN, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(payWithCreditNoteField, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(totalTotalField, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(expensesMinusDeclaredField, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(totalDeclaredField, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(expensesTodayField, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(cnLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(creditNoteField, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(totalField, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(totalCardsField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(totalCashField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)))
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(payWithCreditNoteField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(payWithCN))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(totalTotalField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(expensesMinusDeclaredField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(totalDeclaredField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(expensesTodayField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(creditNoteField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cnLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(totalField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 24, Short.MAX_VALUE)
                .addComponent(updateButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(printAndSendButton))
                .addContainerGap())
        );

        noteField.setName("noteField"); // NOI18N

        noteLabel.setText("Observaciones:");
        noteLabel.setName("noteLabel"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(noteLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(noteField, javax.swing.GroupLayout.DEFAULT_SIZE, 883, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(noteField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(noteLabel))))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void fiscalZFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_fiscalZFocusGained
        
    }//GEN-LAST:event_fiscalZFocusGained

    private void payWayxPosTableFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_payWayxPosTableFocusGained

    }//GEN-LAST:event_payWayxPosTableFocusGained

    private void bankTableFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_bankTableFocusGained

    }//GEN-LAST:event_bankTableFocusGained

    private void formWayxPosesFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formWayxPosesFocusGained

    }//GEN-LAST:event_formWayxPosesFocusGained

    private void saveDepositActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveDepositActionPerformed
        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Guardando depositos");
        DefaultTableModel model = (DefaultTableModel) depositTable.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            try{
                for (int j = 0; j < 3; j++) {
                    if ( model.getValueAt(i, j) == null || ((String)model.getValueAt(i, j)).isEmpty() ){
                        MessageBox msg = new MessageBox(MessageBox.SGN_CAUTION, "Todos los campos son obligatorios!");
                        msg.show(this);
                    }
                }
                Double.parseDouble(((String) model.getValueAt(i, 2)).replace(',', '.'));
            }catch (NumberFormatException ex){
                MessageBox msg = new MessageBox(MessageBox.SGN_CAUTION, "El monto es inválido. Debe corregirse!");
                msg.show(this);
                return;
            }
        }
        try {
            ConnectionDrivers.deleteAllDeposits(myDay);
            ConnectionDrivers.createDeposits(model,myDay);
            updateAll();
            MessageBox msg = new MessageBox(MessageBox.SGN_SUCCESS, "Guardado correctamente");
            msg.show(this);
        } catch (SQLException ex) {
            MessageBox msb = new MessageBox(MessageBox.SGN_CAUTION, "Problemas con la base de datos.",ex);
            msb.show(this);
            this.dispose();
            Shared.reload();
        }
        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Guardados satisfactoriamente");
    }//GEN-LAST:event_saveDepositActionPerformed

    private void deleteDepositActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteDepositActionPerformed
        int n = depositTable.getSelectedRow();
        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Eliminado deposito " + n);
        if ( n != -1 ){
            DefaultTableModel model = (DefaultTableModel) depositTable.getModel();
            model.removeRow(n);
        }else{
            MessageBox msg = new MessageBox(MessageBox.SGN_CAUTION, "Debe seleccionar un depósito!");
            msg.show(this);
        }
    }//GEN-LAST:event_deleteDepositActionPerformed

    private void addDepositActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addDepositActionPerformed
        DefaultTableModel model = (DefaultTableModel) depositTable.getModel();
        model.setNumRows(model.getRowCount()+1);
    }//GEN-LAST:event_addDepositActionPerformed

    private void addExpenseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addExpenseActionPerformed
        DefaultTableModel model = (DefaultTableModel) expenseTable.getModel();
        model.setNumRows(model.getRowCount()+1);
    }//GEN-LAST:event_addExpenseActionPerformed

    private void deleteExpenseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteExpenseActionPerformed
        int n = expenseTable.getSelectedRow();
        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Eliminando gasto " + n);
        if ( n != -1 ){
            DefaultTableModel model = (DefaultTableModel) expenseTable.getModel();
            model.removeRow(n);
        }else{
            MessageBox msg = new MessageBox(MessageBox.SGN_CAUTION, "Debe seleccionar un gasto!");
            msg.show(this);
        }
    }//GEN-LAST:event_deleteExpenseActionPerformed

    private void saveExpenseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveExpenseActionPerformed
        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Guardando gastos");
        DefaultTableModel model = (DefaultTableModel) expenseTable.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            try{
                if ( model.getValueAt(i, 0) == null || model.getValueAt(i, 1) == null || model.getValueAt(i, 2) == null ||
                        ((String)model.getValueAt(i, 0)).isEmpty() || ((String)model.getValueAt(i, 1)).isEmpty() ||  ((String)model.getValueAt(i, 2)).isEmpty() ){
                    MessageBox msg = new MessageBox(MessageBox.SGN_CAUTION, "Todos los campos son obligatorios. No pueden haber gastos con campos vacíos.");
                    msg.show(this);
                    return;
                }
                if ( ((String)model.getValueAt(i, 2)).length() > 45 ){
                    MessageBox msg = new MessageBox(MessageBox.SGN_CAUTION, "La descripcion no puede tener mas de 45 caracteres.");
                    msg.show(this);
                    return;
                }
                Double m = Double.parseDouble(((String) model.getValueAt(i, 1)).replace(',', '.'));
            }catch (NumberFormatException ex){
                MessageBox msg = new MessageBox(MessageBox.SGN_CAUTION, "El monto es inválido. Debe ser positivo");
                msg.show(this);
                return;
            }
        }
        double total = .0;
        for (int i = 0; i < model.getRowCount(); i++) {
            total += Double.parseDouble(((String) model.getValueAt(i, 1)).replace(',', '.'));;
        }

        try {
            if ( total >= ConnectionDrivers.getAllCash(myDay) ){
                MessageBox msg = new MessageBox(MessageBox.SGN_WARNING , "No se puede haber gastado más dinero que la cantidad de efectivo existente en cajas.");
                msg.show(this);
                return;
            }
            ConnectionDrivers.deleteAllExpenses(myDay);
            ConnectionDrivers.createExpenses(model,myDay);
            updateAll();
            System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Guardado satisfactoriamente");
            MessageBox msg = new MessageBox(MessageBox.SGN_SUCCESS, "Guardado correctamente");
            msg.show(this);
        } catch (SQLException ex) {
            MessageBox msb = new MessageBox(MessageBox.SGN_DANGER, "Problemas con la base de datos.",ex);
            msb.show(this);
            this.dispose();
            Shared.reload();
        }
    }//GEN-LAST:event_saveExpenseActionPerformed

    private void printAndSendButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printAndSendButtonActionPerformed
        try {
            System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Enviando cierre administrativo..");
            if (!ConnectionDrivers.allZready(myDay)) {
                SellWithoutStock sws = new SellWithoutStock((Frame) Shared.getMyMainWindows(), true, "Hay cajas sin cerrar.", "sendODWithoutZ");
                Shared.centerFrame(sws);
                sws.setVisible(true);
                if ( !sws.authorized ){
                    return;
                }
            }
            if ( Math.abs(ConnectionDrivers.getTotalDeclared(myDay) - receiptTotal) > Double.parseDouble(Shared.getConfig("moneyExilon")) ){
                SellWithoutStock sws = new SellWithoutStock((Frame) Shared.getMyMainWindows(), true, "Envio con diferencias.", "sendODWithDiff");
                Shared.centerFrame(sws);
                sws.setVisible(true);
                if ( !sws.authorized ){
                    return;
                }
            }

            sendIt();
        } catch (SQLException ex) {
            MessageBox msb = new MessageBox(MessageBox.SGN_DANGER, "Problemas con la base de datos.",ex);
            msb.show(this);
            this.dispose();
            Shared.reload();
        }
        
}//GEN-LAST:event_printAndSendButtonActionPerformed

    private void sendIt(){
        workingFrame = new Working((JFrame) Shared.getMyMainWindows());

        WaitSplash ws = new WaitSplash(this);

        Shared.centerFrame(workingFrame);
        workingFrame.setVisible(true);
        ws.execute();
    }

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        this.dispose();
}//GEN-LAST:event_cancelButtonActionPerformed

    private void addDeposit1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addDeposit1ActionPerformed
        DefaultTableModel model = (DefaultTableModel) bankTable.getModel();
        model.setRowCount( model.getRowCount() + 1 );
        model.setValueAt(".0", model.getRowCount()-1, 3);
    }//GEN-LAST:event_addDeposit1ActionPerformed

    private void updateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateButtonActionPerformed
        try {
            ConnectionDrivers.deleteAllBufferBank(myDay);
            updateAll();
        } catch (Exception ex) {
            Logger.getLogger(ClosingDay.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_updateButtonActionPerformed

    private void deleteNewBanksActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteNewBanksActionPerformed
        int n = bankTable.getSelectedRow();
        if ( n != -1 ){
            DefaultTableModel model = (DefaultTableModel) bankTable.getModel();
            model.removeRow(n);
        }else{
            MessageBox msg = new MessageBox(MessageBox.SGN_CAUTION, "Debe seleccionar una forma de pago de punto de venta de banco!");
            msg.show(this);
        }
    }//GEN-LAST:event_deleteNewBanksActionPerformed

    private void saveNewBanksActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveNewBanksActionPerformed
        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Guardando bancos");
        DefaultTableModel model = (DefaultTableModel) bankTable.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            try{
                for (int j = 0; j < 3; j++) {
                    if ( model.getValueAt(i, j) == null || ((String)model.getValueAt(i, j)).isEmpty() ){
                        MessageBox msg = new MessageBox(MessageBox.SGN_CAUTION, "Todos los campos son obligatorios!");
                        msg.show(this);
                    }
                }
                if ( (model.getValueAt(i, 3) == null || ((String)model.getValueAt(i, 3)).isEmpty()) &&
                        (model.getValueAt(i, 4) == null || ((Double)model.getValueAt(i, 4)) == .0) ){
                    MessageBox msg = new MessageBox(MessageBox.SGN_CAUTION, "Debe indicar el monto en el item " + (i+1));
                    msg.show(this);
                }
            }catch (NumberFormatException ex){
                MessageBox msg = new MessageBox(MessageBox.SGN_CAUTION, "El monto es inválido. Debe corregirse!");
                msg.show(this);
                return;
            }
        }
        try {
            ConnectionDrivers.deleteAllPayments(myDay);
            ConnectionDrivers.createPayments(model,myDay);
            updateAll();
            System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Bancos guardados");
            MessageBox msg = new MessageBox(MessageBox.SGN_SUCCESS, "Guardado correctamente");
            msg.show(this);
        } catch (SQLException ex) {
            MessageBox msb = new MessageBox(MessageBox.SGN_CAUTION, "Problemas con la base de datos.",ex);
            msb.show(this);
            this.dispose();
            Shared.reload();
        }
    }//GEN-LAST:event_saveNewBanksActionPerformed

    private void bankTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bankTableMouseClicked
        
    }//GEN-LAST:event_bankTableMouseClicked

    private void bankTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bankTableMouseReleased
        /*if ( bankTable.getSelectedColumn() == 4 ){
            int n = bankTable.getSelectedRow();
            bankTable.setValueAt(bankTable.getValueAt(n, 3), n, 4);
        }*/
    }//GEN-LAST:event_bankTableMouseReleased

    private void formMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseMoved
        Shared.getScreenSaver().actioned();
    }//GEN-LAST:event_formMouseMoved

    private void bankTableMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bankTableMouseMoved
        Shared.getScreenSaver().actioned();
    }//GEN-LAST:event_bankTableMouseMoved

    private void depositTableMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_depositTableMouseMoved
        Shared.getScreenSaver().actioned();
    }//GEN-LAST:event_depositTableMouseMoved

    private void fiscalZMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fiscalZMouseMoved
        Shared.getScreenSaver().actioned();
    }//GEN-LAST:event_fiscalZMouseMoved

    private void formWayxPosesMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formWayxPosesMouseMoved
        Shared.getScreenSaver().actioned();
    }//GEN-LAST:event_formWayxPosesMouseMoved

    private void formInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosing

    }//GEN-LAST:event_formInternalFrameClosing

    private IXMLElement createXml4CN() throws SQLException, IOException{
        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Agrupando Notas de Credito.");
        List< ReceiptSap > CreditNoteGroup = new LinkedList<ReceiptSap>();
        List<Receipt> receipts = ConnectionDrivers.listOkCN(myDay);

        ReceiptSap rs = new ReceiptSap(myDay);
        int previousId = -1;
        String previousCli = "Contado";
        for (Receipt receipt : receipts) {
            
            if ( receipt.getFiscalNumber().isEmpty() ){
                System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Error con la factura " + receipt.getInternId());
                continue;
            }
            if ( (previousId == -1 || previousId +1 == Integer.parseInt(receipt.getFiscalNumber() )
                    && receipt.getClientId().equals("Contado") && receipt.getClientId().equals(previousCli)) ){
                rs.add(receipt);
            }else{
                CreditNoteGroup.add(rs);
                rs = new ReceiptSap(myDay);
                rs.add(receipt);
            }
            previousId = Integer.parseInt(receipt.getFiscalNumber());
            previousCli = receipt.getClientId();
        }
        if ( rs.getSize() > 0 ){
            CreditNoteGroup.add(rs);
        }

        IXMLElement xmlCN = new XMLElement("NotasDeCredito");

        for (ReceiptSap receiptSap : CreditNoteGroup) {
            IXMLElement child = xmlCN.createElement("CN");
            xmlCN.addChild(child);
            child.setAttribute("getId", receiptSap.getId());
            child.setAttribute("getKind", receiptSap.getKind());
            child.setAttribute("getClient", receiptSap.getClient());
            child.setAttribute("range", receiptSap.getMinFiscalId() + "-" + receiptSap.getMaxFiscalId());
            child.setAttribute("getZ", receiptSap.getZ());
            child.setAttribute("getPrinterId", receiptSap.getPrinterId());
            int position = 1;
            for (Receipt receipt : receiptSap.receipts) {
                for (Item2Receipt item2Receipt : receipt.getItems()) {
                    IXMLElement childchild = child.createElement("CND");
                    child.addChild(childchild);
                    childchild.setAttribute("id", "D" + receiptSap.getId());
                    childchild.setAttribute("position", Shared.df2intSAP.format(position++));
                    childchild.setAttribute("barcode", item2Receipt.getItem().getMainBarcode());
                    childchild.setAttribute("quant", item2Receipt.getQuant().toString());
                    childchild.setAttribute("sellUnits", item2Receipt.getItem().getSellUnits());
                    childchild.setAttribute("sellPrice", item2Receipt.getSellPrice()+"");
                    childchild.setAttribute("discount", (item2Receipt.getSellDiscount()/100.0)*item2Receipt.getSellPrice()+"");
                }

            }
            System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Creando Grupo = " + receiptSap.getMinFiscalId() + "-" + receiptSap.getMaxFiscalId());
        }

        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " XML creado para notas de credito");
        return xmlCN;
    }

    private IXMLElement createXml4Receipt() throws SQLException, IOException{
        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Creando XMl para facturas");
        List< ReceiptSap > receiptGroup = new LinkedList<ReceiptSap>();
        List<Receipt> receipts = ConnectionDrivers.listOkReceipts(myDay);

        if ( receipts.isEmpty() ){
            MessageBox msg = new MessageBox(MessageBox.SGN_SUCCESS, "No se puede continuar, debe existir al menos una factura.");
            msg.show(this);
            return null;
        }
        ReceiptSap rs = new ReceiptSap(myDay);
        int previousId = -1;
        String previousCli = "Contado";
        Double previousDis = -1.0;

        for (Receipt receipt : receipts) {

            if ( !receipt.getClientId().equals("Contado") ){
                clients.add(receipt.getClientId());
            }

            if ( receipt.getFiscalNumber().isEmpty() ){
                System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Error con la factura " + receipt.getInternId());
                continue;
            }
            if ( (previousId == -1 || previousId +1 == Integer.parseInt(receipt.getFiscalNumber() ) &&
                    receipt.getClientId().equals("Contado") && receipt.getClientId().equals(previousCli)) &&
                    ( Math.abs(receipt.getGlobalDiscount() - previousDis) < Double.parseDouble(Shared.getConfig("exilon")) || previousDis == -1.0 )){
                rs.add(receipt);
            }else{
                receiptGroup.add(rs);
                rs = new ReceiptSap(myDay);
                rs.add(receipt);
            }
            previousId = Integer.parseInt(receipt.getFiscalNumber());
            previousCli = receipt.getClientId();
            previousDis = receipt.getGlobalDiscount();
        }
        if ( rs.getSize() > 0 ){
            receiptGroup.add(rs);
        }

        IXMLElement xmlRe = new XMLElement("Facturas");

        for (ReceiptSap receiptSap : receiptGroup) {
            IXMLElement child = xmlRe.createElement("Re");
            xmlRe.addChild(child);
            child.setAttribute("getId", receiptSap.getId());
            child.setAttribute("getKind", receiptSap.getKind());
            child.setAttribute("getClient", receiptSap.getClient());
            child.setAttribute("range", receiptSap.getMinFiscalId() + "-" + receiptSap.getMaxFiscalId());
            child.setAttribute("getZ", receiptSap.getZ());
            child.setAttribute("getPrinterId", receiptSap.getPrinterId());

            int position = 1;
            for (Receipt receipt : receiptSap.receipts) {
                Double gDisc = receipt.getGlobalDiscount();
                for (Item2Receipt item2Receipt : receipt.getItems()) {
                    IXMLElement childchild = child.createElement("CND");
                    child.addChild(childchild);
                    childchild.setAttribute("id", "F" + receiptSap.getId());
                    childchild.setAttribute("position", Shared.df2intSAP.format(position++));
                    childchild.setAttribute("barcode", item2Receipt.getItem().getMainBarcode());
                    childchild.setAttribute("quant", item2Receipt.getQuant().toString());
                    childchild.setAttribute("sellUnits", item2Receipt.getItem().getSellUnits());
                    childchild.setAttribute("sellPrice", item2Receipt.getSellPrice()+"");
                    Double tmpD = (item2Receipt.getSellDiscount()/100.0)*item2Receipt.getSellPrice();
                    childchild.setAttribute("discount", tmpD + gDisc*(item2Receipt.getSellPrice()-tmpD) +"");
                }

            }
            System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " child = " +receiptSap.getMinFiscalId() + "-" + receiptSap.getMaxFiscalId());
        }

        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Creado xml para facturas satisfactoriamente");
        return xmlRe;

    }

    private IXMLElement createClients() throws SQLException{
        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Creando xml para clientes");
        XMLElement clienXML = new XMLElement("Clientes");

        TreeSet<String> clientsAdded = new TreeSet<String>();
        for (String c : clients) {
            Client cc = ConnectionDrivers.listClients(c).get(0);
            if ( !clientsAdded.contains(cc.getId()) ){
                IXMLElement client = clienXML.createElement("C");
                client.setAttribute("ID", cc.getId());
                String tname = cc.getName();
                client.setAttribute("Name", tname.substring(0,Math.min(35, tname.length())));
                String tc = cc.getAddress() + " Tlf: " + cc.getPhone();
                client.setAttribute("Addr", (tc).substring(0, Math.min(30,tc.length())));
                clienXML.addChild(client);
                clientsAdded.add(cc.getId());
            }
        }
        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Creado XML para clientes");

        return clienXML;
    }

    private IXMLElement createHistEnvios() throws SQLException{
        XMLElement xml = new XMLElement("HistoricosDeEnvios");

        xml.setAttribute("ventas", Shared.round((receiptTotal*(Shared.getIva()+100.0)/100.0),2) + "");
        xml.setAttribute("Observaciones", noteField.getText());

        return xml;
    }
    
    private String createCloseEmail() throws SQLException{
        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Creando correo para cierre administrativo");
        String formatDay= myDay.split("-")[2] + "/" + myDay.split("-")[1] + "/" + myDay.split("-")[0];
        return "<html>\n"
                    + "<b><u>RESUMEN DIARIO DE VENTAS</u><br><br></b>\n"
                    + "Dia: " + formatDay + "<br>\n"
                    + "Agencia: " +  Shared.getConfig("storeName") + " <br>"
                    + "Descripcion: " +  Shared.getConfig("storeDescription") + " <br><br>"
                    + "\n"
                    + "Total Ingresos: " + totalTotalField.getText() + "<br>\n"
                    + "Total Gastos: " + expensesTodayField.getText() + "<br>\n"
                    + "Total General: <b>" + expensesMinusDeclaredField.getText() + "</b><br><br>\n"
                    + "<b>Gastos<br></b>"
                    + "<table width=\"600\" cellpadding=\"3\" cellspacing=\"3\">"
                    + "<tr>"
                    + "<td bordercolor=\"#000066\" style=\"background-color:#79BAEC\">Tipo de Gasto</td>"
                    + "<td bordercolor=\"#000066\" style=\"background-color:#79BAEC\">Monto</td>"
                    + "<td bordercolor=\"#000066\" style=\"background-color:#79BAEC\">Observaciones</td>"
                    + "</tr>"
                    + getExpensesTable()
                    + "<tr>"
                    + "<td bordercolor=\"#000066\" style=\"background-color:#79BAEC\">Total</td>"
                    + "<td bordercolor=\"#000066\" style=\"background-color:#79BAEC\">" + expensesTodayField.getText() + "</td>"
                    + "<td bordercolor=\"#000066\" style=\"background-color:#79BAEC\"></td>"
                    + "</tr>"
                    + "</table><br><br><b>Ingresos<br></b><table width=\"800\" cellpadding=\"3\" cellspacing=\"3\"><tr>"
                    + "<td bordercolor=\"#79BAEC\" style=\"background-color:#79BAEC\">Tipo de Ingreso</td><td bordercolor"
                    + "=\"#79BAEC\" style=\"background-color:#79BAEC\">Banco</td><td bordercolor=\"#79BAEC\" style=\"background-"
                    + "color:#79BAEC\">Lote</td><td bordercolor=\"#79BAEC\" style=\"background-color:#79BAEC\">Monto</td></tr>"
                    + getPaymentsTable()
                    + "<tr><td bordercolor=\"#000066\" style=\"background-color:#79BAEC\">Total</td><td bordercolor=\"#000066\" style=\"background-color:#79BAEC\"></td><td bordercolor=\"#000066\" style=\"background-color:#79BAEC\"></td><td bordercolor=\"#000066\" style=\"background-color:#79BAEC\">" + totalTotalField.getText() + "</td></tr></table><br><br><b>Informacion Fiscal<br></b><table width=\"800\" cellpadding=\"3\" cellspacing=\"3\"><tr><td bordercolor=\"#000066\" style=\"background-color:#79BAEC\">Serial Impresora</td><td bordercolor=\"#000066\" style=\"background-color:#79BAEC\">Monto</td><td bordercolor=\"#000066\" style=\"background-color:#79BAEC\">Reporte Z</td><td bordercolor=\"#000066\" style=\"background-color:#79BAEC\">Ultima Factura</td><td bordercolor=\"#000066\" style=\"background-color:#79BAEC\">Cantidad Facturas</td><td bordercolor=\"#000066\" style=\"background-color:#79BAEC\">Ultima Devolucion</td><td bordercolor=\"#000066\" style=\"background-color:#79BAEC\">Cantidad Devolucion</td></tr>"
                    + getZTable()
                    + "<tr><td bordercolor=\"#000066\" style=\"background-color:#79BAEC\">Total</td><td bordercolor=\"#000066\" style=\"background-color:#79BAEC\">" + totalDeclaredField.getText() + "</td><td bordercolor=\"#000066\" style=\"background-color:#79BAEC\"></td><td bordercolor=\"#000066\" style=\"background-color:#79BAEC\"></td><td bordercolor=\"#000066\" style=\"background-color:#79BAEC\"></td><td bordercolor=\"#000066\" style=\"background-color:#79BAEC\"></td><td bordercolor=\"#000066\" style=\"background-color:#79BAEC\"></td></tr></table><br>"
                    + "<br>Diferencia entre reportes Z y ordenes del dia: " + Shared.round((Double.parseDouble(totalDeclaredField.getText().replace(',', '.')) - Double.parseDouble(expensesMinusDeclaredField.getText().replace(',', '.'))),2) + "<br>Cuadre de Caja: "+totalField.getText()+"<br><br>"
                    + "</html>";
    }

    private String createCloseEmailSubject(){
        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Creando asunto para stock negativo");
        String formatDay= myDay.split("-")[2] + "/" + myDay.split("-")[1] + "/" + myDay.split("-")[0];
        return "Cierre del dia " + formatDay + " Agencia " + Shared.getConfig("storeName");
    }

    private String createNegativeStockSubject(){
        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Creando asunto para stock negativo");
        String formatDay= myDay.split("-")[2] + "/" + myDay.split("-")[1] + "/" + myDay.split("-")[0];
        return "Stock Negativo " + formatDay + " Agencia " + Shared.getConfig("storeName");
    }

    private String createNegativeStockEmail(List<Item> l) throws SQLException{
        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Creando email para stock negativo");
        String formatDay= myDay.split("-")[2] + "/" + myDay.split("-")[1] + "/" + myDay.split("-")[0];
        return "<html>\n"
                    + "<b><u>Stock en Negativo</u><br><br></b>\n"
                    + "Dia: " + formatDay + "<br>\n"
                    + "Agencia: " +  Shared.getConfig("storeName") + " <br>"
                    + "Descripcion: " +  Shared.getConfig("storeDescription") + " <br><br>"
                    + "<table width=\"600\" cellpadding=\"3\" cellspacing=\"3\">"
                    + "<tr>"
                    + "<td bordercolor=\"#000066\" style=\"background-color:#79BAEC\">Tipo de Gasto</td>"
                    + "<td bordercolor=\"#000066\" style=\"background-color:#79BAEC\">Monto</td>"
                    + "<td bordercolor=\"#000066\" style=\"background-color:#79BAEC\">Observaciones</td>"
                    + "</tr>"
                    + getNegativeStock(l)
                    + "</html>";
    }

    @Override
    public void doIt(){

        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Operando envio");
        try {
            Double diffe = Math.abs(totalInCard + totalInCash + totalExpenses - (new Price(null,receiptTotal).plusIva().getQuant()));
            if ( diffe > Double.parseDouble(Shared.getConfig("moneyExilon"))){

                MessageBox msg = new MessageBox(MessageBox.SGN_CAUTION, "No se puede enviar el cierre administrativo. "
                        + (totalInCard + totalInCash + totalExpenses - (new Price(null,receiptTotal).plusIva().getQuant()) < 0 ? "Faltan" : "Sobran")
                        + " " + Shared.df.format(diffe) + " bs ");
                msg.show(this);
                return;
            }

            Shared.createBackup();

            List<Item> l = ConnectionDrivers.getNegativeStock();
            if ( !l.isEmpty() ){
                Shared.sendMail(Shared.getConfig("email2sendNegativeStock") , createNegativeStockEmail(l), createNegativeStockSubject());
            }

            if ( Constants.justEmail ){
                return;
            }

            Shared.sendMail(Shared.getConfig("sendEmail"), createCloseEmail(), createCloseEmailSubject());


            IXMLElement data2Sent = new XMLElement("data");
            data2Sent.setAttribute("storeName", Shared.getConfig("storeName"));
            data2Sent.setAttribute("day", myDay);
            data2Sent.addChild(createXml4Receipt());
            data2Sent.addChild(createXml4CN());
            data2Sent.addChild(createClients());
            data2Sent.addChild(createHistEnvios());
            data2Sent.addChild(ConnectionDrivers.createFiscalData(myDay));
            data2Sent.addChild(createCobranzas());

            ByteArrayOutputStream baosF = new ByteArrayOutputStream();
            XMLWriter xmlwF = new XMLWriter(baosF);
            xmlwF.write(data2Sent);

            TotalPosWebService ws = new TotalPosWebServiceService().getTotalPosWebServicePort();

            String ans = ws.closeDay(baosF.toString());
            if ( !ans.isEmpty() ){
                throw new Exception(ans);
            }
            
            /*SrvSap ss = new SrvSap();
            IsrvSap isrvs = ss.getBasicHttpBindingIsrvSap();
            if ( showReport ){
                ZFISHISTENVIOS zfhe = new ZFISHISTENVIOS();
                ArrayOfZFISCOBRANZA lzfc = new ArrayOfZFISCOBRANZA();
                ArrayOfZFISDATAFISCAL aozfdf = new ArrayOfZFISDATAFISCAL();
                zfhe.setMANDT(of.createZFISHISTENVIOSMANDT(Constants.mant));
                System.out.println("MANDT\tIDTIENDA\tFECHAPROCESADO\tTOTALVENTASDIA\tOBSERVACIONES\tMODIFICAR\tFONDOCAJA\tBLOQUEAR\t");
                System.out.print(Constants.mant+"\t");
                zfhe.setIDTIENDA(of.createZFISHISTENVIOSIDTIENDA(Constants.storePrefix + Shared.getConfig("storeName")));
                System.out.print(Constants.storePrefix + Shared.getConfig("storeName") + "\t");
                zfhe.setFECHAPROCESADO(of.createZFISHISTENVIOSFECHAPROCESADO(date4sap));
                System.out.print(date4sap+"\t");
                //////////
                //zfhe.setTOTALVENTASDIA(new BigDecimal(totalInCard + totalInCash));
                
                //zfhe.setTOTALVENTASDIA(new BigDecimal(Shared.round(ConnectionDrivers.getTotalDeclared(myDay)*(Shared.getIva()+100.0)/100.0,2)));
                zfhe.setTOTALVENTASDIA(new BigDecimal( Shared.round((receiptTotal*(Shared.getIva()+100.0)/100.0),2)));
                System.out.print(Shared.round((receiptTotal*(Shared.getIva()+100.0)/100.0),2) + "\t");
                zfhe.setOBSERVACIONES(of.createZFISHISTENVIOSOBSERVACIONES(noteField.getText()));
                System.out.print(noteField.getText() + "\t");
                zfhe.setMODIFICAR(of.createZFISHISTENVIOSMODIFICAR("N"));
                System.out.print("S\t");
                zfhe.setFONDOCAJA(BigDecimal.ZERO);
                System.out.print("0\t");
                zfhe.setBLOQUEAR(of.createZFISHISTENVIOSBLOQUEAR("N"));
                System.out.print("N\t\n");
                fillBanks(lzfc.getZFISCOBRANZA());
                fillExpenses(lzfc.getZFISCOBRANZA());
                fillDeposits(lzfc.getZFISCOBRANZA());
                List<ZFISDATAFISCAL> zFISDATAFISCAL = aozfdf.getZFISDATAFISCAL();
                for (ZFISDATAFISCAL zfdf : ConnectionDrivers.getOperativeDays(myDay)) {
                    zFISDATAFISCAL.add(zfdf);
                }
                //TODO HERE
                Resultado sss = isrvs.sapInsertCobranza(lzfc, aozfdf, zfhe);
                System.out.println(sss.getMensaje().getValue());
                ansMoney = sss.getMensaje().getValue();
                if ( sss.getCodigoError() == 0 ){
                    ansMoney = "OK";
                }else{
                    ansMoney = "ERROR " + ansMoney;
                }
                System.out.println("ansMoney = " + ansMoney);
            }*/

            //Shared.sendSells(myDay,this,ansMoney);

            ConnectionDrivers.closeThisDay(myDay);
            System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Enviado satisfactoriamente. Creando comprobante");
            new CreateClosingDayReport(myDay,noteField.getText(), Shared.round((receiptTotal*(Shared.getIva()+100.0)/100.0),2));
        } catch (SQLException ex) {
            MessageBox msg = new MessageBox(MessageBox.SGN_CAUTION, "Error con la base de datos.",ex);
            msg.show(this);
            System.out.println(ex.getMessage() + ex.getStackTrace());
            ex.printStackTrace();
        } catch (Exception ex) {
            MessageBox msg = new MessageBox(MessageBox.SGN_CAUTION, "Error en el envío del cierre del día. Debe intentar de nuevo.",ex);
            msg.show(this);
            System.out.println(ex.getMessage() + ex.getStackTrace());
            ex.printStackTrace();
        }
    }

    @Override
    public void close() {
        workingFrame.setVisible(false);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addDeposit;
    private javax.swing.JButton addDeposit1;
    private javax.swing.JButton addExpense;
    private javax.swing.JTable bankTable;
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel cnLabel;
    private javax.swing.JTextField creditNoteField;
    private javax.swing.JButton deleteDeposit;
    private javax.swing.JButton deleteExpense;
    private javax.swing.JButton deleteNewBanks;
    private javax.swing.JTable depositTable;
    private javax.swing.JTable expenseTable;
    private javax.swing.JTextField expensesMinusDeclaredField;
    private javax.swing.JTextField expensesTodayField;
    private javax.swing.JTable fiscalZ;
    private javax.swing.JTable formWayxPoses;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTextField noteField;
    private javax.swing.JLabel noteLabel;
    private javax.swing.JTable payWayxPosTable;
    private javax.swing.JLabel payWithCN;
    private javax.swing.JTextField payWithCreditNoteField;
    private javax.swing.JButton printAndSendButton;
    private javax.swing.JButton saveDeposit;
    private javax.swing.JButton saveExpense;
    private javax.swing.JButton saveNewBanks;
    private javax.swing.JTextField totalCardsField;
    private javax.swing.JTextField totalCashField;
    private javax.swing.JTextField totalDeclaredField;
    private javax.swing.JTextField totalField;
    private javax.swing.JTextField totalTotalField;
    private javax.swing.JButton updateButton;
    // End of variables declaration//GEN-END:variables

    private IXMLElement createCobranzas() {
        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Creando XMl para cobranzas");
        IXMLElement xml = new XMLElement("Cobranza");

        // BANKS
        for ( int i = 0 ; i < bankTable.getRowCount() ; i++ ){
            IXMLElement child = xml.createElement("I");
            xml.addChild(child);
            child.setAttribute("waerks", Shared.getConfig("waerks"));
            child.setAttribute("simbo", (bankTable.getValueAt(i, 0).toString().split("-")[0]).trim());

            if ( bankTable.getValueAt(i, 2).equals(Shared.getConfig("creditPaymentName")) ){
                child.setAttribute("mpago", "B");
            }else if ( bankTable.getValueAt(i, 2).equals(Shared.getConfig("debitPaymentName")) ){
                child.setAttribute("mpago", "D");
            }else if ( bankTable.getValueAt(i, 2).equals(Shared.getConfig("americanExpressPaymentName")) ){
                child.setAttribute("mpago", "A");
            }else{
                System.out.println("Banco desconocido... agregando E");
                child.setAttribute("mpago", "E");
            }

            child.setAttribute("bpago", (bankTable.getValueAt(i, 0).toString().split("-")[0]).trim());
            child.setAttribute("lote", (String)bankTable.getValueAt(i, 1));
            child.setAttribute("monto", ((Double)bankTable.getValueAt(i, 4))+"");
            child.setAttribute("text", "");
        }

        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Creando XML para gastos");

        // EXPENSES
        for ( int i = 0 ; i < expenseTable.getRowCount() ; i++ ){
            IXMLElement child = xml.createElement("I");
            xml.addChild(child);
            child.setAttribute("waerks", Shared.getConfig("waerks"));
            child.setAttribute("simbo", Shared.getConfig("genericBank"));

            String tmp = expenseTable.getValueAt(i, 0).toString().split("-")[0];
            child.setAttribute("mpago", tmp.substring(0, tmp.length() - 1));

            child.setAttribute("bpago", Shared.getConfig("genericBank"));
            child.setAttribute("lote", i+"");
            child.setAttribute("monto", ((String)expenseTable.getValueAt(i, 1)).replace(',', '.'));
            child.setAttribute("text", (String)expenseTable.getValueAt(i, 2));
            
        }

        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Creando XML para efectivo");

        // CASH
        for ( int i = 0 ; i < depositTable.getRowCount() ; i++ ){
            IXMLElement child = xml.createElement("I");
            xml.addChild(child);
            child.setAttribute("waerks", Shared.getConfig("waerks"));
            String bancoId = ((String)depositTable.getValueAt(i, 0)).split("-")[0].trim();
            child.setAttribute("simbo", bancoId);
            child.setAttribute("mpago", "E");

            child.setAttribute("bpago", bancoId);
            child.setAttribute("lote", ((String)depositTable.getValueAt(i, 1)));
            child.setAttribute("monto", ((String)depositTable.getValueAt(i, 2)).replace(',','.'));
            child.setAttribute("text", "");

        }
        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " XML Creados satisfactoriamente");
        return xml;
    }

    @Deprecated
    private void fillBanks(List<ZFISCOBRANZA> zFISCOBRANZA) {
        for ( int i = 0 ; i < bankTable.getRowCount() ; i++ ){
            ZFISCOBRANZA zfc = new ZFISCOBRANZA();
            System.out.println("ID\tMANDT\tFECHA\tWERKS\tWAERS\tSIMBO\tMPAGO\tBPAGO\tLOTE\tMONTO\tITEMTEXT");
            zfc.setID(1);
            System.out.print("1\t");
            zfc.setMANDT(of.createZFISCOBRANZAMANDT(Shared.getConfig("mant")));
            System.out.print(Shared.getConfig("mant") + "\t");
            zfc.setFECHA(of.createZFISCOBRANZAFECHA(date4sap));
            System.out.print(date4sap + "\t");
            zfc.setWERKS(of.createZFISCOBRANZAWERKS(Shared.getConfig("storePrefix") + Shared.getConfig("storeName")));
            System.out.print(Shared.getConfig("storePrefix") + Shared.getConfig("storeName") + "\t");
            zfc.setWAERS(of.createZFISCOBRANZAWAERS(Shared.getConfig("waerks")));
            System.out.print(Shared.getConfig("waerks") + "\t");
            zfc.setSIMBO(of.createZFISCOBRANZASIMBO( (bankTable.getValueAt(i, 0).toString().split("-")[0]).trim()));
            System.out.print((bankTable.getValueAt(i, 0).toString().split("-")[0]).trim()  + "\t");
            zfc.setMPAGO( of.createZFISCOBRANZAMPAGO( bankTable.getValueAt(i, 2).equals("Credito")?"B":"D" ) );
            System.out.print(bankTable.getValueAt(i, 2).equals("Credito")?"B":"D" + "\t");
            zfc.setBPAGO(of.createZFISCOBRANZABPAGO( (bankTable.getValueAt(i, 0).toString().split("-")[0]).trim()));
            System.out.print((bankTable.getValueAt(i, 0).toString().split("-")[0]).trim() + "\t");
            zfc.setLOTE(of.createZFISCOBRANZALOTE((String)bankTable.getValueAt(i, 1)));
            System.out.print((String)bankTable.getValueAt(i, 1) + "\t");
            //if ( !bankTable.getValueAt(i, 4).equals("0") ) {
                zfc.setMONTO(new BigDecimal((Double)bankTable.getValueAt(i, 4)));
                System.out.print((Double)bankTable.getValueAt(i, 4) + "\t");
            /*}else{
                zfc.setMONTO(new BigDecimal((String)bankTable.getValueAt(i, 3)));
                System.out.print((String)bankTable.getValueAt(i, 3) + "\t");
            }*/
            zfc.setITEMTEXT(of.createZFISCOBRANZAITEMTEXT(""));
            System.out.print("\t");
            zFISCOBRANZA.add(zfc);
            System.out.println();
        }
    }

    @Deprecated
    private void fillExpenses(List<ZFISCOBRANZA> zFISCOBRANZA) {
        for ( int i = 0 ; i < expenseTable.getRowCount() ; i++ ){
            ZFISCOBRANZA zfc = new ZFISCOBRANZA();
            zfc.setID(1);
            System.out.print(1+ "\t");
            zfc.setMANDT(of.createZFISCOBRANZAMANDT(Shared.getConfig("mant")));
            System.out.print(Shared.getConfig("mant")+"\t");
            zfc.setFECHA(of.createZFISCOBRANZAFECHA(date4sap));
            System.out.print(date4sap+"\t");
            zfc.setWERKS(of.createZFISCOBRANZAWERKS(Shared.getConfig("storePrefix") + Shared.getConfig("storeName")));
            System.out.print(Shared.getConfig("storePrefix") + Shared.getConfig("storeName")+"\t");
            zfc.setWAERS(of.createZFISCOBRANZAWAERS(Shared.getConfig("waerks")));
            System.out.print(Shared.getConfig("waerks")+"\t");
            zfc.setSIMBO(of.createZFISCOBRANZASIMBO(Shared.getConfig("genericBank")));
            System.out.print(Shared.getConfig("genericBank")+"\t");
            String tmp = expenseTable.getValueAt(i, 0).toString().split("-")[0];
            zfc.setMPAGO( of.createZFISCOBRANZAMPAGO( tmp.substring(0, tmp.length() - 1) ) );
            System.out.print(tmp.substring(0, tmp.length() - 1)+"\t");
            zfc.setBPAGO(of.createZFISCOBRANZABPAGO(Shared.getConfig("genericBank")));
            System.out.print(Shared.getConfig("genericBank")+"\t");
            //String[] md = myDay.split("-");
            //zfc.setLOTE(of.createZFISCOBRANZALOTE(md[2] + md[1] + md[0] + Shared.getConfig("storeName")));
            //zfc.setLOTE(of.createZFISCOBRANZALOTE(""));
            zfc.setLOTE(of.createZFISCOBRANZALOTE(i+""));
            //System.out.print(md[2] + md[1] + md[0] + Shared.getConfig("storeName")+"\t");
            zfc.setMONTO(new BigDecimal(((String)expenseTable.getValueAt(i, 1)).replace(',', '.')));
            System.out.print(((String)expenseTable.getValueAt(i, 1)).replace(',', '.')+"\t");
            zfc.setITEMTEXT(of.createZFISCOBRANZAITEMTEXT((String)expenseTable.getValueAt(i, 2)));
            System.out.print((String)expenseTable.getValueAt(i, 2)+"\t");
            zFISCOBRANZA.add(zfc);
            System.out.println("");
        }
    }

    @Deprecated
    private void fillDeposits(List<ZFISCOBRANZA> zFISCOBRANZA) {
        for ( int i = 0 ; i < depositTable.getRowCount() ; i++ ){
            ZFISCOBRANZA zfc = new ZFISCOBRANZA();
            zfc.setID(1);
            System.out.print("1\t");
            zfc.setMANDT(of.createZFISCOBRANZAMANDT(Shared.getConfig("mant")));
            System.out.print(Shared.getConfig("mant")+ "\t");
            zfc.setFECHA(of.createZFISCOBRANZAFECHA(date4sap));
            System.out.print(date4sap+ "\t");
            zfc.setWERKS(of.createZFISCOBRANZAWERKS(Shared.getConfig("storePrefix") + Shared.getConfig("storeName")));
            System.out.print(Shared.getConfig("storePrefix") + Shared.getConfig("storeName")+ "\t");
            zfc.setWAERS(of.createZFISCOBRANZAWAERS(Shared.getConfig("waerks")));
            System.out.print(Shared.getConfig("waerks")+"\t");
            String bancoId = ((String)depositTable.getValueAt(i, 0)).split("-")[0].trim();
            zfc.setSIMBO(of.createZFISCOBRANZASIMBO(bancoId));
            System.out.print(bancoId+"\t");
            zfc.setMPAGO( of.createZFISCOBRANZAMPAGO( "E" ) );
            System.out.print("E"+"\t");
            zfc.setBPAGO(of.createZFISCOBRANZABPAGO(bancoId));
            System.out.print(bancoId+"\t");
            zfc.setLOTE(of.createZFISCOBRANZALOTE(((String)depositTable.getValueAt(i, 1))));
            System.out.print(((String)depositTable.getValueAt(i, 1))+"\t");
            zfc.setMONTO(new BigDecimal(((String)depositTable.getValueAt(i, 2)).replace(',','.')));
            System.out.print(((String)depositTable.getValueAt(i, 2)).replace(',','.')+"\t");
            zfc.setITEMTEXT(of.createZFISCOBRANZAITEMTEXT(""));
            System.out.print(""+"\t");
            zFISCOBRANZA.add(zfc);
            System.out.println("");
        }
    }
}
