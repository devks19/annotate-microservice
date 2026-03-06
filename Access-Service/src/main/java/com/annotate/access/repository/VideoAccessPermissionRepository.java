package com.annotate.access.repository;

import com.annotate.access.entity.VideoAccessPermission;
import com.annotate.access.enums.AccessStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public interface VideoAccessPermissionRepository extends JpaRepository<VideoAccessPermission, Long> {



    List<VideoAccessPermission> findByStatus(AccessStatus status);

    List<VideoAccessPermission> findByViewerId(Long viewerId);

    List<VideoAccessPermission> findByVideoId(Long videoId);

    Optional<VideoAccessPermission> findByVideoIdAndViewerId(Long videoId, Long viewerId);

    Optional<VideoAccessPermission> findByVideoIdAndViewerIdAndStatus(
            Long videoId, Long viewerId, AccessStatus status);

    Optional<VideoAccessPermission> findByResponseMessage(String responseMessage);

    List<VideoAccessPermission> findByViewerIdAndStatus(Long viewerId, AccessStatus status);

//    Optional<VideoAccessPermission> findByVideoIdAndAccessCodeIsNotNull(Long videoId);

//    Optional<VideoAccessPermission> findByAccessCodeAndCodeActiveTrue(String accessCode);

}
