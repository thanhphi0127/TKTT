<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@page import="java.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Tìm kiếm</title>
<meta name="keywords" content="" />
<meta name="description" content="" />
<link href="http://fonts.googleapis.com/css?family=Source+Sans+Pro:200,300,400,600,700,900" rel="stylesheet" />
<link href="default.css" rel="stylesheet" type="text/css" media="all" />
<link href="fonts.css" rel="stylesheet" type="text/css" media="all" />

</head>
<body>
<%
String value = "";
try{
	ServletContext applicationObject = getServletConfig().getServletContext();
	value = (String)applicationObject.getAttribute("input");
} catch(Exception ex){

}
%>
<div id="header-wrapper">
	<div id="header" class="container">
		<div id="logo">
			<h1></span><a href="index.jsp">Search Engine</a></h1>
		</div>
		<div id="menu">
			<ul>
				<li class="current_page_item"><a href="#" accesskey="1" title="">Trang chủ</a></li>
				<li><a href="search.jsp" accesskey="2" title="">Tìm kiếm</a></li>
				<li><a href="invertedindex.jsp" accesskey="3" title="">Lập chỉ mục</a></li>
				<li><a href="introduction.jsp" accesskey="5" title="">Giới thiệu</a></li>
			</ul>
		</div>
	</div>
</div>
<div id="header-featured">
	<div id="banner" class="container"> </div>
</div>

<div id="wrapper">
	<div id="featured-wrapper">
		<div id="featured" class="extra2 margin-btm container">
			<form class="form-wrapper cf" method="post" action="Search">
				<input style="font:'Arial'" type="text" name="input_value" placeholder="Nhập từ khóa..." required value="<%=value%>">
				<button style="font:'Arial'" type="submit">Tìm kiếm</button>
			</form>
		</div>
		
		<div>
			<%
				try{
					ServletContext applicationObject=getServletConfig().getServletContext();
					List<String> result = (List<String>) applicationObject.getAttribute("docResult");
					List<Integer> resultId = (List<Integer>) applicationObject.getAttribute("docIdResult");
					List<Float> timeSearch = (List<Float>) applicationObject.getAttribute("timeSearch");
					List<Integer> numResult = (List<Integer>) applicationObject.getAttribute("numResult");
					
					if(numResult.get(0) > 0){
						out.println("<div class = 'result'>Khoảng " + numResult.get(0) + " kết quả (" + timeSearch.get(0) + " giây) </div>");
						out.println("<div class = 'result_input'>Hiển thị kết quả cho " + 1 + "</div>");
					}

					System.out.println(result.size());
					if(result.size() != 0){
						Iterator iterator = result.iterator(); 
						Iterator iteratorId = resultId.iterator(); 
						while (iterator.hasNext()){
							out.println("<div class='rtitle'><a href=''>Tiêu đề nội dung tìm kiếm " + iteratorId.next() +"</a></div>");
							out.println("<div class='rcontent'> " + iterator.next() + "</div>");
							
							//out.println("<div> Doc: " + iterator.next() + "</div>");  
						}
					} else{
						out.println("Không tìm thấy kết quả");
					}
				}catch(Exception ex){
					//out.println(ex);
				}
			%>
		</div>
	</div>
</div>



<div id="copyright" class="container">
	<p>&copy; Copyright | Designed by <a href="http://www.thanhphi.890m.com">Thanh Phi</a>.</p>
</div>
</body>
</html>
