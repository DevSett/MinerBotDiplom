package app.classes.services;

import app.classes.models.Property;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.lang.reflect.Array;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Сергей on 05.07.2017.
 */
@Component("serviceGpu")
public class ServiceGpu {
    private Logger logger = Logger.getLogger(ServiceGpu.class);

    @Autowired
    Property property;


    public String getInformation() {
        String[] fullInformation;

        String temperature = "";
        String totalMhs = "";
        String mhs = "";

        while (temperature.isEmpty() || totalMhs.isEmpty() || mhs.isEmpty()) {
            fullInformation = getLastTenStrokeDebug();

            for (int i = fullInformation.length - 1; i > 0; i--) {
                String currentStroke = fullInformation[i];

                if (currentStroke.contains("t=") && currentStroke.contains("fan")) {
                    temperature = currentStroke;
                    System.out.println(temperature);
                }
                if (currentStroke.contains("Total Speed:")) {
                    totalMhs = currentStroke;
                    System.out.println(totalMhs);
                }
                if (currentStroke.contains("ETH: GPU0")) {
                    mhs = currentStroke;
                    System.out.println(mhs);
                }
                if (!temperature.isEmpty() && !totalMhs.isEmpty() && !mhs.isEmpty()) break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return ("\uD83D\uDD6F\uD83D\uDD6F\uD83D\uDD6F\nТемпература:\n" + Arrays.asList(temperature.split("GPU")).subList(1, temperature.split("GPU").length) + "\nОбщая мощность:\n" + totalMhs.substring(totalMhs.indexOf("ETH"), totalMhs.indexOf(",")) + "\nМощность:\n" + Arrays.asList(mhs.split("GPU")).subList(1, mhs.split("GPU").length)).replaceAll(",", "\n").replaceAll("\\[", "").replaceAll("]", "");
    }

//    public String formatLineLog(String line, int fistSymbol, int secondSymbol, String symbol) {
//        return line.substring(line.indexOf(symbol, fistSymbol) + 1, line.indexOf(symbol, secondSymbol));
//    }

    public String getLastTen() {
        String[] strokes = getLastTenStrokeDebug();
        String result = "";
        if (strokes == null) return result;

        for (String stroke : strokes) {
            result += stroke + "\n";
        }
        return "\uD83D\uDEE0\uD83D\uDEE0\uD83D\uDEE0\n" + result;
    }

    private String[] getLastTenStrokeDebug() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(property.getPathToLog()));
            String[] lastTenStrokes = new String[10];

            String line = null;
            int i = 0;
            while ((line = bufferedReader.readLine()) != null) {
                if (i != 10) lastTenStrokes[i++] = line;
                else {
                    for (int i1 = 1; i1 < lastTenStrokes.length; i1++) {
                        lastTenStrokes[i1 - 1] = lastTenStrokes[i1];
                    }
                    lastTenStrokes[i - 1] = line;
                }
            }
            if (lastTenStrokes[0] == null) return null;
            return lastTenStrokes;

        } catch (IOException e) {
            logger.error("textFromFile", e);
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(testConnection("45.126.154.121",53281));
    }
    public static boolean testConnection(String host, int port) {
        Socket s = null;
        boolean isSocket = false;
        try {
            isSocket = false;
            s = new Socket(host, port);
            isSocket = true;
//            final URL url = new URL("http://www.telegram.me");
//
//            final URLConnection conn = url.openConnection(new Proxy(Proxy.Type.SOCKS, s.getRemoteSocketAddress()));
//            conn.connect();
//            System.out.println("success proxy: " + host + ":" + port);
            return true;

        } catch (Exception e) {

            System.out.println("("+isSocket+")failed proxy: " + host + ":" + port);
            return false;
        } finally {
            if (s != null)
                try {
                    s.close();
                } catch (Exception e) {
                }
        }
    }
}
