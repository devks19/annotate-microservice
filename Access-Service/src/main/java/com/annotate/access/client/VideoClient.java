package com.annotate.access.client;

import com.annotate.access.dto.VideoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "video-service")
public interface VideoClient {
    @GetMapping("api/videos/{id}")
    VideoResponse getVideo(@PathVariable("id") Long id);
}
