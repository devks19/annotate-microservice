package com.annotate.video.service;

import com.annotate.video.client.AccessClient;
import com.annotate.video.dto.VideoResponse;
import com.annotate.video.dto.VideoUploadRequest;

import com.annotate.video.entity.Video;

import com.annotate.video.enums.VideoStatus;
import com.annotate.video.event.MediaProcessedEvent;
import com.annotate.video.repository.VideoRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoService {
    private final VideoRepository videoRepository;

    @Value("${file.upload.dir:uploads/videos}")
    private String uploadDir;

    @Value("${file.upload.base-url:http://localhost:8080}")
    private String baseUrl;

    private final AccessClient accessClient;

    /**
     * Initialize upload directory on application startup
     */
    @PostConstruct
    public void init() {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                log.info("Created upload directory: {}", uploadPath.toAbsolutePath());
            } else {
                log.info("Upload directory exists: {}", uploadPath.toAbsolutePath());
            }
        } catch (IOException e) {
            log.error("Failed to create upload directory", e);
            throw new RuntimeException("Could not create upload directory: " + e.getMessage());
        }
    }

    /**
     * Upload video file from local system
     */


    @Transactional
    public Video uploadVideoFile(Long creatorId,
                                 MultipartFile file,
                                 String title,
                                 String description,
                                 Long teamId) {

        try {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath();

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : ".mp4";

            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String videoUrl = baseUrl + "/uploads/videos/" + uniqueFilename;

            Video video = Video.builder()
                    .title(title)
                    .description(description != null ? description : "")
                    .videoUrl(videoUrl)
                    .thumbnailUrl(null)
                    .durationSeconds(0)
                    .creatorId(creatorId)
                    .teamId(teamId)
                    .isPublished(true)
                    .build();

            return videoRepository.save(video);

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload video file: " + e.getMessage());
        }
    }


    /**
     * Upload video via URL (existing method)
     */
    @Transactional
    public Video uploadVideo(Long creatorId, VideoUploadRequest request) {

        String key = "videos/" + UUID.randomUUID() + ".mp4";


        Video video = Video.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .videoUrl(request.getVideoUrl())
                .thumbnailUrl(request.getThumbnailUrl())
                .durationSeconds(request.getDurationSeconds())
                .creatorId(creatorId)
                .teamId(request.getTeamId())
                .isPublished(true)
                .build();

        video.setS3Key(key);

        return videoRepository.save(video);
    }

    /**
     * Publish video
     */
    @Transactional
    public Video publishVideo(Long videoId, Long creatorId) {

        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Video not found"));

        if (!video.getCreatorId().equals(creatorId)) {
            throw new RuntimeException("Only the creator can publish this video");
        }

        video.setIsPublished(true);
        return videoRepository.save(video);
    }

    /**
     * Unpublish video (make it draft)
     */
    @Transactional
    public Video unpublishVideo(Long videoId, Long creatorId) {

        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Video not found"));

        if (!video.getCreatorId().equals(creatorId)) {
            throw new RuntimeException("Only the creator can unpublish this video");
        }

        video.setIsPublished(false);
        return videoRepository.save(video);
    }

    /**
     * Get videos by creator (includes both published and unpublished)
     */
    public List<Video> getVideosByCreator(Long creatorId) {
        return videoRepository.findByCreatorId(creatorId);
    }

    /**
     * Get all published videos (visible to everyone)
     */
    public List<Video> getPublishedVideos() {
        return videoRepository.findByIsPublishedTrue();
    }

    /**
     * Get all videos (admin only)
     */
    public List<Video> getAllVideos() {
        return videoRepository.findAll();
    }

    /**
     * Get video by ID
     */
    public Video getVideoById(Long id) {
        return videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Video not found"));
    }

    /**
     * Delete video and associated file
     */
    @Transactional
    public void deleteVideo(Long videoId, Long creatorId) {

        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Video not found"));

        if (!video.getCreatorId().equals(creatorId)) {
            throw new RuntimeException("Only the creator can delete this video");
        }

        try {
            if (video.getVideoUrl().contains("/uploads/videos/")) {
                String filename = video.getVideoUrl().substring(video.getVideoUrl().lastIndexOf("/") + 1);
                Path filePath = Paths.get(uploadDir).resolve(filename);
                Files.deleteIfExists(filePath);
            }
        } catch (IOException e) {
            log.error("Failed to delete video file", e);
        }

        videoRepository.delete(video);
    }

    /**
     * Bulk publish all videos (admin utility)
     */

    @Transactional
    public void markVideoReady(Long videoId, String streamUrl) {

        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Video not found"));

        video.setStreamUrl(streamUrl);
        video.setStatus(VideoStatus.READY);

        videoRepository.save(video);
    }

    public void updateStreamUrl(Long videoId, Long creatorId, String streamUrl) {
        Video video = getVideoById(videoId);

        if (!video.getCreatorId().equals(creatorId)) {
            throw new RuntimeException("Unauthorized");
        }

        video.setVideoUrl(streamUrl);
        videoRepository.save(video);
    }

    public List<VideoResponse> getAccessibleVideos(Long userId) {

        List<Long> videoIds = accessClient.getAccessibleVideoIds(userId);

        if (videoIds.isEmpty()) {
            return List.of();
        }

        return videoRepository
                .findByIdInAndIsPublishedTrue(videoIds)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

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
                .build();
    }
}





