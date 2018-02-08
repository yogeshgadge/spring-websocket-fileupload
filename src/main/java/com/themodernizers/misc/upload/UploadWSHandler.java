package com.themodernizers.misc.upload;

import com.google.common.base.Splitter;
import org.springframework.util.Base64Utils;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;


import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class UploadWSHandler extends BinaryWebSocketHandler {

    List<WebSocketSession> sessions = new CopyOnWriteArrayList();


    Map<WebSocketSession, FileUploadInFlight> sessionToFileMap = new WeakHashMap<>();

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {

        ByteBuffer payload = message.getPayload();

        FileUploadInFlight inflightUpload = sessionToFileMap.get(session);

        if (inflightUpload == null) {
            throw new IllegalStateException("This is not expected");
        }
        inflightUpload.append(payload);

        if (message.isLast()) {
            Path basePath = Paths.get(".", "uploads", UUID.randomUUID().toString());
            Files.createDirectories(basePath);
            FileChannel channel = new FileOutputStream(Paths.get(basePath.toString() ,inflightUpload.name).toFile(), false).getChannel();
            channel.write(ByteBuffer.wrap(inflightUpload.bos.toByteArray()));
            channel.close();
            session.sendMessage(new TextMessage("UPLOAD "+inflightUpload.name));
            session.close();
            sessionToFileMap.remove(session);
        }
        String response = "Upload Chunk: size "+ payload.array().length;
        System.out.println(response);

    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);


        sessionToFileMap.put(session, new FileUploadInFlight(session));
    }

    @Override
    public boolean supportsPartialMessages() {
        return true;
    }



    static class FileUploadInFlight {
        String name;
        String uniqueUploadId;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        FileUploadInFlight(WebSocketSession session) {
            String query = session.getUri().getQuery();

            String uploadSessionIdBase64 = query.split("=")[1];
            String uploadSessionId = new String(Base64Utils.decodeUrlSafe(uploadSessionIdBase64.getBytes()));
            System.out.println(uploadSessionId);
            List<String> sessionIdentifiers = Splitter.on("\\").splitToList(uploadSessionId);
            String uniqueUploadId = session.getRemoteAddress().toString()+sessionIdentifiers.get(0);
            String fileName = sessionIdentifiers.get(1);
            this.name = fileName;
            this.uniqueUploadId = uniqueUploadId;
        }
        public void append(ByteBuffer byteBuffer) throws IOException{
            bos.write(byteBuffer.array());
        }
    }


}
