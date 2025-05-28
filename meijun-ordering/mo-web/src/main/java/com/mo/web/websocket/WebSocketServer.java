package com.mo.web.websocket;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ServerEndpoint("/ws")
public class WebSocketServer {

    private Session session;

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        WebSocketManager.sendToUser(session, "WebSocket is connected!");
        WebSocketManager.addWebSocketServer(this);
        log.info("与SessionId：{}建立连接", session.getId());
    }

    @OnClose
    public void onClose() {
        WebSocketManager.removeWebSocketServer(this);
        log.info("WebSocket连接关闭");
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("来自SessionId:{}的消息:{}", session.getId(), message);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("Session:{}的WebSocket发生错误", session.getId(), error);
    }

    public Session getSession(){
        return session;
    }

    public String getSessionId(){
        return session.getId();
    }
}
