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
import utils.URIAnalyser;

/**
 *
 * @author chris
 */
public class handler_examples {
    private SQL sql;
    private SearchEngine searchengine;
    
    public handler_examples(SQL sql,SearchEngine searchengine) {
        this.sql = sql;
        this.searchengine = searchengine;
    }
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try{
            String command = URIAnalyser.getCommand(request.getRequestURI(), "examples");
            Log.wdln("/examples: command="+command); 
            
            //--------------------------------------------------------------------------------------------------
            //Catch if a file is requested here:  
            //--------------------------------------------------------------------------------------------------
            
            if(command.contains(".")){
                throw new FileNotFoundException(command);
            }
            
            //--------------------------------------------------------------------------------------------------
            // No command --> nothing
            //--------------------------------------------------------------------------------------------------
            
            else if(command.equalsIgnoreCase("")){
                
            }
            
            //--------------------------------------------------------------------------------------------------
            //Default -->  command is example name
            //--------------------------------------------------------------------------------------------------
            
            else if(!command.equalsIgnoreCase("")){
                
                
                if(command!=null){
                    Log.wdln("dispatching jsp: "+command);
                    //RequestDispatcher rd = request.getRequestDispatcher("/dispatch_notfound.jsp");
                    RequestDispatcher rd = request.getRequestDispatcher("/example_pool/"+command+".jsp");
                    rd.include(request, response);
                }else{
                    RequestDispatcher rd = request.getRequestDispatcher("/dispatch_notfound.jsp");
                    rd.include(request, response);
                }
            }
            
            //--------------------------------------------------------------------------------------------------
            //END
            //Exceptions:
            //--------------------------------------------------------------------------------------------------
            
            }catch(IOException e){
            Log.eln("in Examples_ IOException: "+e);
            Log.eln("in Examples_ IOException: "+e.getLocalizedMessage());
            RequestDispatcher rd = request.getRequestDispatcher("/dispatch_ooops.jsp");
            rd.include(request, response);
        }catch(NullPointerException e){
            Log.eln("in Examples_ SQLException "+e);
            Log.eln("in Examples_ SQLException "+e.getLocalizedMessage());
            RequestDispatcher rd = request.getRequestDispatcher("/dispatch_ooops.jsp");
            rd.include(request, response);
        }catch(ServletException e){
            Log.eln("in Examples_ ServletException: "+e);
            Log.eln("in Examples_ ServletException: "+e.getLocalizedMessage());
            RequestDispatcher rd = request.getRequestDispatcher("/dispatch_ooops.jsp");
            rd.include(request, response);
        }catch(PersistenceException e){
            Log.eln("in Examples_ PersistenceException: "+e);
            Log.eln("in Examples_ PersistenceException: "+e.getLocalizedMessage());
            RequestDispatcher rd = request.getRequestDispatcher("/dispatch_ooops.jsp");
            rd.include(request, response);
        }catch(Exception e){
            Log.eln("in Examples_ unknown Exception: "+e);
            Log.eln("in Examples_ unknown Exception: "+e.getLocalizedMessage());
            RequestDispatcher rd = request.getRequestDispatcher("/dispatch_ooops.jsp");
            rd.include(request, response);
        }
    }
}