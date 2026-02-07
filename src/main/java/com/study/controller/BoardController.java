package com.study.controller;

import com.study.Service.AllBoardService;
import com.study.dao.AttachmentDAO;
import com.study.model.Attachment;
import com.study.model.Board;
import com.study.model.BoardComment;
import com.study.util.BoardFormValidator;
import com.study.util.FileUtil;
import com.study.util.ValidationResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.*;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.study.util.FileUtil.MAC_SAVE_PATH;

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

                //EL 표현을 위해 board의 날짜 String 형에 초기화
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                Board board = (Board) boardDetailMap.get("board");
                board.setCreatedAtStr(board.getCreatedAt().format(dtf));
                if(board.getUpdatedAt() != null){
                    board.setUpdatedAtStr(board.getUpdatedAt().format(dtf));
                }
                //Attachment 날짜 변환 추가
                List<Attachment> attList = (List<Attachment>) boardDetailMap.get("attList");
                if(attList != null){
                    for(Attachment att : attList){
                        att.setCreatedAtStr(att.getCreatedAt().format(dtf));
                    }
                }
                //EL 표현을 위해 comment의 날짜 String 형에 초기화
                List<BoardComment> commList = (List<BoardComment>) boardDetailMap.get("commentList");
                if(commList != null){
                    for(BoardComment comm : commList){
                        comm.setCreatedAtStr(comm.getCreatedAt().format(dtf));
                    }
                }

                log.info(board.toString());
                log.info(attList.toString());
                log.info(Objects.requireNonNull(commList).toString());

                req.setAttribute("board", board);
                req.setAttribute("attachments", attList);
                req.setAttribute("comments", commList);

                req.getRequestDispatcher("/WEB-INF/views/board/detail.jsp").forward(req, res);

            } catch (RuntimeException e) {
                // 서비스에서 던진 모든 예외 처리
                log.severe("게시물 조회 오류: " + e.getMessage());
                res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); //500
            }
        }


        // 파일 다운로드 처리
        // URL 패턴: /board/${boardSeq}/attachment/download/${attachmentSeq}
        String pathInfo = req.getPathInfo();
        log.info("pathInfo : " + pathInfo); // 예: /52/attachment/download/6

        // pathInfo가 null인 경우 에러 처리
        if (pathInfo == null) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST); // 400 (500보다 적절)
            return;
        }

        // URL 경로를 '/'로 분리 (예: ["", "52", "attachment", "download", "6"])
        String[] parts = pathInfo.split("/");

        // URL 패턴 검증: 5개 부분 + "attachment" + "download" 확인
        if (parts.length == 5 && "attachment".equals(parts[2]) && "download".equals(parts[3])) {

            // attachmentSeq 추출 (parts[4])
            Long attachmentSeq = Long.valueOf(parts[4]);
            log.info("다운로드 시도, attachmentSeq : " + attachmentSeq);

            // 1. DB에서 첨부파일 정보 조회
            AttachmentDAO aDao = AttachmentDAO.getInstance();
            Attachment att = null;
            try {
                att = aDao.selectAttachment(attachmentSeq);

                // 🔧 수정1: DB에서 데이터를 못 찾은 경우 처리
                if (att == null) {
                    log.info("첨부파일을 찾을 수 없음: attachmentSeq=" + attachmentSeq);
                    res.sendError(HttpServletResponse.SC_NOT_FOUND); // 404
                    return;
                }

                log.info("조회된 첨부파일 정보: " + att.toString());

            } catch (Exception e) {
                log.severe("첨부파일 조회 중 오류: " + e.getMessage());
                e.printStackTrace();
                res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500
                return; // 🔧 수정2: return 추가 (중요!)
            }

            // 2. 실제 파일 경로 구성
            // 최종 경로: /Users/smk/IT_DATAS/ebrain_temp/2026/02/06/uuid.png
            String realPath = MAC_SAVE_PATH + File.separator +
                    att.getFilePath() + File.separator +
                    att.getStoredName();

            log.info("파일 실제 경로: " + realPath); // 🔧 수정3: 경로 로그 추가

            // 3. 파일 존재 여부 확인
            File file = new File(realPath);
            if (!file.exists()) {
                log.severe("파일이 디스크에 없음: " + realPath);
                res.sendError(HttpServletResponse.SC_NOT_FOUND); // 404
                return;
            }

            // 🔧 수정4: 파일 읽기 권한 확인
            if (!file.canRead()) {
                log.severe("파일 읽기 권한 없음: " + realPath);
                res.sendError(HttpServletResponse.SC_FORBIDDEN); // 403
                return;
            }

            // 4. 응답 헤더 설정 ⭐ 핵심
            // MIME 타입을 "다운로드 가능한 바이너리"로 설정
            res.setContentType("application/octet-stream");

            // 파일 크기 명시 (브라우저가 다운로드 진행률 표시 가능)
            res.setContentLengthLong(file.length());

            // 한글 파일명 깨짐 방지 (UTF-8 인코딩 + 공백 처리)
            String encodedName = URLEncoder.encode(att.getOriginName(), "UTF-8")
                    .replaceAll("\\+", "%20"); // 공백을 %20으로 변환

            // Content-Disposition: 브라우저에게 "다운로드 저장창 띄워!"라고 알림
            res.setHeader(
                    "Content-Disposition",
                    "attachment; filename=\"" + encodedName + "\""
            );

            log.info("다운로드 시작: " + att.getOriginName() + " (" + file.length() + " bytes)");

            // 5. 파일 내용을 바이너리로 전송
            // try-with-resources: 자동으로 스트림 닫기
            try (InputStream fis = new FileInputStream(file);
                 OutputStream os = res.getOutputStream()) {

                // 8KB 단위로 읽어서 전송 (메모리 효율)
                byte[] buffer = new byte[8192];
                int read; // 실제로 읽은 바이트 수

                long totalWritten = 0; // 🔧 수정5: 전송량 추적

                while ((read = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, read); // 읽은 만큼만 쓰기
                    totalWritten += read;
                }

                os.flush(); // 🔧 수정6: 버퍼 비우기
                log.info("다운로드 완료: " + totalWritten + " bytes 전송");

            } catch (IOException e) {
                // 🔧 수정7: 다운로드 중 네트워크 오류 처리
                log.severe("파일 전송 중 오류: " + e.getMessage());
                // 이미 응답이 시작되었으므로 sendError() 불가
                // 로그만 남기고 클라이언트에서 재시도하도록 유도
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
                attList = FileUtil.uploadFile(files);
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
                    String fullPath = MAC_SAVE_PATH
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



        //댓글 등록 요청
        //action="${pageContext.request.contextPath}/board/${board.boardSeq}/comment/new"
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) return;
        if (pathInfo.endsWith("/comment/new")) {
            String[] arr = pathInfo.split("/");
            Long boardSeq = Long.parseLong(arr[1]);

            // 댓글 등록 로직시작
            // NPE 방지 기존값 설정
            String comment = req.getParameter("comment");
            if(comment == null || comment.isBlank()){
                comment = "행복하세요!";
            }
            String writer = req.getParameter("writer");
            if(writer == null || writer.isBlank()){
                writer = "[GUEST]";
            }
            String password = req.getParameter("password");
            if(password == null || password.isBlank()){
                password = "0000";
            }

            try {
                AllBoardService allService = new AllBoardService();
                allService.insertCommentOnActiveBoard(boardSeq, writer, password, comment);

                // 성공시
                String redirectUrl = req.getContextPath() + "/board/detail/" + boardSeq;
            } catch (Exception e) {
                log.info("댓글 등록 중 오류 발생 : " + e.getMessage());

                //실패메시지 담아서 게시물 리스트페이지로
                req.getSession().setAttribute("errorMessage", e.getMessage());
                String redirectUrl = req.getContextPath() + "/board/list";
                res.sendRedirect(redirectUrl);
            }

            // 성공시 해당 게시물로
            String redirectUrl = req.getContextPath() + "/board/detail?boardSeq=" + boardSeq;
            res.sendRedirect(redirectUrl);
        }



        //게시물 삭제
        //action="${pageContext.request.contextPath}/board  /delete/${board.boardSeq}">
        String[] parts = reqPathInfo.split("/");
        if(parts.length == 3 && "delete".equals(parts[1])){

            Long boardSeq = Long.valueOf(req.getParameter("boardSeq"));
            String password = req.getParameter("password");

            //게시물 status를 'DELETED'로 업데이트
            AllBoardService abService = new AllBoardService();
            try {
                abService.deleteBoard(boardSeq);
            } catch (Exception e) {
                // 다음엔 에러페이지 분류별로 만들자
                req.getSession().setAttribute("errorMessage", e.getMessage());
                String redirectUrl = req.getContextPath() + "/board/list";
                res.sendRedirect(redirectUrl);
            }




        }


        //댓글 삭제


    }




}
