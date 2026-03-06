package com.annotate.video.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "access-service")
public interface AccessClient {

    @GetMapping("api/video-access/accessible-video-ids")
    List<Long> getAccessibleVideoIds(
            @RequestHeader("X-User-Id") Long userId
    );
}
