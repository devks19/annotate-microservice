package com.annotate.feedback.dto;

import com.annotate.feedback.enums.UserRole;
import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String name;
    private UserRole role;
}
