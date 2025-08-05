package org.xaut.voicemindserver.Service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.xaut.voicemindserver.Mapper.AudioMapper;

import java.time.LocalDateTime;

@SpringBootTest
public class DatabaseConnectionTest {

    @Autowired
    private AudioMapper audioMapper;

    @Test
    public void testDatabaseInsert() {
        audioMapper.saveAudioUrl("test_user", "test_q", "http://example.com/audio.mp3", LocalDateTime.now());
    }
}