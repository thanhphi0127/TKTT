<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="com.searchengine.tktt.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Trang chủ</title>
<meta name="keywords" content="" />
<meta name="description" content="" />
<link href="http://fonts.googleapis.com/css?family=Source+Sans+Pro:200,300,400,600,700,900" rel="stylesheet" />
<link href="default.css" rel="stylesheet" type="text/css" media="all" />
<link href="fonts.css" rel="stylesheet" type="text/css" media="all" />
</head>
<body>

<% if(!StaticVariable.flag){
	StaticVariable.init();
	StaticVariable.flag = true;
   }
%>

<div id="header-wrapper">
	<div id="header" class="container">
		<div id="logo">
			<h1></span><a href="index.jsp">TÌM KIẾM THÔNG TIN</a></h1>
		</div>
		<div id="menu">
			<ul>
				<li class="current_page_item"><a href="index.jsp"" accesskey="1" title="">Trang chủ</a></li>
				<li><a href="search.jsp" accesskey="2" title="">Tìm kiếm</a></li>
				<li><a href="invertedindex.jsp" accesskey="3" title="">Lập chỉ mục</a></li>
				<li><a href="index" accesskey="5" title="">Giới thiệu</a></li>
			</ul>
		</div>
	</div>
</div>
<div id="header-featured">
	<div id="banner" class="container"> </div>
</div>

<div id="wrapper">
	<div id="featured-wrapper">
		<h3 id="group">THÀNH VIÊN NHÓM</h3>
		<div class="extra2 container">
			<div class="ebox1"> <img class="imgRadius" src="images/mrphi.jpg"></img>
				<div class="title">
					<h2>Nguyễn Thanh Phi</h2>
					<span class="byline">Mail: thanhphi0127@gmail.com</span> </div>
				<p>SĐT: 0986.223.165</p>
			</div>
			<div class="ebox2"><img class="imgRadius" src="images/missnha.jpg"></img>
				<div class="title">
					<h2>Huỳnh Thanh Nhã</h2>
					<span class="byline">Mail: nhathanh36@gmail.com</span> </div>
				<p>SĐT: 0168.35.36.544</p>
			</div>
			
			<div class="ebox1"> <img class="imgRadius" src="images/mrnguyen.jpg"></img>
				<div class="title">
					<h2>Trương Hoàng Nguyên</h2>
					<span class="byline">Mail: @gmail.com</span> </div>
				<p>SĐT: </p>
			</div>
			<div class="ebox2"><img class="imgRadius" src="user.jpg"></img>
				<div class="title">
					<h2>Nguyễn Quốc Khánh</h2>
					<span class="byline">Mail: @gmail.com</span> </div>
				<p>SĐT: </p>
			</div>
		</div>
	</div>
</div>
<div id="copyright" class="container">
	<p>&copy; Copyright | Designed by <a href="http://www.thanhphi.890m.com">Thanh Phi</a>.</p></div>
</body>
</html>
