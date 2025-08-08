package org.xaut.voicemindserver.Mapper;

import org.apache.ibatis.annotations.Mapper;
import org.xaut.voicemindserver.entity.AudioFeature;

@Mapper
public interface AudioFeatureMapper {

    void insert(AudioFeature audioFeature);

    AudioFeature getFeatureByAudioId(Long audioId);
}
