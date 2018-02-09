package com.themodernizers.misc.upload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Calendar;
import java.util.Map;
import java.util.WeakHashMap;

@Controller
public class UploadControllersVarious {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    

    @PostMapping("/multipart")
    @ResponseBody
    public ResponseEntity<String> handleFileUploadMultipart(@RequestParam("file") MultipartFile file) throws IOException{

        SaveToFileSystem.save(file.getName(), "multipart", file.getBytes());
        logger.info("multipart uploaded file "+ file.getName());

        return ResponseEntity.ok().header(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getOriginalFilename() + "\"").body("{\"success\": true}");
    }



    @PostMapping(value = "/fetch", produces = "application/json")
    @ResponseBody
    public ResponseEntity<String> handleFileUploadFetchFileBlob(@RequestBody byte[] data, @RequestHeader("X-File-Name") String fileName) throws IOException{


        SaveToFileSystem.save(fileName, "fetch", data);

        logger.info("Uploaded "+fileName);
        return ResponseEntity.ok().header(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + fileName + "\"").body("{\"success\": true}");
    }


    Map<String, ChunkedFileUploadInFlight> sessionToFileMap = new WeakHashMap<>();

    @PostMapping(value = "/chunk-upload", produces = "application/json")
    @ResponseBody
    public ResponseEntity<String> handleChunkedUpload(
            @RequestBody byte[] data,
            @RequestHeader("X-Upload-Session-id") String uploadSessionId,
            @RequestHeader("X-File-Name") String fileName,
            @RequestHeader("X-File-Chunk-Sequence") Integer fileChunkSequence,
            @RequestHeader("X-File-Last") boolean isLast
    ) throws IOException {

        ChunkedFileUploadInFlight inFlightUpload = sessionToFileMap.get(uploadSessionId);
        if (inFlightUpload == null) {
            inFlightUpload = new ChunkedFileUploadInFlight(fileName, uploadSessionId);

        }
        inFlightUpload.addChunk(fileChunkSequence, data);

        if (isLast) {
            SaveToFileSystem.save(fileName, "chunk-upload", inFlightUpload.getAllBytes());
            logger.info("Chunked Upload Final "+fileName);
            return ResponseEntity.ok().header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + fileName + "\"").body("{\"success\": true}");
        } else {
            return ResponseEntity.ok().header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + fileName + "\"").body("{\"chunk-success\": true}");
        }

    }

    @GetMapping("/ping")
    @ResponseBody
    public ResponseEntity<String> ping() {
        logger.info("pinged");
        return ResponseEntity.ok().body(String.format("{pinged: %s", Calendar.getInstance().toString()));
    }
}
