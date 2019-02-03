/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

/**
 *
 * @author chris
 */
public final class URIAnalyser {
    
    /**
     * Picks a command from a specific URI after the specific handler (e.g. "/recipes/") but does not consider any parameters ("?" and thereafter).
     * Example1: request: www.meallion.de/recipe/request_selection?budget=400&vegan=false&veggie=false&time=20
     *          result: "request_selection"
     * Example2: request: www.meallion.de/recipe/spaghetti-pomodoro
     *          result: "spaghetti-pomodoro"
     * Example3: request: www.meallion.de/recipe
     *          result: ""
     * Example4: request: www.meallion.de/recipe/
     *          result: ""
     * @return A string containing the command
     */
    
    public static final String getCommand(String uri, String handler){
        
        int from=0;
        int to=0;
        
        if(uri.contains("?")){
            from = uri.indexOf("/"+handler.replace("/", "")) + handler.replace("/", "").length() + 2;
            to = uri.indexOf("?");
        }else{
            
            from = uri.indexOf("/"+handler.replace("/", "")) + handler.replace("/", "").length() + 2;
            to = uri.length();
        }
        
        if(to>=from+1){
            return uri.substring(from, to);
        }else{
            return "";
        }
        
    }
    
    public static final int findFromRight(String str, char character){
        
        for(int i=str.length();i>=0;i--){
            if(str.charAt(i)==character){
                return i;
            }
        }
        return -1;
    }
}
