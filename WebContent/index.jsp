<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Lập chỉ mục nghịch đảo</title>
</head>
<body>
<h1>Welcome</h1>
	    <form method="post" action="Index" enctype="multipart/form-data">
            <h1 align="center">Lap chi muc nghich dao</h1>
            <table border="1" cellspacing="0" align="center" width="50%">
                <tr>
                    <td>Chon thu muc</td>
                    <td><input  id="file_input" name="file_input" type="file" webkitdirectory directory required=""/></td>
                </tr>
                <tr>
                    <td colspan="2"><input type="submit" value="Xu ly"/></td>
                </tr>
            </table>
        </form>
        
                <%
            try{
                ServletContext applicationObject=getServletConfig().getServletContext();
                Map<String, Map<Integer, Integer>> invertedIndex = (Map<String, Map<Integer, Integer>>) applicationObject.getAttribute("InvertedIndex");
                if(invertedIndex.size()!=0){
                    out.print("<table border='1' cellpadding='0' cellspacing='0' align='center' width='50%'>");
                        out.print("<tr>");
                            out.print("<td colspan='3'><h2 align='center'>Danh sách chỉ mục</h2></td>");
                        out.print("</tr>");
                        out.print("<tr>");
                            out.print("<th>Số thứ tự</th>");
                            out.print("<th>Chỉ mục</th>");
                            out.print("<th>Tài liệu</th>");
                        out.print("</tr>");
                    int count=1;
                    for(String s : invertedIndex.keySet()){
                    	String str = "";
                    	Map<Integer, Integer> posting= invertedIndex.get(s);
                    	for(int item : posting.keySet()){
                    		str += item + " ";
                    	}
                        out.print("<tr>");
                            out.print("<td align='center'>"+count+"</td>");
                            out.print("<td>"+s+"</td>");
                            out.print("<td>"+str+"</td>");
                        out.print("</tr>");
                        count++;
                    }
                    out.print("</table>");
                }
            }
            catch(Exception ex){

            }
        %>
</body>
</html>