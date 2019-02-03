<!DOCTYPE html>
<html lang="en">
    <head>
        <%@include file="../standard_head.jsp" %>
        <title>Meallion Embed Beispiel</title>
        
        <link rel="icon" href="https://www.meallion.de/images/elements/meallion.ico" type="image/x-icon">

        <!-- font-awesome -->
        <link href="http://localhost:8085/css/font-awesome.min.css" rel="stylesheet">
        <!--[if lt IE 9]>
        <script src="//html5shiv.googlecode.com/svn/trunk/html5.js"></script>
        <![endif]-->
        <!--[if IE 7]>
        <link href="css/font-awesome-ie7.css" rel="stylesheet">
        <![endif]-->
        <!-- font- signika medium -->
        <link href="https://fonts.googleapis.com/css?family=Signika" rel="stylesheet">

        <!-- Bootstrap core CSS -->
        <link href="https://www.meallion.de/css/bootstrap.css" rel="stylesheet">

        <!-- custom CSS -->
        <link href="https://www.meallion.de/css/style.css" rel="stylesheet">
        
        <link href="https://www.meallion.de/css/header.css" rel="stylesheet">

            
            <!--additional head tags -->
            <link href="https://www.meallion.de/css/dispatch_recipe.css" rel="stylesheet">
            <link href="https://www.meallion.de/css/dispatch_recipe.css" rel="stylesheet">
            
            <meta property="og:title" content="Krautwickel mit Reis und Quark" />
            <meta property="og:type" content="website" />
            <meta property="og:url" content="/meals-v1/CookBook?recipe=krautwickel-mit-reis-und-quark" />
            <meta property="og:image" content="https://www.meallion.de/images/elements/example_page_4.png" />	
        
    </head>
    <body>
        <%@include file="/header.jsp"%>
        <div class="container main-page-container">

                <div id="headline_section" class="row">
                    <div class="col-lg-6 col-md-6 col-sm-6 recipe-headline-left">
                        <div id="recipe_image" class="ingredient-element-img" style="background-image: url(https://www.meallion.de/images/elements/example_page_4.png)"></div>
                    </div>
                    <div class="col-lg-6 col-md-6 col-sm-6 recipe-headline-right">
                        <h1>Lass dir die Zutaten eines Gerichtes sagen!</h1><br>
                        <div class="recipe-headline-features col-12">
                            <div class="row">
                                <div class="col-lg-6 col-md-6 col-sm-6">

                                    
                                    <div class="row">
                                        <div class="col-lg-12 col-md-12 col-sm-12">
                                            <form action="https://www.meallion.de/upload" id="fileuploadform" enctype="multipart/form-data" method="post">
                                                <input type="file" name="file" id="selectfile" class="btn btn-primary" />
                                                <input type="submit" id="submitfile" class="btn btn-primary" />
                                            </form>

                                            <button id="uploadbutton" class="btn btn-primary"/>Upload!</button>
                                        </div>
                                    </div>

                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                
            </div>
            <%@include file="/footer.jsp"%>
        <script>
            $("#fileuploadform").hide();

            $( "#uploadbutton" ).click(function() {
              $("#selectfile" ).click();
            });

            $("#fileuploadform").change(function() {$("#submitfile").click();  });
        </script>
        
    </body>
</html>
