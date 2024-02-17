package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.UploadedFileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UploadedFileTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UploadedFile.class);
        UploadedFile uploadedFile1 = getUploadedFileSample1();
        UploadedFile uploadedFile2 = new UploadedFile();
        assertThat(uploadedFile1).isNotEqualTo(uploadedFile2);

        uploadedFile2.setId(uploadedFile1.getId());
        assertThat(uploadedFile1).isEqualTo(uploadedFile2);

        uploadedFile2 = getUploadedFileSample2();
        assertThat(uploadedFile1).isNotEqualTo(uploadedFile2);
    }
}
