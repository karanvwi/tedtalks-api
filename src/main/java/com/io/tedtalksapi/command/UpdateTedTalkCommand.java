package com.io.tedtalksapi.command;

import com.io.tedtalksapi.entity.TedTalksRecordT;
import com.io.tedtalksapi.repository.TedTalksRecordRepository;
import com.io.tedtalksapi.request.TedTalk;
import com.io.tedtalksapi.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class UpdateTedTalkCommand implements Command<TedTalksRecordT> {

    private final TedTalksRecordRepository repository;
    private final UUID id;
    private final TedTalk tedTalk;

    @Override
    public TedTalksRecordT execute() {
        log.info("Executing UpdateTedTalkCommand for ID: {}", id);
        TedTalksRecordT existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TedTalk", "id", id));

        existing.setTitle(tedTalk.getTitle());
        existing.setAuthor(tedTalk.getAuthor());
        existing.setReleasedDate(tedTalk.getReleasedDate());
        existing.setViewCount(BigInteger.valueOf(tedTalk.getViewCount()));
        existing.setLikeCount(BigInteger.valueOf(tedTalk.getLikeCount()));
        existing.setUrl(tedTalk.getUrl());

        BigInteger influence = BigInteger.valueOf(tedTalk.getViewCount())
                .add(BigInteger.valueOf(tedTalk.getLikeCount()).multiply(BigInteger.TWO));
        existing.setInfluenceScore(influence);

        TedTalksRecordT saved = repository.save(existing);
        log.info("Successfully updated TedTalk");
        return saved;
    }
}
