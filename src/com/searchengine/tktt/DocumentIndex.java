package com.searchengine.tktt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import jvntextpro.JVnTextPro;

public class DocumentIndex {	
	List<String> tokenWord;
	List<List<String>> tokenDocuments;
	String stopWords;
	
	public static int numDoc;
	public static Map<Integer, Integer> lengthDoc;
	
	/**
	 * 
	 * @throws IOException
	 */
	public DocumentIndex() throws IOException{
		tokenWord = new ArrayList<String>();
		tokenDocuments = new ArrayList<List<String>>();
		lengthDoc = new TreeMap<Integer,Integer>();
		
        File file = new File(StaticVariable.STOPWORDPATH + "\\stopword.txt"); 
        byte[] encoded = Files.readAllBytes(Paths.get(file.getAbsolutePath().toString()));
        stopWords= new String(encoded, "UTF-8");
        System.out.println("stopWords = " + stopWords);
	}
	
	
	/**
	 * 
	 * @param file: DUONG DAN TOI TAI LIEU
	 * @param jvnTextPro
	 * @param index
	 * @param idDoc: SO CUA TAI LIEU
	 * @return numTerm: SO LUONG TU TRONG 1 TAI LIEU
	 * @throws IOException
	 */
	//Tạo chỉ mục cho 1 tập tin tài liệu (KHÔNG CHỨA STOP WORD)
	public int createDocumentIndex(File file, JVnTextPro jvnTextPro, InvertedIndex index, int idDoc) throws IOException{
		boolean flag = true;
		int numTerm = 0;
		String[] tokenWords;
		int mumPara = 0;
		int tokenLength;
		String tokens, line;
		
	
		try {
			FileInputStream fis = new FileInputStream(file.getAbsolutePath());
			HWPFDocument doc = new HWPFDocument(fis);
			WordExtractor we = new WordExtractor(doc);
			String[] paragraphs = we.getParagraphText();
			flag = false;
			
	        //Đọc từng paragraph trong mỗi tập tin tài liệu
			for (String para : paragraphs) {
				para = para.trim();
				//numTerm += tokenParagraph(para, jvnTextPro, index, idDoc); //SO TU TRONG CAC DOAN VAN BAN
		        if (para != null && !para.isEmpty()) {
		        	//Với mỗi từ trong 1 dòng cho tài liệu đã tách từ
		        	tokens = jvnTextPro.wordSegment(para);
		        	tokenWords = tokens.split(" ");
		        	tokenLength = tokenWords.length;
		        	
		    		for (int i = 0; i < tokenLength; i++) {
		    			tokenWords[i] = StaticVariable.replaceString(tokenWords[i], StaticVariable.cmp);
		    			tokenWords[i] = tokenWords[i].toLowerCase();
		    			if(!stopWords.contains(tokenWords[i])){ //Không phải là stop word
		    				//Thêm tài liệu vào chỉ mục
		    				if (tokenWords[i].length() != 0){
		    					index.insertInvertIndex(tokenWords[i].toLowerCase(), idDoc);
		    					numTerm++;
		    				}
		    			}
		    		}
		        }
	        }
	        fis.close();
	        lengthDoc.put(idDoc, numTerm);
		}catch(Exception ex){}
		
		if(flag){
			try {
				String para = "";
				FileInputStream fis = new FileInputStream(file.getAbsolutePath());
				XWPFDocument document = new XWPFDocument(fis);
				List<XWPFParagraph> paragraphs = document.getParagraphs();

		        //Đọc từng dòng trong mỗi tập tin tài liệu
				for (XWPFParagraph s : paragraphs) {
					para = s.getText().trim();
					//numTerm += tokenParagraph(para, jvnTextPro, index, idDoc); //SO TU TRONG CAC DOAN VAN BAN
			        if (para != null && !para.isEmpty()) {
			        	//Với mỗi từ trong 1 dòng cho tài liệu đã tách từ
			        	tokens = jvnTextPro.wordSegment(para);
			        	tokenWords = tokens.split(" ");
			        	tokenLength = tokenWords.length;
			        	
			    		for (int i = 0; i < tokenLength; i++) {
			    			tokenWords[i] = StaticVariable.replaceString(tokenWords[i], StaticVariable.cmp);
			    			tokenWords[i] = tokenWords[i].toLowerCase();
			    			if(!stopWords.contains(tokenWords[i])){ //Không phải là stop word
			    				//Thêm tài liệu vào chỉ mục
			    				if (tokenWords[i].length() != 0){
			    					index.insertInvertIndex(tokenWords[i].toLowerCase(), idDoc);
			    					numTerm++;
			    				}
			    			}
			    		}
			        }
		        }
		        fis.close();
		        lengthDoc.put(idDoc, numTerm);
			}catch(Exception ex){}
		}
 
        return numTerm;
	}
	
