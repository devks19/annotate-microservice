package com.annotate.feedback.client;

import com.annotate.feedback.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "auth-service")
public interface AuthClient {
    @GetMapping("/api/users/{id}")
    UserResponse getUser(@PathVariable Long id);
}