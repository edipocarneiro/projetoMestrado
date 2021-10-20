package br.com.unioeste.view;

import br.com.unioeste.model.PortaAnalogica;
import br.com.unioeste.port.Serial;
import br.com.unioeste.utils.ArduinoUPLOAD;
import br.com.unioeste.utils.LibraryLoader;
import gnu.io.CommPortIdentifier;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Formatter;
import java.util.HashMap;
import java.util.logging.Level;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.text.DefaultCaret;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;

/**
 *
 * @author cpS
 */
public class TPrincipal extends javax.swing.JFrame {

    static final String APP_NAME = "smatdArduino";
    //Variaveis de LOG
    static Logger logger = Logger.getLogger(APP_NAME);
    static FileAppender fileAppender;
    static Serial serial;
    private String codigoEstacao;
    private int timeout = 10000;
    private ArrayList<PortaAnalogica> analogicConfigs = new ArrayList<PortaAnalogica>();
    public static File configFile = null;
    public static File firmwareEspecifico = null;
    HashMap<String, String> linhas = new HashMap<String, String>();
    public static int countClose = 0;
    public String strFinalTeste = "";

    static {
        try {
            System.out.println("carregando DLLs");
            LibraryLoader.loadLibrary();
            System.out.println(System.getProperty("os.name") + " " + System.getProperty("sun.arch.data.model"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public TPrincipal() {
        initComponents();

        //Justificando texto
        StyledDocument doc = jTextPane1.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_JUSTIFIED);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);

        //Carrega portas digitais com informações vazias 
        analogicConfigs.add(new PortaAnalogica(0, "", false, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
        analogicConfigs.add(new PortaAnalogica(1, "", false, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
        analogicConfigs.add(new PortaAnalogica(2, "", false, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
        analogicConfigs.add(new PortaAnalogica(3, "", false, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
        analogicConfigs.add(new PortaAnalogica(4, "", false, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
        analogicConfigs.add(new PortaAnalogica(5, "", false, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
        analogicConfigs.add(new PortaAnalogica(6, "", false, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
        analogicConfigs.add(new PortaAnalogica(7, "", false, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
        analogicConfigs.add(new PortaAnalogica(8, "", false, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
        analogicConfigs.add(new PortaAnalogica(9, "", false, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
        analogicConfigs.add(new PortaAnalogica(10, "", false, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
        analogicConfigs.add(new PortaAnalogica(11, "", false, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
        analogicConfigs.add(new PortaAnalogica(12, "", false, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
        analogicConfigs.add(new PortaAnalogica(13, "", false, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
        analogicConfigs.add(new PortaAnalogica(14, "", false, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
        analogicConfigs.add(new PortaAnalogica(15, "", false, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));

        //Preenche combo portas COM
        fillComboPortas();

        //Faz com que o TextArea sempre atualize para baixo
        DefaultCaret caret = (DefaultCaret) TXConsole.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private void fillComboPortas() {
        System.out.println("preenchendo portas");
        try {
            DefaultComboBoxModel combo = new DefaultComboBoxModel();
            Enumeration portList = CommPortIdentifier.getPortIdentifiers();
            System.out.println(portList.hasMoreElements());
            while (portList.hasMoreElements()) {
                CommPortIdentifier portId = (CommPortIdentifier) portList.nextElement();
                System.out.println(portId);
                if (portId.getName().contains("COM")) {
                    combo.addElement(portId.getName());
                }
            }
            CBPortas.setModel(combo);
            CBPortas.setSelectedIndex(-1);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.fillInStackTrace(), "Erro de LIB", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fillAnalogPorts() {
        try {
            analogicConfigs.set(0, new PortaAnalogica(0, TFIdentificador.getText(), CKAna.isSelected(), new BigDecimal(TFIntervaloIni.getText()), new BigDecimal(TFIntervaloFim.getText()), new BigDecimal(TFOffset.getText())));
        } catch (Exception e) {
        }
        try {
            analogicConfigs.set(1, new PortaAnalogica(1, TFIdentificador2.getText(), CKAna2.isSelected(), new BigDecimal(TFIntervaloIni2.getText()), new BigDecimal(TFIntervaloFim2.getText()), new BigDecimal(TFOffset2.getText())));
        } catch (Exception e) {
        }
        try {
            analogicConfigs.set(2, new PortaAnalogica(2, TFIdentificador3.getText(), CKAna3.isSelected(), new BigDecimal(TFIntervaloIni3.getText()), new BigDecimal(TFIntervaloFim3.getText()), new BigDecimal(TFOffset3.getText())));
        } catch (Exception e) {
        }
        try {
            analogicConfigs.set(3, new PortaAnalogica(3, TFIdentificador4.getText(), CKAna4.isSelected(), new BigDecimal(TFIntervaloIni4.getText()), new BigDecimal(TFIntervaloFim4.getText()), new BigDecimal(TFOffset4.getText())));
        } catch (Exception e) {
        }
        try {
            analogicConfigs.set(4, new PortaAnalogica(4, TFIdentificador5.getText(), CKAna5.isSelected(), new BigDecimal(TFIntervaloIni5.getText()), new BigDecimal(TFIntervaloFim5.getText()), new BigDecimal(TFOffset5.getText())));
        } catch (Exception e) {
        }
        try {
            analogicConfigs.set(5, new PortaAnalogica(5, TFIdentificador6.getText(), CKAna6.isSelected(), new BigDecimal(TFIntervaloIni6.getText()), new BigDecimal(TFIntervaloFim6.getText()), new BigDecimal(TFOffset6.getText())));
        } catch (Exception e) {
        }
        try {
            analogicConfigs.set(6, new PortaAnalogica(6, TFIdentificador7.getText(), CKAna7.isSelected(), new BigDecimal(TFIntervaloIni7.getText()), new BigDecimal(TFIntervaloFim7.getText()), new BigDecimal(TFOffset7.getText())));
        } catch (Exception e) {
        }
        try {
            analogicConfigs.set(7, new PortaAnalogica(7, TFIdentificador8.getText(), CKAna8.isSelected(), new BigDecimal(TFIntervaloIni8.getText()), new BigDecimal(TFIntervaloFim8.getText()), new BigDecimal(TFOffset8.getText())));
        } catch (Exception e) {
        }
        try {
            analogicConfigs.set(8, new PortaAnalogica(8, TFIdentificador9.getText(), CKAna9.isSelected(), new BigDecimal(TFIntervaloIni9.getText()), new BigDecimal(TFIntervaloFim9.getText()), new BigDecimal(TFOffset9.getText())));
        } catch (Exception e) {
        }
        try {
            analogicConfigs.set(9, new PortaAnalogica(9, TFIdentificador10.getText(), CKAna10.isSelected(), new BigDecimal(TFIntervaloIni10.getText()), new BigDecimal(TFIntervaloFim10.getText()), new BigDecimal(TFOffset10.getText())));
        } catch (Exception e) {
        }
        try {
            analogicConfigs.set(10, new PortaAnalogica(10, TFIdentificador11.getText(), CKAna11.isSelected(), new BigDecimal(TFIntervaloIni11.getText()), new BigDecimal(TFIntervaloFim11.getText()), new BigDecimal(TFOffset11.getText())));
        } catch (Exception e) {
        }
        try {
            analogicConfigs.set(11, new PortaAnalogica(11, TFIdentificador12.getText(), CKAna12.isSelected(), new BigDecimal(TFIntervaloIni12.getText()), new BigDecimal(TFIntervaloFim12.getText()), new BigDecimal(TFOffset12.getText())));
        } catch (Exception e) {
        }
        try {
            analogicConfigs.set(12, new PortaAnalogica(12, TFIdentificador13.getText(), CKAna13.isSelected(), new BigDecimal(TFIntervaloIni13.getText()), new BigDecimal(TFIntervaloFim13.getText()), new BigDecimal(TFOffset13.getText())));
        } catch (Exception e) {
        }
        try {
            analogicConfigs.set(13, new PortaAnalogica(13, TFIdentificador14.getText(), CKAna14.isSelected(), new BigDecimal(TFIntervaloIni14.getText()), new BigDecimal(TFIntervaloFim14.getText()), new BigDecimal(TFOffset14.getText())));
        } catch (Exception e) {
        }
        try {
            analogicConfigs.set(14, new PortaAnalogica(14, TFIdentificador15.getText(), CKAna15.isSelected(), new BigDecimal(TFIntervaloIni15.getText()), new BigDecimal(TFIntervaloFim15.getText()), new BigDecimal(TFOffset15.getText())));
        } catch (Exception e) {
        }
        try {
            analogicConfigs.set(15, new PortaAnalogica(15, TFIdentificador16.getText(), CKAna16.isSelected(), new BigDecimal(TFIntervaloIni16.getText()), new BigDecimal(TFIntervaloFim16.getText()), new BigDecimal(TFOffset16.getText())));
        } catch (Exception e) {
        }

    }

    private void createConfigFile() {
        try {
            StringBuilder configs = new StringBuilder();
            //Retira acentos do nome da estacao
            String nome = Normalizer.normalize(TFNome.getText(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
            nome = nome.replaceAll("[^a-zA-Z0-9]", "").trim();

            configs.append("nome = ").append(nome).append("\n");
            configs.append("intervaloColeta = ").append(CBColeta.getSelectedItem().toString()).append("\n");
            configs.append("portaSaida = ").append(CBPortaSaida.getSelectedItem().toString()).append("\n");
            configs.append("D1 = ").append(CKD1.isSelected()).append("\n");
            configs.append("D2 = ").append(CKD2.isSelected()).append("\n");
            configs.append("RS485 = ").append(CKD3.isSelected()).append("\n");

            for (PortaAnalogica pd : analogicConfigs) {
                configs.append("A").append(pd.getId() + 1).append(" = ");
                configs.append(pd.getIdentificador()).append(",");
                configs.append(pd.getStatus().toString()).append(",");
                configs.append(pd.getIntervaloIni().toString()).append(",");
                configs.append(pd.getIntervaloFim().toString()).append(",");
                configs.append(pd.getOffset().toString()).append("\n");
            }

            File dir = new File("C:\\config");
            if (!dir.exists()) {
                dir.mkdir();
            }

            dir = new File("C:\\config\\OverLogger");
            if (!dir.exists()) {
                dir.mkdir();
            }

            //Se não escolher um arquivo, cria no C:
            String urlDestino = "C:\\config\\Logger\\config.txt";

            Formatter saida = new Formatter(urlDestino);
            String strFinal = configs.toString().trim();
            //Adiciona o espaço necessario para leitura do arquivo pelo Arduino.
            strFinal += "\n";
            saida.format(strFinal);
            strFinalTeste = strFinal;

            saida.close();

            if (JOptionPane.showConfirmDialog(null, "As configurações selecionadas foram:\n\n" + configs.toString().trim() + " \n\nDeseja continuar?", "Confirmar configurações?", JOptionPane.OK_CANCEL_OPTION) == 2) {
                return;
            }

            JOptionPane.showMessageDialog(null, "Arquivo criado com sucesso!\nVerifique C:\\config\\OverLogger\\config.txt", "Arquivo criado", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Algum erro ocorreu\n" + e.fillInStackTrace(), "Erro ao criar arquivo", JOptionPane.ERROR_MESSAGE);
        }

    }

    private void fillConfigFile(String fileURL) throws FileNotFoundException, IOException {
        String linha = "";
        linhas = new HashMap<>();
        FileReader reader = new FileReader(fileURL);
        //leitor do arquivo
        BufferedReader leitor = new BufferedReader(reader);
        while (true) {
            linha = leitor.readLine();
            if ((linha == null) || (linha.equals(""))) {
                break;
            }
            String[] parametro = linha.split("=");
            linhas.put(parametro[0].trim(), parametro[1].trim());
        }

        TFNome.setText(linhas.get("nome"));
        CBColeta.setSelectedItem(linhas.get("intervaloColeta"));
        CBPortaSaida.setSelectedItem(linhas.get("portaSaida"));

        CKD1.setSelected(Boolean.parseBoolean(linhas.get("D2")));
        CKD3.setSelected(Boolean.parseBoolean(linhas.get("D3")));

        String[] A1 = linhas.get("A1").split(",");
        String[] A2 = linhas.get("A2").split(",");
        String[] A3 = linhas.get("A3").split(",");
        String[] A4 = linhas.get("A4").split(",");
        String[] A5 = linhas.get("A5").split(",");
        String[] A6 = linhas.get("A6").split(",");
        String[] A7 = linhas.get("A7").split(",");
        String[] A8 = linhas.get("A8").split(",");
        String[] A9 = linhas.get("A9").split(",");
        String[] A10 = linhas.get("A10").split(",");
        String[] A11 = linhas.get("A11").split(",");
        String[] A12 = linhas.get("A12").split(",");
        String[] A13 = linhas.get("A13").split(",");
        String[] A14 = linhas.get("A14").split(",");
        String[] A15 = linhas.get("A15").split(",");
        String[] A16 = linhas.get("A16").split(",");

        analogicConfigs.set(0, new PortaAnalogica(0, A1[0], Boolean.parseBoolean(A1[1]), new BigDecimal(A1[2]), new BigDecimal(A1[3]), new BigDecimal(A1[4])));
        analogicConfigs.set(1, new PortaAnalogica(1, "", Boolean.parseBoolean(A2[0]), new BigDecimal(A2[2]), new BigDecimal(A2[3]), new BigDecimal(A2[4])));
        analogicConfigs.set(2, new PortaAnalogica(2, "", Boolean.parseBoolean(A3[0]), new BigDecimal(A3[2]), new BigDecimal(A3[3]), new BigDecimal(A3[4])));
        analogicConfigs.set(3, new PortaAnalogica(3, "", Boolean.parseBoolean(A4[0]), new BigDecimal(A4[2]), new BigDecimal(A4[3]), new BigDecimal(A4[4])));
        analogicConfigs.set(4, new PortaAnalogica(4, "", Boolean.parseBoolean(A5[0]), new BigDecimal(A5[2]), new BigDecimal(A5[3]), new BigDecimal(A5[4])));
        analogicConfigs.set(5, new PortaAnalogica(5, "", Boolean.parseBoolean(A6[0]), new BigDecimal(A6[2]), new BigDecimal(A6[3]), new BigDecimal(A6[4])));
        analogicConfigs.set(6, new PortaAnalogica(6, "", Boolean.parseBoolean(A7[0]), new BigDecimal(A7[2]), new BigDecimal(A7[3]), new BigDecimal(A7[4])));
        analogicConfigs.set(7, new PortaAnalogica(7, "", Boolean.parseBoolean(A8[0]), new BigDecimal(A8[2]), new BigDecimal(A8[3]), new BigDecimal(A8[4])));
        analogicConfigs.set(8, new PortaAnalogica(8, "", Boolean.parseBoolean(A9[0]), new BigDecimal(A9[2]), new BigDecimal(A9[3]), new BigDecimal(A9[4])));
        analogicConfigs.set(9, new PortaAnalogica(9, "", Boolean.parseBoolean(A10[0]), new BigDecimal(A10[2]), new BigDecimal(A10[3]), new BigDecimal(A10[4])));
        analogicConfigs.set(10, new PortaAnalogica(10, "", Boolean.parseBoolean(A11[0]), new BigDecimal(A11[2]), new BigDecimal(A11[3]), new BigDecimal(A11[4])));
        analogicConfigs.set(11, new PortaAnalogica(11, "", Boolean.parseBoolean(A12[0]), new BigDecimal(A12[2]), new BigDecimal(A12[3]), new BigDecimal(A12[4])));
        analogicConfigs.set(12, new PortaAnalogica(12, "", Boolean.parseBoolean(A13[0]), new BigDecimal(A13[2]), new BigDecimal(A13[3]), new BigDecimal(A13[4])));
        analogicConfigs.set(13, new PortaAnalogica(13, "", Boolean.parseBoolean(A14[0]), new BigDecimal(A14[2]), new BigDecimal(A14[3]), new BigDecimal(A14[4])));
        analogicConfigs.set(14, new PortaAnalogica(14, "", Boolean.parseBoolean(A15[0]), new BigDecimal(A15[2]), new BigDecimal(A15[3]), new BigDecimal(A15[4])));
        analogicConfigs.set(15, new PortaAnalogica(15, "", Boolean.parseBoolean(A16[0]), new BigDecimal(A16[2]), new BigDecimal(A16[3]), new BigDecimal(A16[4])));

    }

    public void sendToArduino(String path) throws Exception {
        try {
            if (serial != null) {
                serial.close();
                Thread.sleep(2000);
            }
            Progress.setValue(Progress.getValue() + 10);
        } catch (Exception e) {
            throw new Exception("Erro ao fechar a porta de comunicação\n" + e.fillInStackTrace());
        }

        //Caso a origem seja externa, ja pega o path
        String finalPath = path;
        InputStream in = TPrincipal.class.getResourceAsStream(path);
        if (in != null) {
            try {
                try {
                    // always write to different location
                    String tempName = path.substring(path.lastIndexOf('/') + 1);
                    File fileOut = File.createTempFile(tempName.substring(0, tempName.lastIndexOf('.')), tempName.substring(tempName.lastIndexOf('.'), tempName.length()));
                    fileOut.deleteOnExit();

                    OutputStream out = new FileOutputStream(fileOut);
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }

                    out.close();
                    finalPath = fileOut.getAbsolutePath();
                } finally {
                    in.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Progress.setValue(Progress.getValue() + 10);

        //Teste para achar pasta correta
        String folderProgram = "Program Files (x86)";
        if (!new File("C:\\Program Files (x86)\\").exists()) {
            folderProgram = "Program Files";
        }

        String hexfile = "\"" + finalPath + "\"";
        String exefile = "\"C:\\" + folderProgram + "\\Arduino\\hardware\\tools\\avr\\bin\\avrdude.exe\"";
        String conffile = "\"C:\\" + folderProgram + "\\Arduino\\hardware\\tools\\avr\\etc\\avrdude.conf\"";
        //String opts = " -v -F -D -V -c wiring -p ATmega2560 -P COM12 -b 115200 ";
        String opts = " -v -F -D -V -c wiring -p ATmega2560 -P " + CBPortas.getSelectedItem().toString() + " -b 115200 ";
        String cmd = exefile + " -C" + conffile + opts + " -Uflash:w:" + hexfile + ":i";

        try {
            System.out.println(cmd);
            ArduinoUPLOAD.execCommand(cmd, TXConsole);

            Progress.setValue(Progress.getValue() + 10);
            try {
                Thread.sleep(1000);
                serial = new Serial(CBPortas.getSelectedItem().toString());
                Progress.setValue(Progress.getValue() + 5);
                Thread.sleep(2000);
                Progress.setValue(Progress.getValue() + 5);
            } catch (Exception e) {
                throw new Exception("Erro ao abrir a porta de comunicação\n" + e.fillInStackTrace());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            java.util.logging.Logger.getLogger(TPrincipal.class.getName()).log(Level.SEVERE, null, ex);
            throw new Exception("Erro ao enviar firmware \n" + ex.fillInStackTrace());
        } catch (InterruptedException ex) {
            ex.printStackTrace();
            java.util.logging.Logger.getLogger(TPrincipal.class.getName()).log(Level.SEVERE, null, ex);
            throw new Exception("Erro ao enviar firmware \n" + ex.fillInStackTrace());
        }
    }

    private void sendFirmwareOverLoggerOLD() {
        TXConsole.append("Processo em execução, aguarde a mensagem de confirmação!");
        try {
            sendToArduino("/firmwares/RTCSinc.cpp.hex");

            Thread.sleep(4000);

            Calendar c = Calendar.getInstance();

            serial.sendString(String.valueOf(c.get(Calendar.YEAR)));
            serial.sendString("\n");
            serial.sendString(String.valueOf(c.get(Calendar.MONTH) + 1));
            serial.sendString("\n");
            serial.sendString(String.valueOf(c.get(Calendar.DAY_OF_MONTH)));
            serial.sendString("\n");
            serial.sendString(String.valueOf(c.get(Calendar.HOUR_OF_DAY)));
            serial.sendString("\n");
            serial.sendString(String.valueOf(c.get(Calendar.MINUTE)));
            serial.sendString("\n");
            serial.sendString(String.valueOf(c.get(Calendar.SECOND)));
            serial.sendString("\n");
            serial.sendString("Zerar");
            serial.sendString("\n");

            System.out.println(String.valueOf(c.getTime()));

            Thread.sleep(4000);

            sendToArduino("/firmwares/OverLogger.cpp.hex");

            JOptionPane.showMessageDialog(null, "Firmware enviado com sucesso!", "Upload", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
        }
    }

    private void sendFirmwareEspecifico() {
        Progress.setVisible(true);
        Progress.setValue(0);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Progress.setValue(10);
                    System.out.println("path" + firmwareEspecifico.getPath());
                    sendToArduino(firmwareEspecifico.getPath());

                    Progress.setValue(100);
                    JOptionPane.showMessageDialog(null, "Firmware enviado com sucesso!", "Upload", JOptionPane.INFORMATION_MESSAGE);
                    TPrincipal.countClose = 0;
                } catch (Exception e) {
                    e.printStackTrace();
                    Progress.setValue(0);
                    JOptionPane.showMessageDialog(null, "Erro ao enviar Firmware!", "Upload", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }).start();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        TFNome = new javax.swing.JTextField();
        CBColeta = new javax.swing.JComboBox();
        CBPortaSaida = new javax.swing.JComboBox();
        jLabel13 = new javax.swing.JLabel();
        PDigitais = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        CKD1 = new javax.swing.JCheckBox();
        CKD2 = new javax.swing.JCheckBox();
        CKD3 = new javax.swing.JCheckBox();
        jLabel8 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        PAnalogicas = new javax.swing.JPanel();
        TPAnalogicas = new javax.swing.JTabbedPane();
        A1 = new javax.swing.JPanel();
        CKAna = new javax.swing.JCheckBox();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        TFIdentificador = new javax.swing.JTextField();
        TFIntervaloIni = new javax.swing.JTextField();
        TFIntervaloFim = new javax.swing.JTextField();
        TFOffset = new javax.swing.JTextField();
        jLabel55 = new javax.swing.JLabel();
        A2 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        CKAna2 = new javax.swing.JCheckBox();
        jLabel20 = new javax.swing.JLabel();
        TFIntervaloIni2 = new javax.swing.JTextField();
        TFIntervaloFim2 = new javax.swing.JTextField();
        jLabel31 = new javax.swing.JLabel();
        TFOffset2 = new javax.swing.JTextField();
        jLabel59 = new javax.swing.JLabel();
        TFIdentificador2 = new javax.swing.JTextField();
        A3 = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        CKAna3 = new javax.swing.JCheckBox();
        jLabel25 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        TFIntervaloIni3 = new javax.swing.JTextField();
        TFIntervaloFim3 = new javax.swing.JTextField();
        TFOffset3 = new javax.swing.JTextField();
        TFIdentificador3 = new javax.swing.JTextField();
        jLabel63 = new javax.swing.JLabel();
        A4 = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        CKAna4 = new javax.swing.JCheckBox();
        jLabel28 = new javax.swing.JLabel();
        TFIntervaloIni4 = new javax.swing.JTextField();
        TFIntervaloFim4 = new javax.swing.JTextField();
        jLabel35 = new javax.swing.JLabel();
        TFOffset4 = new javax.swing.JTextField();
        TFIdentificador4 = new javax.swing.JTextField();
        jLabel67 = new javax.swing.JLabel();
        A5 = new javax.swing.JPanel();
        jLabel29 = new javax.swing.JLabel();
        CKAna5 = new javax.swing.JCheckBox();
        jLabel37 = new javax.swing.JLabel();
        TFIntervaloIni5 = new javax.swing.JTextField();
        TFIntervaloFim5 = new javax.swing.JTextField();
        jLabel38 = new javax.swing.JLabel();
        TFOffset5 = new javax.swing.JTextField();
        TFIdentificador5 = new javax.swing.JTextField();
        jLabel71 = new javax.swing.JLabel();
        A6 = new javax.swing.JPanel();
        jLabel40 = new javax.swing.JLabel();
        CKAna6 = new javax.swing.JCheckBox();
        jLabel41 = new javax.swing.JLabel();
        TFIntervaloIni6 = new javax.swing.JTextField();
        TFIntervaloFim6 = new javax.swing.JTextField();
        jLabel42 = new javax.swing.JLabel();
        TFOffset6 = new javax.swing.JTextField();
        TFIdentificador6 = new javax.swing.JTextField();
        jLabel75 = new javax.swing.JLabel();
        A7 = new javax.swing.JPanel();
        jLabel44 = new javax.swing.JLabel();
        CKAna7 = new javax.swing.JCheckBox();
        jLabel45 = new javax.swing.JLabel();
        TFIntervaloIni7 = new javax.swing.JTextField();
        TFIntervaloFim7 = new javax.swing.JTextField();
        jLabel46 = new javax.swing.JLabel();
        TFOffset7 = new javax.swing.JTextField();
        TFIdentificador7 = new javax.swing.JTextField();
        jLabel79 = new javax.swing.JLabel();
        A8 = new javax.swing.JPanel();
        jLabel48 = new javax.swing.JLabel();
        CKAna8 = new javax.swing.JCheckBox();
        jLabel49 = new javax.swing.JLabel();
        TFIntervaloIni8 = new javax.swing.JTextField();
        TFIntervaloFim8 = new javax.swing.JTextField();
        jLabel50 = new javax.swing.JLabel();
        TFOffset8 = new javax.swing.JTextField();
        TFIdentificador8 = new javax.swing.JTextField();
        jLabel83 = new javax.swing.JLabel();
        A9 = new javax.swing.JPanel();
        jLabel52 = new javax.swing.JLabel();
        CKAna9 = new javax.swing.JCheckBox();
        jLabel53 = new javax.swing.JLabel();
        TFIntervaloIni9 = new javax.swing.JTextField();
        TFIntervaloFim9 = new javax.swing.JTextField();
        jLabel54 = new javax.swing.JLabel();
        TFOffset9 = new javax.swing.JTextField();
        TFIdentificador9 = new javax.swing.JTextField();
        jLabel84 = new javax.swing.JLabel();
        A10 = new javax.swing.JPanel();
        jLabel56 = new javax.swing.JLabel();
        CKAna10 = new javax.swing.JCheckBox();
        jLabel57 = new javax.swing.JLabel();
        TFIntervaloIni10 = new javax.swing.JTextField();
        TFIntervaloFim10 = new javax.swing.JTextField();
        jLabel58 = new javax.swing.JLabel();
        TFOffset10 = new javax.swing.JTextField();
        TFIdentificador10 = new javax.swing.JTextField();
        jLabel85 = new javax.swing.JLabel();
        A11 = new javax.swing.JPanel();
        jLabel60 = new javax.swing.JLabel();
        CKAna11 = new javax.swing.JCheckBox();
        jLabel61 = new javax.swing.JLabel();
        TFIntervaloIni11 = new javax.swing.JTextField();
        TFIntervaloFim11 = new javax.swing.JTextField();
        jLabel62 = new javax.swing.JLabel();
        TFOffset11 = new javax.swing.JTextField();
        TFIdentificador11 = new javax.swing.JTextField();
        jLabel86 = new javax.swing.JLabel();
        A12 = new javax.swing.JPanel();
        jLabel64 = new javax.swing.JLabel();
        CKAna12 = new javax.swing.JCheckBox();
        jLabel65 = new javax.swing.JLabel();
        TFIntervaloIni12 = new javax.swing.JTextField();
        TFIntervaloFim12 = new javax.swing.JTextField();
        jLabel66 = new javax.swing.JLabel();
        TFOffset12 = new javax.swing.JTextField();
        TFIdentificador12 = new javax.swing.JTextField();
        jLabel87 = new javax.swing.JLabel();
        A13 = new javax.swing.JPanel();
        jLabel68 = new javax.swing.JLabel();
        CKAna13 = new javax.swing.JCheckBox();
        jLabel69 = new javax.swing.JLabel();
        TFIntervaloIni13 = new javax.swing.JTextField();
        TFIntervaloFim13 = new javax.swing.JTextField();
        jLabel70 = new javax.swing.JLabel();
        TFOffset13 = new javax.swing.JTextField();
        TFIdentificador13 = new javax.swing.JTextField();
        jLabel88 = new javax.swing.JLabel();
        A14 = new javax.swing.JPanel();
        jLabel72 = new javax.swing.JLabel();
        CKAna14 = new javax.swing.JCheckBox();
        jLabel73 = new javax.swing.JLabel();
        TFIntervaloIni14 = new javax.swing.JTextField();
        TFIntervaloFim14 = new javax.swing.JTextField();
        jLabel74 = new javax.swing.JLabel();
        TFOffset14 = new javax.swing.JTextField();
        TFIdentificador14 = new javax.swing.JTextField();
        jLabel89 = new javax.swing.JLabel();
        A15 = new javax.swing.JPanel();
        jLabel76 = new javax.swing.JLabel();
        CKAna15 = new javax.swing.JCheckBox();
        jLabel77 = new javax.swing.JLabel();
        TFIntervaloIni15 = new javax.swing.JTextField();
        TFIntervaloFim15 = new javax.swing.JTextField();
        jLabel78 = new javax.swing.JLabel();
        TFOffset15 = new javax.swing.JTextField();
        TFIdentificador15 = new javax.swing.JTextField();
        jLabel90 = new javax.swing.JLabel();
        A16 = new javax.swing.JPanel();
        jLabel80 = new javax.swing.JLabel();
        CKAna16 = new javax.swing.JCheckBox();
        jLabel81 = new javax.swing.JLabel();
        TFIntervaloIni16 = new javax.swing.JTextField();
        TFIntervaloFim16 = new javax.swing.JTextField();
        jLabel82 = new javax.swing.JLabel();
        TFOffset16 = new javax.swing.JTextField();
        TFIdentificador16 = new javax.swing.JTextField();
        jLabel91 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel36 = new javax.swing.JLabel();
        CBPortas = new javax.swing.JComboBox();
        BTRefreshSerial = new javax.swing.JButton();
        BTAjustarHorario1 = new javax.swing.JButton();
        BTAjustarHorario = new javax.swing.JButton();
        BTAjustarHorario2 = new javax.swing.JButton();
        Progress = new javax.swing.JProgressBar();
        jPanel2 = new javax.swing.JPanel();
        PConsole = new javax.swing.JPanel();
        ScrollConsole = new javax.swing.JScrollPane();
        TXConsole = new javax.swing.JTextArea();
        TP = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        TFTRD1 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        TFTRD2 = new javax.swing.JTextField();
        PTempoReal = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        TFTRA1 = new javax.swing.JTextField();
        TFTRA2 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        TFTRA3 = new javax.swing.JTextField();
        TFTRA4 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        TFTRA5 = new javax.swing.JTextField();
        TFTRA6 = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        TFTRA7 = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        TFTRA8 = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        TFTRA9 = new javax.swing.JTextField();
        TFTRA10 = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        TFTRA11 = new javax.swing.JTextField();
        TFTRA12 = new javax.swing.JTextField();
        TFTRA13 = new javax.swing.JTextField();
        TFTRA14 = new javax.swing.JTextField();
        TFTRA15 = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        TFTRA16 = new javax.swing.JTextField();
        jLabel43 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        TRLabelUltimaColeta = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Configurador Mestrado - versão 1.0.0");
        setResizable(false);
        addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
                formWindowGainedFocus(evt);
            }
            public void windowLostFocus(java.awt.event.WindowEvent evt) {
            }
        });
        getContentPane().setLayout(null);

        jTabbedPane1.setFont(new java.awt.Font("Candara", 0, 14)); // NOI18N

        jPanel3.setLayout(null);

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/unioeste/view/Icones/logounioeste.png"))); // NOI18N
        jButton2.setBorder(null);
        jButton2.setDefaultCapable(false);
        jButton2.setFocusPainted(false);
        jButton2.setFocusable(false);
        jButton2.setOpaque(false);
        jPanel3.add(jButton2);
        jButton2.setBounds(140, 280, 360, 180);

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/unioeste/view/Icones/ppgcomp.png"))); // NOI18N
        jButton4.setBorder(null);
        jButton4.setDefaultCapable(false);
        jButton4.setFocusPainted(false);
        jButton4.setFocusable(false);
        jButton4.setOpaque(false);
        jPanel3.add(jButton4);
        jButton4.setBounds(510, 280, 250, 180);

        jScrollPane1.setBorder(null);

        jTextPane1.setEditable(false);
        jTextPane1.setBorder(null);
        jTextPane1.setFont(new java.awt.Font("Candara", 0, 21)); // NOI18N
        jTextPane1.setText("Projeto desenvolvido como requisito parcial para a obtenção do grau de Mestre pelo Programa de  Pós-Graduação em Ciência da Computação (PPGComp) da Universidade Estadual do Oeste do Paraná – Unioeste, campus de Cascavel/PR.\n\nO objetivo deste trabalho é projetar, desenvolver e analisar o uso de um protótipo de datalogger de baixo custo, configurável e com ampla aplicabilidade na coleta e  registro de dados ambientais.   \n\nEsse software visa possibilitar a configuração do Datalogger criado bem como dos sensores que serão  utilizados. ");
        jTextPane1.setFocusable(false);
        jTextPane1.setOpaque(false);
        jScrollPane1.setViewportView(jTextPane1);

        jPanel3.add(jScrollPane1);
        jScrollPane1.setBounds(20, 20, 910, 270);

        jTabbedPane1.addTab("Sobre", jPanel3);

        jPanel1.setLayout(null);

        jLabel3.setFont(new java.awt.Font("Candara", 0, 16)); // NOI18N
        jLabel3.setText("Nome da Estação");
        jPanel1.add(jLabel3);
        jLabel3.setBounds(20, 20, 300, 21);

        TFNome.setFont(new java.awt.Font("Candara", 0, 14)); // NOI18N
        TFNome.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                TFNomeKeyReleased(evt);
            }
        });
        jPanel1.add(TFNome);
        TFNome.setBounds(20, 40, 260, 24);

        CBColeta.setFont(new java.awt.Font("Candara", 0, 16)); // NOI18N
        CBColeta.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "5", "15", "30", "60" }));
        CBColeta.setSelectedIndex(-1);
        jPanel1.add(CBColeta);
        CBColeta.setBounds(20, 90, 310, 27);

        CBPortaSaida.setFont(new java.awt.Font("Candara", 0, 16)); // NOI18N
        CBPortaSaida.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "RS232", "RS485", "RS232-RS485" }));
        CBPortaSaida.setSelectedIndex(-1);
        CBPortaSaida.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CBPortaSaidaActionPerformed(evt);
            }
        });
        jPanel1.add(CBPortaSaida);
        CBPortaSaida.setBounds(350, 90, 250, 27);

        jLabel13.setFont(new java.awt.Font("Candara", 0, 16)); // NOI18N
        jLabel13.setText("Porta de saída de dados:");
        jPanel1.add(jLabel13);
        jLabel13.setBounds(350, 70, 270, 21);

        PDigitais.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Portas Digitais", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Calibri", 0, 12))); // NOI18N
        PDigitais.setLayout(null);

        jLabel6.setFont(new java.awt.Font("Candara", 0, 16)); // NOI18N
        jLabel6.setText("Porta RS485:");
        PDigitais.add(jLabel6);
        jLabel6.setBounds(20, 150, 210, 20);

        CKD1.setFont(new java.awt.Font("Candara", 0, 16)); // NOI18N
        CKD1.setText("Habilitada?");
        PDigitais.add(CKD1);
        CKD1.setBounds(40, 55, 100, 20);

        CKD2.setFont(new java.awt.Font("Candara", 0, 16)); // NOI18N
        CKD2.setText("Habilitada?");
        PDigitais.add(CKD2);
        CKD2.setBounds(40, 120, 100, 20);

        CKD3.setFont(new java.awt.Font("Candara", 0, 16)); // NOI18N
        CKD3.setText("Habilitada?");
        PDigitais.add(CKD3);
        CKD3.setBounds(40, 180, 100, 20);

        jLabel8.setFont(new java.awt.Font("Candara", 0, 16)); // NOI18N
        jLabel8.setText("Porta Digital 1: *contadora de pulso");
        PDigitais.add(jLabel8);
        jLabel8.setBounds(20, 30, 240, 20);

        jLabel51.setFont(new java.awt.Font("Candara", 0, 16)); // NOI18N
        jLabel51.setText("Porta Digital 2: *contadora de pulso");
        PDigitais.add(jLabel51);
        jLabel51.setBounds(20, 90, 270, 20);

        jPanel1.add(PDigitais);
        PDigitais.setBounds(20, 130, 270, 220);

        PAnalogicas.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Portas Analógicas", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Calibri", 0, 12))); // NOI18N
        PAnalogicas.setLayout(null);

        TPAnalogicas.setFont(new java.awt.Font("Candara", 0, 16)); // NOI18N

        A1.setLayout(null);

        CKAna.setFont(new java.awt.Font("Candara", 0, 16)); // NOI18N
        CKAna.setText("Habilitada?");
        A1.add(CKAna);
        CKAna.setBounds(20, 40, 100, 20);

        jLabel14.setFont(new java.awt.Font("Candara", 0, 16)); // NOI18N
        jLabel14.setText("Identificador:");
        A1.add(jLabel14);
        jLabel14.setBounds(140, 20, 90, 21);

        jLabel15.setFont(new java.awt.Font("Candara", 0, 16)); // NOI18N
        jLabel15.setText("Intervalo Inicial/Final da leitura:");
        A1.add(jLabel15);
        jLabel15.setBounds(10, 80, 180, 21);

        jLabel18.setFont(new java.awt.Font("Candara", 0, 16)); // NOI18N
        jLabel18.setText("Soma ou Subtração do valor Lido:");
        A1.add(jLabel18);
        jLabel18.setBounds(250, 80, 260, 21);

        TFIdentificador.setFont(new java.awt.Font("Candara", 0, 14)); // NOI18N
        TFIdentificador.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TFIdentificadorActionPerformed(evt);
            }
        });
        A1.add(TFIdentificador);
        TFIdentificador.setBounds(140, 40, 240, 24);

        TFIntervaloIni.setFont(new java.awt.Font("Candara", 0, 14)); // NOI18N
        A1.add(TFIntervaloIni);
        TFIntervaloIni.setBounds(10, 100, 100, 24);

        TFIntervaloFim.setFont(new java.awt.Font("Candara", 0, 14)); // NOI18N
        A1.add(TFIntervaloFim);
        TFIntervaloFim.setBounds(120, 100, 100, 24);

        TFOffset.setFont(new java.awt.Font("Candara", 0, 14)); // NOI18N
        A1.add(TFOffset);
        TFOffset.setBounds(250, 100, 130, 24);

        jLabel55.setFont(new java.awt.Font("Candara", 0, 16)); // NOI18N
        jLabel55.setText("Status:");
        A1.add(jLabel55);
        jLabel55.setBounds(10, 20, 100, 21);

        TPAnalogicas.addTab("A1", A1);

        A2.setLayout(null);

        jLabel17.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel17.setText("Status:");
        A2.add(jLabel17);
        jLabel17.setBounds(10, 20, 37, 16);

        CKAna2.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        CKAna2.setText("Habilitada?");
        CKAna2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                CKAna2FocusLost(evt);
            }
        });
        A2.add(CKAna2);
        CKAna2.setBounds(20, 40, 100, 20);

        jLabel20.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel20.setText("Soma ou Subtração do valor Lido:");
        A2.add(jLabel20);
        jLabel20.setBounds(250, 80, 180, 16);

        TFIntervaloIni2.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        A2.add(TFIntervaloIni2);
        TFIntervaloIni2.setBounds(10, 100, 100, 22);

        TFIntervaloFim2.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        A2.add(TFIntervaloFim2);
        TFIntervaloFim2.setBounds(120, 100, 100, 22);

        jLabel31.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel31.setText("Intervalo Inicial/Final da leitura:");
        A2.add(jLabel31);
        jLabel31.setBounds(10, 80, 220, 16);

        TFOffset2.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        TFOffset2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                TFOffset2FocusLost(evt);
            }
        });
        TFOffset2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TFOffset2ActionPerformed(evt);
            }
        });
        A2.add(TFOffset2);
        TFOffset2.setBounds(250, 100, 130, 22);

        jLabel59.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel59.setText("Identificador:");
        A2.add(jLabel59);
        jLabel59.setBounds(140, 20, 90, 16);

        TFIdentificador2.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        TFIdentificador2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TFIdentificador2ActionPerformed(evt);
            }
        });
        A2.add(TFIdentificador2);
        TFIdentificador2.setBounds(140, 40, 240, 22);

        TPAnalogicas.addTab("A2", A2);

        A3.setLayout(null);

        jLabel24.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel24.setText("Status:");
        A3.add(jLabel24);
        jLabel24.setBounds(10, 20, 37, 16);

        CKAna3.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        CKAna3.setText("Habilitada?");
        CKAna3.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                CKAna3FocusLost(evt);
            }
        });
        A3.add(CKAna3);
        CKAna3.setBounds(20, 40, 100, 20);

        jLabel25.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel25.setText("Soma ou Subtração do valor Lido:");
        A3.add(jLabel25);
        jLabel25.setBounds(250, 80, 310, 16);

        jLabel33.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel33.setText("Intervalo Inicial/Final da leitura:");
        A3.add(jLabel33);
        jLabel33.setBounds(10, 80, 220, 16);

        TFIntervaloIni3.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        A3.add(TFIntervaloIni3);
        TFIntervaloIni3.setBounds(10, 100, 100, 22);

        TFIntervaloFim3.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        A3.add(TFIntervaloFim3);
        TFIntervaloFim3.setBounds(120, 100, 100, 22);

        TFOffset3.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        TFOffset3.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                TFOffset3FocusLost(evt);
            }
        });
        TFOffset3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TFOffset3ActionPerformed(evt);
            }
        });
        A3.add(TFOffset3);
        TFOffset3.setBounds(250, 100, 130, 22);

        TFIdentificador3.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        TFIdentificador3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TFIdentificador3ActionPerformed(evt);
            }
        });
        A3.add(TFIdentificador3);
        TFIdentificador3.setBounds(140, 40, 240, 22);

        jLabel63.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel63.setText("Identificador:");
        A3.add(jLabel63);
        jLabel63.setBounds(140, 20, 90, 16);

        TPAnalogicas.addTab("A3", A3);

        A4.setLayout(null);

        jLabel27.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel27.setText("Status:");
        A4.add(jLabel27);
        jLabel27.setBounds(10, 20, 37, 16);

        CKAna4.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        CKAna4.setText("Habilitada?");
        CKAna4.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                CKAna4FocusLost(evt);
            }
        });
        A4.add(CKAna4);
        CKAna4.setBounds(20, 40, 100, 20);

        jLabel28.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel28.setText("Soma ou Subtração do valor Lido:");
        A4.add(jLabel28);
        jLabel28.setBounds(250, 80, 250, 16);

        TFIntervaloIni4.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        A4.add(TFIntervaloIni4);
        TFIntervaloIni4.setBounds(10, 100, 100, 22);

        TFIntervaloFim4.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        A4.add(TFIntervaloFim4);
        TFIntervaloFim4.setBounds(120, 100, 100, 22);

        jLabel35.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel35.setText("Intervalo Inicial/Final da leitura:");
        A4.add(jLabel35);
        jLabel35.setBounds(10, 80, 220, 16);

        TFOffset4.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        TFOffset4.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                TFOffset4FocusLost(evt);
            }
        });
        TFOffset4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TFOffset4ActionPerformed(evt);
            }
        });
        A4.add(TFOffset4);
        TFOffset4.setBounds(250, 100, 130, 22);

        TFIdentificador4.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        TFIdentificador4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TFIdentificador4ActionPerformed(evt);
            }
        });
        A4.add(TFIdentificador4);
        TFIdentificador4.setBounds(140, 40, 240, 22);

        jLabel67.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel67.setText("Identificador:");
        A4.add(jLabel67);
        jLabel67.setBounds(140, 20, 90, 16);

        TPAnalogicas.addTab("A4", A4);

        A5.setLayout(null);

        jLabel29.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel29.setText("Status:");
        A5.add(jLabel29);
        jLabel29.setBounds(10, 20, 37, 16);

        CKAna5.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        CKAna5.setText("Habilitada?");
        CKAna5.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                CKAna5FocusLost(evt);
            }
        });
        A5.add(CKAna5);
        CKAna5.setBounds(20, 40, 100, 20);

        jLabel37.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel37.setText("Soma ou Subtração do valor Lido:");
        A5.add(jLabel37);
        jLabel37.setBounds(250, 80, 310, 16);

        TFIntervaloIni5.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        A5.add(TFIntervaloIni5);
        TFIntervaloIni5.setBounds(10, 100, 100, 22);

        TFIntervaloFim5.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        A5.add(TFIntervaloFim5);
        TFIntervaloFim5.setBounds(120, 100, 100, 22);

        jLabel38.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel38.setText("Intervalo Inicial/Final da leitura:");
        A5.add(jLabel38);
        jLabel38.setBounds(10, 80, 220, 16);

        TFOffset5.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        TFOffset5.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                TFOffset5FocusLost(evt);
            }
        });
        TFOffset5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TFOffset5ActionPerformed(evt);
            }
        });
        A5.add(TFOffset5);
        TFOffset5.setBounds(250, 100, 130, 22);

        TFIdentificador5.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        TFIdentificador5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TFIdentificador5ActionPerformed(evt);
            }
        });
        A5.add(TFIdentificador5);
        TFIdentificador5.setBounds(140, 40, 240, 22);

        jLabel71.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel71.setText("Identificador:");
        A5.add(jLabel71);
        jLabel71.setBounds(140, 20, 90, 16);

        TPAnalogicas.addTab("A5", A5);

        A6.setLayout(null);

        jLabel40.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel40.setText("Status:");
        A6.add(jLabel40);
        jLabel40.setBounds(10, 20, 37, 16);

        CKAna6.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        CKAna6.setText("Habilitada?");
        CKAna6.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                CKAna6FocusLost(evt);
            }
        });
        A6.add(CKAna6);
        CKAna6.setBounds(20, 40, 100, 20);

        jLabel41.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel41.setText("Soma ou Subtração do valor Lido:");
        A6.add(jLabel41);
        jLabel41.setBounds(250, 80, 220, 16);

        TFIntervaloIni6.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        A6.add(TFIntervaloIni6);
        TFIntervaloIni6.setBounds(10, 100, 100, 22);

        TFIntervaloFim6.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        A6.add(TFIntervaloFim6);
        TFIntervaloFim6.setBounds(120, 100, 100, 22);

        jLabel42.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel42.setText("Intervalo Inicial/Final da leitura:");
        A6.add(jLabel42);
        jLabel42.setBounds(10, 80, 220, 16);

        TFOffset6.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        TFOffset6.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                TFOffset6FocusLost(evt);
            }
        });
        TFOffset6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TFOffset6ActionPerformed(evt);
            }
        });
        A6.add(TFOffset6);
        TFOffset6.setBounds(250, 100, 130, 22);

        TFIdentificador6.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        TFIdentificador6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TFIdentificador6ActionPerformed(evt);
            }
        });
        A6.add(TFIdentificador6);
        TFIdentificador6.setBounds(140, 40, 240, 22);

        jLabel75.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel75.setText("Identificador:");
        A6.add(jLabel75);
        jLabel75.setBounds(140, 20, 90, 16);

        TPAnalogicas.addTab("A6", A6);

        A7.setLayout(null);

        jLabel44.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel44.setText("Status:");
        A7.add(jLabel44);
        jLabel44.setBounds(10, 20, 37, 16);

        CKAna7.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        CKAna7.setText("Habilitada?");
        CKAna7.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                CKAna7FocusLost(evt);
            }
        });
        A7.add(CKAna7);
        CKAna7.setBounds(20, 40, 100, 20);

        jLabel45.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel45.setText("Soma ou Subtração do valor Lido:");
        A7.add(jLabel45);
        jLabel45.setBounds(250, 80, 210, 16);

        TFIntervaloIni7.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        A7.add(TFIntervaloIni7);
        TFIntervaloIni7.setBounds(10, 100, 100, 22);

        TFIntervaloFim7.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        A7.add(TFIntervaloFim7);
        TFIntervaloFim7.setBounds(120, 100, 100, 22);

        jLabel46.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel46.setText("Intervalo Inicial/Final da leitura:");
        A7.add(jLabel46);
        jLabel46.setBounds(10, 80, 220, 16);

        TFOffset7.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        TFOffset7.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                TFOffset7FocusLost(evt);
            }
        });
        TFOffset7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TFOffset7ActionPerformed(evt);
            }
        });
        A7.add(TFOffset7);
        TFOffset7.setBounds(250, 100, 130, 22);

        TFIdentificador7.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        TFIdentificador7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TFIdentificador7ActionPerformed(evt);
            }
        });
        A7.add(TFIdentificador7);
        TFIdentificador7.setBounds(140, 40, 240, 22);

        jLabel79.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel79.setText("Identificador:");
        A7.add(jLabel79);
        jLabel79.setBounds(140, 20, 90, 16);

        TPAnalogicas.addTab("A7", A7);

        A8.setLayout(null);

        jLabel48.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel48.setText("Status:");
        A8.add(jLabel48);
        jLabel48.setBounds(10, 20, 37, 16);

        CKAna8.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        CKAna8.setText("Habilitada?");
        CKAna8.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                CKAna8FocusLost(evt);
            }
        });
        A8.add(CKAna8);
        CKAna8.setBounds(20, 40, 100, 20);

        jLabel49.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel49.setText("Soma ou Subtração do valor Lido:");
        A8.add(jLabel49);
        jLabel49.setBounds(250, 80, 200, 16);

        TFIntervaloIni8.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        A8.add(TFIntervaloIni8);
        TFIntervaloIni8.setBounds(10, 100, 100, 22);

        TFIntervaloFim8.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        A8.add(TFIntervaloFim8);
        TFIntervaloFim8.setBounds(120, 100, 100, 22);

        jLabel50.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel50.setText("Intervalo Inicial/Final da leitura:");
        A8.add(jLabel50);
        jLabel50.setBounds(10, 80, 220, 16);

        TFOffset8.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        TFOffset8.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                TFOffset8FocusLost(evt);
            }
        });
        TFOffset8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TFOffset8ActionPerformed(evt);
            }
        });
        A8.add(TFOffset8);
        TFOffset8.setBounds(250, 100, 130, 22);

        TFIdentificador8.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        TFIdentificador8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TFIdentificador8ActionPerformed(evt);
            }
        });
        A8.add(TFIdentificador8);
        TFIdentificador8.setBounds(140, 40, 240, 22);

        jLabel83.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel83.setText("Identificador:");
        A8.add(jLabel83);
        jLabel83.setBounds(140, 20, 90, 16);

        TPAnalogicas.addTab("A8", A8);

        A9.setLayout(null);

        jLabel52.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel52.setText("Status:");
        A9.add(jLabel52);
        jLabel52.setBounds(10, 20, 37, 16);

        CKAna9.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        CKAna9.setText("Habilitada?");
        CKAna9.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                CKAna9FocusLost(evt);
            }
        });
        A9.add(CKAna9);
        CKAna9.setBounds(20, 40, 100, 20);

        jLabel53.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel53.setText("Soma ou Subtração do valor Lido:");
        jLabel53.setOpaque(true);
        A9.add(jLabel53);
        jLabel53.setBounds(250, 80, 330, 16);

        TFIntervaloIni9.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        A9.add(TFIntervaloIni9);
        TFIntervaloIni9.setBounds(10, 100, 100, 22);

        TFIntervaloFim9.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        A9.add(TFIntervaloFim9);
        TFIntervaloFim9.setBounds(120, 100, 100, 22);

        jLabel54.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel54.setText("Intervalo Inicial/Final da leitura:");
        A9.add(jLabel54);
        jLabel54.setBounds(10, 80, 220, 16);

        TFOffset9.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        TFOffset9.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                TFOffset9FocusLost(evt);
            }
        });
        TFOffset9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TFOffset9ActionPerformed(evt);
            }
        });
        A9.add(TFOffset9);
        TFOffset9.setBounds(250, 100, 130, 22);

        TFIdentificador9.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        TFIdentificador9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TFIdentificador9ActionPerformed(evt);
            }
        });
        A9.add(TFIdentificador9);
        TFIdentificador9.setBounds(140, 40, 240, 22);

        jLabel84.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel84.setText("Identificador:");
        A9.add(jLabel84);
        jLabel84.setBounds(140, 20, 90, 16);

        TPAnalogicas.addTab("A9", A9);

        A10.setLayout(null);

        jLabel56.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel56.setText("Status:");
        A10.add(jLabel56);
        jLabel56.setBounds(10, 20, 37, 16);

        CKAna10.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        CKAna10.setText("Habilitada?");
        CKAna10.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                CKAna10FocusLost(evt);
            }
        });
        A10.add(CKAna10);
        CKAna10.setBounds(20, 40, 100, 20);

        jLabel57.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel57.setText("Soma ou Subtração do valor Lido:");
        A10.add(jLabel57);
        jLabel57.setBounds(250, 80, 220, 16);

        TFIntervaloIni10.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        A10.add(TFIntervaloIni10);
        TFIntervaloIni10.setBounds(10, 100, 100, 22);

        TFIntervaloFim10.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        A10.add(TFIntervaloFim10);
        TFIntervaloFim10.setBounds(120, 100, 100, 22);

        jLabel58.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel58.setText("Intervalo Inicial/Final da leitura:");
        A10.add(jLabel58);
        jLabel58.setBounds(10, 80, 220, 16);

        TFOffset10.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        TFOffset10.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                TFOffset10FocusLost(evt);
            }
        });
        TFOffset10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TFOffset10ActionPerformed(evt);
            }
        });
        A10.add(TFOffset10);
        TFOffset10.setBounds(250, 100, 130, 22);

        TFIdentificador10.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        TFIdentificador10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TFIdentificador10ActionPerformed(evt);
            }
        });
        A10.add(TFIdentificador10);
        TFIdentificador10.setBounds(140, 40, 240, 22);

        jLabel85.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel85.setText("Identificador:");
        A10.add(jLabel85);
        jLabel85.setBounds(140, 20, 90, 16);

        TPAnalogicas.addTab("A10", A10);

        A11.setLayout(null);

        jLabel60.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel60.setText("Status:");
        A11.add(jLabel60);
        jLabel60.setBounds(10, 20, 37, 16);

        CKAna11.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        CKAna11.setText("Habilitada?");
        CKAna11.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                CKAna11FocusLost(evt);
            }
        });
        A11.add(CKAna11);
        CKAna11.setBounds(20, 40, 100, 20);

        jLabel61.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel61.setText("Soma ou Subtração do valor Lido:");
        A11.add(jLabel61);
        jLabel61.setBounds(250, 80, 210, 16);

        TFIntervaloIni11.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        A11.add(TFIntervaloIni11);
        TFIntervaloIni11.setBounds(10, 100, 100, 22);

        TFIntervaloFim11.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        A11.add(TFIntervaloFim11);
        TFIntervaloFim11.setBounds(120, 100, 100, 22);

        jLabel62.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel62.setText("Intervalo Inicial/Final da leitura:");
        A11.add(jLabel62);
        jLabel62.setBounds(10, 80, 220, 16);

        TFOffset11.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        TFOffset11.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                TFOffset11FocusLost(evt);
            }
        });
        TFOffset11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TFOffset11ActionPerformed(evt);
            }
        });
        A11.add(TFOffset11);
        TFOffset11.setBounds(250, 100, 130, 22);

        TFIdentificador11.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        TFIdentificador11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TFIdentificador11ActionPerformed(evt);
            }
        });
        A11.add(TFIdentificador11);
        TFIdentificador11.setBounds(140, 40, 240, 22);

        jLabel86.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel86.setText("Identificador:");
        A11.add(jLabel86);
        jLabel86.setBounds(140, 20, 90, 16);

        TPAnalogicas.addTab("A11", A11);

        A12.setLayout(null);

        jLabel64.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel64.setText("Status:");
        A12.add(jLabel64);
        jLabel64.setBounds(10, 20, 37, 16);

        CKAna12.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        CKAna12.setText("Habilitada?");
        CKAna12.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                CKAna12FocusLost(evt);
            }
        });
        A12.add(CKAna12);
        CKAna12.setBounds(20, 40, 100, 20);

        jLabel65.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel65.setText("Soma ou Subtração do valor Lido:");
        A12.add(jLabel65);
        jLabel65.setBounds(250, 80, 260, 16);

        TFIntervaloIni12.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        A12.add(TFIntervaloIni12);
        TFIntervaloIni12.setBounds(10, 100, 100, 22);

        TFIntervaloFim12.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        A12.add(TFIntervaloFim12);
        TFIntervaloFim12.setBounds(120, 100, 100, 22);

        jLabel66.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel66.setText("Intervalo Inicial/Final da leitura:");
        A12.add(jLabel66);
        jLabel66.setBounds(10, 80, 220, 16);

        TFOffset12.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        TFOffset12.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                TFOffset12FocusLost(evt);
            }
        });
        TFOffset12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TFOffset12ActionPerformed(evt);
            }
        });
        A12.add(TFOffset12);
        TFOffset12.setBounds(250, 100, 130, 22);

        TFIdentificador12.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        TFIdentificador12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TFIdentificador12ActionPerformed(evt);
            }
        });
        A12.add(TFIdentificador12);
        TFIdentificador12.setBounds(140, 40, 240, 22);

        jLabel87.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel87.setText("Identificador:");
        A12.add(jLabel87);
        jLabel87.setBounds(140, 20, 90, 16);

        TPAnalogicas.addTab("A12", A12);

        A13.setLayout(null);

        jLabel68.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel68.setText("Status:");
        A13.add(jLabel68);
        jLabel68.setBounds(10, 20, 37, 16);

        CKAna13.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        CKAna13.setText("Habilitada?");
        CKAna13.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                CKAna13FocusLost(evt);
            }
        });
        A13.add(CKAna13);
        CKAna13.setBounds(20, 40, 100, 20);

        jLabel69.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel69.setText("Soma ou Subtração do valor Lido:");
        A13.add(jLabel69);
        jLabel69.setBounds(250, 80, 260, 16);

        TFIntervaloIni13.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        A13.add(TFIntervaloIni13);
        TFIntervaloIni13.setBounds(10, 100, 100, 22);

        TFIntervaloFim13.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        A13.add(TFIntervaloFim13);
        TFIntervaloFim13.setBounds(120, 100, 100, 22);

        jLabel70.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel70.setText("Intervalo Inicial/Final da leitura:");
        A13.add(jLabel70);
        jLabel70.setBounds(10, 80, 220, 16);

        TFOffset13.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        TFOffset13.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                TFOffset13FocusLost(evt);
            }
        });
        TFOffset13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TFOffset13ActionPerformed(evt);
            }
        });
        A13.add(TFOffset13);
        TFOffset13.setBounds(250, 100, 130, 22);

        TFIdentificador13.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        TFIdentificador13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TFIdentificador13ActionPerformed(evt);
            }
        });
        A13.add(TFIdentificador13);
        TFIdentificador13.setBounds(140, 40, 240, 22);

        jLabel88.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel88.setText("Identificador:");
        A13.add(jLabel88);
        jLabel88.setBounds(140, 20, 90, 16);

        TPAnalogicas.addTab("A13", A13);

        A14.setLayout(null);

        jLabel72.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel72.setText("Status:");
        A14.add(jLabel72);
        jLabel72.setBounds(10, 20, 37, 16);

        CKAna14.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        CKAna14.setText("Habilitada?");
        CKAna14.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                CKAna14FocusLost(evt);
            }
        });
        A14.add(CKAna14);
        CKAna14.setBounds(20, 40, 100, 20);

        jLabel73.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel73.setText("Soma ou Subtração do valor Lido:");
        A14.add(jLabel73);
        jLabel73.setBounds(250, 80, 220, 16);

        TFIntervaloIni14.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        A14.add(TFIntervaloIni14);
        TFIntervaloIni14.setBounds(10, 100, 100, 22);

        TFIntervaloFim14.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        A14.add(TFIntervaloFim14);
        TFIntervaloFim14.setBounds(120, 100, 100, 22);

        jLabel74.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel74.setText("Intervalo Inicial/Final da leitura:");
        A14.add(jLabel74);
        jLabel74.setBounds(10, 80, 220, 16);

        TFOffset14.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        TFOffset14.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                TFOffset14FocusLost(evt);
            }
        });
        TFOffset14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TFOffset14ActionPerformed(evt);
            }
        });
        A14.add(TFOffset14);
        TFOffset14.setBounds(250, 100, 130, 22);

        TFIdentificador14.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        TFIdentificador14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TFIdentificador14ActionPerformed(evt);
            }
        });
        A14.add(TFIdentificador14);
        TFIdentificador14.setBounds(140, 40, 240, 22);

        jLabel89.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel89.setText("Identificador:");
        A14.add(jLabel89);
        jLabel89.setBounds(140, 20, 90, 16);

        TPAnalogicas.addTab("A14", A14);

        A15.setLayout(null);

        jLabel76.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel76.setText("Status:");
        A15.add(jLabel76);
        jLabel76.setBounds(10, 20, 37, 16);

        CKAna15.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        CKAna15.setText("Habilitada?");
        CKAna15.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                CKAna15FocusLost(evt);
            }
        });
        A15.add(CKAna15);
        CKAna15.setBounds(20, 40, 100, 20);

        jLabel77.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel77.setText("Soma ou Subtração do valor Lido:");
        A15.add(jLabel77);
        jLabel77.setBounds(250, 80, 250, 16);

        TFIntervaloIni15.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        A15.add(TFIntervaloIni15);
        TFIntervaloIni15.setBounds(10, 100, 100, 22);

        TFIntervaloFim15.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        A15.add(TFIntervaloFim15);
        TFIntervaloFim15.setBounds(120, 100, 100, 22);

        jLabel78.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel78.setText("Intervalo Inicial/Final da leitura:");
        A15.add(jLabel78);
        jLabel78.setBounds(10, 80, 220, 16);

        TFOffset15.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        TFOffset15.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                TFOffset15FocusLost(evt);
            }
        });
        TFOffset15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TFOffset15ActionPerformed(evt);
            }
        });
        A15.add(TFOffset15);
        TFOffset15.setBounds(250, 100, 130, 22);

        TFIdentificador15.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        TFIdentificador15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TFIdentificador15ActionPerformed(evt);
            }
        });
        A15.add(TFIdentificador15);
        TFIdentificador15.setBounds(140, 40, 240, 22);

        jLabel90.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel90.setText("Identificador:");
        A15.add(jLabel90);
        jLabel90.setBounds(140, 20, 90, 16);

        TPAnalogicas.addTab("A15", A15);

        A16.setLayout(null);

        jLabel80.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel80.setText("Status:");
        A16.add(jLabel80);
        jLabel80.setBounds(10, 20, 37, 16);

        CKAna16.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        CKAna16.setText("Habilitada?");
        CKAna16.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                CKAna16FocusLost(evt);
            }
        });
        A16.add(CKAna16);
        CKAna16.setBounds(20, 40, 100, 20);

        jLabel81.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel81.setText("Soma ou Subtração do valor Lido:");
        A16.add(jLabel81);
        jLabel81.setBounds(250, 80, 240, 16);

        TFIntervaloIni16.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        A16.add(TFIntervaloIni16);
        TFIntervaloIni16.setBounds(10, 100, 100, 22);

        TFIntervaloFim16.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        A16.add(TFIntervaloFim16);
        TFIntervaloFim16.setBounds(120, 100, 100, 22);

        jLabel82.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel82.setText("Intervalo Inicial/Final da leitura:");
        A16.add(jLabel82);
        jLabel82.setBounds(10, 80, 220, 16);

        TFOffset16.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        TFOffset16.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                TFOffset16FocusLost(evt);
            }
        });
        TFOffset16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TFOffset16ActionPerformed(evt);
            }
        });
        A16.add(TFOffset16);
        TFOffset16.setBounds(250, 100, 130, 22);

        TFIdentificador16.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        TFIdentificador16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TFIdentificador16ActionPerformed(evt);
            }
        });
        A16.add(TFIdentificador16);
        TFIdentificador16.setBounds(140, 40, 240, 22);

        jLabel91.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel91.setText("Identificador:");
        A16.add(jLabel91);
        jLabel91.setBounds(140, 20, 90, 16);

        TPAnalogicas.addTab("A16", A16);

        PAnalogicas.add(TPAnalogicas);
        TPAnalogicas.setBounds(20, 30, 600, 170);

        jPanel1.add(PAnalogicas);
        PAnalogicas.setBounds(300, 130, 640, 220);

        jLabel16.setFont(new java.awt.Font("Candara", 0, 16)); // NOI18N
        jLabel16.setText("Intervalo de coleta e transmissão (minutos):");
        jPanel1.add(jLabel16);
        jLabel16.setBounds(20, 70, 300, 21);

        jButton1.setFont(new java.awt.Font("Candara", 0, 16)); // NOI18N
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/unioeste/view/Icones/salvar.png"))); // NOI18N
        jButton1.setText("Gerar arquivo de configuração");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1);
        jButton1.setBounds(320, 380, 300, 40);

        jTabbedPane1.addTab("Configurações", jPanel1);

        jPanel4.setLayout(null);

        jLabel36.setFont(new java.awt.Font("Candara", 0, 16)); // NOI18N
        jLabel36.setText("Selecione a porta:");
        jPanel4.add(jLabel36);
        jLabel36.setBounds(10, 20, 130, 20);

        CBPortas.setFont(new java.awt.Font("Candara", 0, 16)); // NOI18N
        CBPortas.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                CBPortasItemStateChanged(evt);
            }
        });
        CBPortas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CBPortasActionPerformed(evt);
            }
        });
        jPanel4.add(CBPortas);
        CBPortas.setBounds(150, 20, 120, 22);

        BTRefreshSerial.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/unioeste/view/Icones/alterar.png"))); // NOI18N
        BTRefreshSerial.setToolTipText("Recarregar portas");
        BTRefreshSerial.setContentAreaFilled(false);
        BTRefreshSerial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BTRefreshSerialActionPerformed(evt);
            }
        });
        jPanel4.add(BTRefreshSerial);
        BTRefreshSerial.setBounds(270, 20, 30, 25);

        BTAjustarHorario1.setFont(new java.awt.Font("Candara", 0, 16)); // NOI18N
        BTAjustarHorario1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/unioeste/view/Icones/lampadas16.png"))); // NOI18N
        BTAjustarHorario1.setText("Testar Funcionamento (Segundo em Segundo)");
        BTAjustarHorario1.setToolTipText("");
        BTAjustarHorario1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BTAjustarHorario1ActionPerformed(evt);
            }
        });
        jPanel4.add(BTAjustarHorario1);
        BTAjustarHorario1.setBounds(10, 60, 380, 50);

        BTAjustarHorario.setFont(new java.awt.Font("Candara", 0, 16)); // NOI18N
        BTAjustarHorario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/unioeste/view/Icones/alterar.png"))); // NOI18N
        BTAjustarHorario.setText("Sincronizar horário");
        BTAjustarHorario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BTAjustarHorarioActionPerformed(evt);
            }
        });
        jPanel4.add(BTAjustarHorario);
        BTAjustarHorario.setBounds(10, 180, 380, 50);

        BTAjustarHorario2.setFont(new java.awt.Font("Candara", 0, 16)); // NOI18N
        BTAjustarHorario2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/unioeste/view/Icones/accept.png"))); // NOI18N
        BTAjustarHorario2.setText("Enviar aplicação de produção");
        BTAjustarHorario2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BTAjustarHorario2ActionPerformed(evt);
            }
        });
        jPanel4.add(BTAjustarHorario2);
        BTAjustarHorario2.setBounds(10, 120, 380, 50);

        Progress.setFont(new java.awt.Font("Candara", 0, 16)); // NOI18N
        Progress.setStringPainted(true);
        jPanel4.add(Progress);
        Progress.setBounds(630, 20, 290, 30);

        jTabbedPane1.addTab("Aplicações", jPanel4);

        jPanel2.setLayout(null);

        PConsole.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Console", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Candara", 1, 18))); // NOI18N
        PConsole.setLayout(null);

        TXConsole.setEditable(false);
        TXConsole.setColumns(20);
        TXConsole.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        TXConsole.setRows(5);
        ScrollConsole.setViewportView(TXConsole);

        PConsole.add(ScrollConsole);
        ScrollConsole.setBounds(10, 20, 890, 150);

        jPanel2.add(PConsole);
        PConsole.setBounds(0, 220, 920, 180);

        TP.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Analógicas", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Candara", 0, 18))); // NOI18N
        TP.setFont(new java.awt.Font("Candara", 0, 18)); // NOI18N
        TP.setLayout(null);

        jLabel1.setFont(new java.awt.Font("Candara", 0, 16)); // NOI18N
        jLabel1.setText("D1");
        TP.add(jLabel1);
        jLabel1.setBounds(30, 30, 20, 21);

        TFTRD1.setFont(new java.awt.Font("Candara", 0, 14)); // NOI18N
        TP.add(TFTRD1);
        TFTRD1.setBounds(30, 50, 100, 24);

        jLabel4.setFont(new java.awt.Font("Candara", 0, 16)); // NOI18N
        jLabel4.setText("D2");
        TP.add(jLabel4);
        jLabel4.setBounds(30, 80, 20, 21);

        TFTRD2.setFont(new java.awt.Font("Candara", 0, 14)); // NOI18N
        TP.add(TFTRD2);
        TFTRD2.setBounds(30, 100, 100, 24);

        jPanel2.add(TP);
        TP.setBounds(10, 60, 160, 150);

        PTempoReal.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Analógicas", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Candara", 0, 18))); // NOI18N
        PTempoReal.setFont(new java.awt.Font("Candara", 0, 18)); // NOI18N
        PTempoReal.setLayout(null);

        jLabel5.setFont(new java.awt.Font("Candara", 0, 16)); // NOI18N
        jLabel5.setText("A2");
        PTempoReal.add(jLabel5);
        jLabel5.setBounds(20, 80, 20, 20);

        TFTRA1.setFont(new java.awt.Font("Candara", 0, 14)); // NOI18N
        PTempoReal.add(TFTRA1);
        TFTRA1.setBounds(20, 50, 70, 24);

        TFTRA2.setFont(new java.awt.Font("Candara", 0, 14)); // NOI18N
        PTempoReal.add(TFTRA2);
        TFTRA2.setBounds(20, 100, 70, 24);

        jLabel7.setFont(new java.awt.Font("Candara", 0, 16)); // NOI18N
        jLabel7.setText("A1");
        PTempoReal.add(jLabel7);
        jLabel7.setBounds(20, 30, 20, 20);

        jLabel9.setFont(new java.awt.Font("Candara", 0, 16)); // NOI18N
        jLabel9.setText("A4");
        PTempoReal.add(jLabel9);
        jLabel9.setBounds(110, 80, 20, 20);

        TFTRA3.setFont(new java.awt.Font("Candara", 0, 14)); // NOI18N
        PTempoReal.add(TFTRA3);
        TFTRA3.setBounds(110, 50, 70, 24);

        TFTRA4.setFont(new java.awt.Font("Candara", 0, 14)); // NOI18N
        PTempoReal.add(TFTRA4);
        TFTRA4.setBounds(110, 100, 70, 24);

        jLabel10.setFont(new java.awt.Font("Candara", 0, 16)); // NOI18N
        jLabel10.setText("A3");
        PTempoReal.add(jLabel10);
        jLabel10.setBounds(110, 30, 20, 20);

        TFTRA5.setFont(new java.awt.Font("Candara", 0, 14)); // NOI18N
        PTempoReal.add(TFTRA5);
        TFTRA5.setBounds(200, 50, 70, 24);

        TFTRA6.setFont(new java.awt.Font("Candara", 0, 14)); // NOI18N
        PTempoReal.add(TFTRA6);
        TFTRA6.setBounds(200, 100, 70, 24);

        jLabel11.setFont(new java.awt.Font("Candara", 0, 16)); // NOI18N
        jLabel11.setText("A5");
        PTempoReal.add(jLabel11);
        jLabel11.setBounds(200, 30, 20, 20);

        jLabel12.setFont(new java.awt.Font("Candara", 0, 16)); // NOI18N
        jLabel12.setText("A6");
        PTempoReal.add(jLabel12);
        jLabel12.setBounds(200, 80, 20, 20);

        TFTRA7.setFont(new java.awt.Font("Candara", 0, 14)); // NOI18N
        PTempoReal.add(TFTRA7);
        TFTRA7.setBounds(290, 50, 70, 24);

        jLabel19.setFont(new java.awt.Font("Candara", 0, 16)); // NOI18N
        jLabel19.setText("A7");
        PTempoReal.add(jLabel19);
        jLabel19.setBounds(290, 30, 20, 20);

        jLabel21.setFont(new java.awt.Font("Candara", 0, 16)); // NOI18N
        jLabel21.setText("A8");
        PTempoReal.add(jLabel21);
        jLabel21.setBounds(290, 80, 20, 20);

        TFTRA8.setFont(new java.awt.Font("Candara", 0, 14)); // NOI18N
        PTempoReal.add(TFTRA8);
        TFTRA8.setBounds(290, 100, 70, 24);

        jLabel22.setFont(new java.awt.Font("Candara", 0, 16)); // NOI18N
        jLabel22.setText("A10");
        PTempoReal.add(jLabel22);
        jLabel22.setBounds(380, 80, 40, 20);

        jLabel23.setFont(new java.awt.Font("Candara", 0, 16)); // NOI18N
        jLabel23.setText("A9");
        PTempoReal.add(jLabel23);
        jLabel23.setBounds(380, 30, 20, 20);

        TFTRA9.setFont(new java.awt.Font("Candara", 0, 14)); // NOI18N
        PTempoReal.add(TFTRA9);
        TFTRA9.setBounds(380, 50, 70, 24);

        TFTRA10.setFont(new java.awt.Font("Candara", 0, 14)); // NOI18N
        PTempoReal.add(TFTRA10);
        TFTRA10.setBounds(380, 100, 70, 24);

        jLabel26.setFont(new java.awt.Font("Candara", 0, 16)); // NOI18N
        jLabel26.setText("A11");
        PTempoReal.add(jLabel26);
        jLabel26.setBounds(470, 30, 60, 20);

        jLabel30.setFont(new java.awt.Font("Candara", 0, 16)); // NOI18N
        jLabel30.setText("A12");
        PTempoReal.add(jLabel30);
        jLabel30.setBounds(470, 80, 80, 20);

        TFTRA11.setFont(new java.awt.Font("Candara", 0, 14)); // NOI18N
        PTempoReal.add(TFTRA11);
        TFTRA11.setBounds(470, 50, 70, 24);

        TFTRA12.setFont(new java.awt.Font("Candara", 0, 14)); // NOI18N
        PTempoReal.add(TFTRA12);
        TFTRA12.setBounds(470, 100, 70, 24);

        TFTRA13.setFont(new java.awt.Font("Candara", 0, 14)); // NOI18N
        PTempoReal.add(TFTRA13);
        TFTRA13.setBounds(560, 50, 70, 24);

        TFTRA14.setFont(new java.awt.Font("Candara", 0, 14)); // NOI18N
        PTempoReal.add(TFTRA14);
        TFTRA14.setBounds(560, 100, 70, 24);

        TFTRA15.setFont(new java.awt.Font("Candara", 0, 14)); // NOI18N
        PTempoReal.add(TFTRA15);
        TFTRA15.setBounds(650, 50, 70, 24);

        jLabel32.setFont(new java.awt.Font("Candara", 0, 16)); // NOI18N
        jLabel32.setText("A13");
        PTempoReal.add(jLabel32);
        jLabel32.setBounds(560, 30, 40, 20);

        jLabel34.setFont(new java.awt.Font("Candara", 0, 16)); // NOI18N
        jLabel34.setText("A15");
        PTempoReal.add(jLabel34);
        jLabel34.setBounds(650, 30, 50, 20);

        TFTRA16.setFont(new java.awt.Font("Candara", 0, 14)); // NOI18N
        PTempoReal.add(TFTRA16);
        TFTRA16.setBounds(650, 100, 70, 24);

        jLabel43.setFont(new java.awt.Font("Candara", 0, 16)); // NOI18N
        jLabel43.setText("A14");
        PTempoReal.add(jLabel43);
        jLabel43.setBounds(560, 80, 50, 20);

        jLabel47.setFont(new java.awt.Font("Candara", 0, 16)); // NOI18N
        jLabel47.setText("A16");
        PTempoReal.add(jLabel47);
        jLabel47.setBounds(650, 80, 50, 20);

        jPanel2.add(PTempoReal);
        PTempoReal.setBounds(180, 60, 740, 150);

        TRLabelUltimaColeta.setFont(new java.awt.Font("Candara", 0, 18)); // NOI18N
        TRLabelUltimaColeta.setText("30/08/2021 17:05:00");
        jPanel2.add(TRLabelUltimaColeta);
        TRLabelUltimaColeta.setBounds(140, 20, 190, 20);

        jLabel39.setFont(new java.awt.Font("Candara", 0, 18)); // NOI18N
        jLabel39.setText("Última coleta:");
        jPanel2.add(jLabel39);
        jLabel39.setBounds(20, 20, 110, 20);

        jTabbedPane1.addTab("Dados em Tempo Real", jPanel2);

        getContentPane().add(jTabbedPane1);
        jTabbedPane1.setBounds(10, 50, 940, 510);

        jButton3.setFont(new java.awt.Font("Candara", 0, 14)); // NOI18N
        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/unioeste/view/Icones/page.png"))); // NOI18N
        jButton3.setText("Manual de Instalação");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton3);
        jButton3.setBounds(630, 20, 180, 30);

        jButton5.setFont(new java.awt.Font("Candara", 0, 14)); // NOI18N
        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/unioeste/view/Icones/lampadas16.png"))); // NOI18N
        jButton5.setText("Ajuda");
        getContentPane().add(jButton5);
        jButton5.setBounds(820, 20, 130, 30);

        setSize(new java.awt.Dimension(974, 599));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowGainedFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowGainedFocus
