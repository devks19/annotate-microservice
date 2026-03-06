package com.annotate.access.dto;

import com.annotate.access.enums.AccessStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessResponse {
    private Long id;
    private Long videoId;
    private String videoTitle;
    private Long viewerId;
    private String viewerName;
    private AccessStatus status;
    private String requestReason;
    private String responseMessage;
    private LocalDateTime requestedAt;
    private LocalDateTime respondedAt;

    private boolean revoked;              // permanent access removal
    private LocalDateTime suspendedUntil; // temporary suspension until this time
}
