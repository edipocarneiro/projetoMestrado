/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.unioeste.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import javax.swing.JTextArea;

/**
 *
 * @author cpS
 */
public class ArduinoUPLOAD {

    /**
     * @param args the command line arguments
     */
    public synchronized static Integer execCommand(final String commandLine, JTextArea out) throws IOException, InterruptedException {
        Process p = Runtime.getRuntime().exec(commandLine);
        BufferedReader input = new BufferedReader(new InputStreamReader(p.getErrorStream()));

        String lineOut;
        while ((lineOut = input.readLine()) != null) {
            out.append(lineOut+"\n");
            System.out.println(lineOut);
        }
        
        while ((lineOut = input.readLine()) != null) {
            out.append(lineOut+"\n");
            System.out.println(lineOut);
        }
        input.close();
        return p.waitFor();
    }
}
