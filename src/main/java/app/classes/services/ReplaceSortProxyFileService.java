package app.classes.services;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ReplaceSortProxyFileService implements Runnable {
    private File file;

    public static void main(String[] args) {
        File file = new File(System.getProperty("user.dir") + "/proxy.properties");
        ReplaceSortProxyFileService replaceSortProxyFileService = new ReplaceSortProxyFileService(file);
        replaceSortProxyFileService.run();

    }

    public ReplaceSortProxyFileService(File file) {
        this.file = file;
    }

    @Override
    public void run() {
        String parsedText = readFile();
        writeToFile(parsedText);
    }

    private String readFile() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));

            StringBuilder stringBuilder = new StringBuilder();
            String line = null;
            String ls = System.getProperty("line.separator");
            List<String> keys = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                if (line.contains(".")) {
                    int indexHost = line.indexOf(".");
                    int indexProb = line.substring(0,indexHost).lastIndexOf("\t");
                    if (indexProb++!=-1) {
                        line = line.substring(indexProb);
                        line = line.substring(0,line.indexOf("\t",line.indexOf("\t")+1));
                    }
                    line = line.replaceFirst(":", "=").substring(0,line.indexOf("\t")).replace("\t", "");
                    if (!keys.contains(line.split("=")[0])){
                        keys.add(line.split("=")[0]);
                    }else continue;

                    stringBuilder.append(line);
                    stringBuilder.append(ls);
                }
            }

            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            reader.close();

            String content = stringBuilder.toString();
            return content;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void writeToFile(String text) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(text);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
