package com.annotate.video.repository;

import com.annotate.video.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
    List<Video> findByCreatorId(Long creatorId);
//    List<Video> findByTeamId(Long teamId);
    List<Video> findByIsPublishedTrue();
    List<Video> findByIsPublishedFalse();

    List<Video> findByIdInAndIsPublishedTrue(Collection<Long> ids);

//    int countByCreatorId(Long creatorId);

//    Optional<Video> findByAccessCode(String accessCode);
}