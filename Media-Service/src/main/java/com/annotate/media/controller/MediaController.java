package com.annotate.media.controller;

import com.annotate.media.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

//    @PostMapping("/upload-url")
//    public Map<String, String> getUploadUrl(
//            @RequestParam Long videoId,
//            @RequestParam String fileName
//    ) {
//        String url = mediaService.generateUploadUrl(videoId, fileName);
//        return Map.of("uploadUrl", url);
//    }

    @PostMapping("/upload-url")
    public ResponseEntity<Map<String, String>> getUploadUrl(
            @RequestParam String key
    ) {
        String url = mediaService.generateUploadUrl(key);
        return ResponseEntity.ok(Map.of("uploadUrl", url));
    }

//    @GetMapping("/stream-url")
//    public Map<String, String> getStreamUrl(
//            @RequestParam Long videoId,
//            @RequestParam String fileName
//    ) {
//        String url = mediaService.generateStreamUrl(videoId, fileName);
//        return Map.of("streamUrl", url);
//    }

    @GetMapping("/stream-url")
    public ResponseEntity<Map<String, String>> getStreamUrl(
            @RequestParam String key
    ) {
        String presignedUrl = mediaService.generateGetUrl(key);
        return ResponseEntity.ok(Map.of("url", presignedUrl));
    }
}
