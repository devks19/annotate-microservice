package com.annotate.access.client;

import com.annotate.access.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@FeignClient(name = "auth-service")
public interface AuthClient {

    @GetMapping("/api/users/{id}")
    UserResponse getUser(@PathVariable("id") Long id);
    @PostMapping("/api/users/bulk")
    List<UserResponse> getUsersByIds(@RequestBody Set<Long> ids);
}
