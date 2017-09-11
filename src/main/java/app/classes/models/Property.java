package app.classes.models;

import app.classes.services.MinerBot;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
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

    @Getter
    @Setter
    private String linkBalance;

    //    private String gpuLogName;
    public Property() {
        //do not use
    }

    @PostConstruct
    public void init() {
        Properties prop = new Properties();

        try {

            input = new FileInputStream(new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath()).getParent() + "/config.properties");

            // load a properties file
            prop.load(input);

            // get the property value and print it out
            telegramBotName = prop.getProperty("TelegramBotName");
            telegramApiKey = prop.getProperty("TelegramApiKey");
            setLinkBalance(prop.getProperty("LinkBalance"));
//            gpuLogName = prop.getProperty("NameLogFileGpu");

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
                if (password.equals(key.getAsString())) return new String[]{objectRig.get("link").getAsString(),objectRig.get("account").getAsString()};
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getPathToLog() {
        return pathToLog;
    }

    public void setPathToLog(String pathToLog) {
        this.pathToLog = pathToLog;
    }
}
