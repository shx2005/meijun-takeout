package com.mo.web.websocket;

import jakarta.websocket.Session;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
public class WebSocketManager {

    private final static CopyOnWriteArraySet<WebSocketServer> webSocketServerSet = new CopyOnWriteArraySet<>();

    private final static ConcurrentHashMap<String, WebSocketServer> webSocketServerMap = new ConcurrentHashMap<>();

    public static void addWebSocketServer(WebSocketServer webSocketServer){
        if(webSocketServer != null){
            webSocketServerSet.add(webSocketServer);
            webSocketServerMap.put(webSocketServer.getSessionId(), webSocketServer);
        }
    }

    public static void removeWebSocketServer(WebSocketServer webSocketServer){
        if(webSocketServer != null){
            webSocketServerSet.remove(webSocketServer);
            webSocketServerMap.remove(webSocketServer.getSessionId());
        }
    }

    public static void sendToUser(Session session, String message){
        if(session == null){
            log.error("session is null");
        }
        session.getAsyncRemote().sendText(message);
    }

    public static void sendToUser(String sessionId, String message){
        WebSocketServer webSocketServer = webSocketServerMap.get(sessionId);
        if(webSocketServer != null){
            webSocketServer.getSession().getAsyncRemote().sendText(message);
        }else{
            log.error("sessionId is null");
        }
    }

    public static void sendToAll(String message){
        for(WebSocketServer webSocketServer : webSocketServerSet){
            webSocketServer.getSession().getAsyncRemote().sendText(message);
        }
        log.info("发送给所有用户：{}", message);
    }
}
