package com.study.model;

import java.sql.Timestamp;

public class BoardComment {
    private Long commentSeq;
    private Long boardSeq;
    private String writer;
    private String password;
    private String content;
    private String status;
    private Timestamp createdAt;
    private Timestamp deletedAt;

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

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Timestamp deletedAt) {
        this.deletedAt = deletedAt;
    }
}
