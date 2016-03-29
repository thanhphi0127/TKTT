package com.searchengine.tktt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jvntextpro.JVnTextPro;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import vn.hus.nlp.utils.FileIterator;
import vn.hus.nlp.utils.TextFileFilter;

/**
 * Servlet implementation class Index
 */
@WebServlet("/Index")
public class Index extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private final String inputPath = "E://Eclipse//TKTT//data//documents//";
	private final String outputPath = "E://Eclipse//TKTT//data//tokenizes//";
	
	JVnTextPro jvnTextPro = new JVnTextPro(); 
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Index() {
        super();
        jvnTextPro.initSenSegmenter("C://Users//Nguyen Thanh Phi//workspace//JVnTextPro//models//jvnsensegmenter"); 
        jvnTextPro.initSenTokenization();
        jvnTextPro.initSegmenter("C://Users//Nguyen Thanh Phi//workspace//JVnTextPro/models/jvnsegmenter"); 
        jvnTextPro.initPosTagger("C://Users//Nguyen Thanh Phi//workspace//JVnTextPro/models/jvnpostag/maxent");
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
		long startTime = System.currentTimeMillis();
		
		uploadDocument(request, response);
		
        long endTime = System.currentTimeMillis();
        float duration = (float) (endTime - startTime) / 1000;
        System.out.print("Thời gian upload tài liệu: " + duration);
		
		tokenizerDocuments(request, response);
		
	}
	
	//Tải tập tài liệu lên server
	protected void uploadDocument(HttpServletRequest request, HttpServletResponse response) throws IOException {
		boolean isMultipart= ServletFileUpload.isMultipartContent(request);
        if(isMultipart){
            DiskFileItemFactory factory= new DiskFileItemFactory();
            ServletFileUpload upload= new ServletFileUpload(factory);
            
            try {      
                List<FileItem> items= upload.parseRequest(request);
                String contextPath = request.getContextPath(); //Lấy đường dẫn thư mục hiện hành /TKTT
                //System.out.println("<br/>File system context path: " + contextPath);
                
                //Xử lý danh sách các file đã được chọn
                for(FileItem item : items){
                	String filename= new File(item.getName()).getName(); 
                    item.write(new File(inputPath + filename));  //Lưu các file vào đường dẫn "E://Eclipse//TKTT//data//documents//"
                } 
            } 
            catch (Exception e) {
                response.getWriter().print(e.getMessage());
            }
        }
        else{
            response.getWriter().print("Không thể upload tập tài liệu");
        }
	}
	
	//Tách từ cho danh sách các tài liệu
	protected float tokenizerDocuments(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String output, result, line;
		int id = 1;
		Document doc = new Document();
		Dictionary dic = new Dictionary();
		InvertedIndex invertedInx = new InvertedIndex();
		
        TextFileFilter fileFilter = new TextFileFilter(".txt"); //Tìm các tài liệu có phần mở rộng là txt
        File inputDirFile = new File(inputPath); //Đường dẫn đầu vào DS tài liệu 

        //Lấy tất cả các tài liệu
        File[] inputFiles = FileIterator.listFiles(inputDirFile, fileFilter);
        
        long startTime = System.currentTimeMillis();
        
        for (File file : inputFiles) {    	
            //Tạo chỉ mục cho 1 tài liệu gốc và tài liệu đã tách từ
            doc.createDocumentIndex(file, jvnTextPro, invertedInx, id);
            id++;
        }
        
        //TẠO TỪ ĐIỂN CHO TOKEN TÁCH TỪ
        //dic.createDictionary(doc.getListTokenDocument());
        
        //TẠO CHỈ MỤC NGHỊCH ĐẢO CHO CÁC TOKEN
        //invertedInx.createInvertIndex(dic, doc);
        
        Map<String, Map<Integer, Integer>> resultIndex = invertedInx.getInvertedIndex();
        
        ServletContext applicationObject = getServletConfig().getServletContext();
        applicationObject.setAttribute("InvertedIndex", resultIndex);

        long endTime = System.currentTimeMillis();
        float duration = (float) (endTime - startTime) / 1000;
        System.out.print("Thời gian tách từ + lập chỉ mục: " + duration);
        
        response.sendRedirect("http://localhost:8080/TKTT/index.jsp");
        
        return duration;
	}
	
	//protected void createInvertIndex()

	//Lập chỉ mục cho tài liệu gốc
	public void createRawDocumentInvertedIndex(File file) throws FileNotFoundException, IOException{
		String output, result, line;
		FileInputStream fis;
		BufferedReader br;
        fis = new FileInputStream(new File(inputPath + file.getName())); //Lấy tên tập tin
        br = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
        
        //Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output), "UTF-8"));
        
      //Đọc từng dòng trong mỗi tập tin tài liệu
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line != null && !line.isEmpty()) {
                //Tách từ cho mỗi dòng
            	
            	
            	result = jvnTextPro.wordSegment(line);
            	
            	//out.write(result + "\r\n");
            }
        }
        //out.close();
        
		
	}
	
	//Lập chỉ mục cho tài liệu đã tách từ
	public void createTokenizerDocumentInvertedIndex(){
		
	}
	
	protected void readFile(){
        try {
            String input = "E://documents//1001.txt";
            FileInputStream fis = new FileInputStream(new File(input));
            BufferedReader br = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line != null && !line.isEmpty()) {
                    System.out.println("*: " + line);
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
}	
	
	
	
	/*
    for(Map.Entry entry: InvertedIndex.entrySet()){
    	Map<Integer, Integer> posting = (Map<Integer, Integer>) entry.getKey();  //LẤY DANH SÁCH CÁC TÀI LIỆU CỦA TOKEN
        
        out.print("<tr>");
            out.print("<td align='center'>"+count+"</td>");
            out.print("<td>"+entry.getKey()+"</td>");
            String itemPostring = "";
            for(Map.Entry item: posting.entrySet()){
            	itemPostring += String.valueOf(item.getKey()) + " ";
            }
            out.print("<td>"+ itemPostring +"</td>");
        out.print("</tr>");
        count++;
    }
    */

