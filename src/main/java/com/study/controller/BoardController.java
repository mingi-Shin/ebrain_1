package com.study.controller;

import com.study.Service.AllBoardService;
import com.study.dao.BoardDAO;
import com.study.model.Attachment;
import com.study.model.Board;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@WebServlet("/board/*")
@MultipartConfig //용량제한 옵션주면 서블릿에 도달전에 톰캣에서 검증 -> IllegalStateException (500)
public class BoardController extends HttpServlet {

    private static final Logger log =
            Logger.getLogger(BoardController.class.getName());


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

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

            //처음 접속시와 null 파라미터를 위해 초기값 설정 및 메서드용으로써 자료형 변환.
            int categorySeq = (categoryParam == null || categoryParam.isBlank()) ? 0 : Integer.parseInt(categoryParam.trim());

            String searchWord = (searchWordParam == null || searchWordParam.isBlank()) ? "" : searchWordParam.trim();

            //날짜 초기값 : 1년전 ~ 현재
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            String startDate = (startDateParam == null || startDateParam.isBlank())
                    ? LocalDate.now().minusYears(1).format(dtf)
                    : startDateParam.trim();

            String endDate = (endDateParam == null || endDateParam.isBlank())
                    ? LocalDate.now().format(dtf)
                    : endDateParam.trim();

            int page = (pageParam == null || pageParam.isBlank()) ? 1 : Integer.parseInt(pageParam.trim());

            //게시물 리스트 불러오기
            AllBoardService allBoardService = new AllBoardService();
            //List<Board> boardList = allBoardService.selectBoardListAttach(categorySeq, searchWord, startDate, endDate, page);

            List<Board> testList = null;

            // 게시물 조회 갯수에 근거하여 페이지 설정하기
            int totalPage = 8;
            //int totalPage = 1;
            if (testList != null && !testList.isEmpty()) {
                totalPage = (int) Math.ceil(testList.size() / 10.0);
            }
            int endPage = totalPage;
            int startPage = 1;

            log.info("/list 수정후 값 : "
                    + categorySeq + " / " + searchWord + " / " + startDate + " / " + endDate
                    + " / " + page + " / " + totalPage + " / " + endPage + " / " + startPage) ;


            //게시물 전송
            req.setAttribute("boardList", testList);

            //검색 조건 유지 값 전송
            req.setAttribute("categorySeq", categorySeq);
            req.setAttribute("searchWord", searchWord);
            req.setAttribute("startDate", startDate);
            req.setAttribute("endDate", endDate);
            req.setAttribute("page", page);
            req.setAttribute("totalPage", totalPage);
            req.setAttribute("startPage", startPage);
            req.setAttribute("endPage", endPage);

            req.getRequestDispatcher("/WEB-INF/views/board/list.jsp").forward(req, res);
        }

        //게시물 작성 페이지 호출
        if("/new".equals(reqPathInfo)){
            req.getRequestDispatcher("/WEB-INF/views/board/writeForm.jsp").forward(req, res);
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
            res.sendRedirect("/board/list");

        }


    }




}
