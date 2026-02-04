package com.study.model;

import jakarta.servlet.http.HttpServletRequest;

import java.sql.Timestamp;

public class Board {
    private Long boardSeq;
    private Long categorySeq;
    private String username;
    private String password;
    private String title;
    private String content;
    private int hit;
    private String status;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Board(){}

    public Long getBoardSeq() {
        return boardSeq;
    }

    public void setBoardSeq(Long boardSeq) {
        this.boardSeq = boardSeq;
    }

    public Long getCategorySeq() {
        return categorySeq;
    }

    public void setCategorySeq(Long categorySeq) {
        this.categorySeq = categorySeq;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getHit() {
        return hit;
    }

    public void setHit(int hit) {
        this.hit = hit;
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

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static final Board from(HttpServletRequest req){
        Board boardDto = new Board();
        boardDto.setCategorySeq( Long.parseLong(req.getParameter("category")));
        boardDto.setUsername(req.getParameter("username"));
        boardDto.setPassword(req.getParameter("password"));
        boardDto.setTitle(req.getParameter("title"));
        boardDto.setContent(req.getParameter("content"));

        return boardDto;
    }

}
