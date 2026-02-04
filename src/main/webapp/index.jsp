<%@ page import="com.study.connection.ConnectionTest" %>
<%@ page import="java.sql.Connection" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>JSP - Hello World</title>
</head>
<body>
<h1><%= "Hello World!" %>
</h1>
<br/>
<a href="${pageContext.request.contextPath}/board/list">게시판 둘러보기</a>
<hr>

<%

    ConnectionTest t = new ConnectionTest();
    try(Connection conn = t.getConnection()){
        out.println("DB연결됨 : ");
        out.println(conn);

    } catch (Exception e){
        out.print("DB연결 실패 : " + e.getMessage());
    }

%>

<hr>
<a href="${pageContext.request.contextPath}/codeTest">코드 테스트하기</a>

</body>
</html>
