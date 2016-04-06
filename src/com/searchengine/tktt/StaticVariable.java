package com.searchengine.tktt;

import jvntextpro.JVnTextPro;

public class StaticVariable {
	public static boolean flag = false;
	public static String INPUTPATH = "E://Eclipse//TKTT//data//documents//";
	public static String OUTPUTPATH = "E://Eclipse//TKTT//data//tokenizes//";
	public static String STOPWORDPATH = "E://Eclipse//TKTT//data//stopwords//";
	
	public static JVnTextPro jvnTextPro = new JVnTextPro();

	public static void init(){
        jvnTextPro.initSenSegmenter("E://Eclipse//JVnTextPro//models//jvnsensegmenter"); 
        jvnTextPro.initSenTokenization();
        jvnTextPro.initSegmenter("E://Eclipse//JVnTextPro//models//jvnsegmenter"); 
        jvnTextPro.initPosTagger("E://Eclipse//JVnTextPro//models//jvnpostag/maxent");
	}
}
