package com.io.tedtalksapi.command;

import com.io.tedtalksapi.entity.TedTalksRecordT;
import com.io.tedtalksapi.repository.TedTalksRecordRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class SearchTedTalksCommand implements Command<List<TedTalksRecordT>> {

    private final TedTalksRecordRepository repository;
    private final String query;

    @Override
    public List<TedTalksRecordT> execute() {
        return repository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(query, query);
    }
}
