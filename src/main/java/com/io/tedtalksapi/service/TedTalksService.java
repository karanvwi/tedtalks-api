package com.io.tedtalksapi.service;

import com.io.tedtalksapi.entity.TedTalksRecordT;
import com.io.tedtalksapi.repository.TedTalksRecordRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class TedTalksService {

    private final TedTalksRecordRepository tedTalksRecordRepository;

    // SEARCH
    public List<TedTalksRecordT> search(String query) {
        return tedTalksRecordRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(query, query);
    }
}
