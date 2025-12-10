package com.io.tedtalksapi.service;

import com.io.tedtalksapi.repository.TedTalksRecordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CsvImportServiceTest {

    @Mock
    private TedTalksRecordRepository repository;

    @InjectMocks
    private CsvImportService csvImportService;

    @Test
    void importCsvData_ShouldSkipIfDatabasePopulated() {
        when(repository.count()).thenReturn(10L);
        csvImportService.importCsvData();
        verify(repository, never()).saveAll(any());
    }
}
