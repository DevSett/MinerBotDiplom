package app.classes.services;

import app.MainApp;
import app.classes.enums.Commands;
import app.classes.models.Autorithation;
import app.classes.models.Property;
import app.classes.server.ServiceServer;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.*;
import java.util.*;
import java.util.List;

import static app.classes.server.ServiceServer.getScreenshot;
import static app.classes.server.ServiceServer.sessionMap;

/**
 * Created by Сергей on 05.07.2017.
 */
@Log4j
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


    @Getter
    @Setter
    private String test;


    private Logger logger = Logger.getLogger(MinerBot.class);

    public static List<Autorithation> list = new ArrayList<>();

    private String[] bufer;

    public MinerBot(DefaultBotOptions option) {
        super(option);
    }

    public MinerBot() {
    }

    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if (message == null) message = update.getCallbackQuery().getMessage();

        log.info("Введена команда: " + message.getText() + "\n" + message.getChat());
        Autorithation autorithation = checkAutorithation(message.getChatId(), message.getText().replace("/", ""));
        if (!autorithation.status()) {
            sendMessage(message.getChatId(), "Пароль введен неверный, попробуйте снова!");
            return;
        }
        if (message.getText().equalsIgnoreCase("/start")) {
            boolean guest = false;
            if (autorithation.getName().equals("guest")) guest = true;
            sendMessage(message.getChatId(), "Клавиатура доступна!", initCommand(guest));
            return;
        }
        if (message.getText().startsWith(Commands.SUBSCRIBE.toString())) {

        }
        String temp = null;
        for (Commands command : Commands.values()) {


            String text = message.getText().contains("/") ? message.getText() : "/" + message.getText();

            if (command.toString().equalsIgnoreCase(
                    update.getMessage() != null ? text.split(" ")[0].substring(1) :
                            update.getCallbackQuery().getData().split(" ")[0]

            )) {
                switch (command) {
                    case SUBSCRIBE_STOP:
                        sendMessage(message.getChatId(), autorithation.stopSubscribes());
                        return;
                    case SUBSCRIBE:
                        if (update.getMessage() != null)
                            sendMessage(message.getChatId(), "Выберете время уведомления", getKeyboardTime());
                        else {
                            String[] splits = update.getCallbackQuery().getData().split(" ");
                            if (splits.length > 1) {
                                String time = splits[1];
                                if (time.split(":").length == 1) time += ":00:00";
                                if (time.split(":").length == 2) time += ":00";
                                sendMessage(message.getChatId(), autorithation.subscribe(time));
                            }
                        }
                        return;
                    case STATUS: {
                        if (update.getMessage() == null) {
                            String id = UUID.randomUUID().toString();
                            sendMessage(message.getChatId(), ServiceServer.getStatusDebug(id, update.getCallbackQuery().getData().split(" ")[1], autorithation.getName(), false));
                            return;
                        }

                        getRigs(message, autorithation.getName(), Commands.STATUS.toString());
                        return;
                    }
                    case STATUS_DEBUG: {
                        if (update.getMessage() == null) {
                            String id = UUID.randomUUID().toString();
                            sendMessage(message.getChatId(), ServiceServer.getStatusDebug(id, update.getCallbackQuery().getData().split(" ")[1], autorithation.getName(), true));
                            return;
                        }

                        getRigs(message,autorithation.getName(), Commands.STATUS_DEBUG.toString());
                        return;
                    }
                    case AVERAGE:
                        sendMessage(message.getChatId(), (temp = serviceMoney.getAverage(autorithation.getCurrentLink(), autorithation.getAccount(), autorithation.getType())) != null ? temp : "Неверно указана ссылка на пул в файле или ошибка получение данных о валюте(проверьте команду /rate)!");
                        return;
                    case BALANCE:
                        sendMessage(message.getChatId(), (temp = serviceMoney.getBalance(autorithation.getAccount())) != null ? temp : "Неверно указана ссылка на пул в файле или ошибка получение данных о валюте(проверьте команду /rate)!");
                        return;
//                    case SCREENSHOT:
//                        sendImage(message.getChatId(),);
                    case MONEY:
                        sendMessage(message.getChatId(), (temp = serviceMoney.getInfo(autorithation.getCurrentLink(), autorithation.getAccount(), autorithation.getType())) != null ? temp : "Неверно указана ссылка на пул в файле или ошибка получение данных о валюте(проверьте команду /rate)!");
                        return;
                    case RATE:
                        sendMessage(message.getChatId(), (temp = serviceMoney.getRate()) != null ? temp : "Ошибка получение данных о валюте!!!");
                        return;
                    case EXIT:
                        autorithation.exit();
                        return;
                    case SERVICE_REBOOT_RIG:
                        if (update.getMessage() == null) {
                            String id = UUID.randomUUID().toString();
                            sendMessage(message.getChatId(), ServiceServer.shutdownRig(id, update.getCallbackQuery().getData().split(" ")[1], autorithation.getName(), true));
                        } else {
                            getRigs(message, autorithation.getName(),Commands.SERVICE_REBOOT_RIG.toString());
                        }

                        return;
                    case SERVICE_SHUTDOWN_RIG:
                        if (update.getMessage() == null) {
                            String id = UUID.randomUUID().toString();
                            sendMessage(message.getChatId(), ServiceServer.shutdownRig(id, update.getCallbackQuery().getData().split(" ")[1], autorithation.getName(), false));

                        } else {
                            getRigs(message,autorithation.getName(), Commands.SERVICE_SHUTDOWN_RIG.toString());

                        }
                        return;
                    case SCREENSHOT:
                        if (update.getMessage() == null) {
                            String id = UUID.randomUUID().toString();
                            SendPhoto sendPhoto = new SendPhoto();
                            sendPhoto.setChatId(message.getChatId());
//
                            String screenshot = getScreenshot(id, update.getCallbackQuery().getData().split(" ")[1], autorithation.getName());
                            if (screenshot == null) {
                                sendMessage(message.getChatId(), "Не удалось загрузить скриншот!");
                                return;
                            }
                            try {
                                InputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(screenshot));
                                sendPhoto.setNewPhoto(update.getCallbackQuery().getData().split(" ")[1] + " Скриншот от " + new Date(), inputStream);
                                sendPhoto.setCaption(update.getCallbackQuery().getData().split(" ")[1] + " Скриншот от " + new Date());
                                sendPhoto(sendPhoto);

                            } catch (TelegramApiException e) {
                                log.error(e.getMessage(), e);
                            }
                            return;
// sendPhoto.setNewPhoto("Скриншот от " + new Date(), is);
//                        sendPhoto.setCaption("Скриншот от " + new Date());
//                        try {
//                            sendPhoto(sendPhoto);
//                        } catch (TelegramApiException e) {
//                            log.error(e.getMessage(), e);
//                        }
//                        return;
                        }
                        getRigs(message,autorithation.getName(), Commands.SCREENSHOT.toString());
                        return;
//                        SendPhoto sendPhoto = new SendPhoto();
//                        sendPhoto.setChatId(message.getChatId());
//
//                        sendPhoto.setNewPhoto("Скриншот от " + new Date(), is);
//                        sendPhoto.setCaption("Скриншот от " + new Date());
//                        try {
//                            sendPhoto(sendPhoto);
//                        } catch (TelegramApiException e) {
//                            log.error(e.getMessage(), e);
//                        }
//                        return;
                }
            }
        }
    }


    private boolean getRigs(Message message, String name, String command) {
        String id = UUID.randomUUID().toString();
        List<String> names = new ArrayList<>();
        sessionMap.forEach((o1, o2) -> {
            o2.forEach((o3,o4)->{
                if (o3.equals(name)) names.add(o1);
            });
        });

        if (names.size() < 1) {
            sendMessage(message.getChatId(), "Нет подключенных риг!");
            return true;
        }
        if (names != null) {
            List<String> commands = new ArrayList<>();

            for (String namec : names) {
                commands.add(command + " " + namec);
            }
            sendMessage(message.getChatId(), "Выберете нужную ригу.",
                    keyboardFromTheList(names, commands, 2));
        }
        return false;
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
        sendMessage(chatId, text, null);

    }

    public void sendMessage(Long chatId, String text, ReplyKeyboard replyKeyboardMarkup) {
        SendMessage sendMessage = new SendMessage(chatId, text);
        sendMessage.enableMarkdown(true);
        sendMessage.enableHtml(true);
        if (replyKeyboardMarkup != null) {
//            replyKeyboardMarkup.setOneTimeKeyboard(true);
            sendMessage.setReplyMarkup(replyKeyboardMarkup);
        }
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            logger.error("sndMsg", e);
        }

    }

    private InlineKeyboardMarkup keyboardFromTheList(@NonNull List<String> listStr, List<String> commands, int k) {
        List<InlineKeyboardButton> list = new ArrayList<>();
        for (int i = 0; i < listStr.size(); i++) {
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton(listStr.get(i));
            inlineKeyboardButton.setCallbackData(commands.get(i));
//            inlineKeyboardButton.setUrl("asd");
            list.add(inlineKeyboardButton);
        }

        // k - number of buttons in single line
        InlineKeyboardMarkup replyKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        ArrayList<InlineKeyboardButton> line;
        if (list.size() <= k) {
            keyboard.add(list);
        } else {
            int i = 0;
            while (i < list.size()) {
                line = new ArrayList<>();
                for (int j = 0; j < k; j++)
                    if ((i + j) < list.size())
                        line.add(list.get(j + i));
                keyboard.add(line);
                i = i + k;
            }
        }
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    private ReplyKeyboardMarkup initCommand(boolean guest) {
        List<String> commands = new ArrayList<>();
        if (!guest)
            for (Commands commands1 : Commands.values()) {
                commands.add(commands1.toString());
            }
        else {
            commands.add(Commands.BALANCE.toString());
            commands.add(Commands.AVERAGE.toString());
            commands.add(Commands.MONEY.toString());
            commands.add(Commands.RATE.toString());
        }
        return keyboardCommands(commands, 1, false);
    }

    private ReplyKeyboardMarkup keyboardCommands(@NonNull List<String> listStr, int k, boolean one_time) {

        // k - number of buttons in single line
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow line;
        if (listStr.size() <= k) {
            KeyboardRow keyboardButtons = new KeyboardRow();
            for (String s : listStr) {
                keyboardButtons.add(s);
            }
            keyboard.add(keyboardButtons);
        } else {
            int i = 0;
            while (i < listStr.size()) {
                line = new KeyboardRow();
                for (int j = 0; j < k; j++)
                    if ((i + j) < listStr.size())
                        line.add(listStr.get(j + i));
                keyboard.add(line);
                i = i + k;
            }
        }
        replyKeyboardMarkup.setOneTimeKeyboard(one_time);
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    public ReplyKeyboard getKeyboardTime() {
        List<String> messages = new ArrayList<>();
        List<String> commands = new ArrayList<>();
        for (int i = 0; i <= 23; i++) {
            String time = String.valueOf(i).length() == 1 ? "0" + String.valueOf(i) : String.valueOf(i);
            messages.add(String.valueOf(time));
//            messages.add("05:10");
            commands.add(Commands.SUBSCRIBE.toString() + " " + messages.get(messages.size() - 1));
//            commands.add(Commands.SUBSCRIBE.toString() + " " + "05:10");
        }
        return keyboardFromTheList(messages, commands, 6);
    }


}
