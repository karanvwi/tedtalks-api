package com.io.tedtalksapi.controller;

import com.io.tedtalksapi.response.ApiResponse;
import com.io.tedtalksapi.repository.TedTalksRecordRepository;
import com.io.tedtalksapi.mapper.TedTalkEntityMapper;
import java.util.UUID;

import com.io.tedtalksapi.request.TedTalk;
import com.io.tedtalksapi.entity.TedTalksRecordT;
import com.io.tedtalksapi.service.TedTalksService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/tedtalks")
@AllArgsConstructor
public class TedTalksController {

    private final TedTalksService tedTalksService; // Keeping for read operations
    private final TedTalksRecordRepository repository;
    private final TedTalkEntityMapper mapper;
    private final jakarta.validation.Validator validator;

    // endpoint to identify influential TedTalk speakers
    @GetMapping("/speaker/influential")
    public ResponseEntity<ApiResponse<List<java.util.Map<String, Object>>>> getTopInfluentialSpeakers(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "1") int minTalks) {
        log.info("Request to get top influential speakers with limit: {}, minTalks: {}", limit, minTalks);
        List<java.util.Map<String, Object>> result = new com.io.tedtalksapi.command.GetTopInfluentialSpeakersCommand(
                repository, limit, minTalks).execute();
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    // Basic data management operations for TedTalks data

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<TedTalksRecordT>>> search(@RequestParam("value") String value) {
        log.info("Request to search TedTalks with query: {}", value);
        List<TedTalksRecordT> results = tedTalksService.search(value);
        if (results.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.success(results, "No records found matching query: " + value));
        }
        return ResponseEntity.ok(ApiResponse.success(results));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TedTalksRecordT>> saveTedTalkRecord(@Valid @RequestBody TedTalk body) {
        log.info("Request to calculate/save new TedTalk record: {}", body.getTitle());
                
        TedTalksRecordT created = new com.io.tedtalksapi.command.CreateTedTalkCommand(repository, mapper, body, validator)
                .execute();
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED)
                .body(ApiResponse.success(created, "TedTalk created successfully"));

    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TedTalksRecordT>> updateTedTalk(@PathVariable UUID id,
            @Valid @RequestBody TedTalk body) {
        log.info("Request to update TedTalk with ID: {}", id);
        TedTalksRecordT updated = new com.io.tedtalksapi.command.UpdateTedTalkCommand(repository, id, body).execute();
        return ResponseEntity.ok(ApiResponse.success(updated, "TedTalk updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTedTalk(@PathVariable UUID id) {
        log.info("Request to delete TedTalk with ID: {}", id);
        new com.io.tedtalksapi.command.DeleteTedTalkCommand(repository, id).execute();
        return ResponseEntity.ok(ApiResponse.success(null, "TedTalk deleted successfully"));
    }

    // endpoint to get most influential TedTalk speaker per year
    @GetMapping("/influence/year")
    public ResponseEntity<ApiResponse<List<TedTalksRecordT>>> getMostInfluentialPerYear() {
        log.info("Request to get most influential TedTalk per year");
        List<TedTalksRecordT> results = new com.io.tedtalksapi.command.GetMostInfluentialTedTalkPerYearCommand(
                repository).execute();
        return ResponseEntity.ok(ApiResponse.success(results));
    }

}
