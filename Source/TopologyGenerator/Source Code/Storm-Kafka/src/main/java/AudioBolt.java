
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
/**
 * Created by gt784 on 12/3/2016.
 */
public class AudioBolt extends BaseBasicBolt {
    Map<String, Integer> counts = new HashMap<String, Integer>();
    private static final Logger LOG = LoggerFactory.getLogger(AudioBolt.class);
    public void execute(Tuple tuple, BasicOutputCollector basicOutputCollector) {
        try {
            String s = tuple.getString(0);
            String r[] = s.split("_");
            String filename = r[0];
            String features = r[1];
            LOG.info("The features are :"+features);


            double[] feature = fromString(r[1]);
            LOG.info("The sample feature of audio recognition is"+feature[2]);
            //String featureDoubles[] = features.split(";");
            // double feature3 = Double.parseDouble(featureDoubles[2]);
            boolean audio=false;
            audio = checkAudio(feature);

            LOG.info("The decision of audio recognition is"+audio);
//            double[][] windows = new double[r.length - 3][];
//            if (r.length > 3) {
//
//                for (int i = 3; i < r.length; i++) {
//
//                    String a[] = r[i].split("--");
//                    windows[Integer.parseInt(a[0])] = fromString(a[1]);
//
//                }
//            }
//             insertIntoMongoDB(word, count);
//            FeatureExtractor featureExt = new ZeroCrossings();
//            double[] result = featureExt.extractFeature(sample, sampleRate, otherFeatures);
            Boolean check = checkAudio(feature);
            insertIntoMongoDB(check);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        //outputFieldsDeclarer.declare(new Fields("filename","ZeroCrossings"));
        outputFieldsDeclarer.declare(new Fields("context","status"));
    }

    private static double[] fromString(String string) {
        String[] strings = string.split(";");
        double result[] = new double[strings.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = Double.parseDouble(strings[i]);
        }
        return result;
    }

    public static void insertIntoMongoDB(Boolean check) {
        String API_KEY = "eqaDlZIPyrcYKUTb_RaGWzkACO1NpH8m";
        String DATABASE_NAME = "rbdanalytics";
        String COLLECTION_NAME = "results";
        String urlString = "https://api.mlab.com/api/1/databases/" +
                DATABASE_NAME + "/collections/" + COLLECTION_NAME + "?apiKey=" + API_KEY;
        LOG.info(urlString);

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
            jsonObject.put("Context", "Audio");
            jsonObject.put("Decision", check);
            jsonObject.put("Timestamp", System.currentTimeMillis());
            writer.write(jsonObject.toString());
            LOG.info(jsonObject.toString());
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

    public Boolean checkAudio(double[] feature) {
        if (feature[0] <= 8.818) {
            return true;
        }
        else if (feature[0] > 8.818){
            return false;
        }
        return false;
    }
}
