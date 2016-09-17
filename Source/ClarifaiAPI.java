/**
 * Created by gt784 on 9/16/2016.
 */
import com.clarifai.api.ClarifaiClient;
import com.clarifai.api.RecognitionRequest;
import com.clarifai.api.RecognitionResult;
import com.clarifai.api.Tag;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ClarifaiAPI {
    public static void main(String[] args) throws IOException {
        ClarifaiClient client = new ClarifaiClient("_Q6YsOWSuTere8ouVnGKd95BaOau68eDXhZ_ltzU", "FNcZvw66dQ5zoANE_NfkMQVKYsLbTCh9_oz7_1x4");
        File path1 = new File("D:\\Tutorial code\\MainFrameDetection\\output\\frames\\new133.jpg");

        List<RecognitionResult> results =
                client.recognize(new RecognitionRequest(path1));

        List<String> tagList = new ArrayList<String>();
        for (Tag tag : results.get(0).getTags()) {
            System.out.println(tag);
            tagList.add(tag.getName());
        }
    }
}

