package com.annotate.access.entity;


import com.annotate.access.enums.AccessStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "video_access_permissions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"video_id", "viewer_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class VideoAccessPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "video_id", nullable = false)
    private Long videoId;

    @Column(name = "viewer_id", nullable = false)
    private Long viewerId;

    @Column(name = "creator_id", nullable = false)
    private Long creatorId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccessStatus status = AccessStatus.PENDING;

    @Column(length = 500)
    private String requestReason;

//    @Column(name = "access_code")
//    private String accessCode;

    @Column(length = 500)
    private String responseMessage;

    private LocalDateTime suspendedUntil;   // for temporary removal

    @Column(nullable = false)
    private boolean revoked = false;        // for permanent removal


//    @Column(name = "code_active")
//    private Boolean codeActive = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime requestedAt;

    private LocalDateTime respondedAt;

    @PrePersist
    protected void onCreate() {
        requestedAt = LocalDateTime.now();
    }
}
