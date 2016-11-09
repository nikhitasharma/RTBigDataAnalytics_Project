import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.migcomponents.migbase64.Base64;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import sun.misc.IOUtils;

import java.io.*;
import java.util.Properties;

public class SimpleProducer {
    private static Producer<Integer, String> producer;
    private final Properties properties = new Properties();

    public SimpleProducer() {
        properties.put("metadata.broker.list", "localhost:9092");
        properties.put("serializer.class", "kafka.serializer.StringEncoder");
        properties.put("request.required.acks", "1");
        properties.put("message.max.bytes", "10000000");
        producer = new Producer<Integer, String>(new ProducerConfig(properties));
    }


    public static String sendFile(String file) {
        String encodedString = null;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            encodedString = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return encodedString;
    }

        /*InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (Exception e) {
            // TODO: handle exception
        }
        byte[] bytes;
        byte[] buffer = new byte[8192];
        int bytesRead;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        bytes = output.toByteArray();
        encodedString = bytes.toString();*/
        //return encodedString;


    public static void main(String[] args) {
        new SimpleProducer(); //Setting properties for kafka producer
        String topic = args[0];  //Topic Name
        String msg = sendFile(args[1]); //Encoding the Video
        //Iterable<String> result = Splitter.fixedLength(100000).split(msg); //Splitting the video file
        //String[] parts = Iterables.toArray(result, String.class); //Parts of video file
        //for(int i=0; i<parts.length; i++){
        KeyedMessage<Integer, String> data = new KeyedMessage<Integer, String>(topic, msg);
        // System.out.println(parts[i]);
        producer.send(data);
        //}
        System.out.println("File Sent");
        producer.close();
    }
}


