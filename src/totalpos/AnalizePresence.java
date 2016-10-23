/*
 * AnalizePresence.java
 *
 * Created on 06-mar-2012, 18:41:25
 */

package totalpos;

import java.awt.Color;
import java.awt.Component;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Saúl Hidalgo.
 */
public class AnalizePresence extends javax.swing.JInternalFrame implements Doer{

    Date fromDate;
    Date untilDate;
    String fromDateString;
    String untilDateString;
    public boolean isOk = false;
    List<String> header;
    private int offset;
    Map<String, Integer> map4Employs;
    protected Working workingFrame;
    protected boolean isCestaticket;
    protected boolean isSend2Profit = false;

    /** Creates new form AnalizePresence */
    public AnalizePresence(String fd, String ud, boolean isCestaticketA) {
        System.out.println(this.getClass().getName() + " " + Shared.lineNumber() +  "Invocado constructor");
        try {
            initComponents();
            fromDate = Shared.sdfDay2DB.parse(fd);
            untilDate = Shared.sdfDay2DB.parse(ud);
            fromDateString = fd;
            untilDateString = ud;
            this.isCestaticket = isCestaticketA;

            if ( isCestaticket ){
                System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Evaluando cestatickets");
                createHeaderWithCestatickets();
            }else{
                System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Evaluando asistencia");
                createHeaderWithoutCestatickets();
            }
            createMap4Employees();
            setSizes2Table();
            if ( !isCestaticket && !ConnectionDrivers.loadHours( presenceTable, fromDateString,
                    untilDateString, fromDate, untilDate, this) ){
                ConnectionDrivers.calculateExtraHours((DefaultTableModel) presenceTable.getModel(),
                        fromDateString, untilDateString, map4Employs, offset, presenceTable);
            }
            if ( !ConnectionDrivers.loadPresence(presenceTable, fromDateString,
                    untilDateString, fromDate, untilDate, this, map4Employs, offset) ){
                ConnectionDrivers.calculatePresence((DefaultTableModel) presenceTable.getModel(),
                        fromDateString, untilDateString, map4Employs, offset);
            }

            String[] t = fd.split("-");
            fromLabelDate.setText(t[2] + "/" + t[1] + "/" + t[0]);

            t = ud.split("-");
            untilLabelDate.setText(t[2] + "/" + t[1] + "/" + t[0]);

            System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Termino");
            isOk = true;
            System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Construido satisfactoriamente");
        } catch (SQLException ex) {
            Logger.getLogger(AnalizePresence.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void close() {
        workingFrame.setVisible(false);
    }

    @Override
    public void doIt() {
        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Operando!");
        if ( isSend2Profit ){
            send2Profit();
        }else{
            try {
                System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Guardando tabla...");
                ConnectionDrivers.saveTable((DefaultTableModel) presenceTable.getModel(), fromDateString, untilDateString, offset, isCestaticket);
                String[] t = new String[header.size()];
                int i = 0;
                for (String tt : header) {
                    t[i++] = tt;
                }
                new CreateReportFromTable(presenceTable, "Control de Asistencias - Agencia " + Shared.getConfig("storeName"));
            } catch (Exception ex) {
                System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " ERROR = " + ex.getMessage());
                Logger.getLogger(AnalizePresence.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void setSizes2Table(){
        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Calculando tamano para la tabla");
        for ( int i = offset ; i < presenceTable.getColumnModel().getColumnCount() ; i++ ){
            presenceTable.getColumnModel().getColumn(i).setPreferredWidth(50);
        }
        presenceTable.getColumnModel().getColumn(1).setPreferredWidth(250);
        presenceTable.getColumnModel().getColumn(0).setPreferredWidth(90);
    }

    private void createMap4Employees() throws SQLException{
        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Creando mapa de empleados con sus respectivos codigos");
        Map<String, Integer> map = new TreeMap<String, Integer>();
        List<Employ> employs = ConnectionDrivers.getAllEmployBetween(fromDateString, untilDateString);
        String[][] tableModel = new String[employs.size()][3];
        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Consegui " + employs.size() + " Empleados");
        for (int i = 0 ; i < employs.size() ; ++i ) {
            tableModel[i][0] = employs.get(i).getCode();
            tableModel[i][1] = employs.get(i).getName();
            tableModel[i][2] = employs.get(i).getDepartment();
            map.put(tableModel[i][0], i);
        }

        presenceTable.setModel(new DefaultTableModel(tableModel,header.toArray()){
            @Override
             public boolean isCellEditable (int row, int column) {
               if (column < 3){
                    return false;
               }
               return true;
            }
        });

        map4Employs = map;
        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Creado el mapa");
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        presenceTable = new javax.swing.JTable(){
            @Override public Component prepareRenderer(TableCellRenderer renderer, int row, int column){
                Component comp = super.prepareRenderer(renderer, row, column);
                String[] s = presenceTable.getModel().getColumnName(column).split(" ");
                if ( Shared.isSunday(presenceTable, column) || Shared.isSaturday(presenceTable, column) || ( s.length > 1 && Shared.isHoliday( s[1] )) ){
                    comp.setBackground(Color.YELLOW);
                }else{
                    comp.setBackground(Constants.transparent);
                }
                return comp;
            }
        };
        titleLabel = new javax.swing.JLabel();
        fromLabel = new javax.swing.JLabel();
        untilLabel = new javax.swing.JLabel();
        fromLabelDate = new javax.swing.JLabel();
        untilLabelDate = new javax.swing.JLabel();
        send2ProfitButton = new javax.swing.JButton();
        departmentNameLabeLabel = new javax.swing.JLabel();
        saveButton = new javax.swing.JButton();
        recalcularAllButton = new javax.swing.JButton();
        exportButton = new javax.swing.JButton();

        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Control de Asistencias y Horas Extras");
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

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        presenceTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        presenceTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        presenceTable.setName("presenceTable"); // NOI18N
        presenceTable.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        presenceTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(presenceTable);

        titleLabel.setFont(new java.awt.Font("Courier New", 1, 18));
        titleLabel.setText("Control de Asistencias y Horas Extras");
        titleLabel.setName("titleLabel"); // NOI18N

        fromLabel.setFont(new java.awt.Font("Courier New", 1, 14));
        fromLabel.setText("Nómina desde:");
        fromLabel.setName("fromLabel"); // NOI18N

        untilLabel.setFont(new java.awt.Font("Courier New", 1, 14));
        untilLabel.setText("Hasta");
        untilLabel.setName("untilLabel"); // NOI18N

        fromLabelDate.setFont(new java.awt.Font("Courier New", 1, 14));
        fromLabelDate.setText("Nómina desde:");
        fromLabelDate.setName("fromLabelDate"); // NOI18N

        untilLabelDate.setFont(new java.awt.Font("Courier New", 1, 14));
        untilLabelDate.setText("Nómina desde:");
        untilLabelDate.setName("untilLabelDate"); // NOI18N

        send2ProfitButton.setFont(new java.awt.Font("Courier New", 0, 12));
        send2ProfitButton.setText("Enviar a Profit");
        send2ProfitButton.setName("send2ProfitButton"); // NOI18N
        send2ProfitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                send2ProfitButtonActionPerformed(evt);
            }
        });

        departmentNameLabeLabel.setFont(new java.awt.Font("Courier New", 1, 14));
        departmentNameLabeLabel.setName("departmentNameLabeLabel"); // NOI18N

        saveButton.setFont(new java.awt.Font("Courier New", 0, 12));
        saveButton.setText("Guardar Cambios");
        saveButton.setName("saveButton"); // NOI18N
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        recalcularAllButton.setFont(new java.awt.Font("Courier New", 0, 12));
        recalcularAllButton.setText("Borrar Cambios Horas");
        recalcularAllButton.setName("recalcularAllButton"); // NOI18N
        recalcularAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                recalcularAllButtonActionPerformed(evt);
            }
        });

        exportButton.setFont(new java.awt.Font("Courier New", 1, 12));
        exportButton.setText("Guardar y Exportar");
        exportButton.setName("exportButton"); // NOI18N
        exportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1110, Short.MAX_VALUE)
                    .addComponent(titleLabel)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(fromLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fromLabelDate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(untilLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(untilLabelDate)
                        .addGap(108, 108, 108)
                        .addComponent(departmentNameLabeLabel))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(send2ProfitButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(saveButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 111, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(recalcularAllButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 206, Short.MAX_VALUE)
                            .addComponent(exportButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(titleLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fromLabel)
                    .addComponent(untilLabel)
                    .addComponent(fromLabelDate)
                    .addComponent(untilLabelDate)
                    .addComponent(departmentNameLabeLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 417, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(send2ProfitButton)
                    .addComponent(recalcularAllButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(saveButton)
                    .addComponent(exportButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        try {
            System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Invocada funcion guardar");
            ConnectionDrivers.saveTable((DefaultTableModel) presenceTable.getModel(), fromDateString, untilDateString, offset, isCestaticket);
            MessageBox msb = new MessageBox(MessageBox.SGN_SUCCESS, "Guardado Satisfactoriamente.");
            msb.show(null);
        } catch (Exception ex) {
            System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " ERROR " +ex.getMessage());
            Logger.getLogger(AnalizePresence.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }//GEN-LAST:event_saveButtonActionPerformed

    private void recalcularAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_recalcularAllButtonActionPerformed
        try {
            System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Invocado recalcular todo ");
            clearHourTable();
            ConnectionDrivers.calculateExtraHours((DefaultTableModel) presenceTable.getModel(),
                        fromDateString, untilDateString, map4Employs, offset, presenceTable);
            System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Recalculado todo satisfactoriamente");
        } catch (SQLException ex) {
            System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " ERROR " + ex.getMessage());
            MessageBox msb = new MessageBox(MessageBox.SGN_DANGER, "No se ha cargado la información correctamente.",ex);
            msb.show(null);
        }
    }//GEN-LAST:event_recalcularAllButtonActionPerformed

    private void exportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportButtonActionPerformed
        workingFrame = new Working((JFrame) Shared.getMyMainWindows());

        WaitSplash ws = new WaitSplash(this);

        Shared.centerFrame(workingFrame);
        workingFrame.setVisible(true);
        ws.execute();
    }//GEN-LAST:event_exportButtonActionPerformed

    private void send2ProfitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_send2ProfitButtonActionPerformed

        isSend2Profit = true;
        workingFrame = new Working((JFrame) Shared.getMyMainWindows());

        WaitSplash ws = new WaitSplash(this);

        Shared.centerFrame(workingFrame);
        workingFrame.setVisible(true);
        ws.execute();

    }//GEN-LAST:event_send2ProfitButtonActionPerformed

    private void send2Profit(){

        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Enviando a Profit ");
        Connection c = null;
        try{
            if ( !ConnectionDrivers.isConnected2Profit() ){
                System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Conectando con Profit nómina...");
                ConnectionDrivers.initializeProfit();
                System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Conectado satisfactoriamente");
            }else{
                System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Utilizando conexion existente con Profit Nomina");
            }

            System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Iniciando con Horas extras...");

            c = ConnectionDrivers.cpdsProfit.getConnection();
            c.setAutoCommit(false);

            ConnectionDrivers.clearBonuses(c);

            //Extrahours
            for( int i = 0 ; i < presenceTable.getRowCount() ; i++ ){
                String employId = (String) presenceTable.getValueAt(i, 0);

                if ( !isCestaticket ){

                    double hours = .0;
                    double specialBonus = .0;


                    // TODO QUITAR...
                    /*if ( employId.compareTo("017053") >= 0 ){
                        continue;
                    }*/

                    Object curStr = presenceTable.getValueAt(i, 3);
                    if ( curStr != null && !curStr.toString().isEmpty() ){
                        hours = Double.parseDouble(curStr.toString());
                    }

                    if ( hours > 4.0 ){
                        specialBonus = hours - 4.0;
                        hours = 4.0;
                    }
                    if ( presenceTable.getValueAt(i, 2) != null ){
                        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Horas Extras Empleado " + employId + " con " + hours + " " + presenceTable.getValueAt(i, 2).toString());
                    }else{
                        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Horas Extras Empleado " + employId + " con " + hours + " null");
                    }

                    System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " agregando horas extras a empleado ");
                    ConnectionDrivers.addBonus(hours, employId, Shared.getConfig("conceptExtraHour"), c, "");

                    curStr =  presenceTable.getValueAt(i, 3);
                    
                    if ( specialBonus > .0 ){
                        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Bono especial 2 Empleado " + employId + " con " + hours + " " + curStr.toString());
                        ConnectionDrivers.addBonus(specialBonus, employId, Shared.getConfig("conceptSpecialBonus"), c, "");
                    }

                    curStr = presenceTable.getValueAt(i, 5);
                    if ( curStr != null && !curStr.toString().isEmpty() ){
                        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Bono nocturno Empleado " + employId + " con " + hours + " " + curStr.toString());
                        ConnectionDrivers.addBonus(Double.parseDouble(curStr.toString()) * Double.parseDouble(Shared.getConfig("nightBonus")), employId, Shared.getConfig("conceptNightBonus"), c, "");
                    }

                    curStr = presenceTable.getValueAt(i, 10);
                    if ( curStr != null && !curStr.toString().isEmpty() ){
                        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Descuento " + employId + " con " + hours + " " + curStr.toString());
                        ConnectionDrivers.addBonus(Double.parseDouble(curStr.toString()), employId, Shared.getConfig("conceptFingerPrintDiscount"), c ,"");
                    }

                    curStr = presenceTable.getValueAt(i, 6);
                    if ( curStr != null && !curStr.toString().isEmpty() ){
                        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Bono de Asistencia " + employId + " con " + Double.parseDouble(curStr.toString()) + " " + curStr.toString());
                        ConnectionDrivers.addBonus(Double.parseDouble(curStr.toString()), employId, Shared.getConfig("conceptPresenceBonus"), c , "");
                    }

                    curStr = presenceTable.getValueAt(i, 7);
                    if ( curStr != null && !curStr.toString().isEmpty() ){
                        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Bono de Produccion " + employId + " con " + Double.parseDouble(curStr.toString()) + " " + curStr.toString());
                        ConnectionDrivers.addBonus(Double.parseDouble(curStr.toString()), employId, Shared.getConfig("conceptProductionBonus"), c , "");
                    }
                }

                double toDiscount = .0;
                double sundaysWorked = .0;
                double saturdatWorked = .0;
                String comment2discount = "";
                String commentSundaysWorked = "";
                String commentSaturdayWorked = "";

                for ( int j = offset ; j < presenceTable.getColumnCount() ; j++ ){

                    if ( isCestaticket && ( presenceTable.getValueAt(i, j) != null  &&  presenceTable.getValueAt(i, j).equals("L") ) ){
                        toDiscount += 1.0;
                    }
                    if ( presenceTable.getValueAt(i, j) == null || !Shared.didItCome(presenceTable.getValueAt(i, j).toString()) ){
                        if ( presenceTable.getValueAt(i, j) == null || presenceTable.getValueAt(i, j).equals("") || presenceTable.getValueAt(i, j).equals("N") ){
                            double f = .0;
                            if ( Shared.isSunday(presenceTable, j) ){
                                f = Double.parseDouble(Shared.getConfig("sundayNotWorked"));
                            }else if ( Shared.isSaturday(presenceTable, j) ){
                                f = Double.parseDouble(Shared.getConfig("saturdayNotWorked"));
                            }else if ( Shared.isHoliday(presenceTable.getModel().getColumnName(j).split(" ")[1]) ){
                                f = Double.parseDouble(Shared.getConfig("holidayNotWorked"));
                            }else{
                                f = 1.0;
                            }
                            toDiscount += f ;
                            if ( f != 0 ){
                                comment2discount += presenceTable.getModel().getColumnName(j).split(" ")[1] + "; ";
                            }
                        }else if ( presenceTable.getValueAt(i, j).equals("M") || presenceTable.getValueAt(i, j).equals("ML") ){
                            toDiscount += .5;
                            comment2discount += "Medio dia " + presenceTable.getModel().getColumnName(j).split(" ")[1] + "; ";
                            System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Descontado Medio Dia " + presenceTable.getValueAt(i, j));
                        }
                    }else if ( Shared.isHoliday(presenceTable.getModel().getColumnName(j).split(" ")[1]) ||
                             Shared.isSunday(presenceTable, j)){
                        ++sundaysWorked;
                        commentSundaysWorked += presenceTable.getModel().getColumnName(j).split(" ")[1] + "; ";
                    }else if ( Shared.isSaturday(presenceTable, j) ){
                        ++saturdatWorked;
                        commentSaturdayWorked += presenceTable.getModel().getColumnName(j).split(" ")[1] + "; ";
                    }
                }
                if ( toDiscount > .0 ){
                    System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Descontar a " + employId + " " + toDiscount);
                    ConnectionDrivers.addBonus(toDiscount, employId, isCestaticket?Shared.getConfig("conceptNotPresenceTicket"):Shared.getConfig("conceptNotPresence"), c , comment2discount);
                }

                if ( !isCestaticket ){
                    if ( Double.parseDouble(Shared.getConfig("sundayWorked"))*sundaysWorked > .0 ){
                        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Domingos trabajados " + employId + " " + sundaysWorked + " " + commentSundaysWorked);
                        ConnectionDrivers.addBonus(sundaysWorked, employId, Shared.getConfig("conceptSundaysWorked"), c , commentSundaysWorked);
                    }

                    if ( Double.parseDouble(Shared.getConfig("saturdayWorked"))*saturdatWorked > .0 ){
                        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Sabados trabajados " + employId + " " + saturdatWorked);
                        ConnectionDrivers.addBonus(saturdatWorked, employId, Shared.getConfig("conceptSaturdayWorked"), c , commentSaturdayWorked);
                    }
                }else{
                    ConnectionDrivers.addBonus(saturdatWorked + sundaysWorked, employId, Shared.getConfig("conceptAdditionalTicket"), c , commentSaturdayWorked);
                }

            }

            System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Justo antes del commit...");
            c.commit();
            c.setAutoCommit(true);
            MessageBox msb = new MessageBox(MessageBox.SGN_SUCCESS, "Enviado satisfactoriamente");
            msb.show(null);
        }catch(Exception ex){
            try {
                c.rollback();
                System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Rolled back =(");
                MessageBox msb = new MessageBox(MessageBox.SGN_DANGER, "No se han enviado los datos.", ex);
                msb.show(null);
            } catch (SQLException ex1) {
                Logger.getLogger(AnalizePresence.class.getName()).log(Level.SEVERE, null, ex1);
                // We are in problems =(
            }
        }finally{
            try {
                if (c != null && !c.isClosed()) {
                    c.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(AnalizePresence.class.getName()).log(Level.SEVERE, null, ex);
                // We are in problems =(
            }
        }
    }

    private void formInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosing
        Object[] options = {"Si","No","Cancelar"};
        int n = JOptionPane.showOptionDialog(this,"¿Desea guardar la planilla antes de salir?",
                Constants.appName,
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                null);

        if ( n == 2 ) return;

        if ( n == 0 ) {
            try {
                ConnectionDrivers.saveTable((DefaultTableModel) presenceTable.getModel(), fromDateString, untilDateString, offset, isCestaticket);
            } catch (Exception ex) {
                MessageBox msb = new MessageBox(MessageBox.SGN_DANGER, "Error desconocido", ex);
                msb.show(Shared.getMyMainWindows());
            }
        }

        this.dispose();

    }//GEN-LAST:event_formInternalFrameClosing


    protected void createHeaderWithoutCestatickets(){
        header = new LinkedList<String>();
        header.add("Código");
        header.add("Empleado");
        header.add("Departamento");
        header.add("Horas");
        header.add("Horas extras huella");
        header.add("Bono Nocturno");
        header.add("Bono de Asistencia");
        header.add("Bono Produccion");
        header.add("Leves");
        header.add("Graves");
        header.add("Descuento");
        offset = header.size();
        Date t = fromDate;
        Calendar c = Calendar.getInstance();
        c.setTime(t);

        String[] dayName = Shared.getConfig("dayName").split(",");

        while(t.before(untilDate) || t.equals(untilDate)){
            header.add(dayName[t.getDay()] + " " + Shared.df2int2p.format(t.getDate())+ "/" + Shared.df2int2p.format(t.getMonth()+1));
            c.setTime(t);
            c.add(Calendar.DATE, 1);
            t = c.getTime();
        }
    }

    protected void createHeaderWithCestatickets(){
        header = new LinkedList<String>();
        header.add("Código");
        header.add("Empleado");
        header.add("Departamento");
        offset = header.size();
        Date t = fromDate;
        Calendar c = Calendar.getInstance();
        c.setTime(t);

        String[] dayName = Shared.getConfig("dayName").split(",");

        while(t.before(untilDate) || t.equals(untilDate)){
            header.add(dayName[t.getDay()] + " " + Shared.df2int2p.format(t.getDate())+ "/" + Shared.df2int2p.format(t.getMonth()+1));
            c.setTime(t);
            c.add(Calendar.DATE, 1);
            t = c.getTime();
        }
    }

    private void clearHourTable(){
        for (int i = 0; i < presenceTable.getRowCount(); i++) {
            for (int j = 3; j < presenceTable.getColumnCount() && j < offset; j++) {
                presenceTable.setValueAt(null, i, j);
            }
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel departmentNameLabeLabel;
    private javax.swing.JButton exportButton;
    private javax.swing.JLabel fromLabel;
    private javax.swing.JLabel fromLabelDate;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable presenceTable;
    private javax.swing.JButton recalcularAllButton;
    private javax.swing.JButton saveButton;
    private javax.swing.JButton send2ProfitButton;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JLabel untilLabel;
    private javax.swing.JLabel untilLabelDate;
    // End of variables declaration//GEN-END:variables

}
