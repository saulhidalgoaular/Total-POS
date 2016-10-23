/*
 * StartSplash.java
 *
 * Created on 08-jul-2011, 11:40:18
 */

package totalpos;

/**
 *
 * @author Saul Hidalgo
 */
class StartSplash extends javax.swing.JFrame {

    /** Creates new form StartSplash */
    public StartSplash() {
        initComponents();
        setSize(485, 172);
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
        loadingLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        statusLabel = new javax.swing.JLabel();
        statusLabel2Change = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(Constants.appName);
        setAlwaysOnTop(true);
        setName("Form"); // NOI18N
        setResizable(false);

        titleLabel.setFont(new java.awt.Font("Courier New", 1, 18)); // NOI18N
        titleLabel.setText(Constants.appName);
        titleLabel.setName("titleLabel"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance().getContext().getResourceMap(StartSplash.class);
        loadingLabel.setFont(resourceMap.getFont("loadingLabel.font")); // NOI18N
        loadingLabel.setText(resourceMap.getString("loadingLabel.text")); // NOI18N
        loadingLabel.setName("loadingLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        statusLabel.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        statusLabel.setText(resourceMap.getString("statusLabel.text")); // NOI18N
        statusLabel.setName("statusLabel"); // NOI18N

        statusLabel2Change.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        statusLabel2Change.setText(resourceMap.getString("statusLabel2Change.text")); // NOI18N
        statusLabel2Change.setName("statusLabel2Change"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(titleLabel)
                    .addComponent(loadingLabel)
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 450, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(statusLabel)
                    .addComponent(statusLabel2Change))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(titleLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(loadingLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(statusLabel2Change)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel loadingLabel;
    public javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel statusLabel;
    public javax.swing.JLabel statusLabel2Change;
    private javax.swing.JLabel titleLabel;
    // End of variables declaration//GEN-END:variables

    void changeStatus(String status, int perc) {
        statusLabel2Change.setText(status);
        progressBar.setValue(perc);
    }

}
