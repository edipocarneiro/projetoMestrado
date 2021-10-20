package br.com.unioeste.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class LibraryLoader {

    private static final String[] LIB_X64 = {
        "/libs/winx64/rxtxParallel.dll",
        "/libs/winx64/rxtxSerial.dll"};
    private static final String[] LIB_X86 = {
        "/libs/winx86/rxtxSerial.dll",
        "/libs/winx86/rxtxParallel.dll",
        "/libs/winx86/win32com.dll"};

    public static void loadLibrary() {
        String[] paths;
        if (System.getProperty("sun.arch.data.model").equals("64")) {
            paths = LIB_X64;
            System.out.println("64");
        } else {
            paths = LIB_X86;
            System.out.println("32");
        }

        for (String path : paths) {
            try {
                // have to use a stream
                InputStream in = LibraryLoader.class.getResourceAsStream(path);
                if (in != null) {
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
                        Runtime.getRuntime().load(fileOut.toString());
                    } finally {
                        in.close();
                    }
                }
            } catch (Exception e) {
                // ignore
            } catch (UnsatisfiedLinkError e) {
                // ignore
            }
        }
    }
}
