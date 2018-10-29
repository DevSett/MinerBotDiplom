package app.classes.server;

import app.MainApp;
import app.classes.models.Autorithation;
import app.classes.models.Property;
import app.classes.models.ReciveMessage;
import app.classes.services.MinerBot;
import com.google.gson.Gson;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.*;

import static app.classes.server.ServiceServer.*;

@ServerEndpoint("/miner")
@Log4j
@NoArgsConstructor
public class Server {

    public static Server singletonServer;


    public static void connect(String ip, String port) {

        Map<String, Object> properties = Collections.emptyMap();
        org.glassfish.tyrus.server.Server server = new org.glassfish.tyrus.server.Server(ip, Integer.parseInt(port), "/ws", properties, Server.class);
        try {
            server.start();
            log.info("start server");

            while (true) ;

        } catch (DeploymentException e) {
            log.error(e.getMessage(), e);
        } finally {
            server.stop();
            log.info("Server has closed");
        }

    }

    @OnOpen
    public void onOpen(Session session) {
        if (singletonServer == null) singletonServer = this;
        log.info("[open] " + session);
        sessions.add(session);
        new Thread(() -> {
            getConnectedRigs(UUID.randomUUID().toString());
        }).start();
    }

    @OnMessage
    public void onMessage(String message, Session session) {

        if (ids.size() > 1000) ids.clear();
//        System.out.println(this);
        log.info("Получил: " + message);
        ReciveMessage reciveMessage = new Gson().fromJson(message, ReciveMessage.class);

        if (reciveMessage.getKey().equals(((Property) MainApp.ctx.getBean("property")).getKeyHost())) {
            checkHost(session, reciveMessage);
            return;
        }

        if (sessionMap.get(reciveMessage.getNameRig()) == null || sessionMap.get(reciveMessage.getNameRig()).get(reciveMessage.getKey()) == null) {
            Map<String, Session> maps = new HashMap<>();
            maps.put(reciveMessage.getKey(), session);
            sessionMap.put(reciveMessage.getNameRig(), maps);
        }
        List<String> names = ids.get(reciveMessage.getId());
        names.add(reciveMessage.getInformation());
        ids.replace(reciveMessage.getId(), names);
    }

    String key = "";
    String name = "";

    @OnClose
    public void onClose(Session session) {
        name = "";
        key = "";

        log.info("[close] " + session);
        sessionMap.forEach((o1, o2) -> {
            o2.forEach((o3, o4) -> {
                if (o4.equals(session)) {
                    name = o1;
                    key = o3;
                }
            });
        });


        if (!name.isEmpty() && !key.isEmpty()) {
            MinerBot minerBot = ((MinerBot) MainApp.ctx.getBean("minerBot"));
            System.out.println(minerBot);
            for (Autorithation autorithation : MinerBot.list) {
                if (autorithation.getName().equals(key)) {
                    minerBot.sendMessage(autorithation.getChatId(), "Ферма \"" + name + "\" вышла из сети сервера. ");
                }
            }
            sessionMap.get(name).remove(key);

        }

        sessions.remove(session);


    }


}
