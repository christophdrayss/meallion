/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package features;

/**
 *
 * @author chris
 */
public class SearchRequest {
    
    //Search parameters:
    
    //type: recipe or menu
    private int type;
    //time
    private int time;
    //budget
    private double budget;
    //veggie
    private String veggie;
    //vegan
    private String vegan;
    //tags array from free search bar
    private String tags;
    
    public static int TYPE_RECIPE = 1;
    public static int TYPE_MENU = 2;

    //Constructor for recipe searches
    public SearchRequest(int type, int time, double budget, String veggie, String vegan, String tags) {
        this.type = type;
        this.time = time;
        this.budget = budget;
        this.veggie = veggie;
        this.vegan = vegan;
        this.tags = tags;
    }
    
    //Constructor for menu searches
    public SearchRequest(int type, double budget, String veggie, String vegan, String tags) {
        this.type = type;
        this.time = 0;
        this.budget = budget;
        this.veggie = veggie;
        this.vegan = vegan;
        this.tags = tags;
    }
    
    public boolean isRecipesRequest(){
        return this.type==SearchRequest.TYPE_RECIPE;
    }
    
    public boolean isMenusRequest(){
        return this.type==SearchRequest.TYPE_MENU;
    }
    
    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public String getVeggie() {
        return veggie;
    }

    public void setVeggie(String veggie) {
        this.veggie = veggie;
    }

    public String getVegan() {
        return vegan;
    }

    public void setVegan(String vegan) {
        this.vegan = vegan;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
    
    
}
