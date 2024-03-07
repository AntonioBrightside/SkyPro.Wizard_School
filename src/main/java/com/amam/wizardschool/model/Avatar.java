package com.amam.wizardschool.model;

import jakarta.persistence.*;

@Entity(name = "avatars")
public class Avatar {
    @Id
    private Long id;
    private String filePath;
    private Long fileSize;
    private String mediaType;

    @Lob
    private byte[] smallPhoto;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private Student student;

    public void setId(Long id) {
        this.id = id;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public void setPhoto(byte[] photo) {
        this.smallPhoto = photo;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Long getId() {
        return id;
    }

    public String getFilePath() {
        return filePath;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public String getMediaType() {
        return mediaType;
    }

    public Student getStudent() {
        return student;
    }

    public byte[] getSmallPhoto() {
        return smallPhoto;
    }

    public void setSmallPhoto(byte[] smallPhoto) {
        this.smallPhoto = smallPhoto;
    }
}
