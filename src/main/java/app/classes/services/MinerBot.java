package app.classes.services;

import app.MainApp;
import app.classes.enums.Commands;
import app.classes.models.Autorithation;
import app.classes.models.Property;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Сергей on 05.07.2017.
 */
@Component("minerBot")
public class MinerBot extends TelegramLongPollingBot {

    @Autowired
    Property property;

    @Autowired
    ServiceGpu serviceGpu;

    @Autowired
    MainApp mainApp;

    @Autowired
    ServiceMoney serviceMoney;


    private Logger logger = Logger.getLogger(MinerBot.class);

    List<Autorithation> list = new ArrayList<>();

    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        Autorithation autorithation = checkAutorithation(message.getChatId(), message.getText().replace("/", ""));
        if (!autorithation.status()) {
            sendMessage(message.getChatId(), "Пароль введен неверный, попробуйте снова!");
            return;
        }

        if (message.getText().substring(1).toUpperCase().startsWith(Commands.SUBSCRIBE.toString())) {
            String[] splits = message.getText().split(" ");
            if (splits.length > 1) {
                String time = splits[1];
                if (time.split(":").length==1)time+=":00:00";
                if (time.split(":").length==2)time+=":00";
                sendMessage(message.getChatId(), autorithation.subscribe(time));
            }
        }
        String temp = null;
        for (Commands command : Commands.values()) {
            if (command.toString().toLowerCase().equals(message.getText().substring(1).toLowerCase())) {
                switch (command) {
                    case SUBSCRIBE_STOP:
                        sendMessage(message.getChatId(), autorithation.stopSubscribes());
                        return;

                    case STATUS:
                        if (property.getPathToLog() != null) {
                            sendMessage(message.getChatId(), serviceGpu.getInformation());
                            return;
                        }
                        break;
                    case STATUS_DEBUG:
                        if (property.getPathToLog() != null) {
                            sendMessage(message.getChatId(), serviceGpu.getLastTen());
                            return;
                        }
                        break;
                    case AVERAGE:
                        sendMessage(message.getChatId(), (temp = serviceMoney.getAverage(autorithation.getCurrentLink(), autorithation.getAccount())) != null ? temp : "Неверно указана ссылка на пул в файле или ошибка получение данных о валюте(проверьте команду /rate)!");
                        return;
                    case BALANCE:
                        sendMessage(message.getChatId(), (temp = serviceMoney.getBalance(autorithation.getAccount())) != null ? temp : "Неверно указана ссылка на пул в файле или ошибка получение данных о валюте(проверьте команду /rate)!");
                        return;

//                    case SCREENSHOT:
//                        sendImage(message.getChatId(),);
                    case MONEY:
                        sendMessage(message.getChatId(), (temp = serviceMoney.getInfo(autorithation.getCurrentLink(), autorithation.getAccount())) != null ? temp : "Неверно указана ссылка на пул в файле или ошибка получение данных о валюте(проверьте команду /rate)!");
                        return;
                    case RATE:
                        sendMessage(message.getChatId(), (temp = serviceMoney.getRate()) != null ? temp : "Ошибка получение данных о валюте!!!");
                        return;
                    case EXIT:
                        autorithation.exit();
                        return;
                }
            }
        }
    }

    private Autorithation checkAutorithation(Long chatId, String password) {
        for (int i = 0; i < list.size(); i++) {
            if (chatId.equals(list.get(i).getChatId())) {
                if (list.get(i).status()) return list.get(i);
                else {
                    list.get(i).join(password);
                    return list.get(i);
                }
            }
        }

        Autorithation autorithation = (Autorithation) mainApp.getCtx().getBean("autorithation");
        autorithation.setChatId(chatId);
        autorithation.join(password);
        list.add(autorithation);
        return list.get(list.size() - 1);
    }

    public String getBotUsername() {
        return property.getTelegramBotName();
    }

    public String getBotToken() {
        return property.getTelegramApiKey();
    }

    public void onClosing() {

    }

    public void sendMessage(Long chatId, String text) {
        SendMessage sendMessage = new SendMessage(chatId, text);
        sendMessage.enableMarkdown(true);
        sendMessage.enableHtml(true);
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            logger.error("sndMsg", e);
        }

    }

}
