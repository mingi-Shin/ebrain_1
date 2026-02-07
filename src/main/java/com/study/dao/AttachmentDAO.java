package com.study.dao;

import com.study.connection.ConnectionTest;
import com.study.model.Attachment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

//첨부파일 관련 메서드
public class AttachmentDAO {

    private static final Logger log = Logger.getLogger(AttachmentDAO.class.getName());

    //싱글톤
    private static final AttachmentDAO instance = new AttachmentDAO();
    public static AttachmentDAO getInstance(){
        return instance;
    }

    //첨부파일 INSERT

    /**
     * 첨부파일 insert 메서드
     * @param attList 첨부파일 리스트
     * @param conn
     * @param boardSeq
     * @throws SQLException
     */
    public void insertAttachment(Collection<Attachment> attList, Connection conn, Long boardSeq) throws SQLException {
        String insertAttQuery = "INSERT INTO attachment (board_seq, origin_name, stored_name, file_path, file_size, file_ext, file_type) VALUES(?, ?, ?, ?, ?, ?, ?);";

        try(PreparedStatement pstmt = conn.prepareStatement(insertAttQuery);) {

            for(Attachment att : attList){
                pstmt.setLong(1, boardSeq);
                pstmt.setString(2, att.getOriginName());
                pstmt.setString(3, att.getStoredName());
                pstmt.setString(4, att.getFilePath());
                pstmt.setLong(5, att.getFileSize());
                pstmt.setString(6, att.getFileExt());
                pstmt.setString(7, att.getFileType());

                //pstmt.executeUpdate(); //한번할때마다 DB와 왕복통신

                //배치로 처리
                pstmt.addBatch(); //SQL을 JDBC 드라이버 내부에 저장
                int[] result = pstmt.executeBatch(); //한번에 DB로 전송 -> DB 왕복 최소화
                for(int r : result){
                    if (r == 0) throw new SQLException("첨부파일 등록 실패");
                }

            }

        }
    }

    /**
     * 게시물 수정시 첨부파일 수정하는 메서드
     * @param newAttList 새로 추가할 첨부파일
     * @param conn
     * @param boardSeq 수정하는 게시물 pk
     * @param deleteAttachmentSeq 논리적 삭제할 게시물 pk
     */
    public void updateAttachment(Collection<Attachment> newAttList, Connection conn, Long boardSeq, Collection<Long> deleteAttachmentSeq){
        //newAttList는 추가하고
        //deleteAttachmentSeq는 논리적 삭제하고
        //boardSeq는 조건절에 활용

    }

    public List<Attachment> selectAttList(Long boardSeq, Connection conn) throws SQLException {
        List<Attachment> attList = new ArrayList<>();

        String sql = "SELECT * FROM attachment WHERE board_seq = ? AND deleted_at IS NULL ";

        try(PreparedStatement pstmt = conn.prepareStatement(sql);){

            pstmt.setLong(1, boardSeq);

            try(ResultSet rs = pstmt.executeQuery();){
                while (rs.next()){
                    Attachment att = new Attachment();
                    att.setBoardSeq(boardSeq);
                    att.setAttachmentSeq(rs.getLong("attachment_seq"));
                    att.setOriginName(rs.getString("origin_name"));
                    att.setStoredName(rs.getString("stored_name"));
                    att.setFilePath(rs.getString("file_path"));
                    att.setFileType(rs.getString("file_type"));
                    att.setFileExt(rs.getString("file_ext"));
                    att.setFileSize(rs.getLong("file_size"));
                    att.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

                    attList.add(att);
                }
            }
        }
        return attList;
    }

    //파일 조회
    public Attachment selectAttachment(Long attachmentSeq) throws Exception {
        Attachment att = new Attachment();

        String sql = "SELECT * FROM attachment WHERE attachment_seq = ? ";

        try(Connection conn = ConnectionTest.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
        ){
            pstmt.setLong(1, attachmentSeq);

            try(ResultSet rs = pstmt.executeQuery();){
                while(rs.next()){

                    att.setAttachmentSeq(rs.getLong("attachment_seq"));
                    att.setOriginName(rs.getString("origin_name"));
                    att.setStoredName(rs.getString("stored_name"));
                    att.setFilePath(rs.getString("file_path"));
                    att.setFileType(rs.getString("file_type"));
                    att.setFileExt(rs.getString("file_ext"));
                    att.setFileSize(rs.getLong("file_size"));
                    att.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

                }
            }
        }

        return att;
    }




}
