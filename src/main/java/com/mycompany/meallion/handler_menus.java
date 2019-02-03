/**
Mealplan in session:



 */
package com.mycompany.meallion;

import features.SearchEngine;
import features.SearchRequest;
import features.SearchResults;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Locale;
import javax.persistence.PersistenceException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import orm.MealPlan;
import sql.SQL;
import utils.HttpResponder;
import utils.Log;
import utils.URIAnalyser;

/**
 * 
 * Menu Servlet - Understanding Menus in Sessions:
 * 
 * The servlet creates a dispatch_menu.jsp file including a request for the ingredients and a list of the menu recipes
 * 
 * 
 * Every user gets a session with an array of Mealplan objects. Whenever a user access CookBook, the servlet checks, if there is a custom_mealplan MealPlan Object. If there is none, it creates one.
 * Whenever the user enters a pre-created menue, this servlet creates additional MealPlan objects with their keyword as unique name.
 * 
 * ----------------------
 * Menu status:
 * 
 * The dispatch_menu.jsp document has a tag called "mealplan_status", where it saves the data "mealplan_keyword"
 * <div id="mealplan_status" data-mealplan_keyword="custom_mealplan"></div>
 * 
 * This "mealplan_keyword" is either "custom_mealplan", if the user clicked on his own menu. Or it is the keyword of a specific pre-created/saved mealplan. For example:
 * <div id="mealplan_status" data-mealplan_keyword="menu2"></div>
 * 
 * This mealplan_keyword data is important so that the javascript code in dispatch_mealplan.js knows to which menu changes should apply (i.e. a user could have multiple, parallel menues in his session).
 * 
 * 
 * @author Christoph
 */
public class handler_menus {
    private SQL sql;
    private SearchEngine searchengine;
    
    public handler_menus(SQL sql,SearchEngine searchengine) {
        this.sql = sql;
        this.searchengine = searchengine;
    }
    
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try{
            
            String command = URIAnalyser.getCommand(request.getRequestURI(), "menus");
            Log.wdln("/menus: command="+command);
            
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
                Log.wdln("To execute /menus/request_selection..");
                
                SearchRequest search_request = new SearchRequest(SearchRequest.TYPE_MENU,
                    Double.parseDouble(request.getParameter("budget")),
                    request.getParameter("veggie"),
                    request.getParameter("vegan"),
                    request.getParameter("tags")
                );
                
                SearchResults searchresult = this.searchengine.runRequest(search_request);
                
                Log.debug_wdln("menu/request_selection received search results: "+searchresult);
                
                //set the attributes which will be cought by the JSP:
                request.setAttribute("search_results", searchresult);
                request.setAttribute("mealplan", request.getSession().getAttribute("custom_mealplan"));
                
                //Dispatch recipe offerings
                HttpResponder.dispatch(request, response, "/dispatch_mealplans_offering.jsp");
            }
            
            //--------------------------------------------------------------------------------------------------
            // No command --> run current mealplan
            //--------------------------------------------------------------------------------------------------
            
            else if(command.equalsIgnoreCase("")){
                Log.wdln("To execute /menus");
                
                MealPlan current_mealplan = (MealPlan) request.getSession().getAttribute("custom_mealplan");
                
                if((!current_mealplan.IsEmpty())){
                    current_mealplan.UpdateIngredientAmounts();
                    request.setAttribute("mealplan", current_mealplan);
                    request.setAttribute("mealplan_status", "temporary");
                    RequestDispatcher rd = request.getRequestDispatcher("/dispatch_mealplan.jsp");
                    rd.include(request, response);
                }else{
                    Log.debug_wdln("custom_mealplan is empty");
                    RequestDispatcher rd = request.getRequestDispatcher("/dispatch_no_mealplan.jsp");
                    rd.include(request, response);
                }
            }
            
            //--------------------------------------------------------------------------------------------------
            //"save"
            //--------------------------------------------------------------------------------------------------

            else if(command.equalsIgnoreCase("save")){
                Log.wdln("To execute /menus/save");
                
                try{
                    MealPlan tosave = (MealPlan) request.getSession().getAttribute("custom_mealplan");
                    Log.debug_wdln("Mealplan name to be saved: "+request.getParameter("name"));
                    String temp_keyword = request.getParameter("name").replace(" ","").toLowerCase(Locale.GERMANY);
                    Log.debug_wdln("Mealplan keyword to be saved: "+request.getParameter("name"));
                    
                    tosave.setName(request.getParameter("name"));
                    tosave.setDescr(request.getParameter("descr"));
                    tosave.setKeyword(temp_keyword);
                    tosave.setImg_urls("na");
                    tosave.setTags("na");
                    
                    tosave.setTransient_sql(this.sql);
                    
                    if(tosave.SavetoDB()==0){
                        Log.wdln("Mealplan saved: "+tosave.getName());
                        response.setContentType("text/html");
                        PrintWriter out = response.getWriter();
                        out.print("/menus?m="+tosave.getKeyword());
                        out.close();
                    }else{
                        this.sql.getEM().getTransaction().rollback();
                        HttpResponder.print(response,"name_already_exists");
                    }
                }catch(NullPointerException e){
                    Log.edln("/menu: NullPointerException while trying to save mealplan: "+e);
                    HttpResponder.print(response,"error");
                }   
            }
            
            //--------------------------------------------------------------------------------------------------
            //"change_portions"
            //--------------------------------------------------------------------------------------------------

