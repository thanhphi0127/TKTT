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
import java.util.List;

import jvntextpro.JVnTextPro;

public class Document {
	List<String> rawWord;
	List<List<String>> rawDocuments;
	
	List<String> tokenWord;
	List<List<String>> tokenDocuments;
	String stopWords;
	
	public Document() throws IOException{
		rawWord = new ArrayList<String>();
		tokenWord = new ArrayList<String>();
		rawDocuments = new ArrayList<List<String>>();
		tokenDocuments = new ArrayList<List<String>>();
		
        File file = new File(StaticVariable.STOPWORDPATH+"\\stopword.txt"); 
        byte[] encoded = Files.readAllBytes(Paths.get(file.getAbsolutePath().toString()));
        stopWords= new String(encoded, "UTF-8");
	}
	
	//Tạo chỉ mục cho 1 tập tin tài liệu (KHÔNG CHỨA STOP WORD)
	public void createDocumentIndex(File file, JVnTextPro jvnTextPro, InvertedIndex index, int idDoc) throws IOException{
		String[] words, tokenWords;
		int rawLenght, tokenLength, lenght = 0;
		String line, tokens, output;
		FileInputStream fis;
		BufferedReader br;
		String cmp = "•…\\_().,:\"“”\'/-+&;‘’?=–[]<>!";
		
        //Lấy tên tài liệu kết hợp với đường dẫn đầu ra sau khi tách từ
    	output = StaticVariable.OUTPUTPATH + file.getName();
        fis = new FileInputStream(new File(StaticVariable.INPUTPATH + file.getName())); //Lấy tên tập tin
        br = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
        Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output), "UTF-8"));
        int k = 0;
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
        				tokenWords[i] = replaceString(tokenWords[i], cmp);
        				if (tokenWords[i].length() != 0)
        					index.insertInvertIndex(tokenWords[i].toLowerCase(), idDoc);
        			}
        		}
        		out.write(tokens + "\r\n");
            }
        }
        br.close();
        out.close();
	}
	
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
	
	//Tạo chỉ mục cho 1 tập tin tài liệu (KHÔNG CHỨA STOP WORD)
	public void XcreateDocumentIndex(File file, JVnTextPro jvnTextPro) throws IOException{
		String[] words, tokenWords;
		int rawLenght, tokenLength;
		String line, tokens, output;
		FileInputStream fis;
		BufferedReader br;
		
        //Lấy tên tài liệu kết hợp với đường dẫn đầu ra sau khi tách từ
    	output = StaticVariable.OUTPUTPATH + file.getName();
        fis = new FileInputStream(new File(StaticVariable.INPUTPATH + file.getName())); //Lấy tên tập tin
        br = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
        Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output), "UTF-8"));
        
        //Đọc từng dòng trong mỗi tập tin tài liệu
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line != null && !line.isEmpty()) {
            	
            	//Với mỗi từ trong 1 dòng cho tài liệu đã tách từ
            	tokens = jvnTextPro.wordSegment(line);
            	tokenWords = tokens.split(" ");
            	tokenLength = tokenWords.length;
        		for (int i = 0; i < tokenLength; i++) {
        			if(!stopWords.contains(tokenWords[i])) //Không phải là stop word
        				tokenWord.add(tokenWords[i]);
        		}
        		
        		//Với mỗi từ trong 1 dòng cho tài liệu gốc
        		words = line.split(" ");
        		rawLenght = words.length;
        		for (int i = 0; i < rawLenght; i++) {
        			if(!stopWords.contains(words[i])) //Không phải là stop word
        				rawWord.add(words[i]);
        		}
        		
        		out.write(tokens + "\r\n");
            }
        }
        out.close();
        br.close();
        
        rawDocuments.add(rawWord);
        tokenDocuments.add(tokenWord);
	}
	
	public List<List<String>> getListRawDocument(){
		return rawDocuments;
	}
	
	public List<List<String>> getListTokenDocument(){
		return tokenDocuments;
	}
}
