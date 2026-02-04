package com.study.controller;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.logging.Logger;

//테스트용
@WebServlet("/codeTest")
public class CodeTestController extends HttpServlet {

    private static final Logger log =
            Logger.getLogger(CodeTestController.class.getName());


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        //log.info(System.getProperty("os.name")); //Windows 10

        LocalDateTime date = LocalDateTime.now();
        log.info("LocalDateTime.now() : " + date);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {


    }


}
