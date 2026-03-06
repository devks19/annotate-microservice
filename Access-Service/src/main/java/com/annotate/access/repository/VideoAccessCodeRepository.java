package com.annotate.access.repository;

import com.annotate.access.entity.VideoAccessCode;
import org.springframework.data.jpa.repository.JpaRepository;

//import java.lang.ScopedValue;
import java.util.Optional;

public interface VideoAccessCodeRepository
        extends JpaRepository<VideoAccessCode, Long> {

    Optional<VideoAccessCode> findByVideoId(Long videoId);

    Optional<VideoAccessCode> findByAccessCodeAndCodeActiveTrue(String accessCode);

    boolean existsByAccessCode(String accessCode);

    Optional<VideoAccessCode> findByVideoIdAndAccessCodeIsNotNull(Long videoId);
}
