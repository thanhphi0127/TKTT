package com.searchengine.tktt;

import jvntextpro.JVnTextPro;

public class StaticVariable {
	public static boolean flag = false;
	public static String PATH = "E://Eclipse//TKTT//";
	
	public static String INPUTPATH = PATH + "data//documents//";
	public static String OUTPUTPATH = PATH + "data//tokenizes//";
	public static String STOPWORDPATH = PATH + "data//stopwords//";
		public static JVnTextPro jvnTextPro = new JVnTextPro();

	public static void init(){
        jvnTextPro.initSenSegmenter(PATH + "models//jvnsensegmenter"); 
        jvnTextPro.initSenTokenization();
        jvnTextPro.initSegmenter(PATH + "models//jvnsegmenter"); 
        jvnTextPro.initPosTagger(PATH + "models//jvnpostag/maxent");
	}
}
