package com.io.tedtalksapi.entity;

import com.io.tedtalksapi.annotations.ValidTedTalk;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@ValidTedTalk
@Table(name = "ted_talks_record_t")
public class TedTalksRecordT {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ttr_id", nullable = false, length = 36)
    private UUID id;

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title cannot exceed 200 characters")
    private String title;

    @NotBlank(message = "Author is required")
    @Size(max = 100, message = "Author name cannot exceed 100 characters")
    private String author = "Not Provided";

    @NotNull(message = "Released date is required")
    private LocalDate releasedDate;

    @NotNull(message = "View count is required")
    @Min(value = 0, message = "View count cannot be negative")
    @Column(name = "view_count")
    private BigInteger viewCount;

    @NotNull(message = "Like count is required")
    @Min(value = 0, message = "Like count cannot be negative")
    @Column(name = "like_count")
    private BigInteger likeCount;

    @NotBlank(message = "URL is required")
    @Size(max = 500, message = "URL cannot exceed 500 characters")
    @Pattern(regexp = "^(http|https)://.*$", message = "URL must start with http:// or https://")
    private String url;

    @Column(name = "influence_score", columnDefinition = "numeric")
    private BigInteger influenceScore;

}
