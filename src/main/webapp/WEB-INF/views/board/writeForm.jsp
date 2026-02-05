<%--
  Created by IntelliJ IDEA.
  User: SHIN_Arthur
  Date: 26. 2. 2.
  Time: 오후 3:17
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>EbrainStudy | 게시물 작성</title>

    <script type="application/javascript">
        document.addEventListener('DOMContentLoaded', () => {

            //서버로부터 에러 메시지 수신
            showErrorMessage();

            //검증 통과시 submit
            document.getElementById('submit-button').addEventListener('click', (e)=>{
                e.preventDefault();

                //사용자 입력값 검증 함수 호출
                const isValidated = validateBoardForm();
                if(isValidated){

                    //등록 전에 url 주소 서버에 전송
                    let beforeUrl = sessionStorage.getItem("beforeUrl");
                    if(beforeUrl){
                        document.getElementById('before-url').value = beforeUrl;
                    } else {
                        document.getElementById('before-url').value = "/";
                    }

                    const form = document.getElementById("board-form");
                    form.submit();
                }
            })

            //첨부파일 값 검증
            //validateFile();

            //취소 버튼 누를시 전페이지 이동
            document.getElementById('cancel-button').addEventListener('click', ()=>{
                const isCanceled = confirm('게시물 작성을 취소하시겠습니까?');
                if(isCanceled){
                    let url = sessionStorage.getItem("beforeUrl");
                    location.href=url;
                }
            })

            //파일찾기 input을 커스텀 버튼과 input창으로 대체
            document.querySelectorAll('.search-file-button').forEach((e)=>{
                e.addEventListener('click', ()=>{
                    let number = e.dataset.fileId;

                    const fileInput = document.getElementById("file-input-" + number);
                    fileInput.click();
                    fileInput.addEventListener('change', () => {
                        const fileText = document.getElementById('file-text-' + number);
                        fileText.value = fileInput.value;
                    })

                });
            });



        }); //DOMContentLoaded End

        //사용자 입력값 js검증
        function validateBoardForm(){

            //1.카테고리
            const category =  document.getElementById('select-category');
            const categoryValue = category.value;
            if(!categoryValue){
                alert("카테고리를 선택해주세요.");
                category.focus();
                return false;
            }

            //2.작성자
            const username = document.getElementById('username')
            const usernameValue = username.value.trim();
            if(usernameValue.length < 3 || usernameValue.length > 4){
                alert("작성자 이름은 3글자이상, 5글자 미만으로 해주세요.");
                username.focus();
                return false;
            }

            //3.비밀번호
            const password = document.getElementById('password');
            const passwordValue = password.value.trim();
            const passwordC = document.getElementById('password-c');
            const passwordCValue = passwordC.value.trim();

            //영문/숫자/특수문자 포함 4자리 이상, 16자리 미만
            const passwordPattern = /^(?=.*[a-z])(?=.*\d)(?=.*[\W_]).{4,15}$/;

            if(passwordValue.length < 4 || passwordCValue.length > 15){
                alert("비밀번호는 4자리 이상, 16자리 미만으로 설정해주세요.");
                password.focus();
                return false;
            }
            if(!passwordPattern.test(passwordValue)){
                alert("비밀번호는 영문, 숫자, 특수문자가 포함되어야합니다.");
                password.focus();
                return false;
            }
            if(passwordValue !== passwordCValue){
                alert("비밀번호가 서로 일치하지 않습니다.");
                passwordC.focus();
                return false;
            }

            //4.제목
            const title = document.getElementById('title');
            const titleValue = title.value.trim();
            if(titleValue.length < 3 || titleValue.length > 99){
                alert("제목은 3자리 이상, 100자리 미만으로 입력해주세요.");
                titleValue.slice(0,100);
                title.focus();
                return false;
            }

            //5.내용
            const content = document.getElementById('text-area');
            const contentValue = content.value;
            if(contentValue.length < 4 || contentValue.length >= 2000){
                alert("내용은 4자 이상, 2000자 미만으로 입력해주세요.");
                contentValue.slice(0, 2000);
                content.focus();
                return false;
            }

            return true;
        }


        //6.첨부파일
        function validateFile(){
            document.querySelectorAll('.file-input').forEach((input)=>{
                input.addEventListener("change", (e)=>{
                    const fileInput = e.target;

                    const file = fileInput.files?.[0]; // ?. -> 앞의값이 null, undefined면 undefined 반환
                    //console.log(file);

                    if(!file){
                        return;
                    }

                    //이미지 파일 여부 확인
                    if(!file.type.startsWith("image/")){
                        alert("이미지 파일만 등록 가능합니다.");
                        input.value = "";
                        return;
                    }

                    //파일 용량 확인
                    const maxSize = 10 * 1024 * 1024;
                    if(file.size > maxSize){
                        alert("파일 용량은 10MB 이하만 가능합니다.");
                        input.value = "";
                        return;
                    }

                    console.log("image OK : ", file.name, file.type, file.size);

                });
            });
        }

        //서버의 에러메시지 수신
        function showErrorMessage(){
            const errorMessage = document.getElementById("error-message").value;
            if(errorMessage){
                alert(errorMessage);
                return false;
            }
        }

    </script>
