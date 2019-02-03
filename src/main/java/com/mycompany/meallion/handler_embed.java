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
import orm.MealPlan;
import orm.Recipe; 
import sql.SQL;
import utils.Log;
import utils.RecipeBuilder;
import utils.RecipeStep;
import utils.URIAnalyser;

/**
 *
 * @author chris
 */
public class handler_embed {
    private SQL sql;
    private SearchEngine searchengine;
    
    public handler_embed(SQL sql,SearchEngine searchengine) {
        this.sql = sql;
        this.searchengine = searchengine;
    }
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        
        try{
            String command = URIAnalyser.getCommand(request.getRequestURI(), "embed");
            Log.wdln("/recipes: command="+command);
            
            //--------------------------------------------------------------------------------------------------
            //Catch if a file is requested here:  
            //--------------------------------------------------------------------------------------------------
            
            if(command.contains(".")){
                throw new FileNotFoundException(command);
            }
            
            //--------------------------------------------------------------------------------------------------
            //"recipe"
            //--------------------------------------------------------------------------------------------------
            
            else if(!command.equalsIgnoreCase("recipe")){
                Log.wdln("To execute /embed/recipe");
                
                String recipe_keyword = request.getParameter("recipe");

                Log.debug_w("Query meal from database: "+recipe_keyword);

                List<Recipe> recipe_results = sql.getEM().createNamedQuery("Recipe.findByKeyword",Recipe.class).setParameter("keyword", recipe_keyword).getResultList();

                Log.debug_wln("found "+recipe_results.size()+" results");

                Recipe r = recipe_results.get(0);

                if(r!=null){
                    request.setAttribute("recipe", r);
                    RequestDispatcher rd = request.getRequestDispatcher("/dispatch_embed_interface.jsp");
                    rd.include(request, response);
                }else{
                    RequestDispatcher rd = request.getRequestDispatcher("/dispatch_notfound.jsp");
                    rd.include(request, response);
                }
            }
            
            //--------------------------------------------------------------------------------------------------
            //"menu"
            //--------------------------------------------------------------------------------------------------
            
            else if(!command.equalsIgnoreCase("menu")){
                Log.wdln("To execute /embed/menu");
                
                Log.debug_w("Preparing mealplan..");

                MealPlan current_mealplan = (MealPlan) request.getSession().getAttribute("custom_mealplan");
                Log.debug_w("Mealplan prepared");

                if(current_mealplan!=null){
                    if(!current_mealplan.IsEmpty()){
                        current_mealplan.UpdateIngredientAmounts();
                        request.setAttribute("mealplan", current_mealplan);
                        request.setAttribute("mealplan_status", "temporary");
                        RequestDispatcher rd = request.getRequestDispatcher("dispatch_embed_mealplan.jsp");
                        rd.include(request, response);
                    }else{
                        Log.debug_wdln("custom_mealplan is empty");
                        RequestDispatcher rd = request.getRequestDispatcher("dispatch_embed_no_mealplan.jsp");
                        rd.include(request, response);
                    }
                }else{
                    Log.debug_wdln("custom_mealplan is empty");
                    RequestDispatcher rd = request.getRequestDispatcher("dispatch_embed_no_mealplan.jsp");
                    rd.include(request, response);
                }
            }

            //--------------------------------------------------------------------------------------------------
            //END
            //Exceptions:
            //--------------------------------------------------------------------------------------------------

        }catch(IOException e){
            Log.eln("/embed: IOException: "+e);
            RequestDispatcher rd = request.getRequestDispatcher("dispatch_embed_ooops.jsp");
            rd.include(request, response);
        }catch(NullPointerException e){
            Log.eln("/embed: NPEException "+e);
            RequestDispatcher rd = request.getRequestDispatcher("dispatch_embed_ooops.jsp");
            rd.include(request, response);
        }catch(ServletException e){
            Log.eln("/embed: ServletException: "+e);
            RequestDispatcher rd = request.getRequestDispatcher("dispatch_embed_ooops.jsp");
            rd.include(request, response);
        }catch(PersistenceException e){
            Log.eln("/embed: PersistenceException: "+e);
            RequestDispatcher rd = request.getRequestDispatcher("dispatch_embed_ooops.jsp");
            rd.include(request, response);
        }catch(Exception e){
            Log.eln("/embed: unknown Exception: "+e);
            RequestDispatcher rd = request.getRequestDispatcher("dispatch_embed_ooops.jsp");
            rd.include(request, response);
        }
    }
}
