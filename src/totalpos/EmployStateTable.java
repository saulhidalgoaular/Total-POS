/*
 * EmployStateTable.java
 *
 * Created on 29-feb-2012, 18:47:18
 */

package totalpos;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;


/**
 *
 * @author Saúl Hidalgo.
 */
public class EmployStateTable extends javax.swing.JInternalFrame {

    private String myDay;
    public boolean isOk = false;

    private void updateFreeDay() throws SQLException{
        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Actualizar free day");
        List<FreeDay> freedDays = ConnectionDrivers.listAllFreeDays(myDay);

        DefaultTableModel model = (DefaultTableModel) freeDayTable.getModel();
        model.setRowCount(0);

        /*JComboBox jcb = new JComboBox();
        List<Employ> employs = ConnectionDrivers.getAllEmployees();
        for (Employ employ : employs) {
            jcb.addItem( employ.getName4Menu() );
        }

        DefaultTableCellRenderer renderer =
                new DefaultTableCellRenderer();
        renderer.setToolTipText("Click para ver las opciones");
        TableColumn conceptColumn = freeDayTable.getColumnModel().getColumn(0);
        conceptColumn.setCellRenderer(renderer);
        conceptColumn.setCellEditor(new DefaultCellEditor(jcb));*/

        JComboBox jcbConcept = new JComboBox();
        String[] allConcepts = Shared.getConfig("freeDayConcept").split(",");
        jcbConcept.addItem(" ");
        for (String concept : allConcepts) {
            jcbConcept.addItem(concept);
        }

        DefaultTableCellRenderer renderer =
                new DefaultTableCellRenderer();
        renderer.setToolTipText("Click para ver las opciones");
        TableColumn conceptColumn = freeDayTable.getColumnModel().getColumn(1);
        conceptColumn.setCellRenderer(renderer);
        conceptColumn.setCellEditor(new DefaultCellEditor(jcbConcept));

        Set<String> employesAdded = new TreeSet<String>();
        
        for (FreeDay fd : freedDays) {
            String[] s = {fd.getEmploy().getName4Menu(),fd.getConcept(), fd.getExtraHours()};
            model.addRow(s);
            employesAdded.add(fd.getEmploy().getCode());
        }

        List<Employ> l = ConnectionDrivers.getAllEmployees();
        for (Employ employ : l) {
            if ( !employesAdded.contains(employ.getCode()) ){
                String[] s = {employ.getName4Menu()," ", "0"};
                model.addRow(s);
            }
        }

    }

    private void updateOverTime() throws SQLException{
        
        /*DefaultTableModel model = (DefaultTableModel) overTimeTable.getModel();
        model.setRowCount(0);

        JComboBox jcb = new JComboBox();
        List<Employ> employs = ConnectionDrivers.getAllEmployees();
        for (Employ employ : employs) {
            jcb.addItem( employ.getName4Menu() );
        }

        DefaultTableCellRenderer renderer =
                new DefaultTableCellRenderer();
        renderer.setToolTipText("Click para ver las opciones");
        TableColumn conceptColumn = overTimeTable.getColumnModel().getColumn(0);
        conceptColumn.setCellRenderer(renderer);
        conceptColumn.setCellEditor(new DefaultCellEditor(jcb));

        for (OverTime ot : overTimes) {
            String[] s = {ot.getEmploy().getName4Menu() , ot.getHours() + ""};
            model.addRow(s);
        }*/
    }

    private void updateFingerPrints() throws SQLException{
        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Actualizar marcaciones");
        List<Presence4Print> fp = ConnectionDrivers.listAllPresence4Print(myDay);
        DefaultTableModel model = (DefaultTableModel) fingerPrintTable.getModel();
        model.setRowCount(0);

        for (Presence4Print presence4Print : fp) {
            String n4m = "Empleado Eliminado";
            if ( presence4Print.getE() != null ){
                n4m = presence4Print.getE().getName4Menu();
            }
            String []s = {n4m , presence4Print.getMark1() , presence4Print.getMark2() , presence4Print.getMark3() , presence4Print.getMark4()};
            model.addRow(s);
        }
    }

