package br.com.unioeste.view;

import java.io.File;
import javax.swing.JFrame;
import javax.swing.UIManager;

public class TProcuraArquivo extends javax.swing.JDialog {

    private int source;
    private final int UPLOAD = 0;
    private final int DOWNLOAD = 1;
    private String urlDownload;

    public String getUrlDownload() {
        return urlDownload;
    }

    public TProcuraArquivo(int source) {
        this.source = source;
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception exception) {
            System.out.println("Não foi possivel alterar o LookAndFeel para padrão Windows");
        }
        initComponents();
        this.setModal(true);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jFileChooser1 = new javax.swing.JFileChooser();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setLocationByPlatform(true);
        getContentPane().setLayout(null);

        jButton1.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/overtech/view/Icones/deletar.png"))); // NOI18N
        jButton1.setBorderPainted(false);
        jButton1.setContentAreaFilled(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1);
        jButton1.setBounds(540, 365, 40, 20);

        jButton2.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/overtech/view/Icones/accept.png"))); // NOI18N
        jButton2.setBorderPainted(false);
        jButton2.setContentAreaFilled(false);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton2);
        jButton2.setBounds(540, 340, 40, 20);

        jFileChooser1.setControlButtonsAreShown(false);
        jFileChooser1.setDialogTitle("Procura Arquivo");
        jFileChooser1.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        jFileChooser1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFileChooser1ActionPerformed(evt);
            }
        });
        getContentPane().add(jFileChooser1);
        jFileChooser1.setBounds(0, 0, 580, 400);

        setSize(new java.awt.Dimension(599, 432));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        switch (source) {
            case UPLOAD:
                //TPrincipal.TFArquivoURL.setText(jFileChooser1.getSelectedFile().toString());
                TPrincipal.configFile = new File(jFileChooser1.getSelectedFile().toString());
                this.dispose();
                break;
            case DOWNLOAD:                
                this.dispose();
                break;
        }

    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jFileChooser1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFileChooser1ActionPerformed
}//GEN-LAST:event_jFileChooser1ActionPerformed

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JFileChooser jFileChooser1;
    // End of variables declaration//GEN-END:variables
}
