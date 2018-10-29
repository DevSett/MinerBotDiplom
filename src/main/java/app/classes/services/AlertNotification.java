package app.classes.services;

import app.MainApp;
import app.classes.models.Autorithation;
import app.classes.models.Property;
import app.classes.server.ServiceServer;

import java.util.UUID;

public class AlertNotification {
    private static String oldTime = "";
    private static String newTime = "";


    public static void check() {
        MinerBot minerBot = ((MinerBot) MainApp.ctx.getBean("minerBot"));
        while (true) {


            ServiceServer.sessionMap.forEach((name, map) -> {
                map.forEach((key, sesssion) -> {
                    String string = ServiceServer.getStatusDebug(UUID.randomUUID().toString(), name, key, false);
                    newTime = string.split("\n")[2];
                    if (newTime.equals(oldTime)){
                        for (Autorithation autorithation : MinerBot.list) {
                            if (autorithation.getName().equals(key))
                            {
                                minerBot.sendMessage(autorithation.getChatId(),"Ферма:"+name+ ". Майнер скорей всего завис!");
                            }
                        }
                    }else {
                        oldTime = newTime;
                    }
                });
            });
            try {
                Thread.sleep(Integer.valueOf(((Property) MainApp.ctx.getBean("property")).getWaitToAlertMin())*60*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
