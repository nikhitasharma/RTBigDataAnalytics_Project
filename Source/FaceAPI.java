import java.net.URI;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class FaceAPI
{
    public static void main(String[] args)
    {
        HttpClient httpclient = HttpClients.createDefault();

        try
        {
            URIBuilder builder = new URIBuilder("https://api.projectoxford.ai/face/v1.0/detect");

            builder.setParameter("returnFaceId", "true");
            builder.setParameter("returnFaceLandmarks", "false");
            builder.setParameter("returnFaceAttributes", "{string}");

            URI uri = builder.build();


            HttpPost request = new HttpPost(uri);
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Ocp-Apim-Subscription-Key", "{8bd700438cf34ce3b1ec88bfdb526c3f}");


            // Request body
            StringEntity reqEntity = new StringEntity("https://farm9.staticflickr.com/8854/29729020925_24133ce29d_m.jpg");
            //reqEntity   .setContentType("application/json");
            request.setEntity(reqEntity);

            HttpResponse response = httpclient.execute(request);
            System.out.println(response.toString());
            String responseString = new BasicResponseHandler().handleResponse(response);
            System.out.println(responseString);
            HttpEntity entity = response.getEntity();

            //String responseString = EntityUtils.toString(entity, "UTF-8");
            System.out.println(responseString);

            if (entity != null)
            {
                System.out.println(EntityUtils.toString(entity));
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
}