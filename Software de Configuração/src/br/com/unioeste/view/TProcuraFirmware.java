package br.com.unioeste.view;

import java.awt.Frame;
import java.io.File;
import javax.swing.UIManager;

public class TProcuraFirmware extends javax.swing.JDialog {

    private int source;
    private final int UPLOAD = 0;
    private String urlDownload;

    public String getUrlDownload() {
        return urlDownload;
    }

    public TProcuraFirmware(Frame owner) {
        super(owner);

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
        jLabel1 = new javax.swing.JLabel();

        setLocationByPlatform(true);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                teste(evt);
            }
        });
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
        jButton1.setBounds(540, 400, 40, 20);

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
        jButton2.setBounds(540, 370, 40, 20);

        jFileChooser1.setControlButtonsAreShown(false);
        jFileChooser1.setDialogTitle("Procura Arquivo");
        jFileChooser1.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        jFileChooser1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFileChooser1ActionPerformed(evt);
            }
        });
        getContentPane().add(jFileChooser1);
        jFileChooser1.setBounds(0, 30, 580, 400);

        jLabel1.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel1.setText("Atenção, a pasta e nome de arquivo não devem conter ESPAÇOS ou caracteres especiais!");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(10, 10, 530, 17);

        setSize(new java.awt.Dimension(599, 471));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        TPrincipal.firmwareEspecifico = new File(jFileChooser1.getSelectedFile().toString());
        this.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        //this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jFileChooser1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFileChooser1ActionPerformed
}//GEN-LAST:event_jFileChooser1ActionPerformed

    private void teste(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_teste

    }//GEN-LAST:event_teste


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}
