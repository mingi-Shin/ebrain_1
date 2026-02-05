<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: SHIN_Arthur
  Date: 26. 2. 6.
  Time: 오후 4:00
  게시물 보기 페이지
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>EbrainStudy | 게시물 보기</title>

    <script type="application/javascript">
        document.addEventListener('DOMContentLoaded', () => {

            // 목록 버튼 클릭시 이전 페이지로 이동
            document.getElementById('list-button').addEventListener('click', ()=>{
                const url = sessionStorage.getItem("beforeUrl");
                if(!url){
                    location.href = "${pageContext.request.contextPath}/board/list";
                } else {
                    location.href = url;
                }
            });

            // 수정 버튼 클릭시 수정 페이지로 이동
            document.getElementById('edit-button').addEventListener('click', ()=>{
                location.href = "${pageContext.request.contextPath}/board/edit?seq=${board.boardSeq}";
            });

            // 삭제 버튼 클릭시 확인 후 삭제
            document.getElementById('delete-button').addEventListener('click', ()=>{
                const isDeleted = confirm('정말 삭제하시겠습니까?');
                if(isDeleted){
                    location.href = "${pageContext.request.contextPath}/board/delete?seq=${board.boardSeq}";
                }
            });

        }); // DOMContentLoaded End

    </script>
</head>
<style>
    /* 메인 컨테이너 - 전체 페이지 감싸는 영역 */
    main {
        width: 80%;
        margin: 0 auto;
        padding: 20px;
        border-color: black;
        border: solid;
        border-width: 1px;
    }

    /* 섹션 - main 내부의 각 영역 */
    main section {
        width: 100%;
        margin: 20px auto;
    }

    /* 테이블 전체 스타일 */
    table {
        width: 90%;
        border-collapse: collapse;
        table-layout: fixed;  /* td, th width를 고정 비율로 적용 */
        border: black solid 1px;
        border-spacing: 0 10px;
    }

    /* 테이블 제목 셀 (왼쪽) */
    table th {
        border: 1px solid black;
        width: 20%;              /* 왼쪽 th 20% */
        text-align: left;
        padding: 8px;
        vertical-align: top;     /* 상단 정렬 */
        background-color: #f5f5f5; /* 구분을 위한 배경색 */
    }

    /* 테이블 내용 셀 (오른쪽) */
    table td {
        border: 1px solid black;
        width: 80%;              /* 오른쪽 td 80% */
        padding: 8px;
    }

    /* 게시물 내용 영역 스타일 */
    .content-area {
        min-height: 200px;       /* 최소 높이 설정 */
        white-space: pre-wrap;   /* 줄바꿈 유지 */
        word-wrap: break-word;   /* 긴 단어 자동 줄바꿈 */
    }

    /* 첨부파일 다운로드 링크 스타일 */
    .file-link {
        display: block;          /* 각 파일을 줄바꿈 */
        margin: 5px 0;
        color: #0066cc;
        text-decoration: none;
    }

    .file-link:hover {
        text-decoration: underline;
    }

    /* 댓글 영역 스타일 */
    .comment-section {
        margin-top: 30px;
        border-top: 2px solid black;
        padding-top: 20px;
    }

    /* 댓글 목록 */
    .comment-list {
        margin: 20px 0;
    }

    /* 개별 댓글 아이템 */
    .comment-item {
        border-bottom: 1px solid #ddd;
        padding: 10px 0;
    }

    /* 댓글 작성자 정보 */
    .comment-header {
        display: flex;
        justify-content: space-between;
        margin-bottom: 5px;
        font-size: 14px;
        color: #666;
    }

    /* 댓글 내용 */
    .comment-content {
        margin: 10px 0;
        line-height: 1.5;
    }

    /* 댓글 입력 영역 */
    .comment-input-area {
        margin-top: 20px;
    }

    .comment-input-area input {
        width: 100%;
        padding: 10px;
        margin-bottom: 10px;
        border: 1px solid #ddd;
    }

    /* 버튼 영역 - 하단 네비게이션 */
    .nav-section {
        margin-top: 10px;
        max-width: 90%;
        display: flex;
        justify-content: space-between;
    }

    /* 왼쪽 버튼 그룹 (목록) */
    .nav-left {
        display: flex;
        gap: 10px;
    }

    /* 오른쪽 버튼 그룹 (수정, 삭제) */
    .nav-right {
        display: flex;
        gap: 10px;
    }

    /* 버튼 공통 스타일 */
    button {
        padding: 8px 16px;
        cursor: pointer;
    }

</style>
<body>
<main>
    <!-- 헤더 섹션 -->
    <section class="header-section">
        <h1>게시판 - 보기</h1>
    </section>

    <!-- 게시물 정보 섹션 -->
    <section class="board-section">
        <table>
            <!-- 작성자 정보 행 -->
            <tr>
                <th>작성자</th>
                <td>${board.username}</td>
            </tr>

            <!-- 등록일시/수정일시 행 -->
            <tr>
                <th>등록일시 / 수정일시</th>
                <td>
                    등록일시: ${board.createdAtStr} / 수정일시: ${board.updatedAtStr}
                    <span style="float: right;">조회수: ${board.hit}</span>
                </td>
            </tr>

            <!-- 카테고리 + 제목 행 -->
            <tr>
                <th>[${board.categorySeq}]</th>
                <td>${board.title}</td>
            </tr>

            <!-- 내용 행 -->
            <tr>
                <th>내용</th>
                <td>
                    <textarea>
                        ${board.content}
                    </textarea>
                </td>
            </tr>

            <!-- 첨부파일 행 -->
            <tr>
                <th>첨부파일</th>
                <td>
                    <!-- 첨부파일이 있을 경우 반복 출력 -->
                    <c:forEach var="file" items="${attachments}">
                        <a href="${pageContext.request.contextPath}/download?fileSeq=${file.attachmentSeq}"
                           class="file-link">
                            📎 ${file.originName}
                        </a>
                    </c:forEach>

                    <!-- 첨부파일이 없을 경우 -->
                    <c:if test="${empty attachments}">
                        첨부파일이 없습니다.
                    </c:if>
                </td>
            </tr>
        </table>
    </section>

    <!-- 댓글 섹션 -->
    <section class="comment-section">
        <h3>댓글</h3>

        <!-- 댓글 목록 -->
        <div class="comment-list">
            <!-- 댓글 샘플 1 -->
            <div class="comment-item">
                <div class="comment-header">
                    <span>맛굼이 좋습니다.</span>
                    <span>2020.03.09 16:32</span>
                </div>
            </div>

            <!-- 댓글 샘플 2 -->
            <div class="comment-item">
                <div class="comment-header">
                    <span>맛굼이 좋습니다. 맛굼이 좋습니다. 맛굼이 좋습니다...</span>
                    <span>2018.03.09 14:23</span>
                </div>
            </div>
        </div>

        <!-- 댓글 작성 영역 -->
        <div class="comment-input-area">
            <input type="text" placeholder="댓글을 입력해 주세요." id="comment-input">
            <span><button id="comment-submit">등록</button></span>
        </div>
    </section>

    <!-- 하단 버튼 네비게이션 -->
    <div class="nav-section">
        <!-- 왼쪽: 목록 버튼 -->
        <div class="nav-left">
            <button id="list-button">목록</button>
        </div>

        <!-- 오른쪽: 수정, 삭제 버튼 -->
        <div class="nav-right">
            <button id="edit-button">수정</button>
            <button id="delete-button">삭제</button>
        </div>
    </div>

</main>
</body>
</html>
