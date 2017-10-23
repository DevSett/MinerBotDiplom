package app.classes.server;

import app.MainApp;
import app.classes.models.Property;
import app.classes.models.SendCommand;
import com.google.gson.Gson;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j;

import javax.websocket.Session;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

@NoArgsConstructor
@Log4j
public class ServiceServer {
    public static Map<String, List<String>> ids = new HashMap<>();
    public static Map<String, Session> sessionMap = new HashMap<>();
    public static CopyOnWriteArraySet<Session> sessions = new CopyOnWriteArraySet<>();

    public static String[] getConnectedRigs(String id) {
//        System.out.printn(this);
        ids.put(id, new ArrayList());

        for (Session session : sessions) {
            if (session != null)
                session.getAsyncRemote().sendText(
                        new Gson().toJson(
                                new SendCommand(
                                        id,
                                        false,
                                        false,
                                        false,
                                        false,
                                        false,
                                        true
                                )
                        ));
        }


        try {
            Integer sleeper = ((Property) MainApp.ctx.getBean("property")).getWaitToText();
            Integer sleepTime = sleeper/10;

            Thread.sleep(sleepTime);
            if (ids.get(id).size() > 0) return ids.get(id).toArray(new String[ids.size()]);

            while (sleepTime < sleeper) {

                Thread.sleep(sleeper / 100);
                sleepTime += sleeper / 100;


                if (ids.get(id).size() > 0) return ids.get(id).toArray(new String[ids.size()]);
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }

        return ids.get(id).size() == 0 ? null : ids.get(id).toArray(new String[ids.size()]);
    }

    public static String getStatusDebug(String id, String s, boolean isDebug) {
        ids.put(id, new ArrayList());
        Session session = sessionMap.get(s);
        if (session == null) return "Рига не в сети! Обновите список!";
        session.getAsyncRemote().sendText(new Gson().toJson(
                new SendCommand(
                        id,
                        !isDebug,
                        isDebug,
                        false,
                        false,
                        false,
                        false
                )
        ));
        try {
            Integer sleeper = ((Property) MainApp.ctx.getBean("property")).getWaitToText();
            Integer sleepTime = sleeper / 10;

            Thread.sleep(sleepTime);
            if (ids.get(id).size() > 0) return ids.get(id).get(0);


            while (sleepTime < sleeper) {

                Thread.sleep(sleeper / 100);
                sleepTime += sleeper / 100;

                if (ids.get(id).size() > 0) return ids.get(id).get(0);
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }

        return ids.get(id).size() == 0 ? null : ids.get(id).get(0);

    }

    public static String getScreenshot(String id, String s) {
        ids.put(id, new ArrayList());
        Session session = sessionMap.get(s);
        if (session == null) return "Рига не в сети! Обновите список!";
        session.getAsyncRemote().sendText(new Gson().toJson(
                new SendCommand(
                        id,
                        false,
                        false,
                        false,
                        false,
                        true,
                        false
                )
        ));
        try {
            Integer sleeper = ((Property) MainApp.ctx.getBean("property")).getWaitToScreenshot();
            Integer sleepTime = sleeper / 10;

            Thread.sleep(sleepTime);
            if (ids.get(id).size() > 0) return ids.get(id).get(0);

            while (sleepTime < sleeper) {
                Thread.sleep(sleeper / 100);
                sleepTime += sleeper / 100;
                if (ids.get(id).size() > 0) return ids.get(id).get(0);
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }

        return ids.get(id).size() == 0 ? null : ids.get(id).get(0);
    }

    public static String shutdownRig(String id, String s, boolean isReboot) {
        Session session = sessionMap.get(s);
        if (session == null) return "Рига не в сети! Обновите список!";
        session.getAsyncRemote().sendText(new Gson().toJson(
                new SendCommand(
                        id,
                        false,
                        false,
                        isReboot,
                        !isReboot,
                        false,
                        false
                )
        ));

        return !isReboot ? "Отправлена команда на отключение!" : "Отправлена команда на перезагрузку!";
    }


}
