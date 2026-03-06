package com.annotate.access.controller;

import com.annotate.access.client.AuthClient;
import com.annotate.access.dto.AccessRequest;
import com.annotate.access.dto.AccessResponse;
import com.annotate.access.dto.SuspendAccessRequest;
import com.annotate.access.dto.UserResponse;
import com.annotate.access.entity.VideoAccessPermission;
import com.annotate.access.enums.AccessStatus;
import com.annotate.access.repository.VideoAccessPermissionRepository;
import com.annotate.access.service.VideoAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/video-access")
@RequiredArgsConstructor
//@CrossOrigin(origins = "*")
public class VideoAccessController {

    private final VideoAccessService accessService;
    private final AuthClient authClient;
    private final VideoAccessPermissionRepository accessRepository;

    /**
     * Request access to a video
     */
    @PostMapping("/request")
    public ResponseEntity<AccessResponse> requestAccess(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody AccessRequest request
    ) {
        return ResponseEntity.ok(accessService.requestAccess(userId, request));
    }

    /**
     * Approve access request
     */
    @PutMapping("/{requestId}/approve")
    public ResponseEntity<AccessResponse> approveAccess(
            @PathVariable Long requestId,
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody(required = false) Map<String, String> body
    ) {
        String message = body != null ? body.get("message") : null;
        return ResponseEntity.ok(accessService.approveAccess(requestId, userId, message));
    }

    /**
     * Deny access request
     */
    @PutMapping("/{requestId}/deny")
    public ResponseEntity<AccessResponse> denyAccess(
            @PathVariable Long requestId,
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody(required = false) Map<String, String> body
    ) {
        String message = body != null ? body.get("message") : null;
        return ResponseEntity.ok(accessService.denyAccess(requestId, userId, message));
    }

    /**
     * Get pending requests for creator
     */
    @GetMapping("/pending")
    public ResponseEntity<List<AccessResponse>> getPendingRequests(
            @RequestHeader("X-User-Id") Long userId
    ) {
        return ResponseEntity.ok(accessService.getPendingRequests(userId));
    }

    /**
     * Get my access requests
     */
    @GetMapping("/my-requests")
    public ResponseEntity<List<AccessResponse>> getMyRequests(
            @RequestHeader("X-User-Id") Long userId
    ) {
        return ResponseEntity.ok(accessService.getMyRequests(userId));
    }

    /**
     * Check if user has access to video
     */
    @GetMapping("/check/{videoId}")
    public ResponseEntity<Map<String, Boolean>> checkAccess(
            @PathVariable Long videoId,
            @RequestHeader("X-User-Id") Long userId
    ) {
        boolean hasAccess = accessService.hasAccess(videoId, userId);
        return ResponseEntity.ok(Map.of("hasAccess", hasAccess));
    }

    /**
     * Revoke access from a viewer
     */
    @DeleteMapping("/{requestId}/revoke")
    public ResponseEntity<Void> revokeAccess(
            @PathVariable Long requestId,
            @RequestHeader("X-User-Id") Long userId
    ) {
        accessService.revokeAccess(requestId, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get all approved access for creator's videos
     */
    @GetMapping("/approved")
    public ResponseEntity<List<AccessResponse>> getApprovedAccess(
            @RequestHeader("X-User-Id") Long userId
    ) {
        return ResponseEntity.ok(accessService.getApprovedAccess(userId));
    }

    /**
     * TEMPORARY access removal
     */
    @PutMapping("/{permissionId}/suspend")
    public ResponseEntity<AccessResponse> suspendAccess(
            @PathVariable Long permissionId,
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody SuspendAccessRequest request
    ) {
        return ResponseEntity.ok(
                accessService.suspendAccess(permissionId, userId, request.getSuspendedUntil())
        );
    }

    /**
     * PERMANENT access removal
     */
    @PutMapping("/{permissionId}/revoke-permanent")
    public ResponseEntity<AccessResponse> revokeAccessPermanently(
            @PathVariable Long permissionId,
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody(required = false) Map<String, String> body
    ) {
        String message = body != null ? body.get("message") : null;
        return ResponseEntity.ok(
                accessService.revokeAccessPermanently(permissionId, userId, message)
        );
    }

    /**
     * Restore access (undo temp/permanent removal)
     */
    @PutMapping("/{permissionId}/restore")
    public ResponseEntity<AccessResponse> restoreAccess(
            @PathVariable Long permissionId,
            @RequestHeader("X-User-Id") Long userId
    ) {
        return ResponseEntity.ok(accessService.restoreAccess(permissionId, userId));
    }

    /**
     * ACCESS VIEWER – all viewers for a particular video
     */
    @GetMapping("/video/{videoId}")
    public ResponseEntity<List<AccessResponse>> getAccessForVideo(
            @PathVariable Long videoId,
            @RequestHeader("X-User-Id") Long userId
    ) {
        return ResponseEntity.ok(accessService.getAccessForVideo(userId, videoId));
    }

    @GetMapping("/accessible-creators")
    public ResponseEntity<List<UserResponse>> getAccessibleCreators(
            @RequestHeader("X-User-Id") Long userId
    ) {
        List<VideoAccessPermission> approvedAccess =
                accessRepository.findByViewerIdAndStatus(userId, AccessStatus.APPROVED);

        Set<Long> creatorIds = approvedAccess.stream()
                .map(permission -> permission.getCreatorId())
                .collect(Collectors.toSet());

        List<UserResponse> creators =
                authClient.getUsersByIds(creatorIds);

        return ResponseEntity.ok(creators);
    }

    @GetMapping("/accessible-video-ids")
    public ResponseEntity<List<Long>> getAccessibleVideoIds(
            @RequestHeader("X-User-Id") Long userId
    ) {
        List<Long> videoIds = accessRepository
                .findByViewerIdAndStatus(userId, AccessStatus.APPROVED)
                .stream()
                .map(VideoAccessPermission::getVideoId)
                .toList();

        return ResponseEntity.ok(videoIds);
    }

}
