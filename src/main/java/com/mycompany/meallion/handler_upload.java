package com.mycompany.meallion;

import features.FileUpload;
import features.ImageTagger;
import features.SearchEngine;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.persistence.PersistenceException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import orm.UploadRecord;
import sql.SQL;
import utils.Log;
import utils.URIAnalyser;

@MultipartConfig
public class handler_upload {
    private SQL sql;
    private SearchEngine searchengine;
    String uploaddir;
    String file_location_url;
    
    private ImageTagger current_tagger;
    
    public handler_upload(SQL sql,SearchEngine searchengine,String uploaddir, String file_location_url) {
        this.sql = sql;
        this.searchengine = searchengine;
        this.file_location_url = file_location_url;
        this.uploaddir = uploaddir;
    }
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try{
            
            String command = URIAnalyser.getCommand(request.getRequestURI(), "upload");
            Log.wdln("/upload: command="+command);
            
            //--------------------------------------------------------------------------------------------------
            //Catch if a file is requested here:  
            //--------------------------------------------------------------------------------------------------
            
            if(command.contains(".")){
                throw new FileNotFoundException(command);
            }
            
            //--------------------------------------------------------------------------------------------------
            //"tags_update"
            //--------------------------------------------------------------------------------------------------
            
            if(command.equalsIgnoreCase("tags_update")){
                Log.wln("received tag update string: "+request.getParameter("tags_update"));
                UploadRecord record = new UploadRecord(current_tagger.getCurrentURL(),request.getParameter("tags_update"));
                Log.w(record);
                Log.w("Trying to update image tags..");
                this.sql.getEM().getTransaction().begin();
                this.sql.getEM().persist(record);
                this.sql.getEM().getTransaction().commit();
                this.sql.getEM().clear();
                Log.wln("done");
            
            //--------------------------------------------------------------------------------------------------
            //No command
            //--------------------------------------------------------------------------------------------------
                
            }else{
                String file_url = FileUpload.Save(request,this.uploaddir,this.file_location_url);
                Log.debug_wln("FileUpload: saved under URL "+file_url);
                
                ArrayList<String> tags = new ArrayList<>();

                current_tagger = new ImageTagger();
                current_tagger.LoadImage(file_url);
                current_tagger.CreateConcepts();
                tags = current_tagger.GetTags();

                request.setAttribute("fileurl", file_url);
                request.setAttribute("tags",tags);
                RequestDispatcher rd = request.getRequestDispatcher("dispatch_upload.jsp");
                rd.include(request, response);
                Log.wln("done");

                PrintWriter out = response.getWriter();
                out.println(file_url);
                out.close();
            }
           
        }catch(IOException e){
            Log.eln("/upload: IOException: "+e);
            RequestDispatcher rd = request.getRequestDispatcher("dispatch_ooops.jsp");
            rd.include(request, response);
        }catch(NullPointerException e){
            Log.eln("/upload: NPEException "+e);
            RequestDispatcher rd = request.getRequestDispatcher("dispatch_ooops.jsp");
            rd.include(request, response);
        }catch(ServletException e){
            Log.eln("/upload: ServletException: "+e);
            RequestDispatcher rd = request.getRequestDispatcher("dispatch_ooops.jsp");
            rd.include(request, response);
        }catch(PersistenceException e){
            Log.eln("/upload: PersistenceException: "+e);
            RequestDispatcher rd = request.getRequestDispatcher("dispatch_ooops.jsp");
            rd.include(request, response);
        }catch(Exception e){
            Log.eln("/upload: unknown Exception: "+e);
            RequestDispatcher rd = request.getRequestDispatcher("dispatch_ooops.jsp");
            rd.include(request, response);
        }
    }
}