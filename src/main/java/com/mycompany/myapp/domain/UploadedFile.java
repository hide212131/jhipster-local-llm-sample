package com.mycompany.myapp.domain;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A UploadedFile.
 */
@Table("uploaded_file")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UploadedFile implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("file_id")
    private UUID fileId;

    @NotNull(message = "must not be null")
    @Column("filename")
    private String filename;

    @Column("data")
    private byte[] data;

    @NotNull
    @Column("data_content_type")
    private String dataContentType;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public UploadedFile id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getFileId() {
        return this.fileId;
    }

    public UploadedFile fileId(UUID fileId) {
        this.setFileId(fileId);
        return this;
    }

    public void setFileId(UUID fileId) {
        this.fileId = fileId;
    }

    public String getFilename() {
        return this.filename;
    }

    public UploadedFile filename(String filename) {
        this.setFilename(filename);
        return this;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public byte[] getData() {
        return this.data;
    }

    public UploadedFile data(byte[] data) {
        this.setData(data);
        return this;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getDataContentType() {
        return this.dataContentType;
    }

    public UploadedFile dataContentType(String dataContentType) {
        this.dataContentType = dataContentType;
        return this;
    }

    public void setDataContentType(String dataContentType) {
        this.dataContentType = dataContentType;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UploadedFile)) {
            return false;
        }
        return getId() != null && getId().equals(((UploadedFile) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UploadedFile{" +
            "id=" + getId() +
            ", fileId='" + getFileId() + "'" +
            ", filename='" + getFilename() + "'" +
            ", data='" + getData() + "'" +
            ", dataContentType='" + getDataContentType() + "'" +
            "}";
    }
}
