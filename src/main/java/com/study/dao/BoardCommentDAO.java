package com.study.dao;

import com.study.connection.ConnectionTest;
import com.study.model.BoardComment;

import java.sql.*;
import java.util.ArrayList;
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
     * @param boardSeq
     * @param writer
     * @param password
     * @param content
     * @param conn
     * @throws SQLException
     */
    public void insertComment(Long boardSeq, String writer, String password, String content, Connection conn) throws SQLException {

        String insertSql = "INSERT INTO board_comment (board_seq, writer, password, content) VALUES (?, ?, ?, ?) ";

        try(PreparedStatement pstmt = conn.prepareStatement(insertSql);) {

            pstmt.setLong(1, boardSeq);
            pstmt.setString(2, writer);
            pstmt.setString(3, password);
            pstmt.setString(4, content);

            int result = pstmt.executeUpdate();

            if (result == 0) {
                throw new SQLException("게시물 등록 실패");
            }
        }
    }


    /**
     * 게시물 댓글 삭제 메서드
     * @param commentSeq
     */
    public void deleteComment(Long commentSeq) throws Exception {

        String sql = "UPDATE board_comment SET status = 'DELETED' WHERE comment_seq = ? ";

        try(Connection conn = ConnectionTest.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
        ){
            pstmt.setLong(1, commentSeq);

            int resultRow = pstmt.executeUpdate();

            if(resultRow == 0){
                throw new SQLException("댓글 등록 실패");
            }
        }
    }

    /**
     * 게시물 댓글 조회 메서드
     * @param boardSeq
     * @return
     */
    public List<BoardComment> selectCommentList(Long boardSeq, Connection conn) throws SQLException {

        List<BoardComment> commentList = new ArrayList<>();

        String sql = "SELECT * FROM board_comment WHERE board_seq = ? AND status = 'ACTIVE' ORDER BY created_at ASC ";

        try(PreparedStatement pstmt = conn.prepareStatement(sql);){

            pstmt.setLong(1, boardSeq);

            try(ResultSet rs = pstmt.executeQuery();){

                while (rs.next()){
                    BoardComment comment = new BoardComment();
                    comment.setCommentSeq(rs.getLong("comment_seq"));
                    comment.setBoardSeq(rs.getLong("board_seq"));
                    comment.setWriter(rs.getString("writer"));
                    comment.setContent(rs.getString("content"));
                    comment.setPassword(rs.getString("password"));
                    comment.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    comment.setStatus(rs.getString("status"));

                    commentList.add(comment);
                }
            }
        }
        return commentList;
    }


}


