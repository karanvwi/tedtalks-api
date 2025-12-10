package com.io.tedtalksapi.command;

import com.io.tedtalksapi.entity.TedTalksRecordT;
import com.io.tedtalksapi.repository.TedTalksRecordRepository;
import com.io.tedtalksapi.util.DataFormatterUtil;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class GetMostInfluentialTedTalkPerYearCommand implements Command<List<Map<String, Object>>> {

    private final TedTalksRecordRepository repository;

    @Override
    public List<Map<String, Object>> execute() {
        List<TedTalksRecordT> entities = repository.findMostInfluentialPerYear();

        // Transform entities to maps with formatted influenceScore
        List<Map<String, Object>> results = new ArrayList<>();

        for (TedTalksRecordT entity : entities) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", entity.getId());
            map.put("title", entity.getTitle());
            map.put("author", entity.getAuthor());
            map.put("releasedDate", entity.getReleasedDate());
            map.put("viewCount", DataFormatterUtil.formatBigInteger(entity.getViewCount()));
            map.put("likeCount", DataFormatterUtil.formatBigInteger(entity.getLikeCount()));
            map.put("url", entity.getUrl());
            map.put("influenceScore", DataFormatterUtil.formatBigInteger(entity.getInfluenceScore()));

            results.add(map);
        }

        return results;
    }
}
