package com.study.dao;

import com.study.model.BoardComment;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.List;
import java.util.logging.Logger;

/**
 * 댓글 관련 DAO
 */
public class BoardCommentDAO {

    private static final Logger log = Logger.getLogger(BoardCommentDAO.class.getName());

    private static final BoardCommentDAO instance = new BoardCommentDAO();
    public static BoardCommentDAO getInstance(){
        return instance;
    }

    /**
     * 게시물 댓글 작성 메서드
     * @param comment 댓글 객체
     * @param conn 동시성 제어, 같은 conn공유
     */
    public void insertComment(BoardComment comment, Connection conn){


    }


    /**
     * 게시물 댓글 삭제 메서드
     * @param commentSeq
     */
    public void deleteComment(Long commentSeq){

    }

    /**
     * 게시물 댓글 조회 메서드
     * @param boardSeq
     * @return
     */
    public List<BoardComment> commentList(Long boardSeq){

        return null;
    }


}

