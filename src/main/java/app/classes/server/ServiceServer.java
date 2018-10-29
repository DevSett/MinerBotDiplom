package app.classes.server;

import app.MainApp;
import app.classes.models.*;
import app.classes.services.CryptoException;
import app.classes.services.CryptoUtils;
import com.google.gson.Gson;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j;

import javax.websocket.Session;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

@NoArgsConstructor
@Log4j
public class ServiceServer {
    public static Map<String, List<String>> ids = new HashMap<>();
    public static Map<String, Map<String, Session>> sessionMap = new HashMap<>();

    public static CopyOnWriteArraySet<Session> sessions = new CopyOnWriteArraySet<>();

    public static String[] getConnectedRigs(String id) {
//        System.out.printn(this);
        ids.put(id, new ArrayList());

        for (Session session : sessions) {
            if (session != null && session.isOpen())
                session.getAsyncRemote().sendText(
                        new Gson().toJson(
                                new SendCommand(
                                        id,
                                        "",
                                        false,
                                        false,
                                        false,
                                        false,
                                        false,
                                        true,
                                        false,
                                        false,
                                        false,
                                        false
                                )
                        ));
        }


        try {
            Integer sleeper = ((Property) MainApp.ctx.getBean("property")).getWaitToText();
            Integer sleepTime = sleeper / 10;

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

    public static String getStatusDebug(String id, String name, String key, boolean isDebug) {
        ids.put(id, new ArrayList());
        Session session = sessionMap.get(name).get(key);
        if (session == null || !session.isOpen()) {
            sessionMap.get(name).remove(key);
            return "Рига не в сети! Обновите список!";
        }

        session.getAsyncRemote().sendText(new Gson().toJson(
                new SendCommand(
                        id,
                        "",
                        !isDebug,
                        isDebug,
                        false,
                        false,
                        false,
                        false,
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

    public static String getScreenshot(String id, String name, String key) {
        ids.put(id, new ArrayList());
        Session session = sessionMap.get(name).get(key);
        if (session == null || !session.isOpen()) {
            sessionMap.get(name).remove(key);
            return "Рига не в сети! Обновите список!";
        }
        session.getAsyncRemote().sendText(new Gson().toJson(
                new SendCommand(
                        id,
                        "",
                        false,
                        false,
                        false,
                        false,
                        true,
                        false,
                        false,
                        false,
                        false,
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

    public static String shutdownRig(String id, String name, String key, boolean isReboot) {
        Session session = sessionMap.get(name).get(key);
        if (session == null || !session.isOpen()) {
            sessionMap.get(name).remove(key);
            return "Рига не в сети! Обновите список!";
        }
        session.getAsyncRemote().sendText(new Gson().toJson(
                new SendCommand(
                        id,
                        "",
                        false,
                        false,
                        isReboot,
                        !isReboot,
                        false,
                        false,
                        false,
                        false,
                        false,
                        false
                )
        ));

        return !isReboot ? "Отправлена команда на отключение!" : "Отправлена команда на перезагрузку!";
    }


    public static void checkHost(final Session sessionHost, final ReciveMessage reciveMessage) {
        Property property = ((Property) MainApp.ctx.getBean("property"));
        if (!checkSession(sessionHost)) return;

        if (reciveMessage.isGetMoney()) {
            checkMoney(sessionHost, reciveMessage, property);
            return;
        }

        if (reciveMessage.isInfoRig()) {
            getInfoRig(sessionHost, reciveMessage);
            return;
        }

        if (reciveMessage.isMoney()) {
            funcMoney(reciveMessage, property);
            return;
        }

        if (reciveMessage.isConfig()) {
            funcConfig(reciveMessage);
            return;
        }

        if (reciveMessage.isGetConfig()) {
            String file = getFileConfig(sessionMap.get(reciveMessage.getNameRig().split(" ")[0])
                    .get(reciveMessage.getNameRig().split(" ")[1]));
            if (file.isEmpty()) return;
            sessionHost.getAsyncRemote().sendText(new Gson().toJson(new SendCommandHost(reciveMessage.getId(), reciveMessage.getNameRig().split(" ")[1], file)));
            return;
        }
        if (reciveMessage.isAddUser()) {
            property.addJson(reciveMessage.getNameRig(), new Gson().fromJson(reciveMessage.getInformation(), RigsGson.class));
            return;
        }
        if (reciveMessage.isRebootRig()) {
            shutdownRig(UUID.randomUUID().toString(),
                    reciveMessage.getNameRig().split(" ")[0],
                    reciveMessage.getNameRig().split(" ")[1],
                    true);

            return;
        }
        if (reciveMessage.isOffRig()) {
            shutdownRig(UUID.randomUUID().toString(),
                    reciveMessage.getNameRig().split(" ")[0],
                    reciveMessage.getNameRig().split(" ")[1],
                    false);
            return;
        }
    }

    private static void funcConfig(ReciveMessage reciveMessage) {
        String text = reciveMessage.getInformation();
        String name = reciveMessage.getNameRig().split(" ")[0];
        String key = reciveMessage.getNameRig().split(" ")[1];
        Session session = sessionMap.get(name).get(key);
        if (!checkSession(session)) return;
        session.getAsyncRemote().sendText(
                new Gson().toJson(
                        new SendCommand(UUID.randomUUID().toString(),
                                text,
                                false,
                                false,
                                false,
                                false,
                                false,
                                false,
                                false,
                                false,
                                false,
                                true)));
    }

    private static void funcMoney(ReciveMessage reciveMessage, Property property) {
        String natural = funcNaturalFileAutoBase(property);
        natural += "\n " + reciveMessage.getNameRig() + " " + reciveMessage.getInformation() + " " + new Date();
        try {
            String encryptedFile = new String(Base64.getEncoder().encode(CryptoUtils.encrypt(property.getKeyCrypt(), natural.getBytes())));
            sessionMap.forEach((name, map) -> {
                map.forEach((key, session) -> {
                    if (checkSession(session)) {
                        session.getAsyncRemote().sendText(new Gson().toJson(new SendCommand(
                                UUID.randomUUID().toString(),
                                encryptedFile,
                                false,
                                false,
                                false,
                                false,
                                false,
                                false,
                                false,
                                true,
                                false,
                                false
                        )));
                    }
                });
            });
        } catch (CryptoException e) {
            e.printStackTrace();
        }
    }

    private static void getInfoRig(Session sessionHost, ReciveMessage reciveMessage) {
        if (!checkSession(sessionHost)) return;

        List<String> infos = new ArrayList<>();
        try {
            sessionMap.forEach((name, map) -> {
                map.forEach((key, session) -> {

                    if (key.equals(reciveMessage.getNameRig())) {
                        infos.add(getStatusDebug(UUID.randomUUID().toString(), name, key, false));
                    }
                });
            });
        } catch (Exception e) {
            return;
        }
        String fullInfo = "";
        for (String info : infos) {
            fullInfo += info;
        }
        sessionHost.getAsyncRemote().sendText(new Gson().toJson(
                new SendCommandHost(
                        reciveMessage.getId(),
                        reciveMessage.getNameRig(),
                        fullInfo)
        ));
    }

    private static void checkMoney(Session sessionHost, ReciveMessage reciveMessage, Property property) {
        BigDecimal monet = BigDecimal.ZERO;


        String file = funcNaturalFileAutoBase(property);

        String[] split = file.split(" ");
        for (int i = 0; i < split.length; i++) {
            if (split[i].equals(reciveMessage.getNameRig())) {
                monet = monet.add(new BigDecimal(split[i + 1]));
            }
        }
        SendCommandHost sendCommandHost = new SendCommandHost(
                reciveMessage.getId(),
                reciveMessage.getNameRig(),
                monet.toString());

        System.out.println(monet.toString());
        sessionHost.getAsyncRemote().sendText(new Gson().toJson(
                sendCommandHost
        ));
        System.out.println(sendCommandHost.toString());
        return;
    }

    private static String funcNaturalFileAutoBase(Property property) {
        List<String> files = new ArrayList<>();
        sessionMap.forEach((name, maps) -> {
            maps.forEach((key, session) -> {
                if (checkSession(session)) {
                    byte[] fileAutoBase = Base64.getDecoder().decode(getFileAutoBase(session));
                    String encryptedFile = "";
                    try {
                        encryptedFile = new String(CryptoUtils.decrypt(property.getKeyCrypt(), fileAutoBase));
                    } catch (CryptoException e) {
                        e.printStackTrace();
                    }
                    files.add(encryptedFile);
                }
            });
        });

        String natural = getNaturalFileAutoBase(files);

        return natural;
    }

    private static boolean checkSession(Session session) {
        final String[] key = {""};
        final String[] name = {""};
        if (!session.isOpen()) {
            sessionMap.forEach((names, rigs) -> {
                rigs.forEach((keys, sessionss) -> {
                            if (sessionss.equals(session)) {
                                key[0] = keys;
                                name[0] = names;
                            }
                        }
                );
            });
            if (!key[0].isEmpty() && !name[0].isEmpty()) {
                sessionMap.get(name).remove(key);
                sessions.remove(session);
            }
            return false;
        } else {
            return true;
        }

    }

    private static String getFileConfig(Session session) {
        if (!checkSession(session)) return "";
        String id = UUID.randomUUID().toString();
        ids.put(id, new ArrayList());
        session.getAsyncRemote().sendText(
                new Gson().toJson(
                        new SendCommand(
                                id,
                                "",
                                false,
                                false,
                                false,
                                false,
                                false,
                                false,
                                false,
                                false,
                                true,
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

    private static String getFileAutoBase(Session session) {
        String id = UUID.randomUUID().toString();
        ids.put(id, new ArrayList());
        session.getAsyncRemote().sendText(
                new Gson().toJson(
                        new SendCommand(
                                id,
                                "",
                                false,
                                false,
                                false,
                                false,
                                false,
                                false,
                                true,
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


                if (ids.get(id).size() > 0)
                    return ids.get(id).get(0);
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }

        return ids.get(id).size() == 0 ? null : ids.get(id).get(0);
    }


    public static String getNaturalFileAutoBase(List<String> files) {
        String naturalFile = "";

        for (int i = 0, checkMax = 0; i < files.size(); i++) {
            for (String file : files) {
                if (file.equals(files.get(i))) {
                    checkMax++;
                    if (checkMax >= files.size() / 10 * 7) {
                        naturalFile = files.get(i);
                    }
                }
            }
        }

        return naturalFile;
    }

}
