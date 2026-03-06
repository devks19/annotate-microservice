package com.annotate.video.entity;

import com.annotate.video.enums.VideoStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "videos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(name = "video_url", columnDefinition = "TEXT")
    private String videoUrl;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @Enumerated(EnumType.STRING)
    private VideoStatus status;

//    @ManyToOne
//    @JoinColumn(name = "creator_id", nullable = false)
//    private User creator;
    @Column(name = "creator_id", nullable = false)
    private Long creatorId;
    @Column(name = "team_id")
    private Long teamId;

//    @ManyToOne
//    @JoinColumn(name = "team_id")
//    private Team team;


    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "is_published")
    private Boolean isPublished = false;

    @Column(name = "stream_url", columnDefinition = "TEXT")
    private String streamUrl;

    private String s3Key;      // CORRECT

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

}
