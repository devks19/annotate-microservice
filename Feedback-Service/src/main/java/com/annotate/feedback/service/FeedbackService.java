package com.annotate.feedback.service;

import com.annotate.feedback.client.AuthClient;
import com.annotate.feedback.client.VideoClient;
import com.annotate.feedback.dto.FeedbackRequest;
import com.annotate.feedback.entity.Feedback;
import com.annotate.feedback.enums.FeedbackStatus;
import com.annotate.feedback.enums.UserRole;
import com.annotate.feedback.repository.FeedbackRepository;
import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedbackService {
    private final FeedbackRepository feedbackRepository;
    private final VideoClient videoClient;
    private final AuthClient authClient;

    @Transactional
    public Feedback createFeedback(Long viewerId, FeedbackRequest request) {

        var video = videoClient.getVideo(request.getVideoId()); // existence check

        var feedback = Feedback.builder()
                .videoId(request.getVideoId())
                .viewerId(viewerId)
                .comment(request.getComment())
                .timestampSeconds(request.getTimestampSeconds())
                .status(FeedbackStatus.PENDING)
                .build();

        return feedbackRepository.save(feedback);
    }

    public List<Feedback> getFeedbacksByVideo(Long videoId) {
        return feedbackRepository.findByVideoIdOrderByTimestampSecondsAsc(videoId);
    }

    public Feedback updateFeedbackStatus(Long feedbackId, FeedbackStatus status) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));

        feedback.setStatus(status);
        if (status == FeedbackStatus.ACCEPTED) {
            feedback.setResolvedAt(java.time.LocalDateTime.now());
        }

        return feedbackRepository.save(feedback);
    }

    @Transactional
    public Feedback approveFeedback(Long feedbackId, Long userId) {

        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));

        var video = videoClient.getVideo(feedback.getVideoId());
        var user = authClient.getUser(userId);

        if (!video.getCreatorId().equals(userId)
                && user.getRole() != UserRole.ADMIN) {
            throw new RuntimeException("Only creator or admin can approve feedback");
        }

        feedback.setStatus(FeedbackStatus.ACCEPTED);
        feedback.setResolvedAt(LocalDateTime.now());

        return feedbackRepository.save(feedback);
    }

    @Transactional
    public Feedback rejectFeedback(Long feedbackId, Long userId) {

        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));

        var video = videoClient.getVideo(feedback.getVideoId());
        var user = authClient.getUser(userId); // only source of truth

        boolean isCreator = video.getCreatorId().equals(userId);
        boolean isAdmin = user.getRole() == UserRole.ADMIN;

        if (!isCreator && !isAdmin) {
            throw new RuntimeException("Only creator or admin can reject feedback");
        }

        feedback.setStatus(FeedbackStatus.REJECTED);
        return feedbackRepository.save(feedback);
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();

                String authHeader = request.getHeader("Authorization");
                if (authHeader != null) {
                    template.header("Authorization", authHeader);
                }

                String userId = request.getHeader("userId");
                if (userId != null) {
                    template.header("userId", userId);
                }
            }
        };
    }
}
