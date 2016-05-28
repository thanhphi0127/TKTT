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
	public Map<String, Map<Integer, Integer>> wordCount;
	public Dictionary dic;
	public Document documents;
	//public Set<Integer> posting;
	private int documentsSize;
	
	public InvertedIndex(){
		//posting = new HashSet<Integer>();
		//InvertedIndex = new HashMap<String, List<Integer>>();
		InvertedIndex = new TreeMap<String, Map<Integer, Integer>>();
		wordCount= new HashMap<String, Map<Integer, Integer>>();
		documentsSize = 0;
	}
	
	public void XcreateInvertIndex(Dictionary dic, Document doc){
		this.dic = dic;
		this.documents = doc;
		documentsSize = doc.getListTokenDocument().size();
		
		System.out.println("Kích thước dictionary: "+ dic.getDictionary().size());
		int k = 0;
		
        final Iterator<String> it= dic.getDictionary().iterator();
        while(it.hasNext()){
            String token= it.next();
            List<Integer> posting= new ArrayList<Integer>();
            Map<Integer, Integer> count= new HashMap<Integer, Integer>();

            System.out.println("Token thu : "+ k);
            k++;
            
            for(int i=0 ; i< documentsSize ; i++){
                List<String> document = documents.getListTokenDocument().get(i);
                if(document.contains(token)){
                    posting.add(i);
                }

                int occurence=Collections.frequency(document, token);  				//DEM TAN SUAT XUAT HIEN CUA TU
                count.put(i, occurence);
                wordCount.put(token, count); 										//TU KHOA TOKEN O TAI LIEU THU I VOI TAN SO XUAT HIEN LA OCCURENCE
            }
            //InvertedIndex.put(token, posting); 										//TU KHOA TOKEN O DANH SACH TAI LIEU I
        }
		
	}
	
	
	//Thêm từ vào chỉ mục
	public void insertInvertIndex(String word, int idDoc){
		int termFrequency = 1;
		if(word.length() != 0){
			boolean existWord = InvertedIndex.containsKey(word);
			Map<Integer, Integer> posting = new TreeMap<Integer, Integer>();
			//Set<Integer> itemPosting= new HashSet<Integer>();
			
			//Kiểm tra từ tồn tại
			if (existWord){
				//Tìm token đã tồn tại
		        for(String token : InvertedIndex.keySet()){
		            if(word.equals(token)){
		            	posting = (Map<Integer, Integer>)InvertedIndex.get(token);  //LẤY DANH SÁCH CÁC TÀI LIỆU CỦA TOKEN
		                break;
		            }
		        }
	
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
	
	
	public Map<String, List<Integer>> xgetInvertedIndex(){
		return null;
	}
	
}
