package org.xaut.voicemindserver.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AudioFeature {
    private Long id;
    private Long audioId;
    private String audioText;
    private String featureData;
    private Double probability;
    private String featureVersion="0.0.1";
    private LocalDateTime createdAt;
    private LocalDateTime lastUsedAt;
}