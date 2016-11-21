package modelBuilder;

import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by Naga on 27-10-2016.
 */
public class MongoCall {

    public static void insertIntoMongoDB(String model) {
        String API_KEY = "eqaDlZIPyrcYKUTb_RaGWzkACO1NpH8m";
        String DATABASE_NAME = "rbdmodel";
        String COLLECTION_NAME = "modelcollection";
        String urlString = "https://api.mlab.com/api/1/databases/" +
                DATABASE_NAME + "/collections/" + COLLECTION_NAME + "?apiKey=" + API_KEY;


        StringBuilder result = null;
        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Model", model);
            writer.write(jsonObject.toString());

            writer.close();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(urlConnection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Uploaded data to Mongo");

    }
}
