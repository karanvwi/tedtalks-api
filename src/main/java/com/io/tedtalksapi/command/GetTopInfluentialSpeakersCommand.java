package com.io.tedtalksapi.command;

import com.io.tedtalksapi.repository.TedTalksRecordRepository;
import com.io.tedtalksapi.util.DataFormatterUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class GetTopInfluentialSpeakersCommand implements Command<List<Map<String, Object>>> {

    private final TedTalksRecordRepository repository;
    private final int limit;
    private final int minTalks;

    @Override
    public List<Map<String, Object>> execute() {
        List<Map<String, Object>> results = repository.findTopInfluentialSpeakers(minTalks, PageRequest.of(0, limit));

        // Format and enrich results
        List<Map<String, Object>> enrichedResults = new ArrayList<>();

        for (Map<String, Object> r : results) {
            Map<String, Object> map = new HashMap<>(r);

            Number scoreNum = (Number) r.get("influenceScore");
            BigInteger score = (scoreNum instanceof BigInteger) ? (BigInteger) scoreNum
                    : BigInteger.valueOf(scoreNum.longValue());

            // Add derived fields using utility
            map.put("rating", DataFormatterUtil.mapScoreToStars(score));

            // Format big numbers
            Number viewNum = (Number) r.get("totalViews");
            BigInteger views = (viewNum instanceof BigInteger) ? (BigInteger) viewNum
                    : BigInteger.valueOf(viewNum.longValue());
            map.put("viewCount", DataFormatterUtil.formatBigInteger(views));

            Number likeNum = (Number) r.get("totalLikes");
            BigInteger likes = (likeNum instanceof BigInteger) ? (BigInteger) likeNum
                    : BigInteger.valueOf(likeNum.longValue());
            map.put("likeCount", DataFormatterUtil.formatBigInteger(likes));

            enrichedResults.add(map);
        }

        return enrichedResults;
    }
}
