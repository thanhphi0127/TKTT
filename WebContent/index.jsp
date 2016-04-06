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
   } else StaticVariable.flag = true;
%>

<div id="header-wrapper">
	<div id="header" class="container">
		<div id="logo">
			<h1></span><a href="#">Search Engine</a></h1>
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
		<div class="extra2 container">
			<div class="ebox1"> <span class="fa fa-puzzle-piece"></span>
				<div class="title">
					<h2>Etiam rhoncus volutpat</h2>
					<span class="byline">Integer sit amet pede vel arcu aliquet pretium</span> </div>
				<p>Integer nisl risus, sagittis convallis, rutrum id, elementum congue, nibh. Suspendisse dictum porta lectus. Donec placerat odio vel elit. Nullam ante orci, pellentesque eget, tempus quis, ultrices in, est. Sed vel tellus. Curabitur sem urna, consequat vel, suscipit in, mattis placerat, nulla. Sed ac leo. Pellentesque imperdiet. Pellentesque viverra vulputate enim.</p>
				<a href="#" class="button">Etiam posuere</a> </div>
			<div class="ebox2"> <span class="fa fa-comments-o"></span>
				<div class="title">
					<h2>Mauris vulputate dolor</h2>
					<span class="byline">Integer sit amet pede vel arcu aliquet pretium</span> </div>
				<p>Proin gravida orci porttitor enim accumsan lacinia. Donec condimentum, urna non molestie semper, ligula enim ornare nibh, quis laoreet eros quam eget ante. Aliquam libero. Vivamus nisl nibh, iaculis vitae, viverra sit amet, ullamcorper vitae, turpis. Aliquam erat volutpat. Vestibulum dui sem, pulvinar sed, imperdiet nec, iaculis nec, leo. </p>
				<a href="#" class="button">Etiam posuere</a> </div>
		</div>
	</div>
</div>
<div id="copyright" class="container">
	<p>&copy; Untitled. All rights reserved. | Photos by <a href="http://fotogrph.com/">Fotogrph</a> | Design by <a href="http://templated.co" rel="nofollow">TEMPLATED</a>.</p>
</div>
</body>
</html>
