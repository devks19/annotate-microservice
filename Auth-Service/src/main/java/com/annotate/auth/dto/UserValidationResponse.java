package com.annotate.auth.dto;

import com.annotate.auth.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserValidationResponse {
    private Long userId;
    private UserRole role;
    private boolean active;
}
