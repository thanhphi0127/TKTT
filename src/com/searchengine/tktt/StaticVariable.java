package com.searchengine.tktt;

import java.io.IOException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import jvntextpro.JVnTextPro;

public class StaticVariable {
	public static boolean flag = false;
	public static String PATH = "E://Eclipse//TKTT//";
	
	public static String INPUTPATH = PATH + "data//documents//";
	public static String OUTPUTPATH = PATH + "data//tokenizes//";
	public static String STOPWORDPATH = PATH + "data//stopwords//";
	public static JVnTextPro jvnTextPro = new JVnTextPro();
	
	public static String cmp = "•…\\_().,:\"“”\'/-+&;‘’?=–[]<>!*″";

	public static void init(){
        jvnTextPro.initSenSegmenter(PATH + "models//jvnsensegmenter"); 
        jvnTextPro.initSenTokenization();
        jvnTextPro.initSegmenter(PATH + "models//jvnsegmenter"); 
        jvnTextPro.initPosTagger(PATH + "models//jvnpostag/maxent");
	}
	
	/**
	 * 
	 * @param para: DOAN VAN BAN
	 * @return String[] TRA VE MANG CAC CAU TRONG VAN BAN
	 * @throws IOException
	 */
	public static String[] getSentences(String para) throws IOException{
        List<String> sentences= new ArrayList<String>();
        BreakIterator border = BreakIterator.getSentenceInstance(Locale.US);
        border.setText(para);
        int start = border.first();
        
        for (int end = border.next(); end != BreakIterator.DONE; start = end, end = border.next()) {
            sentences.add(para.substring(start,end));
        }
        
        String[] strarray = new String[sentences.size()];
        sentences.toArray(strarray);
        
        return strarray;
	}
	
	/**
	 * 
	 * @param word: CHUOI DAU VAO
	 * @param cmp: CHUOI CAC KI TU SO SANH
	 * @return word: CHUOI DA LOAI BO KI TU THUA O DAU VA CUOI
	 */
	public static String replaceString(String word, String cmp){
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
}
