package com.study.dao;

import com.study.connection.ConnectionTest;
import com.study.model.Board;

import java.sql.*;
import java.util.List;
import java.util.logging.Logger;

public class BoardDAO {

    private static final Logger log = Logger.getLogger(BoardDAO.class.getName());

    // 싱글톤으로 관리
    private static final BoardDAO instance = new BoardDAO();
    public static BoardDAO getInstance(){
        return instance;
    }

    /**
     * 게시물 작성 메서드
     * @param board
     * @param conn
     * @throws SQLException
     */
    public long insertBoard(Board board, Connection conn) throws SQLException {
        String insertBoardSql = "INSERT INTO board (category_seq, username, password, title, content) VALUES (?, ?, ?, ?, ?)";

        try(PreparedStatement pstmt = conn.prepareStatement(insertBoardSql, Statement.RETURN_GENERATED_KEYS);) {

            pstmt.setLong(1, board.getCategorySeq());
            pstmt.setString(2, board.getUsername());
            pstmt.setString(3, board.getPassword());
            pstmt.setString(4, board.getTitle());
            pstmt.setString(5, board.getContent());

            int resultRow = pstmt.executeUpdate();

            if(resultRow == 0){
                throw new SQLException("게시물 등록 실패");
            }

            //위에서 insert한 게시물의 PK값을 반환(board_seq) -> Statement.RETURN_GENERATED_KEYS) 선언 필요
            try(ResultSet rs = pstmt.getGeneratedKeys()){
                if(rs.next()){
                    return rs.getLong(1); // 첫 번째 컬럼, board_seq
                } else {
                    throw new SQLException("PK(board_seq) 가져오기 실패");
                }
            }

        }
    }

    /**
     * 게시물 수정 메서드
     * @param board
     * @param conn
     */
    public void updateBoard(Board board, Connection conn){

    }


    /**
     * 게시물 논리적 삭제 메서드
     * @param boardSeq
     */
    public void deleteBoard(Long boardSeq, Connection conn){

    }

    /**
     * 게시물 조회 메서드
     * @param boardSeq
     */
    public Board selectBoard(Long boardSeq){

        return null;
    }

    /**
     * 게시물 조회 메서드 (동시성 제어 비관적 잠금처리)
     * @param boardSeq
     * @return
     */
    public Board selectBoardLock(Long boardSeq){

        //... for update 사용하여 비관적 락 실행

        return null;
    }

    /**
     * 게시물 전체 조회 메서드
     * @return
     */
    public List<Board> boardList(){

        return null;
    }

}