    /** Creates new form EmployStateTable */
    public EmployStateTable(String day) {
        initComponents();
        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Invocando a constructor");
        myDay = day;
        updateAll();
        String[] days = day.split("-");
        titleLabel.setText(titleLabel.getText() + " " + days[2] + "/" + days[1] + "/" + days[0]);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        titleLabel = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        fingerPrintTable = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        saveFreeDay = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        freeDayTable = new javax.swing.JTable();

        setClosable(true);
        setIconifiable(true);
        setTitle("Tabla de Asistencia Manual de Empleados");
        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                formFocusGained(evt);
            }
        });

        titleLabel.setFont(new java.awt.Font("Courier New", 1, 24));
        titleLabel.setText("Asistencia de Empleados");
        titleLabel.setName("titleLabel"); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Presentes"));
        jPanel1.setName("jPanel1"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        fingerPrintTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Empleado", "Entrada", "Salida", "Entrada", "Salida"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        fingerPrintTable.setName("fingerPrintTable"); // NOI18N
        fingerPrintTable.getTableHeader().setReorderingAllowed(false);
        fingerPrintTable.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fingerPrintTableFocusGained(evt);
            }
        });
        jScrollPane1.setViewportView(fingerPrintTable);
        fingerPrintTable.getColumnModel().getColumn(0).setPreferredWidth(250);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 678, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 468, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Día libre o Medio día"));
        jPanel2.setName("jPanel2"); // NOI18N

        saveFreeDay.setText("Guardar");
        saveFreeDay.setName("saveFreeDay"); // NOI18N
        saveFreeDay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveFreeDayActionPerformed(evt);
            }
        });

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        freeDayTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Empleado", "Concepto", "Horas"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        freeDayTable.setName("freeDayTable"); // NOI18N
        freeDayTable.getTableHeader().setReorderingAllowed(false);
        freeDayTable.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                freeDayTableFocusGained(evt);
            }
        });
        jScrollPane2.setViewportView(freeDayTable);
        freeDayTable.getColumnModel().getColumn(0).setPreferredWidth(250);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(saveFreeDay, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 439, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(saveFreeDay)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(titleLabel))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(titleLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void saveFreeDayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveFreeDayActionPerformed
        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Guardar todo");
        DefaultTableModel model = (DefaultTableModel) freeDayTable.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            for (int j = 0; j < 2; j++) {
                if ( model.getValueAt(i, j) == null || ((String)model.getValueAt(i, j)).isEmpty() ){
                    MessageBox msg = new MessageBox(MessageBox.SGN_CAUTION, "Todos los campos son obligatorios!");
                    msg.show(this);
                    return;
                }
            }
        }
        try {
            ConnectionDrivers.deleteAllFreeDay(myDay);
            ConnectionDrivers.deleteAllOverTime(myDay);
            ConnectionDrivers.createFreeDay(model,myDay);
            updateAll();
            MessageBox msg = new MessageBox(MessageBox.SGN_SUCCESS, "Guardado correctamente");
            msg.show(this);
        } catch (SQLException ex) {
            MessageBox msb = new MessageBox(MessageBox.SGN_CAUTION, "Problemas con la base de datos.",ex);
            msb.show(this);
            this.dispose();
            Shared.reload();
        }
    }//GEN-LAST:event_saveFreeDayActionPerformed

    private void formFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusGained
        
    }//GEN-LAST:event_formFocusGained

    private void fingerPrintTableFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_fingerPrintTableFocusGained
        
    }//GEN-LAST:event_fingerPrintTableFocusGained

    private void freeDayTableFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_freeDayTableFocusGained
        
    }//GEN-LAST:event_freeDayTableFocusGained


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable fingerPrintTable;
    private javax.swing.JTable freeDayTable;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton saveFreeDay;
    private javax.swing.JLabel titleLabel;
    // End of variables declaration//GEN-END:variables

    private void updateAll() {
        System.out.println("[" + Shared.now() + "] " + this.getClass().getName() + " " + Shared.lineNumber() +  " Actualizar todo");
        try {
            updateFreeDay();
            updateOverTime();
            updateFingerPrints();

            isOk = true;
        } catch (SQLException ex) {
            MessageBox msg = new MessageBox(MessageBox.SGN_DANGER, "Problemas con la base de datos.", ex);
            msg.show(Shared.getMyMainWindows());
        }
    }

}
