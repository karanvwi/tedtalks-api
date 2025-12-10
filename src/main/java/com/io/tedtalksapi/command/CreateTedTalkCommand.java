package com.io.tedtalksapi.command;

import com.io.tedtalksapi.entity.TedTalksRecordT;
import com.io.tedtalksapi.repository.TedTalksRecordRepository;
import com.io.tedtalksapi.mapper.TedTalkEntityMapper;
import com.io.tedtalksapi.request.TedTalk;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Slf4j
@RequiredArgsConstructor
public class CreateTedTalkCommand implements Command<TedTalksRecordT> {

    private final TedTalksRecordRepository repository;
    private final TedTalkEntityMapper mapper;
    private final TedTalk tedTalk;
    private final Validator validator;

    @Override
    public TedTalksRecordT execute() {
        log.info("Executing CreateTedTalkCommand for: {}", tedTalk.getTitle());
        TedTalksRecordT record = mapper.toEntity(tedTalk);

        // Calculate influence logic (Inline)
        if (record.getViewCount() != null && record.getLikeCount() != null) {
            java.math.BigInteger influence = record.getViewCount()
                    .add(record.getLikeCount().multiply(java.math.BigInteger.TWO));
            record.setInfluenceScore(influence);
        }

        // Manually validate the entity before saving
        Set<ConstraintViolation<TedTalksRecordT>> violations = validator.validate(record);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        TedTalksRecordT saved = repository.save(record);
        log.info("Successfully created TedTalk");
        return saved;
    }
}
