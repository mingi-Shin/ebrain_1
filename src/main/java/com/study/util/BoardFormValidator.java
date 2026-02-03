package com.study.util;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

// 게시물 작성 폼 검증 클래스
public class BoardFormValidator {

    private static final Logger logger = Logger.getLogger(BoardFormValidator.class.getName());

    //폼 타입 검증
    private static final Map<String, Object> validateBoardFormType(HttpServletRequest req, HttpServletResponse res){

    }


    private static final Map<String, Object> validateBoardForm(HttpServletRequest req, HttpServletResponse res) {

    }
}

        // 업로드 폼 2차 검증
        private boolean validateBoardForm(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
            String category = req.getParameter("category"); //FREEBOARD
            String username = req.getParameter("username");
            String password = req.getParameter("password");
            String title = req.getParameter("title");
            String content = req.getParameter("content");

            if(category == null || category.isEmpty()){
                log.info("잘못된 category 값 : " + category);
                req.setAttribute("errorMessage", "카테고리 입력이 잘못되었습니다.");
                req.getRequestDispatcher("/WEB-INF/views//board/writeForm.jsp").forward(req, res); //작동방식 좀더 공부
            }
            // trim()은 옛날 ASCII 기준, strip()은 현대 Unicode 기준
            if(username.strip().length() < 3 || username.strip().length() > 4){
                log.info("잘못된 username 값 : " + username);
                req.setAttribute("errorMessage", "작성자 입력이 잘못되었습니다.");
                req.getRequestDispatcher("/WEB-INF/views//board/writeForm.jsp").forward(req, res);
            }
            //비밀번호 패턴 검증
            boolean isValidated = PasswordValidator.isValid(password);
            if(password.length() < 4 || password.length() > 15 || !isValidated){
                log.info("잘못된 password 값  : " + password);
                req.setAttribute("errorMessage", "비밀번호 입력이 잘못되었습니다.");
                req.getRequestDispatcher("/WEB-INF/views//board/writeForm.jsp").forward(req, res);
            }
            if(title.strip().length() < 4 || title.strip().length() > 99){
                log.info("잘못된 title 값 : " + title);
                req.setAttribute("errorMessage", "제목 입력이 잘못되었습니다.");
                req.getRequestDispatcher("/WEB-INF/views//board/writeForm.jsp").forward(req, res);
            }
            if(content.length() < 4 || content.length() >= 2000){
                log.info("잘못된 content 값 : " + content);
                req.setAttribute("errorMessage", "내용 입력이 잘못되었습니다.");
                req.getRequestDispatcher("/WEB-INF/views//board/writeForm.jsp").forward(req, res);
            }

            // 첨부파일 검사
            // ...

            return true;
        }