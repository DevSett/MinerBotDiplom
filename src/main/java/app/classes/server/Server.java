package app.classes.server;

import app.MainApp;
import app.classes.models.Property;
import app.classes.models.ReciveMessage;
import app.classes.models.SendCommand;
import com.google.gson.Gson;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

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
        if (sessionMap.get(reciveMessage.getNameRig()) == null)
            sessionMap.put(reciveMessage.getNameRig(), session);
        List<String> names = ids.get(reciveMessage.getId());
        names.add(reciveMessage.getInformation());
        ids.replace(reciveMessage.getId(), names);
    }

    String key = "";

    @OnClose
    public void onClose(Session session) {
        log.info("[close] " + session);
        sessionMap.forEach((o1, o2) -> {
            if (o2.equals(session)) key = o1;
        });
        sessionMap.remove(key);
        sessions.remove(session);

    }


}