</head>
<style>
    main {
        width: 80%;
        margin: 0 auto;
        padding: 20px;

        border-color: black;
        border: solid;
        border-width: 1px;
    }

    /* main의 90% */
    main section {
        width: 100%;
        margin: 20px auto;
    }

    table {
        width: 90%;
        border-collapse: collapse;
        table-layout: fixed;  /* td, th width를 고정 비율로 적용 */
        border: black solid 1px;
        border-spacing: 0 10px;
    }

    table th {
        border: 1px solid black;
        width: 20%;              /* 왼쪽 th 20% */
        text-align: left;
        padding: 8px;
        vertical-align: top;     /* textarea나 여러 줄 입력 시 상단 정렬 */
    }

    table td {
        border: 1px solid black;
        width: 80%;              /* 오른쪽 td 80% */
        padding: 8px;
    }

    .text-area {
        width: 100%;             /* td 폭 전체 채우기 */
        height: 120px;
    }

    .password-input-c {
        margin-left: 10px;
    }



    .nav-section {
        margin-top: 10px;
        max-width: 90%;
        display: flex;
        justify-content: space-between;
    }
</style>
<body>
<main>
    <section class="header-section">
        <h1>게시물 작성 </h1>
    </section>

    <section class="form-section">
        <form id="board-form" method="post" enctype="multipart/form-data" action="${pageContext.request.contextPath}/board/new">
            <div>
                <input type="text" value="" id="before-url" name="beforeUrl" hidden="hidden">
            </div>
            <table>
                <tr>
                    <th>카테고리</th>
                    <td>
                        <select id="select-category" name="category">
                            <option value="0" ${restored.categorySeq == 0 ? 'selected' : ''}>전체 카테고리</option>
                            <option value="1" ${restored.categorySeq == 1 ? 'selected' : ''}>JAVA</option>
                            <option value="2" ${restored.categorySeq == 2 ? 'selected' : ''}>Javascript</option>
                            <option value="3" ${restored.categorySeq == 3 ? 'selected' : ''}>Database</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <th>작성자</th>
                    <td>
                        <input type="text" id="username" maxlength="4" name="username" value="${restored.username}" required>
                    </td>
                </tr>
                <tr>
                    <th rowspan="2">비밀번호</th>
                    <td>
                        <input id="password" name="password" type="password" placeholder="비밀번호" maxlength="15" required>
                        <input id="password-c" type="password" placeholder="비밀번호 확인" required>
                    </td>
                </tr>
                <tr>
                    <td>
                        <small>비밀번호는 4자리 이상, 16자리 미만</small><br>
                        <small>영문, 숫자, 특수문자를 포함하셔야합니다.</small>
                    </td>
                </tr>
                <tr>
                    <th>제목</th>
                    <td>
                        <input type="text" id="title" maxlength="99" name="title" value="${restored.title}" required>
                    </td>
                </tr>
                <tr>
                    <th>내용</th>
                    <td>
                        <textarea class="text-area" id="text-area" name="content" maxlength="1999">${restored.content}</textarea>
                    </td>
                </tr>
                <tr>
                    <th rowspan="3">파일첨부</th>
                    <td>
                        <input type="file" id="file-input-1" class="file-input" hidden="hidden" name="file" accept="image/*">
                        <input type="text" id="file-text-1" readonly>
                        <button type="button" data-file-id="1" class="search-file-button">파일 찾기</button>
                    </td>

                </tr>
                <tr>
                    <td>
                        <input type="file" id="file-input-2" class="file-input" hidden="hidden" name="file" accept="image/*">
                        <input type="text" id="file-text-2" readonly>
                        <button type="button" data-file-id="2" class="search-file-button">파일 찾기</button>
                    </td>

                </tr>
                <tr>
                    <td>
                        <input type="file" id="file-input-3" class="file-input" hidden="hidden" name="file" accept="image/*">
                        <input type="text" id="file-text-3" readonly>
                        <button type="button" data-file-id="3" class="search-file-button">파일 찾기</button>
                    </td>
                </tr>
            </table>

            <div class="nav-section">
                <button id="cancel-button">취소</button>
                <button id="submit-button">저장</button>
            </div>
        </form>

        <div>
            <input type="text" value="${errorMessage}" id="error-message" hidden="hidden">
        </div>

    </section>

</main>
</body>
</html>
