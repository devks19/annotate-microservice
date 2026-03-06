package com.annotate.feedback.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "access-service")
public interface AccessClient {

    @GetMapping("/api/video-access/check/{videoId}")
    Map<String, Boolean> hasAccess(
            @PathVariable Long videoId,
            @RequestHeader("X-User-Id") Long userId
    );
}
