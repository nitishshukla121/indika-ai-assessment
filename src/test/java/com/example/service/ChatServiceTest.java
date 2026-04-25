//package com.example.service;
//
//import com.example.model.FileMetadata;
//import com.example.repository.FileMetadataRepository;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.ai.vectorstore.VectorStore;
//import org.springframework.mock.web.MockMultipartFile;
//
//import java.io.File;
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class IngestionServiceTest {
//
//    @Mock VectorStore vectorStore;
//    @Mock FileMetadataRepository metadataRepository;
//    @Mock TranscriptionService transcriptionService;
//
//    @Test
//    void processAudioFile_callsTranscriptionAndSavesMetadata() throws Exception {
//        TranscriptionService.TranscriptionResult result =
//            new TranscriptionService.TranscriptionResult("Hello world", List.of(
//                new TranscriptionService.TranscriptSegment(0.0, 2.0, "Hello world")
//            ));
//
//        when(transcriptionService.transcribe(any(File.class))).thenReturn(result);
//
//        FileMetadata savedMeta = new FileMetadata();
//        savedMeta.setId(1L);
//        savedMeta.setFileName("test.mp3");
//        when(metadataRepository.save(any(FileMetadata.class))).thenReturn(savedMeta);
//        doNothing().when(vectorStore).accept(any());
//
//        IngestionService service = new IngestionService(
//            vectorStore, metadataRepository, transcriptionService, new ObjectMapper()
//        );
//
//        MockMultipartFile file = new MockMultipartFile(
//            "file", "test.mp3", "audio/mpeg", "fake audio".getBytes()
//        );
//
//        FileMetadata metadata = service.processAndStoreFile(file);
//
//        assertThat(metadata.getFileName()).isEqualTo("test.mp3");
//        verify(transcriptionService, times(1)).transcribe(any(File.class));
//        verify(metadataRepository, times(1)).save(any(FileMetadata.class));
//        verify(vectorStore, times(1)).accept(any());
//    }
//
//    @Test
//    void transcriptSegment_dataIntegrity() {
//        var seg = new TranscriptionService.TranscriptSegment(0.0, 5.0, "Hello");
//        assertThat(seg.start()).isEqualTo(0.0);
//        assertThat(seg.end()).isEqualTo(5.0);
//        assertThat(seg.text()).isEqualTo("Hello");
//    }
//
//    @Test
//    void transcriptionResult_dataIntegrity() {
//        var result = new TranscriptionService.TranscriptionResult(
//            "Full text", List.of(new TranscriptionService.TranscriptSegment(0.0, 1.0, "Full text"))
//        );
//        assertThat(result.text()).isEqualTo("Full text");
//        assertThat(result.segments()).hasSize(1);
//    }
//}