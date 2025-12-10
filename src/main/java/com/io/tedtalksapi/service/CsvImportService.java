package com.io.tedtalksapi.service;

import com.io.tedtalksapi.entity.TedTalksRecordT;
import com.io.tedtalksapi.repository.TedTalksRecordRepository;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@Slf4j
@RequiredArgsConstructor
public class CsvImportService {

    private final TedTalksRecordRepository repository;

    @PostConstruct
    public void importCsvData() {
        if (repository.count() > 0) {
            log.info("Database already populated. Skipping CSV import.");
            return;
        }

        log.info("Starting CSV import...");
        try (CSVReader reader = new CSVReaderBuilder(
                new InputStreamReader(new ClassPathResource("data/iO Data - Java assessment.csv").getInputStream()))
                .withSkipLines(1) // Skip header
                .build()) {

            List<String[]> rows = reader.readAll();
            List<TedTalksRecordT> records = new ArrayList<>();
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.US);

            for (String[] row : rows) {
                // columns: title, author, date, views, likes, link
                if (row.length < 6)
                    continue;

                try {
                    TedTalksRecordT record = new TedTalksRecordT();
                    record.setTitle(row[0].trim());

                    // Set default author if not provided
                    String author = row[1].trim();
                    if (author == null || author.isEmpty()) {
                        author = "Not Provided";
                    }
                    record.setAuthor(author);

                    // Date parsing: "December 2021" -> YearMonth -> LocalDate (first day)
                    YearMonth ym = YearMonth.parse(row[2].trim(), dateFormatter);
                    if (ym.getYear() < 1984) {
                        log.warn("Skipping row with invalid/pre-TED year ({}): {}", ym, String.join(",", row));
                        continue;
                    }
                    record.setReleasedDate(ym.atDay(1));

                    BigInteger views = new BigInteger(row[3].trim());
                    BigInteger likes = new BigInteger(row[4].trim());

                    // Handle negative counts by taking absolute value
                    if (views.compareTo(BigInteger.ZERO) < 0) {
                        views = views.abs();
                        log.warn("Converted negative views to absolute value for row: {}", String.join(",", row));
                    }
                    if (likes.compareTo(BigInteger.ZERO) < 0) {
                        likes = likes.abs();
                        log.warn("Converted negative likes to absolute value for row: {}", String.join(",", row));
                    }

                    // Set views and likes to 0 for future release dates
                    if (record.getReleasedDate().isAfter(LocalDate.now())) {
                        views = BigInteger.ZERO;
                        likes = BigInteger.ZERO;
                        log.info("Setting views/likes to 0 for future date: {}", record.getReleasedDate());
                    }

                    record.setViewCount(views);
                    record.setLikeCount(likes);
                    record.setUrl(row[5].trim());

                    // Calculate Influence Score: views + 2 * likes
                    BigInteger param = likes.multiply(BigInteger.TWO);
                    record.setInfluenceScore(views.add(param));

                    records.add(record);
                } catch (java.time.format.DateTimeParseException e) {
                    log.warn("Skipping row due to invalid date format: {}", String.join(",", row));
                } catch (NumberFormatException e) {
                    log.warn("Skipping row due to invalid number format: {}", String.join(",", row));
                } catch (Exception e) {
                    log.error("Failed to parse row: " + String.join(",", row), e);
                }
            }

            if (!records.isEmpty()) {
                repository.saveAll(records);
                log.info("Successfully imported {} records.", records.size());
            }

        } catch (IOException | CsvException e) {
            log.error("Failed to import CSV data", e);
        }
    }
}
