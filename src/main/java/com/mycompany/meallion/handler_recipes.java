/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.meallion;

import features.SearchEngine;
import features.SearchRequest;
import features.SearchResults;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import javax.persistence.PersistenceException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
public class handler_recipes {
    private SQL sql;
    private SearchEngine searchengine;
    
    public handler_recipes(SQL sql,SearchEngine searchengine) {
        this.sql = sql;
        this.searchengine = searchengine;
    }
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        
        try{
            String command = URIAnalyser.getCommand(request.getRequestURI(), "recipes");
            Log.wdln("/recipes: command="+command);
            
            //--------------------------------------------------------------------------------------------------
            //Catch if a file is requested here:  
            //--------------------------------------------------------------------------------------------------
            
            if(command.contains(".")){
                throw new FileNotFoundException(command);
            }
            
            //--------------------------------------------------------------------------------------------------
            //Command "request_selection"     
            //--------------------------------------------------------------------------------------------------
            
            if(command.equalsIgnoreCase("request_selection")){
                Log.wdln("To execute /recipes/request_selection..");
                
                SearchRequest search_request = new SearchRequest(SearchRequest.TYPE_RECIPE,
                    Integer.parseInt(request.getParameter("time")),
                    Double.parseDouble(request.getParameter("budget")),
                    request.getParameter("veggie"),
                    request.getParameter("vegan"),
                    request.getParameter("tags")
                );
                
                SearchResults searchresult = this.searchengine.runRequest(search_request);
                
                Log.debug_wdln("recipes/request_selection received search results: "+searchresult);
                
                //set the attributes which will be cought by the JSP:
                request.setAttribute("search_results", searchresult);
                request.setAttribute("mealplan", request.getSession().getAttribute("custom_mealplan"));
                
                //Dispatch recipe offerings
                HttpResponder.dispatch(request, response, "/dispatch_recipes_offering.jsp");
            }
            
            //--------------------------------------------------------------------------------------------------
            //Default -->  command is recipes name
            //--------------------------------------------------------------------------------------------------
            
            else if(!command.equalsIgnoreCase("")){
                Log.wdln("To execute /recipes/<RECIPE NAME>");
                
                String recipe_keyword = command;

                Log.debug_w("Query meal from database: "+recipe_keyword);
                
                List<Recipe> recipe_results = sql.getEM().createNamedQuery("Recipe.findByKeyword",Recipe.class).setParameter("keyword", recipe_keyword).getResultList();
                
                Log.debug_wln("found "+recipe_results.size()+" results");
                
                Recipe r = recipe_results.get(0); 

                if(r!=null){
                    
                    Log.debug_wln("Building RecipeStep List..");
                    
                    String recipe_json_manual = r.getBody();

                        List<RecipeStep> steps = RecipeBuilder.createRecipeList(recipe_json_manual);
                        request.setAttribute("recipe", r); 
                        request.setAttribute("steps", steps);
                        request.setAttribute("mealplan", request.getSession().getAttribute("custom_mealplan"));

                    Log.debug_wln("done building recipe list. dispatching recipe..");
                   
                    RequestDispatcher rd = request.getRequestDispatcher("/dispatch_recipe.jsp"); 
                    rd.include(request, response);
                }else{
                    RequestDispatcher rd = request.getRequestDispatcher("/dispatch_notfound.jsp");
                    rd.include(request, response);
                }
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