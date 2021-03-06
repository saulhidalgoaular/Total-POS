/*
 * PasswordNeeded.java
 *
 * Created on 21-jul-2011, 10:39:52
 */

package totalpos;

import java.awt.Frame;
import java.sql.SQLException;
import javax.swing.JDialog;

/**
 *
 * @author shidalgo
 */
public class PasswordNeeded extends JDialog {

    User user;
    private boolean isOk = false;
    Frame parent;

    /** Creates new form PasswordNeeded */
    public PasswordNeeded(java.awt.Frame parent, boolean modal, User u) {
        super(parent, modal);
        initComponents();

        this.user = u;
        this.parent = parent;
        descriptionLabel.setText(("Introduzca la contraseña " + (user.getNombre()!=null && !user.getNombre().isEmpty()?" para " + user.getNombre():" para " + user.getLogin())));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        descriptionLabel = new javax.swing.JLabel();
        passwordField = new javax.swing.JPasswordField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(Constants.appName);
        setResizable(false);

        descriptionLabel.setFont(new java.awt.Font("Courier New", 1, 14)); // NOI18N
        descriptionLabel.setText("Introduzca la contraseña");
        descriptionLabel.setName("descriptionLabel"); // NOI18N

        passwordField.setName("passwordField"); // NOI18N
        passwordField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                passwordFieldActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(passwordField, javax.swing.GroupLayout.DEFAULT_SIZE, 351, Short.MAX_VALUE)
                    .addComponent(descriptionLabel))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(descriptionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void passwordFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_passwordFieldActionPerformed
        try {
            ConnectionDrivers.login(this.user.getLogin(), passwordField.getPassword());
            isOk = true;
            Shared.userInsertedPasswordOk(this.user.getLogin());
            this.setVisible(false);
            dispose();
        } catch (SQLException ex) {
            MessageBox msg = new MessageBox(MessageBox.SGN_CAUTION, "No se pudo establecer conexión con la base de datos.");
            msg.show(this);
        } catch (Exception ex) {
            MessageBox msg = new MessageBox(MessageBox.SGN_CAUTION, ex.getMessage(), ex);
            msg.show(this);
            String wrongPasswordMsg = Shared.getConfig("wrongPasswordMsg");
            if ( ex.getMessage().equals(wrongPasswordMsg) ){
                try {
                    Shared.userTrying(this.user.getLogin());
                } catch (Exception ex1) {
                    String userLocked = Shared.getConfig("userLocked");
                    msg = new MessageBox(MessageBox.SGN_CAUTION,
                                (ex1.getMessage().equals(userLocked)? userLocked :ex1.getMessage()),
                                ex1);
                    msg.show(null);
                    this.dispose();
                    Shared.reload();
                }
            }else{
                msg = new MessageBox(MessageBox.SGN_CAUTION, ex.getMessage() , ex);
                msg.show(null);
            }
        }
        
    }//GEN-LAST:event_passwordFieldActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel descriptionLabel;
    private javax.swing.JPasswordField passwordField;
    // End of variables declaration//GEN-END:variables

    boolean isPasswordOk() {
        return isOk;
    }

}
