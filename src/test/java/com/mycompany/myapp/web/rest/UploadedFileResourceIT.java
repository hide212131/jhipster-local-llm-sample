package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.UploadedFile;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.UploadedFileRepository;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link UploadedFileResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class UploadedFileResourceIT {

    private static final UUID DEFAULT_FILE_ID = UUID.randomUUID();
    private static final UUID UPDATED_FILE_ID = UUID.randomUUID();

    private static final String DEFAULT_FILENAME = "AAAAAAAAAA";
    private static final String UPDATED_FILENAME = "BBBBBBBBBB";

    private static final byte[] DEFAULT_DATA = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_DATA = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_DATA_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_DATA_CONTENT_TYPE = "image/png";

    private static final String ENTITY_API_URL = "/api/uploaded-files";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private UploadedFileRepository uploadedFileRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private UploadedFile uploadedFile;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UploadedFile createEntity(EntityManager em) {
        UploadedFile uploadedFile = new UploadedFile()
            .fileId(DEFAULT_FILE_ID)
            .filename(DEFAULT_FILENAME)
            .data(DEFAULT_DATA)
            .dataContentType(DEFAULT_DATA_CONTENT_TYPE);
        return uploadedFile;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UploadedFile createUpdatedEntity(EntityManager em) {
        UploadedFile uploadedFile = new UploadedFile()
            .fileId(UPDATED_FILE_ID)
            .filename(UPDATED_FILENAME)
            .data(UPDATED_DATA)
            .dataContentType(UPDATED_DATA_CONTENT_TYPE);
        return uploadedFile;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(UploadedFile.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        uploadedFile = createEntity(em);
    }

    @Test
    void createUploadedFile() throws Exception {
        int databaseSizeBeforeCreate = uploadedFileRepository.findAll().collectList().block().size();
        // Create the UploadedFile
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(uploadedFile))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the UploadedFile in the database
        List<UploadedFile> uploadedFileList = uploadedFileRepository.findAll().collectList().block();
        assertThat(uploadedFileList).hasSize(databaseSizeBeforeCreate + 1);
        UploadedFile testUploadedFile = uploadedFileList.get(uploadedFileList.size() - 1);
        assertThat(testUploadedFile.getFileId()).isEqualTo(DEFAULT_FILE_ID);
        assertThat(testUploadedFile.getFilename()).isEqualTo(DEFAULT_FILENAME);
        assertThat(testUploadedFile.getData()).isEqualTo(DEFAULT_DATA);
        assertThat(testUploadedFile.getDataContentType()).isEqualTo(DEFAULT_DATA_CONTENT_TYPE);
    }

    @Test
    void createUploadedFileWithExistingId() throws Exception {
        // Create the UploadedFile with an existing ID
        uploadedFile.setId(1L);

        int databaseSizeBeforeCreate = uploadedFileRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(uploadedFile))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UploadedFile in the database
        List<UploadedFile> uploadedFileList = uploadedFileRepository.findAll().collectList().block();
        assertThat(uploadedFileList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkFileIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = uploadedFileRepository.findAll().collectList().block().size();
        // set the field null
        uploadedFile.setFileId(null);

        // Create the UploadedFile, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(uploadedFile))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<UploadedFile> uploadedFileList = uploadedFileRepository.findAll().collectList().block();
        assertThat(uploadedFileList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkFilenameIsRequired() throws Exception {
        int databaseSizeBeforeTest = uploadedFileRepository.findAll().collectList().block().size();
        // set the field null
        uploadedFile.setFilename(null);

        // Create the UploadedFile, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(uploadedFile))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<UploadedFile> uploadedFileList = uploadedFileRepository.findAll().collectList().block();
        assertThat(uploadedFileList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllUploadedFilesAsStream() {
        // Initialize the database
        uploadedFileRepository.save(uploadedFile).block();

        List<UploadedFile> uploadedFileList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(UploadedFile.class)
            .getResponseBody()
            .filter(uploadedFile::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(uploadedFileList).isNotNull();
        assertThat(uploadedFileList).hasSize(1);
        UploadedFile testUploadedFile = uploadedFileList.get(0);
        assertThat(testUploadedFile.getFileId()).isEqualTo(DEFAULT_FILE_ID);
        assertThat(testUploadedFile.getFilename()).isEqualTo(DEFAULT_FILENAME);
        assertThat(testUploadedFile.getData()).isEqualTo(DEFAULT_DATA);
        assertThat(testUploadedFile.getDataContentType()).isEqualTo(DEFAULT_DATA_CONTENT_TYPE);
    }

    @Test
    void getAllUploadedFiles() {
        // Initialize the database
        uploadedFileRepository.save(uploadedFile).block();

        // Get all the uploadedFileList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(uploadedFile.getId().intValue()))
            .jsonPath("$.[*].fileId")
            .value(hasItem(DEFAULT_FILE_ID.toString()))
            .jsonPath("$.[*].filename")
            .value(hasItem(DEFAULT_FILENAME))
            .jsonPath("$.[*].dataContentType")
            .value(hasItem(DEFAULT_DATA_CONTENT_TYPE))
            .jsonPath("$.[*].data")
            .value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_DATA)));
    }

    @Test
    void getUploadedFile() {
        // Initialize the database
        uploadedFileRepository.save(uploadedFile).block();

        // Get the uploadedFile
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, uploadedFile.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(uploadedFile.getId().intValue()))
            .jsonPath("$.fileId")
            .value(is(DEFAULT_FILE_ID.toString()))
            .jsonPath("$.filename")
            .value(is(DEFAULT_FILENAME))
            .jsonPath("$.dataContentType")
            .value(is(DEFAULT_DATA_CONTENT_TYPE))
            .jsonPath("$.data")
            .value(is(Base64.getEncoder().encodeToString(DEFAULT_DATA)));
    }

    @Test
    void getNonExistingUploadedFile() {
        // Get the uploadedFile
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingUploadedFile() throws Exception {
        // Initialize the database
        uploadedFileRepository.save(uploadedFile).block();

        int databaseSizeBeforeUpdate = uploadedFileRepository.findAll().collectList().block().size();

        // Update the uploadedFile
        UploadedFile updatedUploadedFile = uploadedFileRepository.findById(uploadedFile.getId()).block();
        updatedUploadedFile
            .fileId(UPDATED_FILE_ID)
            .filename(UPDATED_FILENAME)
            .data(UPDATED_DATA)
            .dataContentType(UPDATED_DATA_CONTENT_TYPE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedUploadedFile.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedUploadedFile))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the UploadedFile in the database
        List<UploadedFile> uploadedFileList = uploadedFileRepository.findAll().collectList().block();
        assertThat(uploadedFileList).hasSize(databaseSizeBeforeUpdate);
        UploadedFile testUploadedFile = uploadedFileList.get(uploadedFileList.size() - 1);
        assertThat(testUploadedFile.getFileId()).isEqualTo(UPDATED_FILE_ID);
        assertThat(testUploadedFile.getFilename()).isEqualTo(UPDATED_FILENAME);
        assertThat(testUploadedFile.getData()).isEqualTo(UPDATED_DATA);
        assertThat(testUploadedFile.getDataContentType()).isEqualTo(UPDATED_DATA_CONTENT_TYPE);
    }

    @Test
    void putNonExistingUploadedFile() throws Exception {
        int databaseSizeBeforeUpdate = uploadedFileRepository.findAll().collectList().block().size();
        uploadedFile.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, uploadedFile.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(uploadedFile))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UploadedFile in the database
        List<UploadedFile> uploadedFileList = uploadedFileRepository.findAll().collectList().block();
        assertThat(uploadedFileList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchUploadedFile() throws Exception {
        int databaseSizeBeforeUpdate = uploadedFileRepository.findAll().collectList().block().size();
        uploadedFile.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(uploadedFile))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UploadedFile in the database
        List<UploadedFile> uploadedFileList = uploadedFileRepository.findAll().collectList().block();
        assertThat(uploadedFileList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamUploadedFile() throws Exception {
        int databaseSizeBeforeUpdate = uploadedFileRepository.findAll().collectList().block().size();
        uploadedFile.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(uploadedFile))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the UploadedFile in the database
        List<UploadedFile> uploadedFileList = uploadedFileRepository.findAll().collectList().block();
        assertThat(uploadedFileList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateUploadedFileWithPatch() throws Exception {
        // Initialize the database
        uploadedFileRepository.save(uploadedFile).block();

        int databaseSizeBeforeUpdate = uploadedFileRepository.findAll().collectList().block().size();

        // Update the uploadedFile using partial update
        UploadedFile partialUpdatedUploadedFile = new UploadedFile();
        partialUpdatedUploadedFile.setId(uploadedFile.getId());

        partialUpdatedUploadedFile.fileId(UPDATED_FILE_ID).filename(UPDATED_FILENAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedUploadedFile.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedUploadedFile))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the UploadedFile in the database
        List<UploadedFile> uploadedFileList = uploadedFileRepository.findAll().collectList().block();
        assertThat(uploadedFileList).hasSize(databaseSizeBeforeUpdate);
        UploadedFile testUploadedFile = uploadedFileList.get(uploadedFileList.size() - 1);
        assertThat(testUploadedFile.getFileId()).isEqualTo(UPDATED_FILE_ID);
        assertThat(testUploadedFile.getFilename()).isEqualTo(UPDATED_FILENAME);
        assertThat(testUploadedFile.getData()).isEqualTo(DEFAULT_DATA);
        assertThat(testUploadedFile.getDataContentType()).isEqualTo(DEFAULT_DATA_CONTENT_TYPE);
    }

    @Test
    void fullUpdateUploadedFileWithPatch() throws Exception {
        // Initialize the database
        uploadedFileRepository.save(uploadedFile).block();

        int databaseSizeBeforeUpdate = uploadedFileRepository.findAll().collectList().block().size();

        // Update the uploadedFile using partial update
        UploadedFile partialUpdatedUploadedFile = new UploadedFile();
        partialUpdatedUploadedFile.setId(uploadedFile.getId());

        partialUpdatedUploadedFile
            .fileId(UPDATED_FILE_ID)
            .filename(UPDATED_FILENAME)
            .data(UPDATED_DATA)
            .dataContentType(UPDATED_DATA_CONTENT_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedUploadedFile.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedUploadedFile))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the UploadedFile in the database
        List<UploadedFile> uploadedFileList = uploadedFileRepository.findAll().collectList().block();
        assertThat(uploadedFileList).hasSize(databaseSizeBeforeUpdate);
        UploadedFile testUploadedFile = uploadedFileList.get(uploadedFileList.size() - 1);
        assertThat(testUploadedFile.getFileId()).isEqualTo(UPDATED_FILE_ID);
        assertThat(testUploadedFile.getFilename()).isEqualTo(UPDATED_FILENAME);
        assertThat(testUploadedFile.getData()).isEqualTo(UPDATED_DATA);
        assertThat(testUploadedFile.getDataContentType()).isEqualTo(UPDATED_DATA_CONTENT_TYPE);
    }

    @Test
    void patchNonExistingUploadedFile() throws Exception {
        int databaseSizeBeforeUpdate = uploadedFileRepository.findAll().collectList().block().size();
        uploadedFile.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, uploadedFile.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(uploadedFile))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UploadedFile in the database
        List<UploadedFile> uploadedFileList = uploadedFileRepository.findAll().collectList().block();
        assertThat(uploadedFileList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchUploadedFile() throws Exception {
        int databaseSizeBeforeUpdate = uploadedFileRepository.findAll().collectList().block().size();
        uploadedFile.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(uploadedFile))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UploadedFile in the database
        List<UploadedFile> uploadedFileList = uploadedFileRepository.findAll().collectList().block();
        assertThat(uploadedFileList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamUploadedFile() throws Exception {
        int databaseSizeBeforeUpdate = uploadedFileRepository.findAll().collectList().block().size();
        uploadedFile.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(uploadedFile))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the UploadedFile in the database
        List<UploadedFile> uploadedFileList = uploadedFileRepository.findAll().collectList().block();
        assertThat(uploadedFileList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteUploadedFile() {
        // Initialize the database
        uploadedFileRepository.save(uploadedFile).block();

        int databaseSizeBeforeDelete = uploadedFileRepository.findAll().collectList().block().size();

        // Delete the uploadedFile
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, uploadedFile.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<UploadedFile> uploadedFileList = uploadedFileRepository.findAll().collectList().block();
        assertThat(uploadedFileList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
