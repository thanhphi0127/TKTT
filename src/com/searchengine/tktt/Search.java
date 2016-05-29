package com.searchengine.tktt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.print.Doc;
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
	
	/**
	 * 
	 * @param input: CHUOI TRUY VAN
	 * @param response
	 * @throws IOException
	 */
	private void resultQuery(String input, HttpServletResponse response) throws IOException {
		Map<String, Map<Integer, Integer>> index = InvertedIndex.InvertedIndex;
		Map<Integer, Integer> posting = new HashMap<Integer, Integer>();
		List<Integer> docResult = new ArrayList<Integer>();
		Map<Integer, Integer> lengthDoc = Document.lengthDoc;
		Map<Integer, Float>  score = new TreeMap<Integer, Float> ();
		
		int numDoc = Document.numDoc;
		int t = 1, tf, df;
		float idf;
		
		System.out.println("Num doc = " + numDoc);
		
		//TAO MANG SCORE CHO N TAI LIEU
		for(int i=1; i<=numDoc ; i++){
			score.put(i, (float) 0);
		}
		
		//TACH TU CHO CAU QUY VAN
		String tokenQuery = StaticVariable.jvnTextPro.wordSegment(input);
		System.out.println("Token: " + tokenQuery);
		
		//TACH TU CHO MOI TOKEN
		String words[] = tokenQuery.split(" ");
		int len = words.length;
			
		//VOI MOI TOKEN
		for(int i=0 ; i<len ; i++){
			//TIM TRONG INVERTED INDEX
	        for(String token : index.keySet()){
	            if(words[i].equalsIgnoreCase(token)){
	            	//DANH SACH <ID TAI LIEU, TF>
	            	posting = (Map<Integer, Integer>)index.get(token);
	            	df = posting.size();

	            	//VOI MOI <ID TAI LIEU, TF> TINH score[d] = tf*idf
	            	for(Integer p : posting.keySet()){
	            		tf = posting.get(p);
	            		idf = (float) Math.log((float) numDoc/df);
	            		System.out.println("Vi tri = " + p + " tf = " + tf + " idf =" + idf + " df =" + df);
	            		score.put(p, score.get(p) + tf*idf);
	            	}
	                break;
	            }
	            t++;
	        }
		}
		
		//SCORE[D] = SCORE[D]/LENGTHDOC[D]
		for(int i=1; i<=numDoc ; i++){
			if(lengthDoc.get(i) != 0)
				score.put(i, score.get(i)/lengthDoc.get(i));
			else score.put(i, (float) 0);
		}
		
		//TRA VE TOP K SCORE
		int k = 20;
		List<Entry<Integer, Float>> sortedEntries = entriesSortedByValues(score);
		for(int i=0 ; i<sortedEntries.size() ; i++){
			System.out.println("Sorted = " + sortedEntries.get(i).getKey() + " " + sortedEntries.get(i).getValue());
			if(sortedEntries.get(i).getValue() != 0 && i <= k){
				docResult.add(sortedEntries.get(i).getKey());
			}
		}
		
        ServletContext applicationObject = getServletConfig().getServletContext();
        applicationObject.setAttribute("docResult", docResult);
        
        response.sendRedirect("http://localhost:8080/TKTT/search.jsp");
	}
	
	/**
	 * 
	 * @param map
	 * @return
	 */
	static <K,V extends Comparable<? super V>> List<Entry<K, V>> entriesSortedByValues(Map<K,V> map) {
		List<Entry<K,V>> sortedEntries = new ArrayList<Entry<K,V>>(map.entrySet());
		Collections.sort(sortedEntries, new Comparator<Entry<K,V>>() {
	        @Override
	        public int compare(Entry<K,V> e1, Entry<K,V> e2) {
	            return e2.getValue().compareTo(e1.getValue());
	        }
		});

		return sortedEntries;
	}

}
