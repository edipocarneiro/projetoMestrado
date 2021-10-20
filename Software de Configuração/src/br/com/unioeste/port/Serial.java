package br.com.unioeste.port;

import br.com.unioeste.view.TPrincipal;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.TooManyListenersException;

public class Serial implements SerialPortEventListener {

    InputStream inputStream;
    OutputStream outputStream;
    Boolean toPrint = true;
    Boolean jumpTimeout = true;
    String response = "";
    StringBuilder respostaBuilder = new StringBuilder();
    int timeout = 3000;
    private SerialPort porta;

    public Serial(String nomePorta) throws NoSuchPortException, PortInUseException, IOException, UnsupportedCommOperationException, TooManyListenersException {
        CommPortIdentifier COM = CommPortIdentifier.getPortIdentifier(nomePorta);
        porta = (SerialPort) COM.open("Arduino", 2000);
        porta.setSerialPortParams(
                9600,
                SerialPort.DATABITS_8,
                SerialPort.STOPBITS_1,
                SerialPort.PARITY_NONE);

        porta.setFlowControlMode(
                SerialPort.FLOWCONTROL_NONE);

        this.outputStream = porta.getOutputStream();
        this.inputStream = porta.getInputStream();

        porta.addEventListener(this);
        porta.notifyOnDataAvailable(true);
    }

    @Override
    public void serialEvent(SerialPortEvent event) {
        if (event.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            byte[] readBuffer = new byte[20];
            try {
                int numBytes = 0;
                while ((numBytes = inputStream.read(readBuffer, 0, 19)) > 0) {
                    response = new String(readBuffer, 0, numBytes);
                    respostaBuilder.append(response);
                    System.out.println("resposta: " + response);
                    if (toPrint) {
                        TPrincipal.TXConsole.append(response);
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void close() {
        if (porta != null) {
            try {
                outputStream.flush();
                porta.getOutputStream().flush();
                System.out.println("flush");
                outputStream.close();   //the streams need to be closed

                porta.getOutputStream().close();
                System.out.println("outclose");
                inputStream.close();

                porta.getInputStream().close();
                System.out.println("inclose");
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            porta.notifyOnDataAvailable(false);
            porta.removeEventListener();
            System.out.println("listeners removidos");
            
            porta.close();            
            
            System.out.println("portaclose");
        }
    }

    public void closeOld() {
        new Thread() {
            @Override
            public void run() {
                try {
                    inputStream.close();
                    outputStream.close();
                    porta.removeEventListener();
                    System.out.println("vai fechar");
                    porta.close();
                    System.out.println("fechou");
                } catch (IOException ex) {
                }
            }
        }.start();
    }

    public synchronized String sendAndWait(String at)
            throws IOException {
        String resposta = "";
        respostaBuilder = new StringBuilder();
        sendAtComand(at);
        long inicio = System.currentTimeMillis();
        while (System.currentTimeMillis() < inicio + this.timeout) {
            resposta = respostaBuilder.toString();
            if (jumpTimeout) {
                if ((resposta.indexOf("OK") >= 0)
                        || (resposta.indexOf("ERROR") >= 0)) {
                    break;
                }
            }
        }
        respostaBuilder = new StringBuilder();
        return resposta;
    }

    public void sendAtComand(String at) throws IOException {
        at = at + "\r";
        this.outputStream.write(at.getBytes(), 0, at.length());
    }

    public void sendAtComandSMS(String at) throws IOException {
        at = at + "\032\r";
        this.outputStream.write(at.getBytes(), 0, at.length());
    }

    public void sendString(String at) throws IOException {
        this.outputStream.write(at.getBytes(), 0, at.length());
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void setToPrint(Boolean toPrint) {
        this.toPrint = toPrint;
    }

    public void setJumpTimeout(Boolean jumpTimeout) {
        this.jumpTimeout = jumpTimeout;
    }
}
