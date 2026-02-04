package com.study.util;

import com.study.model.Attachment;
import jakarta.servlet.http.Part;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Logger;

// 파일을 저장하고 삭제하는 메서드 클래스
public class UploadFileUtil {

    private static final Logger log = Logger.getLogger(UploadFileUtil.class.getName());

    //저장 위치
    public static final String MAC_SAVE_PATH = "/Users/smk/IT_DATAS/ebrain_temp";
    private static final String WIN_SAVE_PATH = "";

    //파일 저장 메서드
    public static List<Attachment> saveFile(Collection<Part> files){

        //첨부파일 없음, 파일저장 로직 불필요
        if(files.isEmpty()){
            return Collections.emptyList();
        }

        // 기본 저장경로 생성
        // 왜? 기본과 서브를 따로 ?
        Path rootDir = Paths.get(MAC_SAVE_PATH);
        try {
            Files.createDirectories(rootDir);
        } catch (IOException e) {
            throw new UncheckedIOException("루트 파일저장 경로 생성 실패", e);
        }

        // 파일 객체화 및 실제 하드에 저장
        List<Attachment> attachList = new ArrayList<>();
        for(Part file : files){
            Attachment att = new Attachment();

            //물리저장용 파일이름 설정 (ex. randomUUID.jpg)
            String ext = file.getSubmittedFileName().substring(file.getSubmittedFileName().lastIndexOf("."));
            String uuidName = UUID.randomUUID().toString().replace("-","") + ext;
            //log.info("uuidName : " + uuidName);

            att.setOriginName(file.getSubmittedFileName());
            att.setStoredName(uuidName);
            att.setFileType(file.getContentType());
            att.setFileSize(file.getSize());
            att.setFileExt(ext);

            //상대경로 생성 및 추가
            String subFilePath = createLogicalPath();
            att.setFilePath(subFilePath);

            attachList.add(att);

            //실제 파일 저장
            String realPath = rootDir + File.separator + subFilePath;
            String filePath = realPath + File.separator + uuidName;

            try (InputStream fis = file.getInputStream();
                 OutputStream fos = new FileOutputStream(filePath); )
            {
                byte[] buf =  new byte[1024];
                int len = 0;

                while((len = fis.read(buf, 0, 1024))!= -1){
                    fos.write(buf, 0, len);
                }

            } catch (IOException e){
                throw new UncheckedIOException("첨부파일 저장 실패 : ", e);
            }

        }

        return attachList;
    }

    //파일 삭제 메서드


    // 상대 경로 생성 (업로드 날짜 기반)
    private static String createLogicalPath(){

        LocalDateTime date = LocalDateTime.now();
        log.info("LocalDateTime.now() : " + date);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String[] paths = dtf.format(date).split("/");
        log.info("paths[] : " + Arrays.toString(paths));

        String result = paths[0] + File.separator + paths[1] + File.separator + paths[2];

        //실제 폴더 생성
        Path folder = Paths.get(MAC_SAVE_PATH)
                .resolve(result);
        try {
            Files.createDirectories(folder);
        } catch (IOException e) {
            throw new UncheckedIOException("서브 파일저장 경로 생성 실패", e);
        }

        return result;
    }


}
