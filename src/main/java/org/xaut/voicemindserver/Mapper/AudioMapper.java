package org.xaut.voicemindserver.Mapper;

import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;

@Mapper
public interface AudioMapper {

    void saveAudioUrl(String userId, String questionId, String fileUrl , LocalDateTime uploadTime);
}
