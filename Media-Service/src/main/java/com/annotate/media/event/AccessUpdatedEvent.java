package com.annotate.media.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccessUpdatedEvent {
    private Long videoId;
    private Long viewerId;
    private String status;
}
