package com.annotate.access.service;

import com.annotate.access.client.AuthClient;
import com.annotate.access.client.VideoClient;
import com.annotate.access.dto.AccessRequest;
import com.annotate.access.dto.UserResponse;
import com.annotate.access.dto.VideoResponse;
import com.annotate.access.dto.AccessResponse;
import com.annotate.access.entity.VideoAccessPermission;
import com.annotate.access.enums.AccessStatus;
import com.annotate.access.repository.VideoAccessPermissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoAccessService {

    private final VideoAccessPermissionRepository accessRepository;
    private final VideoClient videoClient;
    private final AuthClient authClient;

    /**
     * Check if user has access to view video
     */
//    public boolean hasAccess(Long videoId, Long userId) {
//        Video video = videoRepository.findById(videoId)
//                .orElseThrow(() -> new RuntimeException("Video not found"));
//
//        // Creator always has access to their own videos
//        if (video.getCreator().getId().equals(userId)) {
//            return true;
//        }
//
//        // Check if video is published AND user has approved access
//        return video.getIsPublished() &&
//                accessRepository.existsByVideoIdAndViewerIdAndStatus(
//                        videoId, userId, AccessStatus.APPROVED
//                );
//    }

    public boolean hasAccess(Long videoId, Long userId) {

        var video = videoClient.getVideo(videoId);

        // creator always has access
        if (video.getCreatorId().equals(userId)) {
            return true;
        }

        if (!video.getIsPublished()) {
            return false;
        }

        var optionalPermission =
                accessRepository.findByVideoIdAndViewerIdAndStatus(
                        videoId, userId, AccessStatus.APPROVED
                );

        if (optionalPermission.isEmpty()) return false;

        var permission = optionalPermission.get();

        if (permission.isRevoked()) return false;

        if (permission.getSuspendedUntil() != null &&
                permission.getSuspendedUntil().isAfter(LocalDateTime.now())) {
            return false;
        }

        return true;
    }




    /**
     * Request access to a video
     */
    @Transactional
    public AccessResponse requestAccess(Long userId, AccessRequest request) {

        var video = videoClient.getVideo(request.getVideoId());

        if (video.getCreatorId().equals(userId)) {
            throw new RuntimeException("Creator already has access");
        }

        var existing = accessRepository.findByVideoIdAndViewerId(
                request.getVideoId(), userId
        );

        if (existing.isPresent()) {
            throw new RuntimeException("Access request already exists");
        }

        var permission = VideoAccessPermission.builder()
                .videoId(request.getVideoId())
                .viewerId(userId)
                .creatorId(video.getCreatorId())
                .status(AccessStatus.PENDING)
                .requestReason(request.getRequestReason())
                .build();

        permission = accessRepository.save(permission);

        return mapToDTO(permission, video, null);
    }

    /**
     * Approve access request
     */
    @Transactional
    public AccessResponse approveAccess(Long requestId, Long creatorId, String message) {

        var permission = accessRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Access request not found"));

        var video = videoClient.getVideo(permission.getVideoId());

        if (!video.getCreatorId().equals(creatorId)) {
            throw new RuntimeException("Only creator can approve");
        }

        permission.setStatus(AccessStatus.APPROVED);
        permission.setResponseMessage(message);
        permission.setRespondedAt(LocalDateTime.now());

        permission = accessRepository.save(permission);

        var viewer = authClient.getUser(permission.getViewerId());

        return mapToDTO(permission, video, viewer);
    }

    /**
     * Deny access request
     */
    @Transactional
    public AccessResponse denyAccess(Long requestId, Long creatorId, String message) {

        var permission = accessRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Access request not found"));

        var video = videoClient.getVideo(permission.getVideoId());

        if (!video.getCreatorId().equals(creatorId)) {
            throw new RuntimeException("Only creator can deny");
        }

        permission.setStatus(AccessStatus.DENIED);
        permission.setResponseMessage(message);
        permission.setRespondedAt(LocalDateTime.now());

        permission = accessRepository.save(permission);

        var viewer = authClient.getUser(permission.getViewerId());

        return mapToDTO(permission, video, viewer);
    }

    /**
     * Get pending requests for creator
     */
    public List<AccessResponse> getPendingRequests(Long creatorId) {

        return accessRepository.findByStatus(AccessStatus.PENDING)
                .stream()
                .map(permission -> {
                    var video = videoClient.getVideo(permission.getVideoId());

                    if (!video.getCreatorId().equals(creatorId)) return null;

                    var viewer = authClient.getUser(permission.getViewerId());

                    return mapToDTO(permission, video, viewer);
                })
                .filter(x -> x != null)
                .collect(Collectors.toList());
    }

    /**
     * Get user's access requests
     */
    public List<AccessResponse> getMyRequests(Long userId) {

        return accessRepository.findByViewerId(userId)
                .stream()
                .map(permission -> {
                    var video = videoClient.getVideo(permission.getVideoId());
                    return mapToDTO(permission, video, null);
                })
                .collect(Collectors.toList());
    }

    private AccessResponse mapToDTO(VideoAccessPermission permission,
                                    VideoResponse video,
                                    UserResponse viewer) {

        return AccessResponse.builder()
                .id(permission.getId())
                .videoId(permission.getVideoId())
                .videoTitle(video != null ? video.getTitle() : null)
                .viewerId(permission.getViewerId())
                .viewerName(viewer != null ? viewer.getName() : null)
                .status(permission.getStatus())
                .requestReason(permission.getRequestReason())
                .responseMessage(permission.getResponseMessage())
                .requestedAt(permission.getRequestedAt())
                .respondedAt(permission.getRespondedAt())
                .revoked(permission.isRevoked())
                .suspendedUntil(permission.getSuspendedUntil())
                .build();
    }

    /**
     * Revoke access from a viewer
     */
    @Transactional
    public void revokeAccess(Long requestId, Long creatorId) {

        var permission = accessRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Access permission not found"));

        var video = videoClient.getVideo(permission.getVideoId());

        if (!video.getCreatorId().equals(creatorId)) {
            throw new RuntimeException("Only creator can revoke");
        }

        accessRepository.delete(permission);
    }

    /**
     * Get all approved access for creator's videos
     */
    public List<AccessResponse> getApprovedAccess(Long creatorId) {

        return accessRepository.findByStatus(AccessStatus.APPROVED)
                .stream()
                .map(permission -> {
                    var video = videoClient.getVideo(permission.getVideoId());

                    if (!video.getCreatorId().equals(creatorId)) return null;

                    var viewer = authClient.getUser(permission.getViewerId());

                    return mapToDTO(permission, video, viewer);
                })
                .filter(x -> x != null)
                .collect(Collectors.toList());
    }

    /**
     * TEMPORARY access removal (suspend viewer until a specific time)
     */
    @Transactional
    public AccessResponse suspendAccess(Long permissionId, Long creatorId, LocalDateTime suspendedUntil) {

        var permission = accessRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Access permission not found"));

        var video = videoClient.getVideo(permission.getVideoId());

        if (!video.getCreatorId().equals(creatorId)) {
            throw new RuntimeException("Only creator can suspend");
        }

        permission.setSuspendedUntil(suspendedUntil);
        permission.setRevoked(false);

        permission = accessRepository.save(permission);

        var viewer = authClient.getUser(permission.getViewerId());

        return mapToDTO(permission, video, viewer);
    }

    /**
     * PERMANENT access removal
     */
    @Transactional
    public AccessResponse revokeAccessPermanently(Long permissionId, Long creatorId, String message) {

        VideoAccessPermission permission = accessRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Access permission not found"));

        var video = videoClient.getVideo(permission.getVideoId());
        var viewer = authClient.getUser(permission.getViewerId());

        if (!video.getCreatorId().equals(creatorId)) {
            throw new RuntimeException("Only video creator can revoke access");
        }

        permission.setRevoked(true);
        permission.setSuspendedUntil(null);
        permission.setStatus(AccessStatus.APPROVED); // keep history (was approved)
        permission.setResponseMessage(message);
        permission.setRespondedAt(LocalDateTime.now());

        permission = accessRepository.save(permission);
        log.info("Permanently revoked access for viewer {} from video {}",
                permission.getViewerId(), permission.getVideoId());

        return mapToDTO(permission, video, viewer);
    }

    /**
     * Restore access (clear temporary suspension / permanent revoke)
     */
    @Transactional
    public AccessResponse restoreAccess(Long permissionId, Long creatorId) {
        VideoAccessPermission permission = accessRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Access permission not found"));

        var video = videoClient.getVideo(permission.getVideoId());
        var viewer = authClient.getUser(permission.getViewerId());

        if (!video.getCreatorId().equals(creatorId)) {
            throw new RuntimeException("Only video creator can restore access");
        }

        permission.setRevoked(false);
        permission.setSuspendedUntil(null);

        permission = accessRepository.save(permission);
        return mapToDTO(permission , video, viewer);
    }

    /**
     * ACCESS VIEWER: get all viewers for one video
     */
    public List<AccessResponse> getAccessForVideo(Long creatorId, Long videoId) {

        var video = videoClient.getVideo(videoId);

        if (!video.getCreatorId().equals(creatorId)) {
            throw new RuntimeException("Only creator can view access list");
        }

        return accessRepository.findByVideoId(videoId)
                .stream()
                .map(permission -> {
                    var viewer = authClient.getUser(permission.getViewerId());
                    return mapToDTO(permission, video, viewer);
                })
                .collect(Collectors.toList());
    }



}
