<%--
  Created by IntelliJ IDEA.
  User: SHIN_Arthur
  Date: 26. 2. 2.
  Time: 오후 3:17
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>EbrainStudy | 게시물 목록</title>
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
        color: #007bff;
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
                    <input type="date" id="startDate" name="startDate" value="${startDate}">
                    ~
                    <input type="date" id="endDate" name="endDate" value="${endDate}">
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
                    <button id="searchBtn">검색</button>
                </div>
            </div>
        </form>
    </section>

    <!-- 게시물 리스트 컨테이너 -->
    <section class="boardList-section">

        <div>${boardList == null ? "게시물이 없습니다." : boardList}</div>


    </section>

    <section class="pagination-section">
        <nav class="pagination-nav">
            <ul class="pagination-ul">

                <!-- 맨앞 -->
                <c:if test="${page > 1}">
                    <li>
                        <a href="?page=1
                            &categorySeq=${categorySeq}
                            &searchWord=${searchWord}
                            &startDate=${startDate}
                            &endDate=${endDate}">
                            &lt;&lt;
                        </a>
                    </li>
                </c:if>

                <!-- 이전 -->
                <c:if test="${page > 1}">
                    <li>
                        <a href="?page=${page - 1}
                            &categorySeq=${categorySeq}
                            &searchWord=${searchWord}
                            &startDate=${startDate}
                            &endDate=${endDate}">
                            &lt;
                        </a>
                    </li>
                </c:if>

                <!-- 페이지 번호 -->
                <c:forEach var="pageNo" begin="${startPage}" end="${endPage}">
                    <li>
                        <a href="?page=${pageNo}
                            &categorySeq=${categorySeq}
                            &searchWord=${searchWord}
                            &startDate=${startDate}
                            &endDate=${endDate}"
                           class="${pageNo == page ? 'active' : ''}">
                                ${pageNo}
                        </a>
                    </li>
                </c:forEach>

                <!-- 다음 -->
                <c:if test="${page < totalPage}">
                    <li>
                        <a href="?page=${page + 1}
                            &categorySeq=${categorySeq}
                            &searchWord=${searchWord}
                            &startDate=${startDate}
                            &endDate=${endDate}">
                            &gt;
                        </a>
                    </li>
                </c:if>

                <!-- 맨뒤 -->
                <c:if test="${page < totalPage}">
                    <li>
                        <a href="?page=${totalPage}
                            &categorySeq=${categorySeq}
                            &searchWord=${searchWord}
                            &startDate=${startDate}
                            &endDate=${endDate}">
                            &gt;&gt;
                        </a>
                    </li>
                </c:if>

            </ul>
        </nav>
    </section>




    <div class="write-button">
        <button type="button" onclick="location.href='/board/new'">등록</button>
    </div>

</main>


</body>
</html>
