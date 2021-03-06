/*
 * Logon.java
 *
 * Created on 27-mar-2012, 10:17:54
 */

package totalpos;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author shidalgo
 */
public class Logon extends javax.swing.JFrame implements Doer{

    protected Working workingFrame;

    /** Creates new form Logon */
    public Logon() {
        initComponents();

        Shared.centerFrame(this);
        updateAll();
    }

    private void updateAll(){
        try {
            DefaultTableModel model = (DefaultTableModel) logonTable.getModel();
            model.setRowCount(0);
            Scanner sc = new Scanner(new File(Shared.getConfig("logonFile")));

            while( sc.hasNextLine() ){
                model.addRow(sc.nextLine().split("--"));
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Logon.class.getName()).log(Level.SEVERE, null, ex);
        }
        
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
        logonTable = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        loginButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        addButton = new javax.swing.JButton();
        updateButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Total Pos Logon");

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        logonTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Nombre", "Ip"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        logonTable.setName("logonTable"); // NOI18N
        logonTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(logonTable);

        jLabel1.setFont(new java.awt.Font("Courier New", 1, 24));
        jLabel1.setText("Total Pos Logon");
        jLabel1.setName("jLabel1"); // NOI18N

        loginButton.setText("Acceder");
        loginButton.setName("loginButton"); // NOI18N
        loginButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginButtonActionPerformed(evt);
            }
        });

        saveButton.setText("Guardar");
        saveButton.setName("saveButton"); // NOI18N
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        deleteButton.setText("Eliminar");
        deleteButton.setName("deleteButton"); // NOI18N
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        addButton.setText("Agregar");
        addButton.setName("addButton"); // NOI18N
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        updateButton.setText("Actualizar");
        updateButton.setName("updateButton"); // NOI18N
        updateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 490, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(loginButton, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(updateButton, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(deleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel1))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {addButton, deleteButton, loginButton, saveButton, updateButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(loginButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 150, Short.MAX_VALUE)
                        .addComponent(updateButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(addButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteButton)
                        .addGap(9, 9, 9)
                        .addComponent(saveButton))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        DefaultTableModel model = (DefaultTableModel) logonTable.getModel();
        model.setRowCount(model.getRowCount()+1);
    }//GEN-LAST:event_addButtonActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        DefaultTableModel model = (DefaultTableModel) logonTable.getModel();
        if ( logonTable.getSelectedRow() == -1 ){
            
        }else{
            model.removeRow(logonTable.getSelectedRow());
        }
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void loginButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loginButtonActionPerformed
        if ( logonTable.getSelectedRow() != -1 ){
            /*workingFrame = new Working((Window) Shared.getMyMainWindows());

            WaitSplash ws = new WaitSplash(this);

            Shared.centerFrame(workingFrame);
            workingFrame.setVisible(true);
            ws.execute();*/
            String[] s = new String[1];
            s[0] = logonTable.getValueAt(logonTable.getSelectedRow(), 1).toString();
            Main.main(s);
        }else{
            
        }
    }//GEN-LAST:event_loginButtonActionPerformed

    private void updateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateButtonActionPerformed

        /*DefaultTableModel model = (DefaultTableModel) logonTable.getModel();

        model.setRowCount(0);
        try {
            /*WS ws = new WSService().getWSPort();
            String xmlIp = ws.getIpStoreName();
            System.out.println("WS =  " + xmlIp);

            IXMLParser parser = XMLParserFactory.createDefaultXMLParser();
            IXMLReader reader = StdXMLReader.stringReader(xmlIp);
            parser.setReader(reader);
            IXMLElement xml = (IXMLElement) parser.parse();

            for (Object x : xml.getChildren()) {
                XMLElement xmlI = (XMLElement)x;

                String[] s = {xmlI.getAttribute("A"), xmlI.getAttribute("I")};

                model.addRow(s);
            }

            System.out.println("Termine");
        } catch (XMLException ex) {
            Logger.getLogger(Logon.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Logon.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(Logon.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Logon.class.getName()).log(Level.SEVERE, null, ex);
        }*/
    }//GEN-LAST:event_updateButtonActionPerformed

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed

        PrintWriter pw = null;
        try {
            File f = new File(Shared.getConfig("logonFile"));
            f.delete();
            pw = new PrintWriter(f);

            DefaultTableModel model = (DefaultTableModel) logonTable.getModel();
            for( int i = 0 ; i < model.getRowCount() ; i++ ){
                if ( model.getValueAt(i, 0) == null || model.getValueAt(i, 1) == null ){
                    MessageBox msg = new MessageBox(MessageBox.SGN_CAUTION, "Todos los campos son obligatorios. No se ha guardado la información");
                    msg.show(this);
                    break;
                }
                pw.println(model.getValueAt(i, 0) + "--" + model.getValueAt(i, 1));
            }
        } catch (FileNotFoundException ex) {
            MessageBox msg = new MessageBox(MessageBox.SGN_CAUTION, "No se ha guardado la información",ex);
            msg.show(this);
        } finally {
            pw.close();
        }
        
    }//GEN-LAST:event_saveButtonActionPerformed

    @Override
    public void close() {
        workingFrame.setVisible(false);
    }

    @Override
    public void doIt() {
        
    }



    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Logon().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton loginButton;
    private javax.swing.JTable logonTable;
    private javax.swing.JButton saveButton;
    private javax.swing.JButton updateButton;
    // End of variables declaration//GEN-END:variables

}
