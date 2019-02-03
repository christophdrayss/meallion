/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.meallion;

import features.SearchEngine;
import features.SearchResults;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import javax.persistence.PersistenceException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import orm.MealPlan;
import orm.Recipe;
import sql.SQL;
import utils.HttpResponder;
import utils.Log;
import utils.RecipeBuilder;
import utils.RecipeStep;
import utils.URIAnalyser;

/**
 *
 * @author chris
 */
public class handler_system {
    private SQL sql;
    private SearchEngine searchengine;
    
    public handler_system(SQL sql,SearchEngine searchengine) {
        this.sql = sql;
        this.searchengine = searchengine;
    }
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try{
            String command = URIAnalyser.getCommand(request.getRequestURI(), "system");
            Log.wdln("/system: command="+command);
            
            //--------------------------------------------------------------------------------------------------
            //Catch if a file is requested here:  
            //--------------------------------------------------------------------------------------------------
            
            if(command.contains(".")){
                throw new FileNotFoundException(command);
            }
            
            //--------------------------------------------------------------------------------------------------
            //Command "heap"     
            //--------------------------------------------------------------------------------------------------
            
            if(command.equalsIgnoreCase("heap")){
                Log.wdln("To execute /system/heap..");
                
                Log.wd("Getting Heap information...");
                
                //All values returned in byte
                
                /*
                Returns the maximum amount of memory that the Java virtual machine will attempt to use.
                If there is no inherent limit then the value Long.MAX_VALUE will be returned.
                */
                long max = Runtime.getRuntime().maxMemory();
                /*
                Returns the total amount of memory in the Java virtual machine.
                The value returned by this method may vary over time, depending on the host environment.
                */
                long total = Runtime.getRuntime().totalMemory();
                /*
                Returns the amount of free memory in the Java Virtual Machine.
                Calling the gc method may result in increasing the value returned by freeMemory.
                */
                long free = Runtime.getRuntime().freeMemory();
                
                StringBuilder response_string = new StringBuilder(); 
                response_string.append("Current heap:").append("<br>");
                response_string.append("Max: "+max+";").append("<br>");
                response_string.append("Used: "+total+";").append("<br>");
                response_string.append("Free: "+free+";").append("<br>");
                
                HttpResponder.print(response, response_string);
                
                Log.wln("done.");
                
            }
            
            //--------------------------------------------------------------------------------------------------
            //Command "reindex"     
            //--------------------------------------------------------------------------------------------------
            
            else if(command.equalsIgnoreCase("reindex")){
                Log.wdln("To execute /system/reindex..");
                
                Log.wdln(("Reindexing started..."));
                
                //this function at first clears the entire Solr document base. Then creates it from scratch
                this.searchengine.IndexAll();
                
                HttpResponder.print(response, "reindexed!");
                Log.wdln("Reindexed.");
                
            }
            
            //--------------------------------------------------------------------------------------------------
            //Command "echo"     
            //--------------------------------------------------------------------------------------------------
            
            else if(command.equalsIgnoreCase("echo")){
                Log.wdln("To execute /system/echo..");
                
                Log.w(("Echoing..."));
                HttpResponder.print(response, "echo echo...");
                Log.wln("done.");
                
            }
            
            //--------------------------------------------------------------------------------------------------
            //Command "clear"     
            //--------------------------------------------------------------------------------------------------
            
            else if(command.equalsIgnoreCase("clear")){
                Log.wdln("To execute /system/clear..");
                
                Log.wd(("Clearing the cache/entity manager..."));
                
                /**
                * This does not seem to work:
                * 
                * this.sql.getEM().clear();
                * this.sql.getEM().getEntityManagerFactory().getCache().evictAll();
                * this.sql.ClearCache();
                * 
                */
                
                //very lame but only stable solution by now:
                this.sql.getEM().close();
                this.sql = new SQL();
                Log.wln("cache/entity manager cleared.");
                HttpResponder.print(response, "succcess");
            }
            
            //--------------------------------------------------------------------------------------------------
            //Command "email_address_input"     
            //--------------------------------------------------------------------------------------------------
            
            else if(command.equalsIgnoreCase("email_address_input")){
                Log.wdln("To execute /system/email_address_input..");
                
                Log.wdln("Contact mail "+request.getParameter("email_address"));
                
            }
            
            //--------------------------------------------------------------------------------------------------
            //Command "getallsessionmealplans"     
            //--------------------------------------------------------------------------------------------------
            
            else if(command.equalsIgnoreCase("getallsessionmealplans")){
                Log.wdln("To execute /system/getallsessionmealplans..");
                
                StringBuilder response_string = new StringBuilder();
                
                MealPlan custom_mealplan = (MealPlan) request.getSession().getAttribute("custom_mealplan"); 
                
                response_string.append("CUSTOM_MEALPAN (isEmtpy: ").append(custom_mealplan.IsEmpty()).append(")<br>");
                response_string.append("---------------").append("<br>");
                response_string.append(custom_mealplan);
                response_string.append("---------------").append("<br>");
                
                Enumeration attributeNames = request.getSession().getAttributeNames();
                while (attributeNames.hasMoreElements()) {
                    MealPlan saved_mealplan = (MealPlan) request.getSession().getAttribute((String)attributeNames.nextElement());
                    
                    response_string.append("MEALPAN ")
                            .append(saved_mealplan.getKeyword())
                            .append("").append("(isEmtpy: ")
                            .append(saved_mealplan.IsEmpty()).append(")<br>");
                    response_string.append("---------------").append("<br>");
                    response_string.append(saved_mealplan);
                }
                response_string.append("---------------").append("<br>");
                HttpResponder.print(response, response_string);
                Log.wln("All session mealplans showed.");
                
            }
            
            //--------------------------------------------------------------------------------------------------
            //No command
            //--------------------------------------------------------------------------------------------------
            
            else{
                
            }
            
        //--------------------------------------------------------------------------------------------------
        //END
        //Exceptions:
        //--------------------------------------------------------------------------------------------------
            
        /*}catch(IOException e){
            Log.eln("handler_recipes: IOException: "+e);
            RequestDispatcher rd = request.getRequestDispatcher("dispatch_ooops.jsp");
            rd.include(request, response);*/
        }catch(NullPointerException e){
            Log.eln("handler_recipes: SQLException "+e);
            RequestDispatcher rd = request.getRequestDispatcher("/dispatch_ooops.jsp");
            rd.include(request, response);
        /*}catch(ServletException e){
            Log.eln("handler_recipes: ServletException: "+e);
            RequestDispatcher rd = request.getRequestDispatcher("dispatch_ooops.jsp");
            rd.include(request, response);*/
        }catch(PersistenceException e){
            Log.eln("handler_recipes: PersistenceException: "+e);
            RequestDispatcher rd = request.getRequestDispatcher("/dispatch_ooops.jsp");
            rd.include(request, response);
        }catch(FileNotFoundException fnfe){
            Log.eln("handler_recipes: FileNotFoundException: "+fnfe);
            RequestDispatcher rd = request.getRequestDispatcher("/dispatch_ooops.jsp");
            rd.include(request, response);
        }catch(Exception e){
            Log.eln("handler_recipes: unknown Exception: "+e);
            RequestDispatcher rd = request.getRequestDispatcher("/dispatch_ooops.jsp");
            rd.include(request, response);
        }
    
    }
    
}
