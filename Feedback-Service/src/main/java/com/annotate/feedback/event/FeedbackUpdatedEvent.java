package com.annotate.feedback.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackUpdatedEvent {
    Long feedbackId;
    String status;
}
