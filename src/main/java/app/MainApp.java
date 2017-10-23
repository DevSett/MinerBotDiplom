package app;

import app.classes.configurations.MainConfig;
import app.classes.models.Property;
import app.classes.server.Server;
import lombok.Getter;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.generics.LongPollingBot;


/**
 * Created by Сергей on 05.07.2017.
 */
@Component("mainApp")
@Log4j
public class MainApp {

    @Autowired
    Property property;


    @Getter
    public static AnnotationConfigApplicationContext ctx;

    public MainApp() {
        //do not use
    }

    /**
     * arg[0] - path to log
     *
     * @param args
     */
    public static void main(String[] args) {

        log.info("Start bot");
        ctx = new AnnotationConfigApplicationContext(MainConfig.class);

        MainApp mainApp = (MainApp) ctx.getBean("mainApp");


        Thread threadBotVk = new Thread(() -> {
            ApiContextInitializer.init();
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
            try {
                telegramBotsApi.registerBot((LongPollingBot) ctx.getBean("minerBot"));
            } catch (TelegramApiException e) {
                e.printStackTrace();
                log.error("Error TelegramApi: ", e);
            }
        });
        threadBotVk.start();

        Thread threadServ = new Thread(() -> {
            Server.connect(mainApp.property.getIp(),mainApp.property.getPort());
        });
        threadServ.start();
    }

    public AnnotationConfigApplicationContext getCtx() {
        return ctx;
    }
}
