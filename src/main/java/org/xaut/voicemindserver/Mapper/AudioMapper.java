package org.xaut.voicemindserver.Mapper;

import org.apache.ibatis.annotations.Mapper;
import org.xaut.voicemindserver.entity.AudioRecord;

import java.time.LocalDateTime;

@Mapper
public interface AudioMapper {

    void saveAudioUrl(String userId, String questionId, String fileUrl , LocalDateTime uploadTime);

    AudioRecord findAudioIdByUrl(String audioUrl);

    AudioRecord getAudioByUserIdAndQusetionId(String userId, String questionId);
}
