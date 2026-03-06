package com.annotate.feedback.repository;

import com.annotate.feedback.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByVideoId(Long videoId);
    List<Feedback> findByViewerId(Long viewerId);
    List<Feedback> findByVideoIdOrderByTimestampSecondsAsc(Long videoId);
}