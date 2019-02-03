# meallion

meallion is an AI-based universal online grocery ordering system based on a scaleable and dynamic recipes database.

:tomato::eggplant::corn::tangerine::pizza::spaghetti::stew:

The meallion project consists of a mySQL recipe database and allowes recipe searches by keywords or ingredients. The search queries are run by Apache Lucene, one of the worlds most powerful full-text search engine algorithm that is also used by Twitter(c).
The database consists of a simplified structure of recipes, ingredients and menu objects as well as joint-tables: - a recipe is a compilation of ingredients. - a menu is a compilation of recipes.

On top, meallion has a fully-featured html front-end that allows users to select recipes, save them in personalized HTTP sessions and compile grocery carts.
These generated grocery shopping carts can be exported as JSON or XMLs strings and used universially - e.g. to send to grocery suppliers as a qualified consumer lead. 

In addition to the front-end, meallion offers widgets, that can be integrated on food bloggers pages. These widgets allow users to add blogger recipes to users shopping cart.

The meallion database on meallion.de is constantly growing. To help the database scaling, meallion integrates an AI-based ingredients detection features for food pictures. As an example: uploading a picture of a plate of Paella will let meallion detect the ingredient "rice".

Example pages of the running code are live on https://www.meallion.de.

# 1. Database / Base Model

meallion uses the Java hibernate / JPA framework to communicate with mySQL. The base model includes the following object in /orm (object relationship model):
 
- Recipe
- Ingredient
- MealPlan
- IngredientRecipe (matching table)
- MealPlanRecipe (matching table)

- IngredientRecipePK (primary key object for matching purposes)
- MealPlanRecipePK (primary key object for matching purposes)

The object MealPlanIngredient is NOT part of the orm part. This object is temporarily generated by MealPlan.GetMealPlanIngredients() to dynamically match ingredients to mealplans.
Before creating a MealPlanIngredient, you should ideally update the ingredient amounts of a MealPlan first: UpdateIngredientAmounts(). This function merges all ingredients of all recipes in a mealplan.

Example:
One MealPlan holds these tree Recipes: "One Potato with eggs", "One Potato with butter", "One Potato with sauce".
Calling MealPlan.UpdateIngredientAmounts() will merge the total ingredients together, i.e. calculating that 3 potatoes are needed in total.
Calling MealPlan.GetMealPlanIngredients() will then return a list of MealPlanIngredient objects, which holds one Ingredient object and a amount number.

# 2. Client-server communication

meallion.de offers several webhooks to interact with the server:

https://meallion.de/ingredients
https://meallion.de/recipes
https://meallion.de/menus
https://meallion.de/system
https://meallion.de/embed
https://meallion.de/upload
https://meallion.de/recipes


A. ingredients

	Allows interaction with the ingredients database.
	
	Command: [name of ingredient]
	Parameter: none
	Example: https://meallion.de/ingredients/potato
	
	Response: Returns a plain string of potato ingredient
	
	Command: getbyid
	Parameter: id
	Example: https://meallion.de/ingredients/getbyid?id=2
	Response: Returns a plain string of ingredient with the id=2
		
	
## B. recipes

	Allows interaction with the recipes database.
	
	
	Command: [name of recipe]
	Parameter: none
	Example: https://meallion.de/recipes/spaghetti-bolognese
	Response: Returns a fully featured and css-designed recipe html page
	
	Command: request_selection
	Parameter: buget, time, veggie, vegan, tags
	Example: https://www.meallion.de/recipes/request_selection?budget=10000&time=10000&veggie=false&vegan=false&tags=tags%3Ditalian%252Cquick%252Chaute%2520cuisine
	Response: Returns a html page with a list of fully featured and css-designed recipes objects (search results)
	
	
## C. menus

	Allows interaction with the menus database.
	
	
	Command: none
	Parameter: none
	Example: https://meallion.de/menus
	Response: Returns a fully featured and css-designed html page of a user's current menu (saved in his http session)
	
	Command: [name of menu]
	Parameter: none
	Example: https://meallion.de/menu/fit-in-20-days
	Response: Returns a fully featured and css-designed menu html page
	
	Command: request_selection
	Parameter: buget, veggie, vegan, tags
	Example: https://www.meallion.de/recipes/request_selection?budget=10000&veggie=false&vegan=false&tags=tags%3Ditalian%252Cquick%252Chaute%2520cuisine
	Response: Returns a html page with a list of fully featured and css-designed menus objects (search results)
	
	Command: change_portions
	Parameter: mealplan_keyword, recipeid, portions
	Example: https://www.meallion.de/recipes/change_portions?mealplan_keyword=current_mealplan?recipeid=45?portions=4?request_ingredient_list=true
	Response: if request_ingredient_list=true: Returns a fully featured and css-designed ingredients list in html; if request_ingredient_list=false: no response
	
	Command: save (saves current session mealplan into the database)
	Parameter: name recipeid, descr
	Example: https://www.meallion.de/recipes/change_portions?name=vegan-for-beginners?descr=this is a hardcore 1 month mealplan without animal products
	Response: if request_ingredient_list=true: Returns a fully featured and css-designed ingredients list in html; if request_ingredient_list=false: no response
	
		
## D. upload

	Allows to upload a food pic for an AI to detect the ingredients.
	
	Command: request_selection
	Parameter: image object (multipart http post)
	Example: n.m.
	Response: Returns a html page showing the image and the AI outcome.
	
## E. system

	Allows general system administration.
	
	
	Command: heap
	Parameter: none
	Example: https://meallion.de/system/heap
	Response: Returns plain string JRE heap information
	
	Command: reindex
	Parameter: none
	Example: https://meallion.de/system/reindex
	Response: "Reindexed" if successfully reindexed the database for the Apache Lucene search engine
	
	Command: echo
	Parameter: none
	Example: https://meallion.de/system/echo
	Response: "echo echo.."
	
	Command: clear
	Parameter: none
	Example: https://meallion.de/system/clear
	Response: "success" if the database chache is sucessfully cleared
	
	Command: email_address_input
	Parameter: none
	Example: https://meallion.de/system/email_address_input?email_address=christoph.drayss@gmail.com
	Response: none. Saves the email address in the Java log files for contact purposes.
	
	Command: getallsessionmealplans
	Parameter: none
	Example: https://meallion.de/system/getallsessionmealplans
	Response: Returns plain string of a list of a user's current session mealplans.

# 3. Search Engine

meallion.de runs Solr (http://meallion.de:8983/solr/meallion). 
The features.SearchEngine object can run searches based on user input on the main page. The result is a SearchResults object, which holds a list of recipes and menus found as well as a meta integer, giving information about how/if the search went well.

The method SearchEngine.IndexAll() downloads the entire database and reindexes it into Solr documents.

# 4. Init to run on local machine

1. In the resources folder, make sure the persistence.xml file contains the content of the file persistence_FOR LOCAL USE.xml.
2. Copy the meallion.conf file into the WEB-INF folder
3. replace https://www.meallion.de with http://localhost/Meallion everywhere in the project
3. Now meallion should run on local machine, connecting to meallion.de mySQL and Lucene.

