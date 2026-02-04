package com.study.controller;

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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collection;
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

        //List<Board> list = boardDao.findAll();
        //request.setAttribute("boards", list);
        //request.getRequestDispatcher("/WEB-INF/views/board/boardList.jsp")
        //        .forward(request, response);

        if("/list".equals(reqPathInfo)){
            log.info("/baord/list doGet 요청 완료");
            req.getRequestDispatcher("/WEB-INF/views/board/list.jsp").forward(req, res);
        }

        if("/new".equals(reqPathInfo)){
            log.info("/baord/new doGet 요청 완료");
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

            //3. 실제 값이 있는 첨부파일만 골라내기
            Collection<Part> files = req.getParts().stream()
                    .filter(part -> "file".equals(part.getName()))
                    .filter(part -> part.getSize() > 0)
                    .filter(part -> part.getSubmittedFileName() != null)
                    .collect(Collectors.toCollection(ArrayList::new));

            // 4. 실제 값이있는 첨부파일들 검증하기
            ValidationResult fileResult = BoardFormValidator.validateFileAttachment(files);
            if(!fileResult.isValid()){

                req.setAttribute("restored", boardDto);
                req.setAttribute("errorMessage", fileResult.getMessage());
                req.getRequestDispatcher("/WEB-INF/views/board/writeForm.jsp").forward(req, res);
                return;
            }

            // ------------------------ 검증 모두 통과 --------------------------

            //1. 첨부파일을 하드에 저장
            try {
                UploadFileUtil.saveFile(files);
            } catch (UncheckedIOException e){
                log.severe(e.getMessage());

                req.setAttribute("restored", boardDto);
                req.setAttribute("errorMessage", "파일 저장 중 서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
                req.getRequestDispatcher("/WEB-INF/views/board/writeForm.jsp").forward(req, res);
            }

            log.info("--------------- 첨부파일 하드에 저장 성공 -----------------");
            //2-1. Board 테이블 INSERT & Attachment 테이블 INSERT



            //2-2. 테이블 작업 롤백시(=실패) 하드 저장된 파일도 삭제


            log.info("/new 끝 ~~~");


            //------------ 최종 --------------
            //다른 페이지로 리다이렉트

        }


    }




}
