package com.annotate.access.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "video_access_codes",
        uniqueConstraints = @UniqueConstraint(columnNames = "video_id"))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class VideoAccessCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "video_id", nullable = false, unique = true)
    private Long videoId;

    @Column(name = "access_code", nullable = false, unique = true)
    private String accessCode;

    @Column(name = "code_active", nullable = false)
    private Boolean codeActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
