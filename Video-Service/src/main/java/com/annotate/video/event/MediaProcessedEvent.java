package com.annotate.video.event;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MediaProcessedEvent {
    private Long videoId;
    private String streamUrl;
}
