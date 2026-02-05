package com.study.Service;

import com.study.connection.ConnectionTest;
import com.study.dao.AttachmentDAO;
import com.study.dao.BoardCommentDAO;
import com.study.dao.BoardDAO;
import com.study.model.Attachment;
import com.study.model.Board;
import com.study.model.BoardComment;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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

        // DB작업을 묶기 위해 공통된 Connection 객체 준비 (Transactional) 및 오토커밋을 false.
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
                    conn.rollback(); //데이터 롤백
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

    /**
     * 같은 Connection으로 묶을까?
     * 1. 조회작업은 다른 트랜잭션을 Lock하지 않음
     * 2. 3개를 묶으면 Connection Hold가 길어져서, Connection Pool이 작을 경우 다른 유저들이 대기하는 시간이 연장됨
     * 3. 3개를 별도 SELECT하면 DB부하가 커짐 -> 근데 어떻게해 이걸?
     * 4. 정합성을 위해서라면(내가 요청한 시점의 DB 스냅샷) 트랜잭션을 걸어도 됨. 하지만 큰 문제가 아님
     *
     * Q. 같은 conn을 써서 두 커리를 실행하고 수동커밋하면 정합성이 지켜져? 맞다. 하지만 커밋이 필요없다.
     */

    /**
     * 첨부파일 첨부 여부 포함 게시물 리스트 조회
     * @param categorySeq 카테고리 번호
     * @param searchWord 검색어(기본 "")
     * @param startDateStr 작성일(기본 1년전)
     * @param endDateStr 작성일(기본 오늘)
     * @param page 페이지
     * @param size 페이지당 게시물 개수
     * @return
     */
    public Map<String, Object> selectBoardListAttach(
            Long categorySeq, String searchWord, String startDateStr, String endDateStr, int page, int size){

        log.info("selectBoardListAttach() 받은값 : " + categorySeq + "/" + searchWord + "/" + startDateStr + "/" + endDateStr + "/" + page + "/" + size );

        //게시물 객체 리스트와 총개수를 담을 Map 생성
        Map<String, Object> boardListMap = new HashMap<>();


        // 해당 page에 해당하는 게시물을 size개씩 가져오기기 위한 변수 초기화
        int limit = size;
        int offset = (page - 1) * size;

        //String 자료형인 날짜를 LocalDate를 거쳐서, LocalDateTime으로 변환. DB의 TimeStamp와 맞추기 위해서
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDateLd = LocalDate.parse(startDateStr, dtf);
        LocalDate endDateLd = LocalDate.parse(endDateStr, dtf);
        LocalDateTime startDateLdt = startDateLd.atStartOfDay();
        LocalDateTime endDateLdt = endDateLd.atTime(23,59,59);


        //게시물 리스트를 첨부파일 여부 포함해서 가져오기
        List<Board> boardList = new ArrayList<>();
        BoardDAO bDao = BoardDAO.getInstance();
        try {
            boardList = bDao.selectBoardList(categorySeq, searchWord, startDateLdt, endDateLdt, limit, offset);
            boardListMap.put("boardList", boardList);
            log.info(boardList.toString());
        } catch (Exception e) {
            log.warning("게시물 리스트 조회 중 오류 발생 : " + e.getMessage());
            throw new RuntimeException(e);
        }

        //게시물 총 개수 구하기
        try {
            log.info("게시물 총 개수 구하기 매개변수 확인 : " + categorySeq + "/" + searchWord + "/" + startDateLdt + "/" +endDateLdt);
            int boardListCount = bDao.selectListCount(categorySeq, searchWord, startDateLdt, endDateLdt);
            log.info("게시물 총 개수 : " + boardListCount);

            boardListMap.put("boardListCount", boardListCount);
        } catch (Exception e) {
            log.warning("게시물 총 개수 조회 중 오류 발생 : " + e.getMessage());
            throw new RuntimeException(e);
        }

        //Board 리스트 반환
        return boardListMap;
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
    public Map<String, Object> selectBoardAttachmentComment(Long boardSeq) {
        Map<String, Object> boardDetailMap = new HashMap<>();

        try (Connection conn = ConnectionTest.getConnection()) { //수동 커밋 필요없어서 이렇게 함.
            BoardDAO bDao = BoardDAO.getInstance();
            AttachmentDAO aDao = AttachmentDAO.getInstance();
            BoardCommentDAO bcDao = BoardCommentDAO.getInstance();

            Board board = bDao.selectBoard(boardSeq, conn);
            log.info(board.toString());
            List<Attachment> attList = aDao.selectAttList(boardSeq, conn);
            log.info(attList.toString());
            List<BoardComment> commentList = bcDao.selectCommentList(boardSeq, conn);
            log.info(commentList.toString());

            boardDetailMap.put("board", board);
            boardDetailMap.put("attList", attList);
            boardDetailMap.put("commentList", commentList);

        } catch (Exception e) {
            // SQLException, NullPointerException 등 모든 예외를 Runtime으로 래핑
            throw new RuntimeException("게시물 상세 조회 실패", e);
        }

        return boardDetailMap;
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
