package com.annotate.video.controller;


import com.annotate.video.client.AccessClient;
import com.annotate.video.dto.VideoResponse;
import com.annotate.video.dto.VideoUploadRequest;
import com.annotate.video.entity.Video;


import com.annotate.video.repository.VideoRepository;
import com.annotate.video.service.VideoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
@Slf4j
//@CrossOrigin(origins = "*")
public class VideoController {
    private final VideoService videoService;
    private final VideoRepository videoRepository;

    private final AccessClient accessClient;

    /**
     * Upload video file from local system
     */
//    @PostMapping(value = "/upload-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<Video> uploadVideoFile(
//            @RequestAttribute("userId") Long creatorId,
//            @RequestParam("file") MultipartFile file,
//            @RequestParam("title") String title,
//            @RequestParam(value = "description", required = false) String description
//    ) {
//        return ResponseEntity.ok(videoService.uploadVideoFile(creatorId, file, title, description));
//    }

//    @PostMapping(value = "/upload-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<VideoResponse> uploadVideoFile(
//            @RequestHeader("X-User-Id") Long creatorId,
//            @RequestParam("file") MultipartFile file,
//            @RequestParam("title") String title,
//            @RequestParam(value = "description", required = false) String description,
//            @RequestParam(value = "teamId", required = false) Long teamId
//    ) {
//        Video video = videoService.uploadVideoFile(creatorId, file, title, description, teamId);
//        return ResponseEntity.ok(mapToResponse(video));
//    }

    /**
     * Upload video via URL
     */
    @PostMapping("/upload")
    public ResponseEntity<VideoResponse> uploadVideo(
            @RequestHeader("X-User-Id") Long creatorId,
            @RequestBody VideoUploadRequest request
    ) {
        Video video = videoService.uploadVideo(creatorId, request);
        return ResponseEntity.ok(mapToResponse(video));
    }

    /**
     * Publish video
     */
    @PutMapping("/{videoId}/publish")
    public ResponseEntity<VideoResponse> publishVideo(
            @PathVariable Long videoId,
            @RequestHeader("X-User-Id") Long creatorId
    ) {
        return ResponseEntity.ok(mapToResponse(videoService.publishVideo(videoId, creatorId)));
    }

    /**
     * Get all videos accessible by the current user (for viewers)
     */
    @GetMapping("/accessible")
    public ResponseEntity<List<VideoResponse>> getAccessibleVideos(
            @RequestHeader("X-User-Id") Long userId
    ) {

        log.info("UserId header received: {}", userId);

        List<Long> videoIds = accessClient.getAccessibleVideoIds(userId);

        if (videoIds.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }

        List<Video> videos = videoRepository
                .findByIdInAndIsPublishedTrue(videoIds);

        return ResponseEntity.ok(
                videos.stream()
                        .map(this::mapToResponse)
                        .toList()
        );
    }

    /**
     * Get videos by creator
     */
    @GetMapping("/creator/{creatorId}")
    public ResponseEntity<List<Video>> getVideosByCreator(@PathVariable Long creatorId) {
        return ResponseEntity.ok(videoService.getVideosByCreator(creatorId));
    }

    /**
     * Get all published videos
     */
    @GetMapping("/published")
    public ResponseEntity<List<Video>> getPublishedVideos() {
        return ResponseEntity.ok(videoService.getPublishedVideos());
    }

    /**
     * Get video by ID
     */
    @GetMapping("/{id:\\d+}")
    public ResponseEntity<Video> getVideo(@PathVariable Long id) {
        return ResponseEntity.ok(videoService.getVideoById(id));
    }

    /**
     * Delete video
     */
    @DeleteMapping("/{videoId}")
    public ResponseEntity<Void> deleteVideo(
            @PathVariable Long videoId,
            @RequestHeader("X-User-Id") Long creatorId
    ) {
        videoService.deleteVideo(videoId, creatorId);
        return ResponseEntity.noContent().build();
    }


    @PutMapping("/{videoId}/stream-url")
    public ResponseEntity<Void> saveStreamUrl(
            @PathVariable Long videoId,
            @RequestBody Map<String, String> body,
            @RequestHeader("X-User-Id") Long creatorId
    ) {
        videoService.updateStreamUrl(videoId, creatorId, body.get("streamUrl"));
        return ResponseEntity.ok().build();
    }

    // helper method to convert Video entity to VideoResponse
    private VideoResponse mapToResponse(Video video) {
        return VideoResponse.builder()
                .id(video.getId())
                .title(video.getTitle())
                .description(video.getDescription())
                .videoUrl(video.getVideoUrl())
                .thumbnailUrl(video.getThumbnailUrl())
                .durationSeconds(video.getDurationSeconds())
                .isPublished(video.getIsPublished())
                .creatorId(video.getCreatorId())
                .teamId(video.getTeamId())
                .createdAt(video.getCreatedAt())
                .s3Key(video.getS3Key())
                .build();
    }



    // helper method to convert User to UserResponse
//    private UserResponse mapUserToResponse(com.annotate.video.entity.User user) {
//        return UserResponse.builder()
//                .id(user.getId())
//                .name(user.getName())
//                .email(user.getEmail())
//                .role(user.getRole())
//                .build();
//    }

}

