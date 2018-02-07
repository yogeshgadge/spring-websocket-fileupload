package com.themodernizers.misc.upload;

import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;


import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class UploadWSHandler extends BinaryWebSocketHandler {

    List<WebSocketSession> sessions = new CopyOnWriteArrayList();


    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {

        ByteBuffer payload = message.getPayload();

        System.out.println(message);
//        FileChannel channel =  new FileOutputStream(new File("file.png"), false).getChannel();
//        channel.write(payload);
//        channel.close();
        String response = "Upload Received: size "+ payload.array().length;
        System.out.println(response);

        for(WebSocketSession webSocketSession : sessions) {
            // webSocketSession.sendMessage(new BinaryMessage(response.getBytes()));

            // webSocketSession.sendMessage(new TextMessage("UPLOADED file "));
        }
        session.sendMessage(new BinaryMessage("UPLOADed ".getBytes()));
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
    }

    @Override
    public boolean supportsPartialMessages() {
        return true;
    }

    //    @Override
//    protected void handleTextMessage(WebSocketSession session, TextMessage message)  {
//        System.out.println("FILE RECEIVING "+message.getPayload());
//        try {
//            session.sendMessage(new TextMessage("FILE RECEIVING "+message.getPayload()));
//        } catch(Exception e) {
//            e.printStackTrace();
//        }
////        for(WebSocketSession webSocketSession : sessions) {
////            try {
////                webSocketSession.sendMessage(new TextMessage("Received " + message.getPayload()));
////            } catch (Exception e) {
////                e.printStackTrace();
////            }
////        }
//    }



}
