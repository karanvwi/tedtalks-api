package com.io.tedtalksapi.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TedTalk {
    @JsonProperty("author")
    @Size(min = 10, max = 100)
    @NotBlank
    private String author;

    @JsonProperty("title")
    @Size(min = 10, max = 200)
    @NotBlank
    private String title;

    @JsonProperty("releasedDate")
    @NotNull
    private LocalDate releasedDate;

    @JsonProperty("viewCount")
    @PositiveOrZero
    private Long viewCount;

    @JsonProperty("likeCount")
    @PositiveOrZero
    private Long likeCount;

    @JsonProperty("url")
    @Pattern(regexp = "https?://.*", message = "Invalid URL")
    private String url;
}
