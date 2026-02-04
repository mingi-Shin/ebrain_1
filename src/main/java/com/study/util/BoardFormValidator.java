package com.study.util;

import com.study.model.Board;
import jakarta.servlet.http.Part;

import java.util.Collection;
import java.util.logging.Logger;

// 게시물 작성 폼 검증 클래스
public class BoardFormValidator {

    private static final Logger log = Logger.getLogger(BoardFormValidator.class.getName());

    //폼 타입 검증
    public static final ValidationResult validateBoardFormType(String formType){

        if(!formType.startsWith("multipart")){
            return ValidationResult.fail("입력 폼 요청이 multipart/form-data가 아닙니다.");
        }

        return ValidationResult.ok();
    }

    //사용자 입력값 2차 검증 (if문 요약 버전)
    public  static final ValidationResult validateBoardForm(Board boardDto) {

        Long categorySeq = boardDto.getCategorySeq();
        String username = boardDto.getUsername();
        String password = boardDto.getPassword();
        String title = boardDto.getTitle();
        String content = boardDto.getContent();

        //카테고리 검증
        if(categorySeq == 0){
            return ValidationResult.fail("카테고리 입력이 잘못되었습니다.");
        }
        //작성자 검증 : trim()은 옛날 ASCII 기준, strip()은 현대 Unicode 기준
        if(username.strip().length() < 3 || username.strip().length() > 4){
            return ValidationResult.fail("작성자 입력이 잘못되었습니다.");
        }
        //비밀번호 검증
        boolean isValidated = PasswordValidator.isValid(password);
        if(password.length() < 4 || password.length() > 15 || !isValidated){
            return ValidationResult.fail("비밀번호 입력이 잘못되었습니다.");
        }
        //제목 검증
        if(title.strip().length() < 3 || title.strip().length() > 99){
            return ValidationResult.fail("제목 입력이 잘못되었습니다.");
        }
        //내용 검증
        if(content.length() < 4 || content.length() >= 2000){
            return ValidationResult.fail("내용 입력이 잘못되었습니다.");
        }

        return ValidationResult.ok();
    }

    // 첨부파일 검증
    public static final ValidationResult validateFileAttachment(Collection<Part> parts) {

        for(Part p : parts){

            log.info("p : " + p.getSubmittedFileName() + " / " + p.getContentType() + " / " + p.getSize());

            //1. 파일 이미지 여부 검증 (application/octet-stream)
            if(p.getContentType() == null || !p.getContentType().startsWith("image/")){
                return ValidationResult.fail("이미지 파일만 업로드가 가능합니다.");
            }
            //2. 파일 용량 검증 (10MB 이하)
            int maxFileSize = 10 * 1024 * 1024;
            if(p.getSize() > maxFileSize){
                //String fileName = p.getSubmittedFileName();
                return ValidationResult.fail( "첨부파일 용량은 10MB를 넘을수 없습니다.");
            }

        }

        return ValidationResult.ok();
    }


}
