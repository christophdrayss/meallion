package features;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.api.request.model.PredictRequest;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.model.Model;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import okhttp3.OkHttpClient;


public class ImageTagger {
    
    Model<Concept> generalModel;
    ClarifaiClient client;
    static String CLARIFAI_ID = "f40f1bc3c2ec4a78b608949bc59dcc61";
    String current_url;
    List<Concept> current_concepts;
    
    public ImageTagger(){
        client = new ClarifaiBuilder(CLARIFAI_ID).client(new OkHttpClient()).buildSync();
        generalModel = client.getDefaultModels().generalModel();
    }

    public String getCurrentURL() {
        return current_url;
    }

    public void setCurrentURL(String current_url) {
        this.current_url = current_url;
    }
    
    public String LoadImage(String location){
        return current_url = location;
    }
    
    public List<Concept> CreateConcepts(){
        PredictRequest<Concept> ai_request = generalModel.predict().withInputs(ClarifaiInput.forImage(this.current_url));
        List<ClarifaiOutput<Concept>> result = ai_request.executeSync().get();

        ClarifaiOutput<Concept> perpicture = result.get(0);

        List<Concept> conlist = perpicture.data();
        this.current_concepts = conlist;
        return conlist;
    }
    
    public ArrayList<String> GetTags(){
        ArrayList<String> res = new ArrayList<>();
        for(Concept c : this.current_concepts){
            res.add(c.name());
        }
        return res;
    }
}
