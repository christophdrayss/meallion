package features;

import java.io.File;
import orm.MealPlan;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import javax.persistence.Transient;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import orm.IngredientRecipe;
import orm.MealPlanRecipe;
import orm.MealPlanRecipePK;
import sql.SQL;
import utils.Log;

public class FileUpload {
    
    public static String Save(HttpServletRequest request, String uploaddir, String url_prefix){
        try{ 

            File uploadFolder = new File(uploaddir);
            if (!uploadFolder.exists()) {
                uploadFolder.mkdirs();
            }
            
            int part_count = request.getParts().size();
            Log.wln("multimedia parts received: "+part_count);
            
            String fileName = "";

            // write all files in upload folder
            for (Part part : request.getParts()) {
                if (part != null && part.getSize() > 0) {
                    //String fileName = FileUpload.getCurrentTimeStamp()+"-"+part_count;
                    fileName = FileUpload.getCurrentTimeStamp()+"-"+Math.round(Math.random()*10000);

                    String contentType = part.getContentType();
                    if (contentType.equalsIgnoreCase("image/jpeg")) {
                        fileName+=".jpg";
                    }
                    if (contentType.equalsIgnoreCase("image/gif")) {
                        fileName+=".gif";
                    }
                    if (contentType.equalsIgnoreCase("image/png")) {
                        fileName+=".png";
                    }
                    
                    Log.wln("save file at: "+uploaddir + File.separator + fileName);
                    
                    //part.write(uploadFilePath + File.separator + fileName);uploaddir
                    part.write(uploaddir +"/"+ fileName);
                }
            }
            
            return url_prefix+"/"+fileName;
             
        
        }catch(IOException ioe){
            Log.eln("FileUpload: ioe: "+ioe.getLocalizedMessage());
            return null;
        }catch(ServletException se){
            Log.eln("FileUpload: se: "+se.getLocalizedMessage());
            return null;
        }
    }
        
    public static String getCurrentTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMddHHmmss");
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate;
    }
        
}
