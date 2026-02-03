package com.study.controller;

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
@MultipartConfig
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

        //req.setCharacterEncoding("UTF-8");
        String reqPathInfo = req.getPathInfo();
        log.info("reqPathInfo : " + reqPathInfo);

        //파일들 처리
        if("/new".equals(reqPathInfo)){
            Collection<Part> parts = req.getParts();
            for(Part p : parts){
                if("file".equals(p.getName()) && p.getSize() > 0){
                   //DB성공 후에

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
