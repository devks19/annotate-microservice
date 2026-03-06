package com.annotate.auth.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MediaProcessedEvent {
    private Long videoId;
    private String streamUrl;
}
