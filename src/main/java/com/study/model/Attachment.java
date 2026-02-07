package com.study.model;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Attachment {
    private Long attachmentSeq;
    private Long boardSeq;
    private String originName;
    private String storedName;
    private String filePath;
    private Long fileSize;
    private String fileExt;
    private String fileType;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;

    private String createdAtStr;

    public Attachment(){};

    public String getCreatedAtStr() {
        return createdAtStr;
    }

    public void setCreatedAtStr(String createdAtStr) {
        this.createdAtStr = createdAtStr;
    }

    public Long getAttachmentSeq() {
        return attachmentSeq;
    }

    public void setAttachmentSeq(Long attachmentSeq) {
        this.attachmentSeq = attachmentSeq;
    }

    public Long getBoardSeq() {
        return boardSeq;
    }

    public void setBoardSeq(Long boardSeq) {
        this.boardSeq = boardSeq;
    }

    public String getOriginName() {
        return originName;
    }

    public void setOriginName(String originName) {
        this.originName = originName;
    }

    public String getStoredName() {
        return storedName;
    }

    public void setStoredName(String storedName) {
        this.storedName = storedName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileExt() {
        return fileExt;
    }

    public void setFileExt(String fileExt) {
        this.fileExt = fileExt;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    @Override
    public String toString() {
        return "Attachment{" +
                "attachmentSeq=" + attachmentSeq +
                ", boardSeq=" + boardSeq +
                ", originName='" + originName + '\'' +
                ", storedName='" + storedName + '\'' +
                ", filePath='" + filePath + '\'' +
                ", fileSize=" + fileSize +
                ", fileExt='" + fileExt + '\'' +
                ", fileType='" + fileType + '\'' +
                ", createdAt=" + createdAt +
                ", deletedAt=" + deletedAt +
                ", createdAtStr='" + createdAtStr + '\'' +
                '}';
    }
}

