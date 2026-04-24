package com.example.service;

import com.example.model.FileMetadata;
import com.example.repository.FileMetadataRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IngestionServiceTest {

    @Mock VectorStore vectorStore;
    @Mock FileMetadataRepository metadataRepository;
    @Mock TranscriptionService transcriptionService;

    @Test
    void processAudioFile_callsTranscriptionAndSavesMetadata() throws Exception {
        FileMetadataRepository repo = mock(FileMetadataRepository.class);
        TranscriptionService ts = mock(TranscriptionService.class);
        VectorStore vs = mock(VectorStore.class);

        TranscriptionService.TranscriptionResult result =
            new TranscriptionService.TranscriptionResult("Hello world", List.of(
                new TranscriptionService.TranscriptSegment(0.0, 2.0, "Hello world")
            ));

        when(ts.transcribe(any(File.class))).thenReturn(result);

        FileMetadata savedMeta = new FileMetadata();
        savedMeta.setId(1L);
        savedMeta.setFileName("test.mp3");
        when(repo.save(any(FileMetadata.class))).thenReturn(savedMeta);

        doNothing().when(vs).accept(any());

        IngestionService service = new IngestionService(vs, repo, ts, new ObjectMapper());

        MockMultipartFile file = new MockMultipartFile(
            "file", "test.mp3", "audio/mpeg", "fake audio".getBytes()
        );

        FileMetadata metadata = service.processAndStoreFile(file);

        assertThat(metadata.getFileName()).isEqualTo("test.mp3");
        verify(ts, times(1)).transcribe(any(File.class));
        verify(repo, times(1)).save(any(FileMetadata.class));
        verify(vs, times(1)).accept(any());
    }

    @Test
    void ingestText_callsVectorStore() {
        IngestionService service = new IngestionService(
            vectorStore, metadataRepository, transcriptionService, new ObjectMapper()
        );
        doNothing().when(vectorStore).accept(any());

        service.ingestText("Some long text content to ingest", "source-1");

        verify(vectorStore, times(1)).accept(any());
    }
}