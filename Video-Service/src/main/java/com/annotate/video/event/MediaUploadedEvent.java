package com.annotate.video.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MediaUploadedEvent {
    private Long videoId;
    private String s3Key;
}
