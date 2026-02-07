package com.study.model;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class BoardComment {
    private Long commentSeq;
    private Long boardSeq;
    private String writer;
    private String password;
    private String content;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;

    private String createdAtStr;

    public BoardComment(){};

    public Long getCommentSeq() {
        return commentSeq;
    }

    public void setCommentSeq(Long commentSeq) {
        this.commentSeq = commentSeq;
    }

    public Long getBoardSeq() {
        return boardSeq;
    }

    public void setBoardSeq(Long boardSeq) {
        this.boardSeq = boardSeq;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getCreatedAtStr() {
        return createdAtStr;
    }

    public void setCreatedAtStr(String createdAtStr) {
        this.createdAtStr = createdAtStr;
    }

    @Override
    public String toString() {
        return "BoardComment{" +
                "commentSeq=" + commentSeq +
                ", boardSeq=" + boardSeq +
                ", writer='" + writer + '\'' +
                ", password='" + password + '\'' +
                ", content='" + content + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", deletedAt=" + deletedAt +
                ", createdAtStr='" + createdAtStr + '\'' +
                '}';
    }
}
