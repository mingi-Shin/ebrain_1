package com.study.controller;

import com.study.Service.AllBoardService;
import com.study.dao.BoardDAO;
import com.study.model.Attachment;
import com.study.model.Board;
import com.study.model.BoardComment;
import com.study.util.BoardFormValidator;
import com.study.util.UploadFileUtil;
import com.study.util.ValidationResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@WebServlet("/board/*")
@MultipartConfig //용량제한 옵션주면 서블릿에 도달전에 톰캣에서 검증 -> IllegalStateException (500)
public class BoardController extends HttpServlet {

    private static final Logger log =
            Logger.getLogger(BoardController.class.getName());


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        //한글 패치
        res.setContentType("text/html; charset=UTF-8");
        res.setCharacterEncoding("UTF-8");


        String reqPathInfo = req.getPathInfo();
        log.info("reqPathInfo : " + reqPathInfo);

        //게시물 리스트 페이지 호출
        if("/list".equals(reqPathInfo)){

            //파라미터값 수신
            String categoryParam = req.getParameter("categorySeq");
            String searchWordParam = req.getParameter("searchWord");
            String startDateParam = req.getParameter("startDate");
            String endDateParam = req.getParameter("endDate");
            String pageParam = req.getParameter("page");

            log.info("/list 초기 수신값 : " + categoryParam + " / " + searchWordParam + " / " + startDateParam + " / " + endDateParam + " / " + pageParam ) ;

            //초기 접속시와 값없는 검색을 위해서, NPE방지 초기값으로 설정 및 메서드용으로써 자료형 변환.
            Long categorySeq = (categoryParam == null || categoryParam.isBlank()) ? 0 : Long.parseLong(categoryParam.trim());
            String searchWord = (searchWordParam == null || searchWordParam.isBlank()) ? "" : searchWordParam.trim();

            //날짜 초기값 : 1년전 ~ 현재
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            String startDateStr = (startDateParam == null || startDateParam.isBlank())
                    ? LocalDate.now().minusYears(1).format(dtf)
                    : startDateParam.trim();

            String endDateStr = (endDateParam == null || endDateParam.isBlank())
                    ? LocalDate.now().format(dtf)
                    : endDateParam.trim();

            //String자료형인 입력된 날짜를 형변환 후 검증
            LocalDate start = LocalDate.parse(startDateStr, dtf);
            LocalDateTime startDate = start.atStartOfDay();      // 00:00:00

            LocalDate end = LocalDate.parse(endDateStr, dtf);
            LocalDateTime endDate = end.atTime(23, 59, 59);      // 하루 끝
            log.info("날짜 검증 : " + startDate + " / " + endDate);

            if(endDate.isBefore(startDate)){
                req.setAttribute("errorMessage", "종료일은 시작일 이후여야 합니다.");
                res.sendRedirect("/");
                return;
            }

            int page = (pageParam == null || pageParam.isBlank()) ? 1 : Integer.parseInt(pageParam.trim());

            // 추후 옵션 파라미터로 받을 예정 (10개씩 보기..)
            int size = 10;

            //게시물 객체 리스트 및 총 개수 가져오기
            AllBoardService allBoardService = new AllBoardService();
            Map<String, Object> boardListMap = null;
            try {
                boardListMap = allBoardService.selectBoardListAttach(categorySeq, searchWord, startDateStr, endDateStr, page, size);
            } catch (RuntimeException e){
                log.severe("서버 오류 발생: " + e.getMessage());

                //redirect는 302 응답이므로, 실제 에러를 던지자 (web.xml에 선언)
                res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }

            // 게시물 리스트 가져오기
            List<Board> boardList = (List<Board>) boardListMap.get("boardList");
            log.info(boardList.toString());

            //EL 표현위해서 LocalDateTime 변수를 String으로 변환
            DateTimeFormatter dtfStr = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            for(Board b : boardList){
                b.setCreatedAtStr(b.getCreatedAt().format(dtfStr));
                if(b.getUpdatedAt() != null){
                    b.setUpdatedAtStr(b.getUpdatedAt().format(dtfStr));
                }
            }

            //게시물 리스트를 전송 준비
            req.setAttribute("boardList", boardList);
            //게시물 총 개수 전송 준비
            int listCount = (int) boardListMap.get("boardListCount");
            req.setAttribute("listCount", listCount);

            //페이징 처리를 위한 변수 초기화
            int totalPage = 1;
            if(listCount > 0){
                totalPage = (int) Math.ceil(listCount / (double) size);
            }
            int startPage = ((page - 1) / 10) * 10 + 1; // 1~10, 11~20 페이지 단위
            int endPage = Math.min(startPage + 9, totalPage);

            log.info("/list 수정후 값 : "
                    + categorySeq + " / " + searchWord + " / " + startDateStr + " / " + endDateStr
                    + " / " + page + " / " + totalPage + " / " + endPage + " / " + startPage) ;


            //검색 조건 유지 값 전송
            req.setAttribute("categorySeq", categorySeq);
            req.setAttribute("searchWord", searchWord);
            req.setAttribute("startDate", startDateStr);
            req.setAttribute("endDate", endDateStr);
            req.setAttribute("page", page);
            req.setAttribute("totalPage", totalPage);
            req.setAttribute("startPage", startPage);
            req.setAttribute("endPage", endPage);

            // req.setAttribute("size", size); //추후 구현 예정 코드

            req.getRequestDispatcher("/WEB-INF/views/board/list.jsp").forward(req, res);
        }