//        System.out.println(configFile != null);
//        if (configFile != null) {
//            try {
//                fillConfigFile("");
//            } catch (IOException ex) {
//                JOptionPane.showMessageDialog(null, "Erro ao abrir arquivo de configurações", "Erro Config", JOptionPane.ERROR_MESSAGE);
//            }
//        }
    }//GEN-LAST:event_formWindowGainedFocus

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if (CBColeta.getSelectedIndex() < 0) {
            JOptionPane.showMessageDialog(this, "Campo obrigatório (Intervalo Coleta) não foi preenchido", "Campo obrigatório", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (CBPortaSaida.getSelectedIndex() < 0) {
            JOptionPane.showMessageDialog(this, "Campo obrigatório (Porta de saída de dados) não foi preenchido", "Campo obrigatório", JOptionPane.ERROR_MESSAGE);
            return;
        }

        fillAnalogPorts();
        createConfigFile();


    }//GEN-LAST:event_jButton1ActionPerformed

    private void TFOffset8FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_TFOffset8FocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_TFOffset8FocusLost

    private void TFOffset8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TFOffset8ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TFOffset8ActionPerformed

    private void CKAna8FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_CKAna8FocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_CKAna8FocusLost

    private void TFOffset7FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_TFOffset7FocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_TFOffset7FocusLost

    private void TFOffset7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TFOffset7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TFOffset7ActionPerformed

    private void CKAna7FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_CKAna7FocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_CKAna7FocusLost

    private void TFOffset6FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_TFOffset6FocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_TFOffset6FocusLost

    private void TFOffset6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TFOffset6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TFOffset6ActionPerformed

    private void CKAna6FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_CKAna6FocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_CKAna6FocusLost

    private void TFOffset5FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_TFOffset5FocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_TFOffset5FocusLost

    private void TFOffset5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TFOffset5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TFOffset5ActionPerformed

    private void CKAna5FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_CKAna5FocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_CKAna5FocusLost

    private void TFOffset4FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_TFOffset4FocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_TFOffset4FocusLost

    private void TFOffset4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TFOffset4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TFOffset4ActionPerformed

    private void CKAna4FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_CKAna4FocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_CKAna4FocusLost

    private void TFOffset3FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_TFOffset3FocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_TFOffset3FocusLost

    private void TFOffset3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TFOffset3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TFOffset3ActionPerformed

    private void CKAna3FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_CKAna3FocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_CKAna3FocusLost

    private void TFOffset2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_TFOffset2FocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_TFOffset2FocusLost

    private void TFOffset2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TFOffset2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TFOffset2ActionPerformed

    private void CKAna2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_CKAna2FocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_CKAna2FocusLost

    private void CKAna9FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_CKAna9FocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_CKAna9FocusLost

    private void TFOffset9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TFOffset9ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TFOffset9ActionPerformed

    private void TFOffset9FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_TFOffset9FocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_TFOffset9FocusLost

    private void CKAna10FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_CKAna10FocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_CKAna10FocusLost

    private void TFOffset10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TFOffset10ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TFOffset10ActionPerformed

    private void TFOffset10FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_TFOffset10FocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_TFOffset10FocusLost

    private void CKAna11FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_CKAna11FocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_CKAna11FocusLost

    private void TFOffset11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TFOffset11ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TFOffset11ActionPerformed

    private void TFOffset11FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_TFOffset11FocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_TFOffset11FocusLost

    private void CKAna12FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_CKAna12FocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_CKAna12FocusLost

    private void TFOffset12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TFOffset12ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TFOffset12ActionPerformed

    private void TFOffset12FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_TFOffset12FocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_TFOffset12FocusLost

    private void CKAna13FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_CKAna13FocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_CKAna13FocusLost

    private void TFOffset13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TFOffset13ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TFOffset13ActionPerformed

    private void TFOffset13FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_TFOffset13FocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_TFOffset13FocusLost

    private void CKAna14FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_CKAna14FocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_CKAna14FocusLost

    private void TFOffset14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TFOffset14ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TFOffset14ActionPerformed

    private void TFOffset14FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_TFOffset14FocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_TFOffset14FocusLost

    private void CKAna15FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_CKAna15FocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_CKAna15FocusLost

    private void TFOffset15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TFOffset15ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TFOffset15ActionPerformed

    private void TFOffset15FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_TFOffset15FocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_TFOffset15FocusLost

    private void CKAna16FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_CKAna16FocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_CKAna16FocusLost

    private void TFOffset16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TFOffset16ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TFOffset16ActionPerformed

    private void TFOffset16FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_TFOffset16FocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_TFOffset16FocusLost

    private void CBPortasItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_CBPortasItemStateChanged
        if (CBPortas.getSelectedIndex() >= 0) {
            String porta = CBPortas.getSelectedItem().toString();
            try {
                //BTDebug.setEnabled(true);                
                //BTSimulaLogger.setEnabled(true);

                //Habilita os botoes
                //            } catch (NoSuchPortException ex) {
                //                JOptionPane.showMessageDialog(null, "A porta " + porta + " não existe!", "Erro COM", JOptionPane.ERROR_MESSAGE);
                //                CBPortas.setSelectedIndex(-1);
                //            } catch (PortInUseException ex) {
                //                JOptionPane.showMessageDialog(null, "A porta " + porta + " está em uso!", "Erro COM", JOptionPane.ERROR_MESSAGE);
                //                CBPortas.setSelectedIndex(-1);
            } catch (Exception ex) {
                logger.error("Erro desconhecido ao abrir a porta de comunicação" + "\n" + ex.fillInStackTrace());
                JOptionPane.showMessageDialog(null, "Algum erro desconhecido ocorreu ao abrir a porta  " + porta + "\n" + ex.fillInStackTrace(), "Erro COM", JOptionPane.ERROR_MESSAGE);
                CBPortas.setSelectedIndex(-1);
            }
        }
    }//GEN-LAST:event_CBPortasItemStateChanged

    private void BTRefreshSerialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BTRefreshSerialActionPerformed
        fillComboPortas();
    }//GEN-LAST:event_BTRefreshSerialActionPerformed

    private void BTAjustarHorarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BTAjustarHorarioActionPerformed
        Progress.setValue(0);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Progress.setValue(1);
                    sendToArduino("/firmwares/RTCSincV3.ino.hex");

                    for (int i = 0; i < 15; i++) {
                        Thread.sleep(1000);
                        Progress.setValue(Progress.getValue() + 2);
                    }

                    Calendar c = Calendar.getInstance();

                    serial.sendString(String.valueOf(c.get(Calendar.YEAR)));
                    serial.sendString("\n");
                    serial.sendString(String.valueOf(c.get(Calendar.MONTH) + 1));
                    serial.sendString("\n");
                    serial.sendString(String.valueOf(c.get(Calendar.DAY_OF_MONTH)));
                    serial.sendString("\n");
                    serial.sendString(String.valueOf(c.get(Calendar.HOUR_OF_DAY)));
                    serial.sendString("\n");
                    serial.sendString(String.valueOf(c.get(Calendar.MINUTE)));
                    serial.sendString("\n");
                    serial.sendString(String.valueOf(c.get(Calendar.SECOND)));
                    serial.sendString("\n");
                    serial.sendString("Zerar");
                    serial.sendString("\n");
                    Progress.setValue(100);

                    JOptionPane.showMessageDialog(null, "Horário atualizado com sucesso!\nCaso o horário não tenha sido atualizado, executar processo manualmente pelo HyperTerminal/Termite", "Horário", JOptionPane.WARNING_MESSAGE);
                    Progress.setValue(0);
                } catch (Exception e) {
                    e.printStackTrace();
                    Progress.setValue(0);
                    JOptionPane.showMessageDialog(null, "Erro ao atualizar horario!", "Upload", JOptionPane.INFORMATION_MESSAGE);
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
                return;
            }
        }).start();
    }//GEN-LAST:event_BTAjustarHorarioActionPerformed

    private void BTAjustarHorario1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BTAjustarHorario1ActionPerformed
        Progress.setVisible(true);
        Progress.setValue(0);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Progress.setValue(10);
                    sendToArduino("/firmwares/Debug.ino.hex");

                    Progress.setValue(100);
                    JOptionPane.showMessageDialog(null, "Firmware enviado com sucesso!", "Upload", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    e.printStackTrace();
                    Progress.setValue(0);
                    JOptionPane.showMessageDialog(null, "Erro ao enviar Firmware!", "Upload", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }).start();
    }//GEN-LAST:event_BTAjustarHorario1ActionPerformed

    private void CBPortasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CBPortasActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CBPortasActionPerformed

    private void BTAjustarHorario2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BTAjustarHorario2ActionPerformed
        Progress.setVisible(true);
        Progress.setValue(0);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Progress.setValue(10);
                    sendToArduino("/firmwares/MestradoLogger.ino.hex");

                    Progress.setValue(100);
                    JOptionPane.showMessageDialog(null, "Firmware enviado com sucesso!", "Upload", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    e.printStackTrace();
                    Progress.setValue(0);
                    JOptionPane.showMessageDialog(null, "Erro ao enviar Firmware!", "Upload", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }).start();
    }//GEN-LAST:event_BTAjustarHorario2ActionPerformed

    private void CBPortaSaidaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CBPortaSaidaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CBPortaSaidaActionPerformed

    private void TFIdentificadorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TFIdentificadorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TFIdentificadorActionPerformed

    private void TFIdentificador2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TFIdentificador2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TFIdentificador2ActionPerformed

    private void TFIdentificador3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TFIdentificador3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TFIdentificador3ActionPerformed

    private void TFIdentificador4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TFIdentificador4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TFIdentificador4ActionPerformed

    private void TFIdentificador5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TFIdentificador5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TFIdentificador5ActionPerformed

    private void TFIdentificador6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TFIdentificador6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TFIdentificador6ActionPerformed

    private void TFIdentificador7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TFIdentificador7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TFIdentificador7ActionPerformed

    private void TFIdentificador8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TFIdentificador8ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TFIdentificador8ActionPerformed

    private void TFIdentificador9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TFIdentificador9ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TFIdentificador9ActionPerformed

    private void TFIdentificador10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TFIdentificador10ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TFIdentificador10ActionPerformed

    private void TFIdentificador11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TFIdentificador11ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TFIdentificador11ActionPerformed

    private void TFIdentificador12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TFIdentificador12ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TFIdentificador12ActionPerformed

    private void TFIdentificador13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TFIdentificador13ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TFIdentificador13ActionPerformed

    private void TFIdentificador14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TFIdentificador14ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TFIdentificador14ActionPerformed

    private void TFIdentificador15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TFIdentificador15ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TFIdentificador15ActionPerformed

    private void TFIdentificador16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TFIdentificador16ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TFIdentificador16ActionPerformed

    private void TFNomeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TFNomeKeyReleased
        TFNome.setText(maxlength(TFNome.getText()));
    }//GEN-LAST:event_TFNomeKeyReleased

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed
    public static String maxlength(String str) {
        String valor = "";
        if (str.length() > 10) {
            valor = str.substring(0, 10);
            str = valor;
        }
        return str;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                } catch (Exception exception) {
                    System.out.println("Não foi possivel alterar o LookAndFeel para padrão Windows");
                }
                new TPrincipal().setVisible(true);

            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel A1;
    private javax.swing.JPanel A10;
    private javax.swing.JPanel A11;
    private javax.swing.JPanel A12;
    private javax.swing.JPanel A13;
    private javax.swing.JPanel A14;
    private javax.swing.JPanel A15;
    private javax.swing.JPanel A16;
    private javax.swing.JPanel A2;
    private javax.swing.JPanel A3;
    private javax.swing.JPanel A4;
    private javax.swing.JPanel A5;
    private javax.swing.JPanel A6;
    private javax.swing.JPanel A7;
    private javax.swing.JPanel A8;
    private javax.swing.JPanel A9;
    private javax.swing.JButton BTAjustarHorario;
    private javax.swing.JButton BTAjustarHorario1;
    private javax.swing.JButton BTAjustarHorario2;
    private javax.swing.JButton BTRefreshSerial;
    private javax.swing.JComboBox CBColeta;
    private javax.swing.JComboBox CBPortaSaida;
    private javax.swing.JComboBox CBPortas;
    private javax.swing.JCheckBox CKAna;
    private javax.swing.JCheckBox CKAna10;
    private javax.swing.JCheckBox CKAna11;
    private javax.swing.JCheckBox CKAna12;
    private javax.swing.JCheckBox CKAna13;
    private javax.swing.JCheckBox CKAna14;
    private javax.swing.JCheckBox CKAna15;
    private javax.swing.JCheckBox CKAna16;
    private javax.swing.JCheckBox CKAna2;
    private javax.swing.JCheckBox CKAna3;
    private javax.swing.JCheckBox CKAna4;
    private javax.swing.JCheckBox CKAna5;
    private javax.swing.JCheckBox CKAna6;
    private javax.swing.JCheckBox CKAna7;
    private javax.swing.JCheckBox CKAna8;
    private javax.swing.JCheckBox CKAna9;
    private javax.swing.JCheckBox CKD1;
    private javax.swing.JCheckBox CKD2;
    private javax.swing.JCheckBox CKD3;
    private javax.swing.JPanel PAnalogicas;
    private javax.swing.JPanel PConsole;
    private javax.swing.JPanel PDigitais;
    private javax.swing.JPanel PTempoReal;
    private javax.swing.JProgressBar Progress;
    private javax.swing.JScrollPane ScrollConsole;
    private javax.swing.JTextField TFIdentificador;
    private javax.swing.JTextField TFIdentificador10;
    private javax.swing.JTextField TFIdentificador11;
    private javax.swing.JTextField TFIdentificador12;
    private javax.swing.JTextField TFIdentificador13;
    private javax.swing.JTextField TFIdentificador14;
    private javax.swing.JTextField TFIdentificador15;
    private javax.swing.JTextField TFIdentificador16;
    private javax.swing.JTextField TFIdentificador2;
    private javax.swing.JTextField TFIdentificador3;
    private javax.swing.JTextField TFIdentificador4;
    private javax.swing.JTextField TFIdentificador5;
    private javax.swing.JTextField TFIdentificador6;
    private javax.swing.JTextField TFIdentificador7;
    private javax.swing.JTextField TFIdentificador8;
    private javax.swing.JTextField TFIdentificador9;
    private javax.swing.JTextField TFIntervaloFim;
    private javax.swing.JTextField TFIntervaloFim10;
    private javax.swing.JTextField TFIntervaloFim11;
    private javax.swing.JTextField TFIntervaloFim12;
    private javax.swing.JTextField TFIntervaloFim13;
    private javax.swing.JTextField TFIntervaloFim14;
    private javax.swing.JTextField TFIntervaloFim15;
    private javax.swing.JTextField TFIntervaloFim16;
    private javax.swing.JTextField TFIntervaloFim2;
    private javax.swing.JTextField TFIntervaloFim3;
    private javax.swing.JTextField TFIntervaloFim4;
    private javax.swing.JTextField TFIntervaloFim5;
    private javax.swing.JTextField TFIntervaloFim6;
    private javax.swing.JTextField TFIntervaloFim7;
    private javax.swing.JTextField TFIntervaloFim8;
    private javax.swing.JTextField TFIntervaloFim9;
    private javax.swing.JTextField TFIntervaloIni;
    private javax.swing.JTextField TFIntervaloIni10;
    private javax.swing.JTextField TFIntervaloIni11;
    private javax.swing.JTextField TFIntervaloIni12;
    private javax.swing.JTextField TFIntervaloIni13;
    private javax.swing.JTextField TFIntervaloIni14;
    private javax.swing.JTextField TFIntervaloIni15;
    private javax.swing.JTextField TFIntervaloIni16;
    private javax.swing.JTextField TFIntervaloIni2;
    private javax.swing.JTextField TFIntervaloIni3;
    private javax.swing.JTextField TFIntervaloIni4;
    private javax.swing.JTextField TFIntervaloIni5;
    private javax.swing.JTextField TFIntervaloIni6;
    private javax.swing.JTextField TFIntervaloIni7;
    private javax.swing.JTextField TFIntervaloIni8;
    private javax.swing.JTextField TFIntervaloIni9;
    private javax.swing.JTextField TFNome;
    private javax.swing.JTextField TFOffset;
    private javax.swing.JTextField TFOffset10;
    private javax.swing.JTextField TFOffset11;
    private javax.swing.JTextField TFOffset12;
    private javax.swing.JTextField TFOffset13;
    private javax.swing.JTextField TFOffset14;
    private javax.swing.JTextField TFOffset15;
    private javax.swing.JTextField TFOffset16;
    private javax.swing.JTextField TFOffset2;
    private javax.swing.JTextField TFOffset3;
    private javax.swing.JTextField TFOffset4;
    private javax.swing.JTextField TFOffset5;
    private javax.swing.JTextField TFOffset6;
    private javax.swing.JTextField TFOffset7;
    private javax.swing.JTextField TFOffset8;
    private javax.swing.JTextField TFOffset9;
    private javax.swing.JTextField TFTRA1;
    private javax.swing.JTextField TFTRA10;
    private javax.swing.JTextField TFTRA11;
    private javax.swing.JTextField TFTRA12;
    private javax.swing.JTextField TFTRA13;
    private javax.swing.JTextField TFTRA14;
    private javax.swing.JTextField TFTRA15;
    private javax.swing.JTextField TFTRA16;
    private javax.swing.JTextField TFTRA2;
    private javax.swing.JTextField TFTRA3;
    private javax.swing.JTextField TFTRA4;
    private javax.swing.JTextField TFTRA5;
    private javax.swing.JTextField TFTRA6;
    private javax.swing.JTextField TFTRA7;
    private javax.swing.JTextField TFTRA8;
    private javax.swing.JTextField TFTRA9;
    private javax.swing.JTextField TFTRD1;
    private javax.swing.JTextField TFTRD2;
    private javax.swing.JPanel TP;
    private javax.swing.JTabbedPane TPAnalogicas;
    private javax.swing.JLabel TRLabelUltimaColeta;
    public static javax.swing.JTextArea TXConsole;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel75;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel78;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel80;
    private javax.swing.JLabel jLabel81;
    private javax.swing.JLabel jLabel82;
    private javax.swing.JLabel jLabel83;
    private javax.swing.JLabel jLabel84;
    private javax.swing.JLabel jLabel85;
    private javax.swing.JLabel jLabel86;
    private javax.swing.JLabel jLabel87;
    private javax.swing.JLabel jLabel88;
    private javax.swing.JLabel jLabel89;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel90;
    private javax.swing.JLabel jLabel91;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextPane jTextPane1;
    // End of variables declaration//GEN-END:variables
}
