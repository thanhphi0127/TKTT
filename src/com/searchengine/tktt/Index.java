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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import vn.hus.nlp.utils.FileIterator;
import vn.hus.nlp.utils.TextFileFilter;

/**
 * Servlet implementation class Index
 */
@WebServlet("/Index")
public class Index extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	MongoDBConnection db = new MongoDBConnection();
	
	//JVnTextPro jvnTextPro = new JVnTextPro(); 
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Index() {
        super();
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
		
		deleteDocument();
		uploadDocument(request, response);
		
        long endTime = System.currentTimeMillis();
        float duration = (float) (endTime - startTime) / 1000;
        System.out.print("Thời gian upload tài liệu: " + duration);
		
        //Tách từ và lập chỉ mục nghịch đảo
		tokenizerDocuments(request, response);
		
	}
	
	/**
	 * Xóa tất cả tập tin tài liệu trước khi upload
	 */
	protected void deleteDocument(){
		File directory = new File(StaticVariable.INPUTPATH);
		if(directory.exists()){
			File[] files = directory.listFiles();
			for(File file : files){
				file.delete();
			}
		}
		else System.out.println("Không tìm thấy thư mục");
	}
	
	/**
	 * Tải tập tài liệu lên server
	 */
	protected void uploadDocument(HttpServletRequest request, HttpServletResponse response) throws IOException {
		boolean isMultipart= ServletFileUpload.isMultipartContent(request);
		int idDoc = 1;
        if(isMultipart){
            DiskFileItemFactory factory= new DiskFileItemFactory();
            ServletFileUpload upload= new ServletFileUpload(factory);
            
            try {      
                List<FileItem> items= upload.parseRequest(request);
                //String contextPath = request.getContextPath(); //Lấy đường dẫn thư mục hiện hành /TKTT
                //Xử lý danh sách các file đã được chọn
                for(FileItem item : items){
                	//String filename= new File(item.getName()).getName(); 
                    item.write(new File(StaticVariable.INPUTPATH + idDoc + ".txt"));  //Lưu các file vào đường dẫn "E://Eclipse//TKTT//data//documents//"
                    idDoc++;
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
	
	/**
	 * Tách từ cho danh sách các tài liệu
	 */
	protected void tokenizerDocuments(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String output, result, line, filename;
		List<Float> timeInvertedIndex = new ArrayList<Float>();
		String[] name;
		Document docNumTerm;
		int id = 1, countDoc = 0, numTerm;
		DocumentIndex doc = new DocumentIndex();
		InvertedIndex invertedInx = new InvertedIndex();
		// Lay bang trong co so du lieu
		MongoCollection<Document> collection = db.getCollectionLength();
		collection.drop();
		
		
        TextFileFilter fileFilter = new TextFileFilter(".txt"); 				//Tìm các tài liệu có phần mở rộng là txt
        File inputDirFile = new File(StaticVariable.INPUTPATH); 				//Đường dẫn đầu vào DS tài liệu 
        File[] inputFiles = FileIterator.listFiles(inputDirFile, fileFilter);	//Lấy tất cả các tài liệu
        long startTime = System.currentTimeMillis();
        
        List<Document> list_doc = new ArrayList<Document>();
        
        for (File file : inputFiles) {
        	name = file.getName().toString().split(".txt");
        	id = Integer.parseInt(name[0]);
        	countDoc++;
            numTerm = doc.createDocumentIndex(file, StaticVariable.jvnTextPro, invertedInx, id); //Tạo chỉ mục cho 1 tài liệu gốc và tài liệu đã tách từ
            
            docNumTerm = new Document();
            docNumTerm.put("_id", id);
            docNumTerm.put("length", numTerm);
			list_doc.add(docNumTerm);	
        }
        
        collection.insertMany(list_doc);

        //So luong tai lieu
        //doc.numDoc = countDoc;

        Map<String, Map<Integer, Integer>> resultIndex = invertedInx.getInvertedIndex();
        
        long endTime = System.currentTimeMillis();
        float duration = (float) (endTime - startTime) / 1000;
        timeInvertedIndex.add(duration);
        System.out.print("Thời gian tách từ + lập chỉ mục: " + duration);
    
        startTime = System.currentTimeMillis();
        //LUU CHI MUC NGHICH DAO VAO MONGODB
        saveInvertedIndex(resultIndex);
        endTime = System.currentTimeMillis();
        duration = (float) (endTime - startTime) / 1000;
        System.out.print("Thời gian lưu vào Mongodb: " + duration);
        
        ServletContext applicationObject = getServletConfig().getServletContext();
        applicationObject.setAttribute("InvertedIndex", resultIndex);
        applicationObject.setAttribute("timeInvertedIndex", timeInvertedIndex);
        
        response.sendRedirect("http://localhost:8080/TKTT/invertedindex.jsp");
	}
	
	public void saveInvertedIndex(Map<String, Map<Integer, Integer>> resultIndex){
		Map<Integer, Integer> posting = new HashMap<Integer, Integer>();
		MongoCollection<Document> collection = db.getcollectionInvertedIndex();
		collection.drop();
		
		/*================================================*/
		//LUU DU LIEU VAO MONGODB		
		List<Document> list_doc = new ArrayList<Document>();
		Document invert;
		Map<String, Object> docID_TF;
		for(String token : resultIndex.keySet()){
			posting = (Map<Integer, Integer>)resultIndex.get(token);
			invert = new Document();
			invert.put("_id", token);				 				//TERM
			docID_TF = new TreeMap<String, Object>();				//DANH SACH TAI LIEU
			
            for(Integer p : posting.keySet()){
            	docID_TF.put(String.valueOf(p), posting.get(p));
            }
            
			invert.put("docID_tf", docID_TF);
			list_doc.add(invert);	
		}
		
		collection.insertMany(list_doc);
		/*================================================*/
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

