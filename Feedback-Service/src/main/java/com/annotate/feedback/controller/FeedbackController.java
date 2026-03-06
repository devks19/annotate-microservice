package com.annotate.feedback.controller;

import com.annotate.feedback.dto.FeedbackRequest;
import com.annotate.feedback.entity.Feedback;
import com.annotate.feedback.enums.FeedbackStatus;
import com.annotate.feedback.service.FeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
//@CrossOrigin(origins = "*")
public class FeedbackController {
    private final FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<Feedback> createFeedback(
            @RequestHeader("X-User-Id") Long viewerId,
            @Valid @RequestBody FeedbackRequest request
    ) {
        return ResponseEntity.ok(feedbackService.createFeedback(viewerId, request));

    }

    @GetMapping("/video/{videoId}")
    public ResponseEntity<List<Feedback>> getFeedbacksByVideo(@PathVariable Long videoId) {
        return ResponseEntity.ok(feedbackService.getFeedbacksByVideo(videoId));
    }

    @PutMapping("/{feedbackId}/status")
    public ResponseEntity<Feedback> updateFeedbackStatus(
            @PathVariable Long feedbackId,
            @RequestParam FeedbackStatus status
    ) {
        return ResponseEntity.ok(feedbackService.updateFeedbackStatus(feedbackId, status));
    }

    @PutMapping("/{feedbackId}/approve")
    public ResponseEntity<Feedback> approveFeedback(
            @PathVariable Long feedbackId,
            @RequestHeader("X-User-Id") Long userId
    ) {
        return ResponseEntity.ok(feedbackService.approveFeedback(feedbackId, userId));
    }

    @PutMapping("/{feedbackId}/reject")
    public ResponseEntity<Feedback> rejectFeedback(
            @PathVariable Long feedbackId,
            @RequestHeader("X-User-Id") Long userId
    ) {
        return ResponseEntity.ok(feedbackService.rejectFeedback(feedbackId, userId));
    }

}
