package com.themodernizers.misc.upload;

import com.google.common.base.Splitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Base64Utils;
import org.springframework.web.socket.BinaryMessage;
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

public class UploadWSHandler extends BinaryWebSocketHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    Map<WebSocketSession, FileUploadInFlight> sessionToFileMap = new WeakHashMap<>();

    @Override
    public boolean supportsPartialMessages() {
        return true;
    }

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
            FileChannel channel = new FileOutputStream(
                    Paths.get(basePath.toString() ,inflightUpload.name).toFile(), false).getChannel();
            channel.write(ByteBuffer.wrap(inflightUpload.bos.toByteArray()));
            channel.close();
            session.sendMessage(new TextMessage("UPLOAD "+inflightUpload.name));
            session.close();
            sessionToFileMap.remove(session);
            logger.info("Uploaded "+inflightUpload.name);
        }
        String response = "Upload Chunk: size "+ payload.array().length;
        logger.debug(response);

    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessionToFileMap.put(session, new FileUploadInFlight(session));
    }



    static class FileUploadInFlight {
        private final Logger logger = LoggerFactory.getLogger(this.getClass());
        String name;
        String uniqueUploadId;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        /**
         * Fragile constructor - beware not prod ready
         * @param session
         */
        FileUploadInFlight(WebSocketSession session) {
            String query = session.getUri().getQuery();
            String uploadSessionIdBase64 = query.split("=")[1];
            String uploadSessionId = new String(Base64Utils.decodeUrlSafe(uploadSessionIdBase64.getBytes()));

            List<String> sessionIdentifiers = Splitter.on("\\").splitToList(uploadSessionId);
            String uniqueUploadId = session.getRemoteAddress().toString()+sessionIdentifiers.get(0);
            String fileName = sessionIdentifiers.get(1);
            this.name = fileName;
            this.uniqueUploadId = uniqueUploadId;
            logger.info("Preparing upload for "+this.name+" uploadSessionId "+uploadSessionId);
        }
        public void append(ByteBuffer byteBuffer) throws IOException{
            bos.write(byteBuffer.array());
        }
    }
}
