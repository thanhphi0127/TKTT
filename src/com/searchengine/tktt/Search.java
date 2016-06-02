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

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

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
    
    MongoDBConnection db = new MongoDBConnection();

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
	 * @return: TRA VE CHI MUC NGHICH DAO
	 */
	Map<String, Map<Integer, Integer>> getInvertedIndex(){
		try{
			Map<String, Map<Integer, Integer>> resultIndex = new TreeMap<String, Map<Integer,Integer>>();
			Map<Integer, Integer> posting = new HashMap<Integer, Integer>();
			String term = "";
			Document doc = new Document();

			FindIterable<Document> iterable = db.getcollectionInvertedIndex().find();
			for (Document d: iterable){
				for (String s: d.keySet()) {
					if(s.equals("_id")){
						term = (String) d.get(s);	
					}
					//MOI MOI TERM
					if(s.equals("docID_tf")){
						doc = (Document) d.get(s);
						posting = new HashMap<Integer, Integer>();
						for (String str: doc.keySet()) {
							posting.put(Integer.parseInt(str), (Integer) doc.get(str));		//System.out.print("doc" + str + "tf = " + doc.get(str) + " ");
						}
					}
					resultIndex.put(term, posting);
				}
			}
			
			return resultIndex;

		} catch(Exception ex){}
		return null;
	}
	
	/**
	 * 
	 * @return: TRA VE MANG LENGTH[N]
	 */
	Map<Integer, Integer> getLengthIndex(){
		try{
			Map<Integer, Integer> lengthDoc = new TreeMap<Integer, Integer>();
			int term = 0, length = 0;
	
			FindIterable<Document> iterable = db.getCollectionLength().find();
			for (Document d: iterable){
				for (String s: d.keySet()) {
					if(s.equals("_id")){
						term = (Integer) d.get(s);	//System.out.print("Doc id = " + term);
					}
					//MOI MOI TERM
					if(s.equals("length")){
						length = (Integer) d.get(s);  //System.out.print("Length = " + length);	
					}
					lengthDoc.put(term, length);
				}
			}
			
			return lengthDoc;
		} catch(Exception ex){}
		return null;
	}
	
	/**
	 * 
	 * @param input: CHUOI TRUY VAN
	 * @param response
	 * @throws IOException
	 */
	private void resultQuery(String input, HttpServletResponse response) throws IOException {
		long startTime = System.currentTimeMillis();
		Map<String, Map<Integer, Integer>> index = getInvertedIndex();  //InvertedIndex.InvertedIndex; //
		Map<Integer, Integer> posting = new HashMap<Integer, Integer>();
		List<String> docResult = new ArrayList<String>();
		List<Integer> docIdResult = new ArrayList<Integer>();
		List<Float> timeSearch = new ArrayList<Float>();
		List<Integer> numResult = new ArrayList<Integer>();
		Map<Integer, Integer> lengthDoc = getLengthIndex();//DocumentIndex.lengthDoc;
		Map<Integer, Float>  score = new TreeMap<Integer, Float>();
		String path = StaticVariable.INPUTPATH;
		int numDoc = lengthDoc.size(); //DocumentIndex.numDoc;
		int t = 1, tf, df, count = 0;
		float idf;
		
        long endTime = System.currentTimeMillis();
        float duration = (float) (endTime - startTime) / 1000;
        System.out.print("Thời gian lay chi muc tu Mongodb: " + duration);

		//System.out.println("Num doc = " + numDoc);
    	startTime = System.currentTimeMillis();
		
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
	            		//System.out.println("Vi tri = " + p + " tf = " + tf + " idf =" + idf + " df =" + df);
	            		score.put(p, score.get(p) + tf * idf);
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
		int k = 10;
		List<Entry<Integer, Float>> sortedEntries = entriesSortedByValues(score);
		for(int i=0 ; i<sortedEntries.size() ; i++){
			path = StaticVariable.INPUTPATH;
			//System.out.println("Sorted = " + sortedEntries.get(i).getKey() + " " + sortedEntries.get(i).getValue());
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
		
        endTime = System.currentTimeMillis();
        duration = (float) (endTime - startTime) / 1000;
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
	 * @param word
	 * @param cmp
	 * @return
	 */
	public String replaceString(String word, String cmp){
		word = word.trim();
		if(word.length() != 0){
			//Loại bỏ kí tự thừa đầu chuổi
	        while(cmp.contains(String.valueOf(word.charAt(0)))){
	        	word = word.substring(1);
	        	if(word.length() == 0) break;
	        }
		}
	       
		if(word.length() != 0){
	        //Loại bỏ kí tự thừa cuối chuỗi
			while(cmp.contains(String.valueOf(word.charAt(word.length()-1)))){
	        	word = word.substring(0, word.length()-1);
	        	if(word.length() == 0) break;
	        }
		}
		return word;
	}
	
	
	/**
	 * 
	 * @return MANG CAC CAU TRONG TAI LIEU
	 * @throws IOException
	 */
	public String[] getSentences(String path) throws IOException{
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
    public String gettextSumerizer(String[] Sentences, String[] wordsQuery){
        int[] counts = new int[Sentences.length];
        int count = 0, t;
        String temp, regex, lower, s;
        String[] words;
        Pattern pattern;
        Matcher matcher;
        
        //VOI MOI CAU TRONG TAI LIEU
        for(int i=0 ; i<Sentences.length ; i++){
            count = 0;
            //VOI MOI TU TRONG CAU TRUY VAN
            for(int j=0 ; j<wordsQuery.length ; j++){
            	wordsQuery[j] = replaceString(wordsQuery[j], StaticVariable.cmp);
                regex = "\\b" + wordsQuery[j].toLowerCase() + "\\b";
                pattern = Pattern.compile(regex);
                matcher = pattern.matcher(Sentences[i].toLowerCase());
                if(matcher.find()){
                    count++;
                }
            }
            counts[i] = count;
        }
        /*======================================================================================*/
        //SAP XEP GIAM DAN THEO SO LAN XUAT HIEN CUA TU TRONG CAU
        for(int i = 0 ; i<counts.length-1 ; i++){
        	for(int j = i+1 ; j<counts.length ; j++){
        		if(counts[i] < counts[j]){
        			t = counts[i];
                    counts[i] = counts[j];
                    counts[j] = t;

                    temp = Sentences[i];
                    Sentences[i] = Sentences[j];
                    Sentences[j] = temp;
                }
            }
        }
        
        String snippet = "";
        //LAY 3 CAU GAN GIONG VOI CAU TRUY VAN NHAT
        int numResult = 3;
        if(counts.length <= 3){
        	numResult = counts.length;
        }
        
        /*======================================================================================*/
        for(int i=0 ; i < numResult ; i++){
            if(counts[i] != 0){
                s = Sentences[i];
                words = s.split("\\s+");							//TACH TU TRONG 1 CAU
                for(int j = 0 ; j < wordsQuery.length ; j++){		//MOI TU TRONG CAU TRUY VAN
                    for(int k = 0 ; k < words.length ; k++){
                        lower = words[k].toLowerCase();
                        regex = "\\b" + wordsQuery[j].toLowerCase() + "\\b";
                        pattern = Pattern.compile(regex);
                        matcher = pattern.matcher(lower);
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
