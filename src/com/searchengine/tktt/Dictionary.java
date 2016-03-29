/**
 * 
 */
package com.searchengine.tktt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Nguyen Thanh Phi
 *
 */
public class Dictionary {
    public Set<String> dictionary;
    public Set<Integer> posting;
    private Map<String, Integer> wordTotalCount;
    
    public Dictionary() throws IOException{
        dictionary= new HashSet<String>();
        posting = new HashSet<Integer>();
        wordTotalCount=new HashMap<String, Integer>();
    }
    
    public Set<String> createDictionary(List<List<String>> documents) throws FileNotFoundException, IOException{
    	boolean contains;
    	int count;
    	
    	int numOfDocument = documents.size();
        for(int i=0 ; i<numOfDocument ; i++){         					//DUYỆT TẤT CẢ CÁC TÀI LIỆU
            List<String> document=documents.get(i);
            for(String token : document){								//VỚI MỖI TỪ TRONG TÀI LIỆU THỨ i
            	contains = wordTotalCount.containsKey(token);			//KIẾM TRA TỒN TẠI TOKEN HAY CHƯA
                count = 0;
                if(contains){											//TOKEN ĐÃ TỒN TẠI THÌ 
                	count = wordTotalCount.get(token);
                }
                wordTotalCount.put(token, (count+1));
            }
        }

        dictionary = wordTotalCount.keySet();
        return dictionary;
    }
    
    public Set<String> getDictionary(){
    	return dictionary;
    }
    
    public Map<String, Integer> getWordTotalCount(){
    	return wordTotalCount;
    }
    
}
