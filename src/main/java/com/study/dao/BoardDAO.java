package com.study.dao;

import com.study.connection.ConnectionTest;
import com.study.model.Board;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    public Board selectBoard(Long boardSeq, Connection conn) throws SQLException {

        Board board = null;
        String sql = "SELECT * FROM board WHERE board_seq = ? ";

        try(PreparedStatement pstmt = conn.prepareStatement(sql);
        ){
            pstmt.setLong(1, boardSeq);

            try(ResultSet rs = pstmt.executeQuery();){
                while (rs.next()){
                    board = new Board();
                    board.setBoardSeq(rs.getLong("board_seq"));
                    board.setCategorySeq(rs.getLong("category_seq"));
                    board.setTitle(rs.getString("title"));
                    board.setContent(rs.getString("content"));
                    board.setUsername(rs.getString("username"));
                    board.setPassword(rs.getString("password"));
                    board.setHit(rs.getInt("hit"));
                    board.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    //updatedAt은 NPE 가능성 존재, toLocalDateTime()은 NPE발생시킴
                    Timestamp updatedAtTs = rs.getTimestamp("updated_at");
                    if(updatedAtTs != null){
                        board.setUpdatedAt(updatedAtTs.toLocalDateTime());
                    } else {
                        board.setUpdatedAt(null);
                    }
                }
            }
        }

        return board;
    }

    /**
     * 게시물 상세 조회 메서드 (동시성 제어 비관적 잠금처리 -> 댓글작성때 호출 필요)
     * @param boardSeq
     * @return
     */
    public Board selectBoardLock(Long boardSeq){

        //... for update 사용하여 비관적 락 실행

        return null;
    }

    /**
     * 게시물 리스트 조회 메서드 (조건은 동적으로)
     * @return
     */
    public List<Board> selectBoardList(Long categorySeq, String searchWord, LocalDateTime startDate, LocalDateTime endDate,
                                       int limit, int offset) throws Exception {

        List<Board> boardListlist = new ArrayList<>();

        String boardListsql = "SELECT \n" +
                "    b.* ,\n" +
                "    EXISTS(SELECT 1 FROM attachment a WHERE a.board_seq = b.board_seq AND a.deleted_at IS NULL) AS has_attachment\n" +
                "FROM board b\n" +
                "WHERE (? = 0 OR b.category_seq = ? ) \n" +
                "  AND (b.title LIKE ? OR b.content LIKE ? OR b.username LIKE ?)\n" +
                "  AND b.created_at BETWEEN ? AND ? \n" +
                "  AND b.status = 'ACTIVE' \n" +
                "ORDER BY b.created_at DESC \n" +
                "LIMIT ? OFFSET ? ";

        try(Connection conn = ConnectionTest.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(boardListsql);
        ){
            String keyword = "%" + searchWord + "%";

            pstmt.setLong(1, categorySeq); // ? = 0
            pstmt.setLong(2, categorySeq); // b.category_seq = ?
            pstmt.setString(3, keyword);
            pstmt.setString(4, keyword);
            pstmt.setString(5, keyword);
            pstmt.setObject(6, startDate);
            pstmt.setObject(7, endDate);
            pstmt.setInt(8, limit);
            pstmt.setInt(9, offset);

            try(ResultSet rs = pstmt.executeQuery();){
                while(rs.next()){
                    Board board = new Board();
                    board.setBoardSeq(rs.getLong("board_seq"));
                    board.setCategorySeq(rs.getLong("category_seq"));
                    board.setTitle(rs.getString("title"));
                    board.setUsername(rs.getString("username"));
                    board.setHit(rs.getInt("hit"));
                    board.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    //updatedAt은 NPE 가능성 존재, toLocalDateTime()은 NPE발생시킴
                    Timestamp updatedAtTs = rs.getTimestamp("updated_at");
                    if(updatedAtTs != null){
                        board.setUpdatedAt(updatedAtTs.toLocalDateTime());
                    } else {
                        board.setUpdatedAt(null);
                    }
                    board.setHasAttachment(rs.getBoolean("has_attachment"));
                    board.setStatus("ACTIVE");

                    boardListlist.add(board);
                }
            }

        }

        return boardListlist;
    }

    public int selectListCount(Long categorySeq, String searchWord, LocalDateTime startDate, LocalDateTime endDate) throws Exception {

        String countSql = "SELECT COUNT(*) " +
                "FROM board " +
                "WHERE ( ? = 0 OR category_seq = ? )" +
                "  AND (title LIKE ? OR content LIKE ? OR username LIKE ?) " +
                "  AND created_at BETWEEN ? AND ?" +
                "  AND status = 'ACTIVE' " ;

        int totalCount = 0;

        try (Connection conn = ConnectionTest.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(countSql);
        ){
            String keyword = "%" + searchWord + "%";

            pstmt.setLong(1, categorySeq);
            pstmt.setLong(2, categorySeq);
            pstmt.setString(3, keyword);
            pstmt.setString(4, keyword);
            pstmt.setString(5, keyword);
            pstmt.setObject(6, startDate); // LocalDateTime → TIMESTAMP 자동 매핑
            pstmt.setObject(7, endDate);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    totalCount = rs.getInt(1);
                }
            }
        }
        return totalCount;
    }

}
