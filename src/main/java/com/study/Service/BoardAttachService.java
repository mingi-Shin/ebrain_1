package com.study.Service;

import com.study.dao.AttachmentDAO;
import com.study.dao.BoardDAO;
import com.study.model.Attachment;
import com.study.model.Board;

import java.util.Collection;
import java.util.List;

// BoardInsert와 AttachInsert 작업을 하나로 묶기위한 트랜잭셔널 클래스
public class BoardAttachService {

    //Board, Attachment 테이블 insert 작업 처리
    public void createBoard(Board board, List<Attachment> attList){

        BoardDAO boardDao = new BoardDAO();
        AttachmentDAO attDao = new AttachmentDAO();





    }


}
