package com.annotate.access.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SuspendAccessRequest {
    private LocalDateTime suspendedUntil;  // when access should auto-restore
}
