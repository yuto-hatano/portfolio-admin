<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
	<title>Home</title>
</head>
<body>
	<form method="get" action="<%=request.getContextPath()%>/skillupload">
		<div style="padding: 5px;">
			<button type="submit">移動</button>
		</div>
	</form>
<h1>
	Hello world!
</h1>

<P>  The time on the server is ${serverTime}. </P>
</body>
</html>
