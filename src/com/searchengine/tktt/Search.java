package com.searchengine.tktt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
		List<String> docResult = new ArrayList<String>();
		List<Integer> docIdResult = new ArrayList<Integer>();
		List<Float> timeSearch = new ArrayList<Float>();
		List<Integer> numResult = new ArrayList<Integer>();
		Map<Integer, Integer> lengthDoc = DocumentIndex.lengthDoc;
		Map<Integer, Float>  score = new TreeMap<Integer, Float> ();
		String path = StaticVariable.INPUTPATH;
		int numDoc = DocumentIndex.numDoc;
		int t = 1, tf, df, count = 0;
		float idf;
		
		//System.out.println("Num doc = " + numDoc);
		long startTime = System.currentTimeMillis();
		
		//TAO MANG SCORE CHO N TAI LIEU
		for(int i=1; i<=numDoc ; i++){
			score.put(i, (float) 0);
		}
		
		//TACH TU CHO INPUT
		String words_input[] = input.split(" ");
		int len_input = words_input.length;
		
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
			path = StaticVariable.INPUTPATH;
			System.out.println("Sorted = " + sortedEntries.get(i).getKey() + " " + sortedEntries.get(i).getValue());
			if(sortedEntries.get(i).getValue() != 0 && i <= k){
				//HIEN THI 1 SO CAU TIEU BIEU
				path = path + sortedEntries.get(i).getKey() + ".txt";
				docResult.add(gettextSumerizer(getSentences(path), words_input));
				docIdResult.add(sortedEntries.get(i).getKey());
			}
			if(sortedEntries.get(i).getValue() != 0){
				count++;
			}
		}
		
        long endTime = System.currentTimeMillis();
        float duration = (float) (endTime - startTime) / 1000;
        System.out.print("Thời gian tìm kiếm: " + duration);
        
        timeSearch.add(duration);
        numResult.add(count);
		
        ServletContext applicationObject = getServletConfig().getServletContext();
        applicationObject.setAttribute("docResult", docResult);
        applicationObject.setAttribute("docIdResult", docIdResult);
        applicationObject.setAttribute("timeSearch", timeSearch);
        applicationObject.setAttribute("numResult", numResult);
        applicationObject.setAttribute("input", input);
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

	
	/**
	 * 
	 * @return MANG CAC CAU TRONG TAI LIEU
	 * @throws IOException
	 */
	public static String[] getSentences(String path) throws IOException{
        List<String> document= new ArrayList<String>();
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        String str= new String(encoded, "UTF-8");
        
        List<String> sentences= new ArrayList<String>();
        
        BreakIterator border = BreakIterator.getSentenceInstance(Locale.US);
        border.setText(str);
        int start = border.first();
        
        for (int end = border.next(); end != BreakIterator.DONE; start = end, end = border.next()) {
            sentences.add(str.substring(start,end));
        }
        String[] strarray = new String[sentences.size()];
        sentences.toArray(strarray );
        return strarray;
	}
	
	/**
	 * 
	 * @param Sentences: MANG CAC CAU CUA 1 TAI LIEU
	 * @param wordsQuery: MANG TU CUA CAU TRUY VAN
	 * @return
	 */
    public static String gettextSumerizer(String[] Sentences, String[] wordsQuery){
        int[] counts = new int[Sentences.length];
        int count = 0;
        
        //VOI MOI CAU TRONG TAI LIEU
        for(int i=0;i<Sentences.length;i++){
            count = 0;
            //VOI MOI TU TRONG CAU TRUY VAN
            for(int j=0;j<wordsQuery.length;j++){
                String regex = "\\b"+wordsQuery[j].toLowerCase()+"\\b";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(Sentences[i].toLowerCase());
                if(matcher.find())
                    count++;
            }
            counts[i] = count;
        }
        
        //SAP XEP THEO SO LAN XUAT HIEN CUA TU TRONG CAU
        for(int i=0;i<counts.length-1;i++){
        	for(int j=i+1;j<counts.length;j++){
        		if(counts[i]<counts[j]){
        			int t=counts[i];
                    counts[i]=counts[j];
                    counts[j]=t;

                    String temp=Sentences[i];
                    Sentences[i]=Sentences[j];
                    Sentences[j]=temp;
                }
            }
        }
        
        String snippet = "";
        //LAY 3 CAU GAN GIONG VOI CAU TRUY VAN NHAT
        int numResult = 3;
        if(counts.length <= 3)
        	numResult = counts.length;
        
        for(int i=0 ; i<numResult ; i++){
            if(counts[i] != 0){
                String s = Sentences[i];
                
                //TACH TU TRONG 1 CAU
                String[] words = s.split("\\s+");
                for(int j=0 ; j<wordsQuery.length ; j++){
                    for(int k=0 ; k<words.length ; k++){
                        String lower= words[k].toLowerCase();
                        String regex = "\\b"+wordsQuery[j].toLowerCase()+"\\b";
                        Pattern pattern = Pattern.compile(regex);
                        Matcher matcher = pattern.matcher(lower);
                        if(matcher.find()){
                            words[k] = "<span style='color:blue; font-weight: bold'>" + words[k] + "</span>";
                        }
                    }
                }
                
                s = "";
                for(int k=0 ; k<words.length ; k++){
                    if(k != words.length-1){
                        s += words[k] + " ";
                    }else{
                        s += words[k];
                    }
                }
                
                snippet += s + "...";
            }
        }
        return snippet;
        
    }
}
