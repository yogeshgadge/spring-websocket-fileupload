package com.themodernizers.misc.upload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class ChunkedFileUploadInFlight {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    String name;
    String uploadSessionId;
    Map<Integer, byte[]> orderedChunks = Collections.synchronizedMap(new TreeMap<>());

    /**
     * @param name
     * @param uploadSession
     */
    ChunkedFileUploadInFlight(String name, String uploadSession) {
        this.name = name;
        this.uploadSessionId = uploadSession;

        logger.info("Preparing upload for " + this.name + " uploadSessionId " + uploadSessionId);
    }

    public void addChunk(Integer packetNumber, byte[] chunk) {
        this.orderedChunks.put(packetNumber, chunk);
    }

    public byte[] getAllBytes() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        this.orderedChunks.values()
                .forEach(
                        bytes ->
                        {
                            try {
                                bos.write(bytes);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                );
        return bos.toByteArray();
    }
}
