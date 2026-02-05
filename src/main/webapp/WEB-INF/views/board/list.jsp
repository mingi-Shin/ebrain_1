<%--
  Created by IntelliJ IDEA.
  User: SHIN_Arthur
  Date: 26. 2. 2.
  Time: 오후 3:17
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<html>
<head>
    <title>EbrainStudy | 게시물 목록</title>

    <script type="application/javascript">
        document.addEventListener("DOMContentLoaded", ()=>{

            submitSearchForm();
            saveUrlBeforeMove();

        });

        //검색 전에 날짜 검증하기
        function submitSearchForm(){
            const submitBtn = document.getElementById('searchBtn');
            submitBtn.addEventListener('click', (e) => {
                e.preventDefault();

                //날짜값 검증
                const startDateInput = document.getElementById('startDate');
                const endDateInput = document.getElementById('endDate');
                let startDate= new Date(startDateInput.value);
                let endDate = new Date(endDateInput.value);
                if(startDate > endDate){
                    alert("시작날짜가 종료일보다 뒤에 있을 수 없습니다.");
                    const todayStr = new Date().toISOString().slice(0, 10);
                    startDateInput.value = todayStr;
                    endDateInput.value = todayStr;
                    return false;
                }

                const form = document.getElementById('searchForm');
                form.submit();

            });
        }

        //페이지 이동 전, 세션스토리지에 url 저장
        function saveUrlBeforeMove(){
            const btn = document.getElementById('write-button');
            if(btn){
                btn.addEventListener('click', ()=>{
                    sessionStorage.setItem("beforeUrl", window.location.href);
                    location.href="/board/new";
                })
            }

            document.querySelectorAll('.detail-link').forEach((link)=>{
                link.addEventListener('click', () => {
                    sessionStorage.setItem("beforeUrl", window.location.href);
                })
            })
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

    .search-section {
        border-color: black;
        border: solid;
        border-width: 1px;
    }

    .search-section form {
        margin: 20px auto;
    }

    .search-controls {
        display: flex;
        justify-content: space-evenly;
    }

    .boardList-section {
        min-height: 20%;
    }

    .pagination-section {
        text-align: center;
    }

    /* 페이징 영역 전체 */
    .pagination-section {
        text-align: center;       /* 중앙 정렬 */
        margin: 30px 0;           /* 위아래 여백 */
    }
    /* 활성 페이지 */
    .pagination-ul li a.active {
        background-color: #007bff;
        color: #fff;
        border-color: #007bff;
        pointer-events: none; /* 클릭 불가 */
    }
    /* ul 기본 스타일 제거 */
    .pagination-ul {
        list-style: none;
        padding: 0;
        margin: 0;
        display: inline-flex;     /* 가로 정렬 */
        gap: 5px;                 /* 버튼 사이 간격 */
    }

    .write-button {
        display: flex;
        justify-content: flex-end;
        cursor: pointer;
    }

    .search-input {
        min-width: 70%;
    }

    .active {
        font-size: 16px;
        color: cornflowerblue;
    }

    table {
        border: solid 1px black;
        width: 100%;
    }
    table th, td {
        border: solid 1px black;
        text-align: center;
        vertical-align: middle;
    }



</style>
<body>

<main>
    <h1>자유 게시판 - 목록</h1>

    <!-- 검색/등록 컨테이너 -->
    <section class="search-section">
        <form id="searchForm" method="GET" action="${pageContext.request.contextPath}/board/list">
            <div class="search-controls">
                <div>
                    <label for="startDate">등록일</label>
                    <input type="date" id="startDate" class="date-select" name="startDate" value="${startDate}">
                    ~
                    <input type="date" id="endDate" class="date-select" name="endDate" value="${endDate}">
                </div>
                <div style="min-width: 50%">
                    <select id="select" name="categorySeq">
                        <option value="0" ${(categorySeq == null || categorySeq == 0) ? 'selected' : ''}>전체 카테고리</option>
                        <option value="1" ${categorySeq == 1 ? 'selected' : ''}>JAVA</option>
                        <option value="2" ${categorySeq == 2 ? 'selected' : ''}>Javascript</option>
                        <option value="3" ${categorySeq == 3 ? 'selected' : ''}>Database</option>
                    </select>
                    <input class="search-input" type="text" placeholder="검색어를 입력해 주세요 (제목+작성자+내용)" name="searchWord"
                        value="${searchWord}">
                    <button type="button" id="searchBtn">검색</button>
                </div>
            </div>
        </form>
    </section>

    <!-- 게시물 리스트 컨테이너 -->
    <section class="boardList-section">
        <div id="list-count-div">
            총 <span>${listCount}</span>건
        </div>
        <table >
            <thead>
                <tr>
                    <th>카테고리</th>
                    <th>첨</th>
                    <th>제목</th>
                    <th>작성자</th>
                    <th>조회수</th>
                    <th>등록 일시</th>
                    <th>수정 일시</th>
                </tr>
            </thead>
            <tbody>
                <c:if test="${empty boardList}" >
                    <td colspan="7">등록된 게시물이 없습니다.</td>
                </c:if>
                <c:if test="${not empty boardList}">
                    <c:forEach var="board" items="${boardList}">
                        <tr>
                            <td>
                                <c:choose>
                                    <c:when test="${board.categorySeq == 1}">JAVA</c:when>
                                    <c:when test="${board.categorySeq == 2}">Javascript</c:when>
                                    <c:when test="${board.categorySeq == 3}">Database</c:when>
                                </c:choose>
                            </td>
                            <td>
                                <c:if test="${board.hasAttachment}">&#128206;</c:if>
                                <c:if test="${!board.hasAttachment}"> </c:if>
                            </td>
                            <td>
                                <a href="${pageContext.request.contextPath}/board/detail?boardSeq=${board.boardSeq}" class="detail-link">
                                    <c:choose>
                                        <c:when test="${board.title != null && fn:length(board.title) > 80}">
                                            ${fn:substring(board.title, 0, 80)}...
                                        </c:when>
                                        <c:otherwise>
                                            ${board.title}
                                        </c:otherwise>
                                    </c:choose>
                                </a>
                            </td>
                            <td>${board.username}</td>
                            <td>${board.hit}</td>
                            <!-- fmt는 Date자료형만 가능. El에는 LocalDateTime 안됨. String 쓰면 됨 -->
                            <td>${board.createdAtStr}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${empty board.updatedAtStr }">
                                        -
                                    </c:when>
                                    <c:otherwise>
                                        ${board.updatedAtStr}
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                </c:if>
            </tbody>
        </table>

    </section>

    <section class="pagination-section">
        <nav class="pagination-nav">
            <ul class="pagination-ul">

                <!-- 맨앞 -->
                <c:if test="${page > 1}">
                    <li>
                        <a href="?page=1&categorySeq=${fn:trim(categorySeq)}&searchWord=${fn:trim(searchWord)}&startDate=${fn:trim(startDate)}&endDate=${fn:trim(endDate)}">&lt;&lt;</a>
                    </li>
                </c:if>

                <!-- 이전 -->
                <c:if test="${page > 1}">
                    <li>
                        <a href="?page=${page - 1}&categorySeq=${fn:trim(categorySeq)}&searchWord=${fn:trim(searchWord)}&startDate=${fn:trim(startDate)}&endDate=${fn:trim(endDate)}">&lt;</a>
                    </li>
                </c:if>

                <!-- 페이지 번호 -->
                <c:forEach var="pageNo" begin="${startPage}" end="${endPage}">
                    <li>
                        <a href="?page=${pageNo}&categorySeq=${fn:trim(categorySeq)}&searchWord=${fn:trim(searchWord)}&startDate=${fn:trim(startDate)}&endDate=${fn:trim(endDate)}"
                           class="${pageNo == page ? 'active' : ''}">${pageNo}</a>
                    </li>
                </c:forEach>

                <!-- 다음 -->
                <c:if test="${page < totalPage}">
                    <li>
                        <a href="?page=${page + 1}&categorySeq=${fn:trim(categorySeq)}&searchWord=${fn:trim(searchWord)}&startDate=${fn:trim(startDate)}&endDate=${fn:trim(endDate)}">&gt;</a>
                    </li>
                </c:if>

                <!-- 맨뒤 -->
                <c:if test="${page < totalPage}">
                    <li>
                        <a href="?page=${totalPage}&categorySeq=${fn:trim(categorySeq)}&searchWord=${fn:trim(searchWord)}&startDate=${fn:trim(startDate)}&endDate=${fn:trim(endDate)}">&gt;&gt;</a>
                    </li>
                </c:if>

            </ul>
        </nav>
    </section>


    <div>
        <input type="text" value="${errorMessage}" id="error-message" hidden="hidden">
    </div>

    <div class="write-button">
        <button type="button" id="write-button">등록</button>
    </div>

</main>


</body>
</html>
