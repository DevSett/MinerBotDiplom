package app.classes.models;

import app.classes.services.MinerBot;
import app.classes.services.ServiceGpu;
import app.classes.services.ServiceMoney;
import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;


/**
 * Created by Сергей on 05.07.2017.
 */
@Component("autorithation")
@Log4j
public class Autorithation {

    @Autowired
    Property property;

    @Autowired
    ServiceMoney serviceMoney;

    @Autowired
    ServiceGpu serviceGpu;

    @Autowired
    MinerBot minerBot;


    @Setter
    @Getter
    private String account;

    private String currentLink = null;
//    private Timer timer;

    private Long chatId;

    private boolean session = false;

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public boolean status() {
        return session;
    }

    @Getter
    @Setter
    private String type;
//    public String getLeftTime() {
//        return new Integer(timer.getInitialDelay() / 1000 / 60).toString();
//    }

    @Getter
    @Setter
    private String name;


    private List<Boolean> list = new ArrayList<>();

    public String subscribe(String time) {
        if (!time.matches("[0-9:]+")) return "Неправельный формат времени!";

        Boolean checkStop = false;
        list.add(checkStop);
        Thread threadSubscribe = new Thread(() -> {
            while (!list.get(list.indexOf(checkStop))) {
                Date date = new Date();
                String fullTime = new SimpleDateFormat("HH:mm:ss").format(date);
                String hoursAndMinuts = new SimpleDateFormat("HH:mm:ss").format(date);
                String hours = new SimpleDateFormat("HH:mm:ss").format(date);


//                System.out.println("Проверка 1 "+hoursAndMinuts);
//                System.out.println("Проверка 2 "+time);
                if (fullTime.equalsIgnoreCase(time) || hoursAndMinuts.equalsIgnoreCase(time) || hours.equalsIgnoreCase(time)) {
                    minerBot.sendMessage(getChatId(), "⏰Уведомление:\n\n"+serviceMoney.getInfo(currentLink, account,getType()));
                    try {

                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        log.error("sleepThread", e);
                    }
                }
                if (list.indexOf(checkStop) == -1) break;
            }
        });
        threadSubscribe.start();

        return "⏰Успешно оформленна подписка на " + time;
    }

    public String stopSubscribes() {
        list.forEach(stop -> stop = true);
        list.clear();

        return "Подписки отключены!";
    }

    public boolean join(String password) {
        String[] propertys = property.checkPassword(password);
        if (propertys != null) {
            currentLink = propertys[0];
            setAccount(propertys[1]);
            setType(propertys[2]);
            setName(propertys[3]);
            session = true;
        } else {
            exit();
        }
        return session;
    }

    public void exit() {
        stopSubscribes();
        session = false;
        currentLink = null;
        account = null;
    }

    public String getCurrentLink() {
        return currentLink;
    }


}
