package app.classes.models;

import app.classes.services.MinerBot;
import app.classes.services.ServiceMoney;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by killsett on 13.06.17.
 */
@Component("property")
public class Property {
    private Logger logger = Logger.getLogger(MinerBot.class);

    private String telegramBotName;
    private String telegramApiKey;
    private InputStream input = null;
    private String pathToLog;

    @Autowired
    ServiceMoney serviceMoney;

    @Getter
    @Setter
    private Integer waitToText;
    @Getter
    @Setter
    private Integer waitToScreenshot;
    @Getter
    @Setter
    private String linkBalance;

    @Getter
    @Setter
    private String ip;

    @Getter
    @Setter
    private String port;

    @Getter
    @Setter
    private String keyHost;

    @Getter
    @Setter
    private String keyCrypt;

    @Getter
    @Setter
    private List<String> hosts;
    @Getter
    @Setter
    private List<Integer> ports;

    @Getter
    @Setter
    private String waitToAlertMin;

    //    private String gpuLogName;
    public Property() {
        //do not use
    }

    @PostConstruct
    public void init() {
        Properties prop = new Properties();

        hosts = new ArrayList<>();
        ports = new ArrayList<>();
        try {

            input = new FileInputStream(new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath()).getParent() + "/config.properties");

            // load a properties file
            prop.load(input);

            // get the property value and print it out
            telegramBotName = prop.getProperty("TelegramBotName");
            telegramApiKey = prop.getProperty("TelegramApiKey");
            setLinkBalance(prop.getProperty("LinkBalance"));
            setIp(prop.getProperty("Ip"));
            setPort(prop.getProperty("Port"));
            setWaitToScreenshot(Integer.valueOf(prop.getProperty("WaitToScreenshot")));
            setWaitToText(Integer.valueOf(prop.getProperty("WaitToText")));
            setKeyHost(prop.getProperty("KeyHost"));
            setKeyCrypt(prop.getProperty("KeyCryptAutoBase"));
            setWaitToAlertMin(prop.getProperty("WaitToAlertMin"));

            List<String> list = serviceMoney.getProxy();
            for (String s : list) {
                hosts.add(s.split(":")[0]);
                ports.add(Integer.valueOf(s.split(":")[1]));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.error("input", e);
                }
            }
        }
    }


//    public String getGpuLogName() {
//        return gpuLogName;
//    }

    public String getTelegramBotName() {
        return telegramBotName;
    }

    public String getTelegramApiKey() {
        return telegramApiKey;
    }

    public String[] checkPassword(String password) {
        BufferedReader reader = null;
        try {
            Path path = new File(
                    new File(
                            getClass()
                                    .getProtectionDomain()
                                    .getCodeSource()
                                    .getLocation()
                                    .getPath()).getParent() + "/rigs.json").toPath();

            Path path1 = new File("rigs.json").toPath();
            reader = Files.newBufferedReader(path);

            String json;
            String fullJson = "";
            while ((json = reader.readLine()) != null) {
                fullJson += json;
            }
            JsonElement jelement = new JsonParser().parse(fullJson);
            JsonObject jobject = jelement.getAsJsonObject();

            for (Map.Entry<String, JsonElement> stringJsonElementEntry : jobject.entrySet()) {
                JsonObject objectRig = stringJsonElementEntry.getValue().getAsJsonObject();
                JsonElement key = objectRig.get("key");
                if (password.equals(key.getAsString()))
                    return new String[]{objectRig.get("link").getAsString(),
                            objectRig.get("account").getAsString(),
                            objectRig.get("type").getAsString(),
                            stringJsonElementEntry.getKey()};
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addJson(String name, final RigsGson gsonElement) {
        BufferedReader reader = null;
        try {
            Path path = new File(
                    new File(
                            getClass()
                                    .getProtectionDomain()
                                    .getCodeSource()
                                    .getLocation()
                                    .getPath()).getParent() + "/rigs.json").toPath();

            reader = Files.newBufferedReader(path);

            String json;
            String fullJson = "";
            while ((json = reader.readLine()) != null) {
                fullJson += json;
            }
            JsonElement jelement = new JsonParser().parse(fullJson);
            JsonObject jobject = jelement.getAsJsonObject();

            Gson gson = new Gson();
            JsonElement jsonElement1 = gson.toJsonTree(gsonElement);
            jobject.add(name, jsonElement1);

            BufferedWriter bufferedWriter = Files.newBufferedWriter(path);
            bufferedWriter.write(jobject.toString());
            bufferedWriter.flush();
            bufferedWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getPathToLog() {
        return pathToLog;
    }

    public void setPathToLog(String pathToLog) {
        this.pathToLog = pathToLog;
    }

    public String getIp() {
        return ip;
    }
}
