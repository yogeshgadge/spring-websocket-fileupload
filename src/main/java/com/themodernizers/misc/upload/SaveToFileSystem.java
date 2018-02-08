package com.themodernizers.misc.upload;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class SaveToFileSystem {

    static void save(String fileName, String prefix, byte[] data) throws IOException {
        Path basePath = Paths.get(".", "uploads", prefix, UUID.randomUUID().toString());
        Files.createDirectories(basePath);

        FileChannel channel =  new FileOutputStream(Paths.get(basePath.toString(), fileName).toFile(), false).getChannel();
        channel.write(ByteBuffer.wrap(data));
        channel.close();
    }
}
