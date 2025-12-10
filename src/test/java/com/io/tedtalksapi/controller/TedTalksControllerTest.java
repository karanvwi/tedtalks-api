package com.io.tedtalksapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.io.tedtalksapi.request.TedTalk;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.io.tedtalksapi.repository.TedTalksRecordRepository;
import com.io.tedtalksapi.mapper.TedTalkEntityMapper;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import java.util.Optional;

import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TedTalksControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TedTalksRecordRepository repository;

    @Mock
    private TedTalkEntityMapper mapper;

    @Mock
    private jakarta.validation.Validator validator;

    @InjectMocks
    private TedTalksController tedTalksController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(tedTalksController)
                .setControllerAdvice(new com.io.tedtalksapi.exception.TedTalkGlobalExceptionHandler())
                .build();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void saveTedTalkRecord_ShouldReturnCreated() throws Exception {
        TedTalk dto = new TedTalk();
        dto.setTitle("Brand New Talk");
        dto.setAuthor("New Author");
        dto.setReleasedDate(LocalDate.now());
        dto.setViewCount(500L);
        dto.setLikeCount(50L);
        dto.setUrl("https://ted.com/talks/new");

        // Mock mapper call
        when(mapper.toEntity(any(TedTalk.class))).thenReturn(new com.io.tedtalksapi.entity.TedTalksRecordT());
        // Mock validator to return no violations
        when(validator.validate(any())).thenReturn(java.util.Collections.emptySet());

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/tedtalks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        verify(repository).save(any());
    }

    @Test
    void updateTedTalk_ShouldReturnOk() throws Exception {
        UUID id = UUID.randomUUID();
        TedTalk dto = new TedTalk();
        dto.setTitle("New Title Long Enough");
        dto.setAuthor("New Author Name");
        dto.setReleasedDate(LocalDate.now().minusDays(1));
        dto.setViewCount(100L);
        dto.setLikeCount(10L);
        dto.setUrl("https://ted.com/talks/test");

        // Mock findById
        when(repository.findById(id)).thenReturn(Optional.of(new com.io.tedtalksapi.entity.TedTalksRecordT()));

        mockMvc.perform(put("/tedtalks/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(repository).save(any());
    }

    @Test
    void deleteTedTalk_ShouldReturnOk() throws Exception {
        UUID id = UUID.randomUUID();

        when(repository.existsById(id)).thenReturn(true);

        mockMvc.perform(delete("/tedtalks/{id}", id))
                .andExpect(status().isOk());

        verify(repository).deleteById(id);
    }

    @Test
    void search_ShouldReturnResults() throws Exception {
        String query = "test";
        // Mock repository for command execution
        when(repository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(query, query))
                .thenReturn(java.util.Collections.emptyList());

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/tedtalks/search")
                .param("value", query))
                .andExpect(status().isOk()); // Returns 200 with empty list message
    }

    @Test
    void getTopInfluentialSpeakers_ShouldReturnOk() throws Exception {
        // Mock repository for command execution
        // Since command is instantiated with new, we must mock repository response
        when(repository.findTopInfluentialSpeakers(any(Integer.class),
                any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(java.util.Collections.emptyList());

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                .get("/tedtalks/speaker/influential"))
                .andExpect(status().isOk());
    }

    @Test
    void getMostInfluentialPerYear_ShouldReturnOk() throws Exception {
        // Mock repository for command execution
        when(repository.findMostInfluentialPerYear()).thenReturn(java.util.Collections.emptyList());

        mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/tedtalks/influence/year"))
                .andExpect(status().isOk());
    }
}
