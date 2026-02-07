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

            goBackList();

            updateBoard();

            deleteBoard();

            deleteComment();

            insertComment();


        }); // DOMContentLoaded End


        // 목록 버튼 클릭시 이전 페이지로 이동
        function goBackList(){
            document.getElementById('list-button').addEventListener('click', ()=>{
                const url = sessionStorage.getItem("beforeUrl");
                if(!url){
                    location.href = "${pageContext.request.contextPath}/board/list";
                } else {
                    location.href = url;
                }
            });
        }

        // 수정 버튼 클릭시 수정 페이지로 이동
        function updateBoard(){
            // 수정 버튼 클릭시 수정 페이지로 이동
            document.getElementById('edit-button').addEventListener('click', ()=>{
                location.href = "${pageContext.request.contextPath}/board/edit?seq=${board.boardSeq}";
            });
        }

        // 게시물 삭제 버튼 클릭시
        function deleteBoardBtn(){
            document.getElementById('delete-board-button').addEventListener('click', ()=>{
                //게시물 삭제 비밀번호 레이아웃


            });
        }

        // 게시물 삭제 비밀번호 확인 누를시
        function deleteBoardPassword(){
            document.getElementById('board-modal-submit-button').addEventListener('click', ()=>{

                location.href = "${pageContext.request.contextPath}/board/delete?seq=${board.boardSeq}";
            })
        }


        // 댓글 등록 요청
        function insertComment(){
            const commentBtn = document.getElementById('comment-button');
            if(!commentBtn){
                return;
            }
            commentBtn.addEventListener('click', ()=>{
                const comment = document.getElementById('comment-textarea').value;
                if(!comment){
                    alert("댓글을 입력후 등록버튼을 눌러주세요.");
                    return;
                }
                const writer = document.getElementById('comment-writer').value;
                if(!writer){
                    alert("작성자를 입력후 등록버튼을 눌러주세요.");
                    return;
                }
                const password = document.getElementById('comment-password').value;
                if(!password){
                    alert("비밀번호 입력후 등록버튼을 눌러주세요.");
                    return;
                }


                const commentForm = document.getElementById('comment-form');
                commentForm.submit();
            })
        }


        // 댓글 삭제 요청
        function deleteComment(){
            //댓글 삭제 누르면 해당 댓글의 dataset.commentId로 읽어옴
            //이값을

            //action="{pageContext.request.contextPath}/board/{board.boardSeq}/comment/delete/{commentSeq}"

        }



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

    .comment-input-area input {
        width: 100%;
        padding: 10px;
        margin-bottom: 10px;
        border: 1px solid #ddd;
    }

    .write-button {
        display: flex;           /* 가로로 나열 */
        gap: 8px;                /* 입력칸과 버튼 사이 간격 */
    }

    .write-button textarea {
        flex: 9;                 /* 90% 비율 */
        resize: vertical;        /* 세로로만 크기 조절 가능 */
        height: 40px;            /* 버튼과 높이를 맞추기 위한 기본 높이 */
        padding: 8px;
        box-sizing: border-box;  /* padding 포함 너비 계산 */
        font-size: 14px;
    }

    .write-button button {
        flex: 1;                 /* 10% 비율 */
        height: 40px;            /* textarea와 같은 높이 */
        font-size: 14px;
        cursor: pointer;
    }

    .board-password-modal {
        display: none; /* 기본 숨김 */
        position: fixed;
        z-index: 1000;
        left: 0; top: 0;
        width: 100%; height: 100%;
        background-color: rgba(0,0,0,0.5); /* 반투명 배경 */
    }
    .comment-password-modal {
        display: none; /* 기본 숨김 */
        position: fixed;
        z-index: 1000;
        left: 0; top: 0;
        width: 100%; height: 100%;
        background-color: rgba(0,0,0,0.5); /* 반투명 배경 */
    }

    .nav-section {
        display: flex;
        justify-content: center;
        gap: 10px;
    }

    #comment-form {
        display: flex;
        width: 100%;
        gap: 8px;              /* textarea와 버튼 간격 */
        flex-direction: column;
    }

    #comment-textarea {
        flex: 9;               /* 90% */
        resize: none;          /* 크기 조절 비활성화 */
        height: 60px;
        padding: 8px;
    }

    #comment-button {
        flex: 1;               /* 10% */
        height: 60px;
        cursor: pointer;
    }

    .nav-section button {
        width: 80px;
        height: 30px;
        cursor: pointer;
    }



    /* GPT 도와줘! * /

/* 댓글 섹션 전체 */
    .comment-section {
        margin-top: 30px;
        border-top: 1px solid #e5e5e5;
        padding-top: 20px;
        font-size: 14px;
    }

    /* 댓글 하나 */
    .comment-list > div {
        margin-bottom: 12px;
    }

    /* 작성자 / 날짜 */
    .comment-writer-date {
        display: flex;
        gap: 8px;
        color: #666;
        margin-bottom: 4px;
    }

    /* 댓글 내용 */
    .comment-content {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        background: #f9f9f9;
        padding: 10px 12px;
        border-radius: 6px;
    }

    /* 댓글 텍스트 */
    .comment-content p {
        margin: 0;
        line-height: 1.4;
        word-break: break-word;
    }

    /* 삭제 버튼 */
    .delete-comment-button {
        background: none;
        border: none;
        color: #999;
        cursor: pointer;
        font-size: 14px;
    }

    .delete-comment-button:hover {
        color: #d00;
    }

    /* 댓글 작성 영역 */
    .comment-write {
        margin-top: 20px;
    }

    /* 작성자 / 비밀번호 */
    #comment-form > div:first-child {
        display: flex;
        gap: 8px;
        margin-bottom: 8px;
    }

    #comment-writer,
    #comment-password {
        flex: 1;
        padding: 6px 8px;
        border: 1px solid #ccc;
        border-radius: 4px;
    }

    /* textarea + 버튼 */
    #comment-form > div:last-child {
        display: flex;
        gap: 8px;
    }

    #comment-textarea {
        flex: 9;
        resize: none;
        height: 60px;
        padding: 8px;
        border: 1px solid #ccc;
        border-radius: 4px;
    }

    #comment-button {
        flex: 1;
        border: none;
        background: #333;
        color: #fff;
        border-radius: 4px;
        cursor: pointer;
    }

    #comment-button:hover {
        background: #000;
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
        <!-- 작성자, 날짜 행 -->
        <div class="writer-date" style="display: flex; justify-content: space-between;">
            <div class="writer">
                ${board.username}
            </div>
            <div class="date">
                <span style="margin-right: 20px;">등록일시 : ${board.createdAtStr}</span>
                <span>수정일시 :
                    <c:choose>
                        <c:when test="${empty board.updatedAtStr}">--</c:when>
                        <c:otherwise>${board.updatedAtStr}</c:otherwise>
                    </c:choose>
                </span>
            </div>
        </div>
        <br>
        <!-- 카테고리, 제목, 조회수 행 -->
        <div class="category-title-hit" style="display: flex; justify-content: space-between;">
            <div class="category" style="font-size: 20px; margin-right: 20px;">
                <c:choose>
                    <c:when test="${board.categorySeq == 1}">[JAVA]</c:when>
                    <c:when test="${board.categorySeq == 2}">[Javascript]</c:when>
                    <c:when test="${board.categorySeq == 3}">[Database]</c:when>
                    <c:otherwise>[미정]</c:otherwise>
                </c:choose>
                <span style="margin-right: 20px; font-size: 25px;">${board.title}</span>
            </div>
            <div class="hit">
                조회수: ${board.hit}
            </div>
        </div>

        <hr>
        <!-- 내용 행-->
        <div class="content" style="margin: 20px auto;">
            ${board.content}
        </div>

        <hr>

        <!-- 첨부파일 -->
        <div class="attachment" style="margin-bottom: 30px;">
            <c:if test="${empty attachments}">
                <p>첨부파일이 존재하지 않습니다.</p>
            </c:if>
            <c:if test="${not empty attachments}">
                <small>첨부파일 DB 난수도입 고려 중 </small><br>
                <c:forEach var="att" items="${attachments}" >
                    <div>
                        <a href="${pageContext.request.contextPath}/board/${board.boardSeq}/attachment/download/${att.attachmentSeq}"> &#x1F4E5; ${att.originName}</a>
                    </div>
                </c:forEach>
            </c:if>
        </div>

    </section>

    <!-- 댓글 섹션 -->
    <section class="comment-section">
        <!-- 댓글 목록 -->
        <div class="comment-list">
            <c:if test="${not empty comments}">
                <c:forEach var="comment" items="${comments}">
                    <div class="comment-writer-date" style="gap: 10px;">
                        <span><small>${comment.writer}</small></span>
                        <span><small>${comment.createdAtStr}</small></span>
                    </div>
                    <div class="comment-content">
                        <p>${comment.content}</p>
                        <span><button class="delete-comment-button" data-comment-id="${comment.commentSeq}">&#9746;</button></span>
                    </div>
                </c:forEach>
            </c:if>
        </div>

        <!-- 댓글 작성 영역 -->
        <div class="comment-write" >
            <div class="write-button" >
                <form id="comment-form" method="post" action="${pageContext.request.contextPath}/board/${board.boardSeq}/comment/new">
                    <div>
                        <input type="text" name="writer" id="comment-writer" placeholder="작성자 " required>
                        <input type="password" name="password" id="comment-password" placeholder="비밀번호" required>
                    </div>
                    <div>
                        <textarea name="comment" id="comment-textarea" placeholder="댓글을 입력해 주세요." required></textarea>
                        <button type="button" id="comment-button">등록</button>
                    </div>
                </form>
            </div>
        </div>
    </section>

    <!-- 하단 버튼 섹션  -->
    <section class="nav-section">
        <div>
            <button type="button" id="list-button">목록</button>
        </div>
        <div>
            <button type="button" id="edit-button">수정</button>
        </div>
        <div>
            <button type="button" id="delete-board-button">삭제</button>
        </div>
    </section>

    <!-- 게시물 삭제시 비밀번호 확인 모달 -->
    <div class="board-password-modal">
        <form id="board-password-modal-form" method="post" action="${pageContext.request.contextPath}/board/delete/${board.boardSeq}">
            <div>
                <input type="hidden" value="${beforeUrl}" name="beforeUrl">
                <input type="hidden" value="${board.boardSeq}" name="boardSeq">
                <span style="background-color: #666666">비밀번호*</span>
                <span><input type="password" name="password" placeholder="비밀번호를 입력해 주세요."></span>
            </div>
            <div>
                <button type="button" id="board-modal-cancel-button" style="background-color: #666666">취소</button>
                <button type="button" id="board-modal-submit-button" style="background-color: #007bff">확인</button>
            </div>
        </form>
    </div>

    <!-- 댓글 삭제시 비밀번호 확인 모달 -->
    <div class="comment-password-modal">
        <form id="comment-password-modal-form" method="post" >
            <div>
                <input type="hidden" value="" name="commentPassword" >
                <span style="background-color: #666666">비밀번호*</span>
                <span><input type="password" name="password" placeholder="비밀번호를 입력해 주세요."></span>
            </div>
            <div>
                <button type="button" id="comment-modal-cancel-button" style="background-color: #666666">취소</button>
                <button type="button" id="comment-modal-submit-button" style="background-color: #007bff">확인</button>
            </div>
        </form>
    </div>

    <div>
        <input type="hidden" value="${errorMessage}" id="error-message">
    </div>

</main>
</body>
</html>
