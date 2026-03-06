package com.annotate.media.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackCreatedEvent {
    private Long feedbackId;
    private Long videoId;
    private Long viewerId;
}
