package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.UploadedFile;
import com.mycompany.myapp.repository.UploadedFileRepository;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class StoreFileService {

    private UploadedFileRepository uploadedFileRepository;

    private VectorStore vectorStore;

    private JdbcTemplate jdbcTemplate;

    public StoreFileService(UploadedFileRepository uploadedFileRepository, VectorStore vectorStore, JdbcTemplate jdbcTemplate) {
        this.uploadedFileRepository = uploadedFileRepository;
        this.vectorStore = vectorStore;
        this.jdbcTemplate = jdbcTemplate;
    }

    public Mono<UploadedFile> storeFile(UploadedFile uploadedFile) {
        var fileId = java.util.UUID.randomUUID();
        uploadedFile.setFileId(fileId);
        return uploadedFileRepository
            .save(uploadedFile)
            .flatMap(savedFile -> {
                var pdfResource = new ByteArrayResource(savedFile.getData()) {
                    @Override
                    public File getFile() throws IOException {
                        return new File(uploadedFile.getFilename());
                    }
                };
                var config = PdfDocumentReaderConfig
                    .builder()
                    .withPageExtractedTextFormatter(
                        new ExtractedTextFormatter.Builder()
                            .withNumberOfBottomTextLinesToDelete(3)
                            .withNumberOfTopPagesToSkipBeforeDelete(1)
                            .build()
                    )
                    .withPagesPerDocument(1)
                    .build();

                var pdfReader = new PagePdfDocumentReader(pdfResource, config);
                var textSplitter = new TokenTextSplitter();
                List<Document> documents = textSplitter.apply(pdfReader.get());
                documents.forEach(doc -> doc.getMetadata().put("fileId", fileId.toString()));
                vectorStore.accept(documents);

                return Mono.just(savedFile);
            });
    }

    public Mono<Void> deleteFile(Long id) {
        return uploadedFileRepository
            .findById(id)
            .flatMap(uploadedFile -> {
                String deleteSql = "DELETE FROM vector_store WHERE metadata->>'fileId' = ?";
                jdbcTemplate.update(deleteSql, uploadedFile.getFileId().toString());
                return uploadedFileRepository.deleteById(id);
            })
            .then();
    }
}
