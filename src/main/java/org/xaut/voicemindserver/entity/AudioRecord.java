package org.xaut.voicemindserver.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AudioRecord {

    private Long id;

    private String userId;

    private String questionId;

    private String fileUrl;

    private LocalDateTime uploadTime;
}
