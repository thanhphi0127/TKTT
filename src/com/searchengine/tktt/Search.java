package com.searchengine.tktt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.print.attribute.HashAttributeSet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Search
 */
@WebServlet("/Search")
public class Search extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Search() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		


		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		String input = request.getParameter("input_value");
		System.out.println("Input: " + input);
		
		resultQuery(input, response);
	}
	
	//////
	
	private void resultQuery(String input, HttpServletResponse response) throws IOException {
		Map<String, Map<Integer, Integer>> index = InvertedIndex.InvertedIndex;
		Map<Integer, Integer> posting = new HashMap<Integer, Integer>();
		Set<Integer> posResult = new HashSet<Integer>();
		
		//Tách từ cho câu truy vấn
		String tokenQuery = StaticVariable.jvnTextPro.wordSegment(input);
		System.out.println("Token: " + tokenQuery);
		
		//Tach moi tu ra mang
		String words[] = tokenQuery.split(" ");
		int len = words.length;
		System.out.println("Len = " + len);
		
		
		//Tim trong chi muc
		for(int i=0 ; i<len ; i++){
			//Tim trong moi index
	        for(String token : index.keySet()){
	            if(words[i].equals(token)){
	            	posting = (Map<Integer, Integer>)index.get(token);
	            	
	            	//Moi term thi them tat cac doc
	            	for(Integer p : posting.keySet()){
	            		posResult.add(p);
	            		System.out.println(p + " ");
	            	}
	                break;
	            }
	        }
		}
		
		System.out.println("Pos search = " + posResult.size());
		
        ServletContext applicationObject = getServletConfig().getServletContext();
        applicationObject.setAttribute("posResult", posResult);
        
        response.sendRedirect("http://localhost:8080/TKTT/search.jsp");
	}

}
