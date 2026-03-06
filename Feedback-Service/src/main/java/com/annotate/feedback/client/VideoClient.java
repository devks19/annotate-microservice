package com.annotate.feedback.client;

import com.annotate.feedback.dto.VideoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "video-service")
public interface VideoClient {
    @GetMapping("/api/videos/{id}")
    VideoResponse getVideo(@PathVariable Long id);
}
