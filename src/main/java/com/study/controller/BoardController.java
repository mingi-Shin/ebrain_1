package com.study.controller;

import com.study.model.Attachment;
import com.study.util.PasswordValidator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.util.Collection;
import java.util.logging.Logger;

@WebServlet("/board/*")
@MultipartConfig(
    maxFileSize = 10 * 1024 * 1024,      // 10MB
    maxRequestSize = 20 * 1024 * 1024,   // 20MB
    fileSizeThreshold = 1 * 1024 * 1024  // 1MB
)
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
            req.setAttribute("test", "신민기천재야");
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
        log.info("req.getContentType : " + req.getContentType()); // multipart/form-data;

        String category = req.getParameter("category"); //FREEBOARD
        String username = req.getParameter("username"); //신민기
        String password = req.getParameter("password"); //ssy4260!
        String title = req.getParameter("title"); //한글제목인데요
        String content = req.getParameter("content"); //출력되나요?

        log.info("category : " + category);
        log.info("username : " + username);
        log.info("password : " + password);
        log.info("title : " + title);
        log.info("content : " + content);

        // 새 게시물 작성 요청 : /new
        if("/new".equals(reqPathInfo)){

            // Form enctype 검증
            if(!req.getContentType().startsWith("multipart")){
                req.setAttribute("errorMessage", "올바른 형식의 폼 타입이 아닙니다.");
                req.getRequestDispatcher("/WEB-INF/views/board/writeForm.jsp").forward(req, res);
            }

            // 사용자 입력값 검증



            Collection<Part> parts = req.getParts();
            for(Part p : parts){
                if("file".equals(p.getName()) && p.getSize() > 0){
                    //DB성공 후에
                    String pname = p.getSubmittedFileName();
                    log.info("pname : " + pname);
                    //Attachment newAtta = new Attachment();

                    //Board테이블 완료 -> Attachment 테이블 완료 (Transactional로 묶어야)


                   //실제 폴더에 upload
                }
            }

            Part filePart = req.getPart("file");
            log.info(filePart.toString()); //org.apache.catalina.core.ApplicationPart@20168866
            log.info(filePart.getName()); //file
            log.info(filePart.getSubmittedFileName()); //thumbnail.png
            log.info(filePart.getContentType()); //image/png
        }


    }




}
