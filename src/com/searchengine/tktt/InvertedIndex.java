/**
 * 
 */
package com.searchengine.tktt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.sun.org.apache.xalan.internal.xsltc.compiler.Template;

/**
 * @author Nguyen Thanh Phi
 *
 */
public class InvertedIndex {
	public static Map<String, Map<Integer, Integer>> InvertedIndex;
	
	/**
	 * 
	 */
	public InvertedIndex(){
		InvertedIndex = new TreeMap<String, Map<Integer, Integer>>();
	}
	
	//Thêm từ vào chỉ mục
	/**
	 * 
	 * @param word
	 * @param idDoc
	 */
	public void insertInvertIndex(String word, int idDoc){
		int termFrequency = 1;
		if(word.length() != 0){
			boolean existWord = InvertedIndex.containsKey(word);
			Map<Integer, Integer> posting = new TreeMap<Integer, Integer>();
			
			//Kiểm tra từ tồn tại
			if (existWord){
				//Lay danh sach tai lieu
				posting = (Map<Integer, Integer>)InvertedIndex.get(word);
	
				//Kiểm tra idDoc tồn tại hay chưa
	        	if(!posting.containsKey(idDoc)){
	        		posting.put(idDoc, termFrequency);
	        		InvertedIndex.put(word, posting);					//CAP NHAT LAI POSTING
	        	}
	        	else{
	        		int tf = (int)posting.get(idDoc);
	        		tf = tf + termFrequency;
	        		posting.put(idDoc, tf);
	        		//System.out.println("Term: " + word + " docID" + idDoc + " tf = " + tf);
	        		InvertedIndex.put(word, posting);
	        	}
			}
			else{  
				//Token chưa tồn tại trong chỉ mục						
				posting.put(idDoc, 	termFrequency); 					//THÊM DANH SÁCH TÀI LIỆU
				InvertedIndex.put(word, posting);						//THÊM TỪ MỚI VÀO CHỈ MỤC
			}	
		}
	}
	
	//Lấy danh sách chỉ mục
	public Map<String, Map<Integer, Integer>> getInvertedIndex(){
		return InvertedIndex;
	}
}
