package com.annotate.access.dto;

import lombok.Data;

@Data
public class VideoResponse {

    private Long id;
    private String title;
    private Boolean isPublished;
    private Long creatorId;
    private Long teamId;
}
