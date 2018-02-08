package com.themodernizers.misc.upload;

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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;

@Controller
public class UploadControllerSTOMPBased {


    @MessageMapping("/upload")
    public String upload(@RequestBody byte[] data, @RequestHeader("X-File-Name") String fileName) throws Exception {
        System.out.println("/upload "+fileName);
        System.out.println(data);
        return "ACK";
    }

    @PostMapping("/multipart/upload")
    @ResponseBody
    public ResponseEntity<String> handleFileUploadMultipart(@RequestParam("file") MultipartFile file) {

        System.out.println(file);

        return ResponseEntity.ok().header(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getOriginalFilename() + "\"").body("{success: true}");
    }



    @PostMapping("/fetch/upload")
    @ResponseBody
    public ResponseEntity<String> handleFileUploadFetchFileBlob(@RequestBody byte[] data, @RequestHeader("X-File-Name") String fileName) throws IOException{


        FileChannel channel =  new FileOutputStream(Paths.get(".", "uploads", fileName).toFile(), false).getChannel();
        channel.write(ByteBuffer.wrap(data));
        channel.close();

        System.out.println("received "+fileName);
        return ResponseEntity.ok().header(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + fileName + "\"").body("{success: true, uploaded: "+fileName+"}");
    }

    @GetMapping("/multipart/ping")
    @ResponseBody
    public ResponseEntity<String> ping() {
        System.out.println("pinged");
        return ResponseEntity.ok().body(String.format("{pinged: %s", Calendar.getInstance().toString()));
    }
}