            else if(command.equalsIgnoreCase("change_portions")){
                Log.wdln("To execute /menu/change_portions");
                
                //choose which mealplan shall be updated. Default is custom_mealplan: 
                
                // get unique mealplan keyword:
                String mealplan_keyword = request.getParameter("mealplan_keyword");
                
                Log.debug_wdln("requested mealplan: "+mealplan_keyword);
                MealPlan mealplan_to_change = (MealPlan) request.getSession().getAttribute(mealplan_keyword);
                
                //if portions is not 0, add or update the number of servings
                if(!request.getParameter("portions").equals("0")){
                    
                    Log.debug_wdln("Adding recipe with id: "+request.getParameter("recipeid")+", portions: "+request.getParameter("portions"));
                    
                    int recipeid = Integer.parseInt(request.getParameter("recipeid"));
                                        
                    mealplan_to_change.QuickAdd(recipeid,Integer.parseInt(request.getParameter("portions")));
                    
                    Log.debug_wdln("Recipe added.");

                //if portions is 0, remove the serving from the menu
                }else{
                    Log.debug_wdln("Removing Recipe with id: "+request.getParameter("recipeid"));
                    int recipeid = Integer.parseInt(request.getParameter("recipeid"));
                    mealplan_to_change.QuickRemove(recipeid);
                    Log.debug_wln("Done removing recipe with id: "+request.getParameter("recipeid"));
                }

                //Check if the request needs an ingredients list sent back. This is the case when the user does changes while beeing on the menu site. Then, the ingredients list must be updated with every change:
                if(request.getParameter("request_ingredient_list")!=null){
                    if(!request.getParameter("request_ingredient_list").equals("false")){
                        
                        Log.debug_wdln("Update the MealPlan's ingredient list..");//Update the MealPlan
                        mealplan_to_change.UpdateIngredientAmounts();
                        Log.debug_wdln("Mealplan updated.");
                        
                        Log.debug_wdln("Dispatching ingredient section.");
                        request.setAttribute("mealplan", mealplan_to_change);
                        RequestDispatcher rd = request.getRequestDispatcher("/dispatch_ingredients.jsp");
                        rd.include(request, response);
                    }
                }
                
            }
            
            //--------------------------------------------------------------------------------------------------
            //Default -->  command is menu name
            //--------------------------------------------------------------------------------------------------
            
            else if(!command.equalsIgnoreCase("")){
                Log.wdln("To execute /menu/<MENU NAME>");
                String mealplan_keyword = command;
                
                Log.debug_wdln("/menu: parameter: "+mealplan_keyword);
                
                List<MealPlan> mealplan_results = sql.getEM().createNamedQuery("MealPlan.findByKeyword",MealPlan.class).setParameter("keyword", mealplan_keyword).setHint(mealplan_keyword, sql).getResultList();
                
                Log.debug_wdln("/menu: found "+mealplan_results.size()+" results");
                
                if(mealplan_results.size()==1){
                
                    MealPlan m = mealplan_results.get(0);
 
                    m.setTransient_sql(this.sql);
                    m.UpdateIngredientAmounts();
                    
                    Log.debug_wln("/menu: found meal: "+m);
                    
                    if(request.getSession().getAttribute(m.getKeyword())==null){
                        request.getSession().setAttribute(m.getKeyword(),m);
                    }
                    
                    request.getSession().setAttribute(m.getKeyword(), m);

                    request.setAttribute("mealplan", m);
                    request.setAttribute("mealplan_status", "saved");
                    
                    RequestDispatcher rd = request.getRequestDispatcher("/dispatch_mealplan.jsp");
                    rd.include(request, response);
                }else{
                    RequestDispatcher rd = request.getRequestDispatcher("/dispatch_no_mealplan.jsp");
                    rd.include(request, response);
                }
                
            }
            
            //--------------------------------------------------------------------------------------------------
            //END
            //Exceptions:
            //--------------------------------------------------------------------------------------------------
            
            
        }catch(IOException e){
            Log.eln("/menu IOException: "+e);
            Log.eln("/menu IOException: "+e.getLocalizedMessage());
            RequestDispatcher rd = request.getRequestDispatcher("dispatch_ooops.jsp");
            rd.include(request, response);
        }catch(NullPointerException e){
            Log.eln("/menu SQLException "+e);
            Log.eln("/menu SQLException "+e.getLocalizedMessage());
            RequestDispatcher rd = request.getRequestDispatcher("dispatch_ooops.jsp");
            rd.include(request, response);
        }catch(ServletException e){
            Log.eln("/menu ServletException: "+e);
            Log.eln("/menu ServletException: "+e.getLocalizedMessage());
            RequestDispatcher rd = request.getRequestDispatcher("dispatch_ooops.jsp");
            rd.include(request, response);
        }catch(PersistenceException e){
            Log.eln("/menu PersistenceException: "+e);
            Log.eln("/menu PersistenceException: "+e.getLocalizedMessage());
            RequestDispatcher rd = request.getRequestDispatcher("dispatch_ooops.jsp");
            rd.include(request, response);
        }catch(Exception e){
            Log.eln("/menu unknown Exception: "+e);
            Log.eln("/menu unknown Exception: "+e.getLocalizedMessage());
            RequestDispatcher rd = request.getRequestDispatcher("dispatch_ooops.jsp");
            rd.include(request, response);
        }
    }
}
