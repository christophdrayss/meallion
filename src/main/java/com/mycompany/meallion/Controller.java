/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mycompany.meallion;

import features.SearchEngine;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import orm.MealPlan;
import sql.SQL;
import utils.Configs;
import utils.Log;


@WebServlet(name = "Controller", urlPatterns = {"/menus/*","/system/*","/embed/*","/examples/*","/recipes/*","/upload/*"})
@MultipartConfig
public class Controller extends HttpServlet {

    private SQL sql;
    
    private handler_menus handler_menus;
    private handler_embed handler_embed;
    private handler_examples handler_examples;
    private handler_recipes handler_recipes;
    private handler_ingredients handler_ingredients;
    private handler_system handler_system;
    private handler_upload handler_upload;
    
    private SearchEngine searchengine;

    public Controller() throws IOException {
    }
    
    /**
     * Inits the Cookbook: creating the SQL and Solr connections, setting up the Search Engine and Menu Servlet. Therefore taking the file "meallion.conf" from WEB-INF folder.
     * 
     * @param config Information about the configurations of the servlet, such as path names 
     * @throws ServletException 
     */
    
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        
        try{
            Log.wdln("Meallion Confi: start configuring...");
            
            String config_fullPath = config.getServletContext().getRealPath("/WEB-INF")+"/meallion.conf";
            
            Log.wdln("meallion onfig file path: "+config_fullPath);
             
            // Set config parameters:
            
            Configs configs = new Configs(config_fullPath);
            String solr_url = configs.FetchString("solr_url");
            int solr_max_results = configs.FetchInt("solr_max_results");
            
            Log.wdln("Meallion config: solr_url="+solr_url);
            Log.wdln("Meallion config: solr_max_results="+solr_max_results);
            
            // End setting config parameters 
            
            this.searchengine = new SearchEngine(this.sql,solr_url,solr_max_results);
            
            this.sql = new SQL();
            this.handler_menus = new handler_menus(this.sql, this.searchengine); 
            this.handler_embed = new handler_embed(this.sql, this.searchengine); 
            this.handler_examples = new handler_examples(this.sql, this.searchengine); 
            this.handler_ingredients = new handler_ingredients(this.sql, this.searchengine); 
            this.handler_recipes = new handler_recipes(this.sql, this.searchengine); 
            this.handler_system = new handler_system(this.sql, this.searchengine); 
            this.handler_upload = new handler_upload(this.sql,
                    this.searchengine,
                    "/media/meallion/images/upload",
                    "http://www.meallion.de/images/upload"); 
            
            Log.wdln("Meallion config: success.");
            
        }catch(FileNotFoundException fnfe){ 
            Log.edln("Meallion Config file could not be found: "+fnfe);
        }catch(IOException ioe){ 
            Log.edln("while CookBook initializing. Probably SearchEngine could not be started by CookBook: "+ioe);
        }
    }
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     * 
     * 
     */
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        Log.wdln("Executing /Controller Servlet");
        Log.wdln("path info: "+request.getPathInfo());
        Log.wdln("Servlet path: "+request.getServletPath());
        Log.wdln("requested URL: "+request.getRequestURL());
            
            response.setContentType("text/html;charset=UTF-8");
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");
            response.addHeader("Access-Control-Allow-Headers", "X-PINGOTHER, Origin, X-Requested-With, Content-Type, Accept");
            response.addHeader("Access-Control-Max-Age", "1728000");
            
            //Check if user session already has a "custom_mealplan" object. If not, create one:
            Log.wd("Check existence of custom_mealplan object..");
            if(request.getSession().getAttribute("custom_mealplan")==null){
                Log.w("no custom_mealplan available so far. Create new custom_mealplan object and store in HttpSession.");
                MealPlan menu = new MealPlan(this.sql);
                menu.setKeyword("custom_mealplan");
                menu.setName("mealplan");
                request.getSession().setAttribute("custom_mealplan", menu);
                Log.wln(" Done creating custom_mealplan object.");
            }else{
                MealPlan custom_mealplan = (MealPlan) request.getSession().getAttribute("custom_mealplan");
                Log.wln("custom_mealplan available. isEmpty: "+custom_mealplan.IsEmpty());
            }
            
        //---------------------------- 
        // --> FORWARD TO HANDLERS:
        //----------------------------
        
        // /embed
        if(request.getServletPath().equals("/embed")){ 
            Log.wln("Handing over request to embed handler");
            this.handler_embed.processRequest(request, response);
        
        // /recipes
        }else if(request.getServletPath().equals("/recipes")){ 
            Log.wln("Handing over request to recipes handler");
            this.handler_recipes.processRequest(request, response);
        
        // /menus
        }else if(request.getServletPath().equals("/menus")){ 
            Log.wln("Handing over request to menus handler");
            this.handler_menus.processRequest(request, response);
            
        // /ingredients
        }else if(request.getServletPath().equals("/ingredients")){ 
            Log.wln("Handing over request to ingredients handler");
            this.handler_ingredients.processRequest(request, response);
            
        // /system
        }else if(request.getServletPath().equals("/system")){ 
           Log.wln("Handing over request to system handler");
           this.handler_system.processRequest(request, response);
            
        // /examples    
        }else if(request.getServletPath().equals("/examples")){ 
            Log.wln("Handing over request to examples handler");
            this.handler_examples.processRequest(request, response);
        }
        
        // /upload
        else if(request.getServletPath().equals("/upload")){ 
            Log.wln("Handing over request to upload handler");
            this.handler_upload.processRequest(request, response);
        
        }
       
        else{
            
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}