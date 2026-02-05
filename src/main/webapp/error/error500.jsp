<%--
  Created by IntelliJ IDEA.
  User: smk
  Date: 26. 2. 5.
  Time: 오후 8:05
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<html>
<head>
    <title>서버 오류 | EbrainStudy</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f8f8f8;
            margin: 0;
            padding: 0;
        }

        main {
            width: 60%;
            margin: 100px auto;
            padding: 30px;
            border: 1px solid #ccc;
            background-color: #fff;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            text-align: center;
        }


        a {
            text-decoration: none;
            color: #2980b9;
            font-weight: bold;
        }

        a:hover {
            text-decoration: underline;
        }

    </style>
</head>
<body>
<main>
    <h1>서버 오류가 발생했습니다 (500)</h1>
    <p>죄송합니다. 요청을 처리하는 도중 서버에서 문제가 발생했습니다.</p>
    <p><a href="<%= request.getContextPath() %>/board/list">게시물 목록으로 돌아가기</a></p>


</main>
</body>
</html>
