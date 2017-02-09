<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>会话列表</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
  </head>
  
  <body>
	<%
		java.util.Map<String,com.coreService.entity.OneTalk> talks=com.coreService.util.TalkManager.getAllTalk();
		if(talks!=null&&talks.size()>0){
	%>	
		<h2>当前会话数：<%=talks.size()%></h2>
	<%
		}
		else{
	%>
		<h2>当前没有会话</h2>
	<%
		}
	%>
  </body>
</html>