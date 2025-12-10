package com.io.tedtalksapi.repository;

import com.io.tedtalksapi.entity.TedTalksRecordT;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public interface TedTalksRecordRepository extends JpaRepository<TedTalksRecordT, UUID> {
        @Query("SELECT t.author as author, " +
                        "SUM(t.viewCount) as totalViews, " +
                        "SUM(t.likeCount) as totalLikes, " +
                        "COUNT(t) as talkCount, " +
                        "(SUM(t.viewCount) + 2 * SUM(t.likeCount)) as influenceScore " +
                        "FROM TedTalksRecordT t " +
                        "GROUP BY t.author " +
                        "HAVING COUNT(t) >= :minTalks " +
                        "ORDER BY influenceScore DESC")
        List<Map<String, Object>> findTopInfluentialSpeakers(@Param("minTalks") int minTalks, Pageable pageable);

        // Search by title or author (case-insensitive)
        // Search by title or author (case-insensitive)
        List<TedTalksRecordT> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(
                        String title, String author);

        @Query(value = "SELECT DISTINCT ON (EXTRACT(YEAR FROM t.released_date)) t.* " +
                        "FROM ted_talks_record_t t " +
                        "ORDER BY EXTRACT(YEAR FROM t.released_date) DESC, t.influence_score DESC", nativeQuery = true)
        List<TedTalksRecordT> findMostInfluentialPerYear();
}