        //게시물 작성 페이지 호출
        if("/new".equals(reqPathInfo)){
            req.getRequestDispatcher("/WEB-INF/views/board/writeForm.jsp").forward(req, res);
        }

        //특정 게시물 자세히 보기 페이지 호출
        if("/detail".equals(reqPathInfo)){
            Long boardSeq = Long.parseLong(req.getParameter("boardSeq"));
            log.info("boardSeq : " + boardSeq);

            //해당 boardSeq의 게시물과 첨부파일, 댓글 모두 가져오기
            try {
                AllBoardService service = new AllBoardService();
                Map<String, Object> boardDetailMap = service.selectBoardAttachmentComment(boardSeq);

                // EL표현을 위해 board의 날짜 String 형에 초기화
                Board board = (Board) boardDetailMap.get("board");
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                board.setCreatedAtStr(board.getCreatedAt().format(dtf));
                if(board.getUpdatedAt() != null){
                    board.setUpdatedAtStr(board.getUpdatedAt().format(dtf));
                }

                req.setAttribute("board", boardDetailMap.get("board"));
                req.setAttribute("attachments", boardDetailMap.get("attList"));
                req.setAttribute("comments", boardDetailMap.get("commentList"));

                req.getRequestDispatcher("/WEB-INF/views/board/detail.jsp").forward(req, res);

            } catch (RuntimeException e) {
                // 서비스에서 던진 모든 예외 처리
                log.severe("게시물 조회 오류: " + e.getMessage());
                res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); //500
            }
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        //req.setCharacterEncoding("UTF-8"); 미설정시 ISO-8859-1로 처리해서 한글포함 요청 데이터가 손상된다.
        String reqPathInfo = req.getPathInfo();
        log.info("reqPathInfo : " + reqPathInfo); // /new

        //새 게시물 작성 요청 : /new
        if("/new".equals(reqPathInfo)){

            // req에서 파라미터를 받아 board객체로 생성하는 메서드 (복구용, 검증용)
            Board boardDto = Board.from(req);

            //1. Form enctype 검증
            String formType = req.getContentType();
            ValidationResult typeResult = BoardFormValidator.validateBoardFormType(formType);
            if(!typeResult.isValid()){

                req.setAttribute("restored", boardDto);
                req.setAttribute("errorMessage", typeResult.getMessage());
                req.getRequestDispatcher("/WEB-INF/views/board/writeForm.jsp").forward(req, res);
                return;
            }

            //2. 사용자 입력값 검증
            ValidationResult boardResult = BoardFormValidator.validateBoardForm(boardDto);
            if(!boardResult.isValid()){

                req.setAttribute("restored", boardDto);
                req.setAttribute("errorMessage", boardResult.getMessage());
                req.getRequestDispatcher("/WEB-INF/views/board/writeForm.jsp").forward(req, res);
                return;
            }

            //3. 실제 값이 있는 file 첨부파일만 골라내기
            Collection<Part> files = req.getParts().stream()
                    .filter(part -> "file".equals(part.getName()))
                    .filter(part -> part.getSize() > 0)
                    .filter(part -> part.getSubmittedFileName() != null)
                    .collect(Collectors.toCollection(ArrayList::new));

            // 4. 실제 값이 있는 첨부파일들 검증하기
            ValidationResult fileResult = BoardFormValidator.validateFileAttachment(files);
            if(!fileResult.isValid()){

                req.setAttribute("restored", boardDto);
                req.setAttribute("errorMessage", fileResult.getMessage());
                req.getRequestDispatcher("/WEB-INF/views/board/writeForm.jsp").forward(req, res);
                return;
            }

            // ------------------------ 검증 모두 통과 --------------------------

            //1. 첨부파일을 하드에 저장 및 객체 리스트화
            List<Attachment> attList = null;
            try {
                attList = UploadFileUtil.saveFile(files);
            } catch (UncheckedIOException e){
                log.severe(e.getMessage());

                req.setAttribute("restored", boardDto);
                req.setAttribute("errorMessage", "파일 저장 중 서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
                req.getRequestDispatcher("/WEB-INF/views/board/writeForm.jsp").forward(req, res);
            }

            log.info("--------------- 첨부파일 하드에 저장 성공 -----------------");

            //2. Board 테이블 INSERT & Attachment 테이블 INSERT -> Transactional 처리
            AllBoardService baService = new AllBoardService();
            try{
                baService.createBoardAttachment(boardDto, attList);
            } catch (RuntimeException e){
                //테이블 작업 실패 -> 하드에 저장된 첨부파일 삭제
                //log.info("----------- DB작업 실패, rollback 처리. 저장된 사진 삭제를 시도합니다. -----------");
                for(Attachment att : attList){
                    // 절대 경로 조합
                    String fullPath = UploadFileUtil.MAC_SAVE_PATH
                            + File.separator + att.getFilePath()
                            + File.separator + att.getStoredName();
                    File file = new File(fullPath);
                    if (file.exists()) {
                        boolean deleted = file.delete();
                        log.info(att.getStoredName() + " 삭제 " + deleted);
                    } else {
                        log.warning("삭제할 파일 없음: " + fullPath);
                    }
                }
            }

            //------------ 최종 --------------
            //다른 페이지로 리다이렉트
            String url = req.getParameter("beforeUrl");
            res.sendRedirect(url);

        }


    }




}
