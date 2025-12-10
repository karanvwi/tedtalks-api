package com.io.tedtalksapi.command;

import com.io.tedtalksapi.entity.TedTalksRecordT;
import com.io.tedtalksapi.repository.TedTalksRecordRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class GetMostInfluentialTedTalkPerYearCommand implements Command<List<TedTalksRecordT>> {

    private final TedTalksRecordRepository repository;

    @Override
    public List<TedTalksRecordT> execute() {
        return repository.findMostInfluentialPerYear();
    }
}
