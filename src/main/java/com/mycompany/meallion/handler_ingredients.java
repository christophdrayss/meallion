/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.meallion;

import features.SearchEngine;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import javax.persistence.PersistenceException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import orm.Ingredient;
import sql.SQL;
import utils.HttpResponder;
import utils.Log;
import utils.URIAnalyser;

/**
 *
 * @author chris
 */
public class handler_ingredients {
    private SQL sql;
    private SearchEngine searchengine;
    
    public handler_ingredients(SQL sql,SearchEngine searchengine) {
        this.sql = sql;
        this.searchengine = searchengine;
    }
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try{
            String command = URIAnalyser.getCommand(request.getRequestURI(), "ingredients");
            Log.wdln("/ingredients: command="+command);
            
            //--------------------------------------------------------------------------------------------------
            //Catch if a file is requested here:  
            //--------------------------------------------------------------------------------------------------
            
            if(command.contains(".")){
                throw new FileNotFoundException(command);
            }
            
            //--------------------------------------------------------------------------------------------------
            //Command "getbyid"     
            //--------------------------------------------------------------------------------------------------
            
            if(command.equalsIgnoreCase("getbyid")){
                Log.wdln("To execute /ingredients/getbyid..");
                
                int id = Integer.parseInt(request.getParameter("id"));
                
                List<Ingredient> ingredient_results = sql.getEM().createNamedQuery("Ingredient.findById",Ingredient.class).setParameter("id", id).getResultList();
                
                response.setContentType("text/html");
                HttpResponder.print(response, ingredient_results.get(0));
                Log.wln("done.");
            }
            
            //--------------------------------------------------------------------------------------------------
            //Default command -> name of ingredient
            //--------------------------------------------------------------------------------------------------
            
            else if(!command.equalsIgnoreCase("")){
                
                Log.wdln("To execute /ingredients/<Ingredient Name>..");
                
                List<Ingredient> ingredient_results = sql.getEM().createNamedQuery("Ingredient.findByKeyword",Ingredient.class).setParameter("keyword", command).getResultList();
                
                Log.debug_wln("found "+ingredient_results.size()+" results");
                
                HttpResponder.print(response, ingredient_results.get(0));
                Log.wln("done.");
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
