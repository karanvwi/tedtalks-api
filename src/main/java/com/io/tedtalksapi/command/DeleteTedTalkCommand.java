package com.io.tedtalksapi.command;

import com.io.tedtalksapi.repository.TedTalksRecordRepository;
import com.io.tedtalksapi.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class DeleteTedTalkCommand implements Command<Void> {

    private final TedTalksRecordRepository repository;
    private final UUID id;

    @Override
    public Void execute() {
        log.info("Executing DeleteTedTalkCommand for ID: {}", id);
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("TedTalk", "id", id);
        }
        repository.deleteById(id);
        log.info("Successfully deleted TedTalk");
        return null;
    }
}
