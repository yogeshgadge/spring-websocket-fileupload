package com.themodernizers.misc.upload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.UUID;

@Controller
public class UploadControllerSTOMPAndFetch {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @MessageMapping("/upload")
    public String upload(@RequestBody byte[] data, @RequestHeader("X-File-Name") String fileName) throws Exception {
        logger.info("/upload "+fileName);
        logger.info(data.toString());
        return "ACK";
    }

    @PostMapping("/multipart/upload")
    @ResponseBody
    public ResponseEntity<String> handleFileUploadMultipart(@RequestParam("file") MultipartFile file) {

        logger.info(file.toString());

        return ResponseEntity.ok().header(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getOriginalFilename() + "\"").body("{success: true}");
    }



    @PostMapping("/fetch/upload")
    @ResponseBody
    public ResponseEntity<String> handleFileUploadFetchFileBlob(@RequestBody byte[] data, @RequestHeader("X-File-Name") String fileName) throws IOException{

        Path basePath = Paths.get(".", "uploads", "fetch-based", UUID.randomUUID().toString());
        Files.createDirectories(basePath);

        FileChannel channel =  new FileOutputStream(Paths.get(basePath.toString(), fileName).toFile(), false).getChannel();
        channel.write(ByteBuffer.wrap(data));
        channel.close();

        logger.info("received "+fileName);
        return ResponseEntity.ok().header(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + fileName + "\"").body("{success: true, uploaded: "+fileName+"}");
    }

    @GetMapping("/multipart/ping")
    @ResponseBody
    public ResponseEntity<String> ping() {
        logger.info("pinged");
        return ResponseEntity.ok().body(String.format("{pinged: %s", Calendar.getInstance().toString()));
    }
}
