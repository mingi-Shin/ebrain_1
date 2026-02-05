package com.study.Service;

import com.study.connection.ConnectionTest;
import com.study.dao.AttachmentDAO;
import com.study.dao.BoardDAO;
import com.study.model.Attachment;
import com.study.model.Board;
import com.study.model.BoardComment;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

// BoardInsert와 AttachInsert 작업을 하나로 묶기위한 트랜잭셔널 클래스
public class AllBoardService {

    private static final Logger log = Logger.getLogger(AllBoardService.class.getName());


    /**
     * 게시물 작성 메서드
     * Board, Attachment 테이블 insert 작업 처리
     * @param board 게시물 객체
     * @param attList 첨부파일 리스트
     */
    public void createBoardAttachment(Board board, Collection<Attachment> attList){

        // DB작업을 묶기 위해 공통된 Connection 객체 준비 (Transactional)
        Connection conn = null;

        try {
            conn = ConnectionTest.getConnection();
            conn.setAutoCommit(false);

            //게시물 등록
            BoardDAO boardDao = BoardDAO.getInstance();
            Long boardSeq = boardDao.insertBoard(board, conn);

            //첨부파일이 존재할 때 등록
            if(!attList.isEmpty()){
                AttachmentDAO attDao = AttachmentDAO.getInstance();
                attDao.insertAttachment(attList, conn, boardSeq);
            }

            //모두 성공하면 커밋
            conn.commit();

        } catch (Exception e) {
            if(conn != null){
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new RuntimeException(e);
        } finally {
            if(conn != null){
                try {
                    conn.close(); // SQLException 발생 가능
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    public List<Board> selectBoardListAttach(int categorySeq, String searchWord, String startDate, String endDate, int page){

        int size = 10;
        int limit = size;
        int offset = (page - 1) * size;

        List<Board> boardList = new ArrayList<>();

        // page를 줘서 게시물 10개 받아오기 쿼리
        // 첨부파일 표시를 위해, 받아온 게시물들 boardSeq로 board객체에 hasAttachment 변수를 true로 변환하여 boardList에 add.
        // page를 제외한 조건 (매개변수)을 줘서 총 게시물 개수 받아오기 쿼리

        BoardDAO bDao = BoardDAO.getInstance();

        AttachmentDAO aDao = AttachmentDAO.getInstance();



        return boardList;
    }




    /**
     * 게시물 삭제 메서드
     * Board, Attachment, BoardComment 테이블 soft delete 작업 처리 : update
     * @param boardSeq
     */
    public void deleteBoardAttachment(Long boardSeq){

    }

    /**
     * 게시물 수정 메서드
     * Board, Attachment 테이블 update 작업 처리
     * @param board
     * @param attList
     */
    public void updateBoardAttachment(Board board, Collection<Attachment> attList){

    }

    /**
     * 게시물 상세보기 메서드
     * Board, Attachment, BoardComment 테이블 select 작업 처리
     * @param boardSeq
     */
    public void selectBoardAttachmentComment(Long boardSeq){
        //조회는 트랜잭션이 필요한가?

    }

    /**
     * 댓글 작성 메서드
     * @param boardSeq
     * @param comment
     */
    public void insertCommentOnActiveBoard(Long boardSeq, BoardComment comment){
        //BoardDAO.getInstance()의 selectBoardLock() 사용
        //해당 게시물 조회 결과 true -> 댓글 insert

        //해당 게시물 조회 결과 false(논리삭제) -> 댓글 insert 실패
    }

}