	/**
	 * 
	 * @param paragraph: CHUOI GIA TRI CUA 1 DOAN VAN BAN
	 * @param jvnTextPro: LOP CUA THU VIEN TACH TU
	 * @param index: CHI MUC NGHICH DAO
	 * @param idDoc: SO CUA TAI LIEU
	 * @return numTerm: SO LUONG TU TRONG 1 DOAN VAN BAN
	 * @throws IOException
	 */
	private int tokenParagraph(String paragraph, JVnTextPro jvnTextPro, InvertedIndex index, int idDoc) throws IOException{
		String[] tokenWords, paragraphs;
		int numTerm = 0, numPara = 0;
		int tokenLength;
		String tokens, line;
		
		//LAY DANH SACH CAC CAU CUA 1 PARAGRAPH
		paragraphs = StaticVariable.getSentences(paragraph);
		numPara = paragraphs.length;
		
		for(int p=0; p<numPara; p++){
			line = paragraphs[p];
	        if (line != null && !line.isEmpty()) {
	        	//Với mỗi từ trong 1 dòng cho tài liệu đã tách từ
	        	tokens = jvnTextPro.wordSegment(line);
	        	tokenWords = tokens.split(" ");
	        	tokenLength = tokenWords.length;
	        	
	    		for (int i = 0; i < tokenLength; i++) {
	    			if(!stopWords.contains(tokenWords[i])){ //Không phải là stop word
	    				//Thêm tài liệu vào chỉ mục
	    				tokenWords[i] = StaticVariable.replaceString(tokenWords[i], StaticVariable.cmp);
	    				if (tokenWords[i].length() != 0){
	    					index.insertInvertIndex(tokenWords[i].toLowerCase(), idDoc);
	    					numTerm++;
	    				}
	    			}
	    		}
	        }
		}
		return numTerm;
	}
	
	
	public int createDocumentIndex_Old(File file, JVnTextPro jvnTextPro, InvertedIndex index, int idDoc) throws IOException{
		String[] words, tokenWords;
		int k = 0, numTerm = 0;
		int rawLenght, tokenLength, lenght = 0;
		String line, tokens, output;
		FileInputStream fis;
		BufferedReader br;
		String cmp = "•…\\_().,:\"“”\'/-+&;‘’?=–[]<>!";
		
        //Lấy tên tài liệu kết hợp với đường dẫn đầu ra sau khi tách từ
    	//output = StaticVariable.OUTPUTPATH + file.getName();
        fis = new FileInputStream(new File(StaticVariable.INPUTPATH + file.getName())); //Lấy tên tập tin
        br = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
        //Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output), "UTF-8"));
        
        //Đọc từng dòng trong mỗi tập tin tài liệu
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if(k == 0){
	            line = line.substring(1); //BO KI TU DAU TIEN TRONG FILE UTF-8
	            k++;
            }

            if (line != null && !line.isEmpty()) {
            	//Với mỗi từ trong 1 dòng cho tài liệu đã tách từ
            	tokens = jvnTextPro.wordSegment(line);
            	tokenWords = tokens.split(" ");
            	tokenLength = tokenWords.length;
        		for (int i = 0; i < tokenLength; i++) {
        			if(!stopWords.contains(tokenWords[i])){ //Không phải là stop word
        				//Thêm tài liệu vào chỉ mục
        				//System.out.println("Token: " + tokenWords[i]);
        				tokenWords[i] = StaticVariable.replaceString(tokenWords[i], cmp);
        				if (tokenWords[i].length() != 0){
        					index.insertInvertIndex(tokenWords[i].toLowerCase(), idDoc);
        					numTerm++;
        				}
        			}
        		}
        		//out.write(tokens + "\r\n");
            }
        }
        
        lengthDoc.put(idDoc, numTerm);
        
        br.close();
        //out.close();
        
        return numTerm;
	}
	
	
	
	public List<List<String>> getListTokenDocument(){
		return tokenDocuments;
	}
}
