package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.UploadedFile;
import com.mycompany.myapp.repository.UploadedFileRepository;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.UploadedFile}.
 */
@RestController
@RequestMapping("/api/uploaded-files")
@Transactional
public class UploadedFileResource {

    private final Logger log = LoggerFactory.getLogger(UploadedFileResource.class);

    private static final String ENTITY_NAME = "uploadedFile";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UploadedFileRepository uploadedFileRepository;

    public UploadedFileResource(UploadedFileRepository uploadedFileRepository) {
        this.uploadedFileRepository = uploadedFileRepository;
    }

    /**
     * {@code POST  /uploaded-files} : Create a new uploadedFile.
     *
     * @param uploadedFile the uploadedFile to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new uploadedFile, or with status {@code 400 (Bad Request)} if the uploadedFile has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<UploadedFile>> createUploadedFile(@Valid @RequestBody UploadedFile uploadedFile) throws URISyntaxException {
        log.debug("REST request to save UploadedFile : {}", uploadedFile);
        if (uploadedFile.getId() != null) {
            throw new BadRequestAlertException("A new uploadedFile cannot already have an ID", ENTITY_NAME, "idexists");
        }

        // set dummy random UUID
        uploadedFile.setFileId(java.util.UUID.randomUUID());

        return uploadedFileRepository
            .save(uploadedFile)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/uploaded-files/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /uploaded-files/:id} : Updates an existing uploadedFile.
     *
     * @param id the id of the uploadedFile to save.
     * @param uploadedFile the uploadedFile to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated uploadedFile,
     * or with status {@code 400 (Bad Request)} if the uploadedFile is not valid,
     * or with status {@code 500 (Internal Server Error)} if the uploadedFile couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<UploadedFile>> updateUploadedFile(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody UploadedFile uploadedFile
    ) throws URISyntaxException {
        log.debug("REST request to update UploadedFile : {}, {}", id, uploadedFile);
        if (uploadedFile.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, uploadedFile.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return uploadedFileRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return uploadedFileRepository
                    .save(uploadedFile)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /uploaded-files/:id} : Partial updates given fields of an existing uploadedFile, field will ignore if it is null
     *
     * @param id the id of the uploadedFile to save.
     * @param uploadedFile the uploadedFile to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated uploadedFile,
     * or with status {@code 400 (Bad Request)} if the uploadedFile is not valid,
     * or with status {@code 404 (Not Found)} if the uploadedFile is not found,
     * or with status {@code 500 (Internal Server Error)} if the uploadedFile couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<UploadedFile>> partialUpdateUploadedFile(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody UploadedFile uploadedFile
    ) throws URISyntaxException {
        log.debug("REST request to partial update UploadedFile partially : {}, {}", id, uploadedFile);
        if (uploadedFile.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, uploadedFile.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return uploadedFileRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<UploadedFile> result = uploadedFileRepository
                    .findById(uploadedFile.getId())
                    .map(existingUploadedFile -> {
                        if (uploadedFile.getFileId() != null) {
                            existingUploadedFile.setFileId(uploadedFile.getFileId());
                        }
                        if (uploadedFile.getFilename() != null) {
                            existingUploadedFile.setFilename(uploadedFile.getFilename());
                        }
                        if (uploadedFile.getData() != null) {
                            existingUploadedFile.setData(uploadedFile.getData());
                        }
                        if (uploadedFile.getDataContentType() != null) {
                            existingUploadedFile.setDataContentType(uploadedFile.getDataContentType());
                        }

                        return existingUploadedFile;
                    })
                    .flatMap(uploadedFileRepository::save);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /uploaded-files} : get all the uploadedFiles.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of uploadedFiles in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<UploadedFile>> getAllUploadedFiles() {
        log.debug("REST request to get all UploadedFiles");
        return uploadedFileRepository.findAll().collectList();
    }

    /**
     * {@code GET  /uploaded-files} : get all the uploadedFiles as a stream.
     * @return the {@link Flux} of uploadedFiles.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<UploadedFile> getAllUploadedFilesAsStream() {
        log.debug("REST request to get all UploadedFiles as a stream");
        return uploadedFileRepository.findAll();
    }

    /**
     * {@code GET  /uploaded-files/:id} : get the "id" uploadedFile.
     *
     * @param id the id of the uploadedFile to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the uploadedFile, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<UploadedFile>> getUploadedFile(@PathVariable("id") Long id) {
        log.debug("REST request to get UploadedFile : {}", id);
        Mono<UploadedFile> uploadedFile = uploadedFileRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(uploadedFile);
    }

    /**
     * {@code DELETE  /uploaded-files/:id} : delete the "id" uploadedFile.
     *
     * @param id the id of the uploadedFile to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteUploadedFile(@PathVariable("id") Long id) {
        log.debug("REST request to delete UploadedFile : {}", id);
        return uploadedFileRepository
            .deleteById(id)
            .then(
                Mono.just(
                    ResponseEntity
                        .noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }
}
