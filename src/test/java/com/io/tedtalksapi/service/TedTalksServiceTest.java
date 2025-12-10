package com.io.tedtalksapi.service;

import com.io.tedtalksapi.entity.TedTalksRecordT;
import com.io.tedtalksapi.repository.TedTalksRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class TedTalksServiceTest {

    @Mock
    private TedTalksRecordRepository tedTalksRecordRepository;

    @InjectMocks
    private TedTalksService tedTalksService;

    private TedTalksRecordT record;
    private UUID uuid;

    @BeforeEach
    void setUp() {
        uuid = UUID.randomUUID();
        record = new TedTalksRecordT();
        record.setId(uuid);
        record.setTitle("Test Talk");
        record.setViewCount(BigInteger.valueOf(100));
        record.setLikeCount(BigInteger.valueOf(10));
        // Influence = 100 + (2 * 10) = 120
        record.setInfluenceScore(BigInteger.valueOf(120));
    }

    // Update test removed as logic moved to UpdateTedTalkHandler

    // Tests for write operations have been moved to Command Handlers logic
    // or are covered by Controller Integration tests verifying the CommandBus
    // dispatch.
    // The Service layer is now effectively Read-Only / Query side.
}
