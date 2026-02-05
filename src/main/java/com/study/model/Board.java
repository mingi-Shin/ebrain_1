package com.study.model;

import jakarta.servlet.http.HttpServletRequest;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Board {
    private Long boardSeq;
    private Long categorySeq;
    private String username;
    private String password;
    private String title;
    private String content;
    private int hit;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    //해당 게시물 첨부파일 존재여부 : 게시물 리스트 호출에 쓰일 변수
    private boolean hasAttachment;

    // EL표현을 위해 출력용 날짜변수 선언
    private String createdAtStr;
    private String updatedAtStr;

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isHasAttachment() {
        return hasAttachment;
    }

    public void setHasAttachment(boolean hasAttachment) {
        this.hasAttachment = hasAttachment;
    }

    public String getCreatedAtStr() {
        return createdAtStr;
    }

    public void setCreatedAtStr(String createdAtStr) {
        this.createdAtStr = createdAtStr;
    }

    public String getUpdatedAtStr() {
        return updatedAtStr;
    }

    public void setUpdatedAtStr(String updatedAtStr) {
        this.updatedAtStr = updatedAtStr;
    }

    @Override
    public String toString() {
        return "Board{" +
                "boardSeq=" + boardSeq +
                ", categorySeq=" + categorySeq +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", hit=" + hit +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", hasAttachment=" + hasAttachment +
                ", createdAtStr='" + createdAtStr + '\'' +
                ", updatedAtStr='" + updatedAtStr + '\'' +
                '}';
    }

    //request에 담긴 값을 바탕으로 객체를 생성하는 메서드
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
