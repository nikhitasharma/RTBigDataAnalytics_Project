/**
 * Created by Naga on 04-10-2016.
 */
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class SimpleConsumer {

    private final ConsumerConnector consumer;
    private final String topic;
    private final String path;

    public SimpleConsumer(String zookeeper, String groupId, String topic, String path) {
        Properties props = new Properties();
        props.put("zookeeper.connect", zookeeper);
        props.put("group.id", groupId);
        props.put("zookeeper.session.timeout.ms", "500");
        props.put("zookeeper.sync.time.ms", "250");
        props.put("auto.commit.interval.ms", "1000");

        consumer = Consumer.createJavaConsumerConnector(new ConsumerConfig(props));
        this.topic = topic;
        this.path = path;
    }

    public void testConsumer() throws UnsupportedEncodingException {
        Map<String, Integer> topicCount = new HashMap<String, Integer>();
        topicCount.put(topic, 1);

        BufferedWriter bw = null;
        try {
            FileWriter fw = new FileWriter(path);
            bw = new BufferedWriter(fw);
        }  catch (IOException e) {
            e.printStackTrace();
        }
        Map<String, List<KafkaStream<byte[], byte[]>>> consumerStreams = consumer.createMessageStreams(topicCount);
        List<KafkaStream<byte[], byte[]>> streams = consumerStreams.get(topic);
        System.out.println(streams.size());
        for (final KafkaStream stream : streams) {
            ConsumerIterator<byte[], byte[]> it = stream.iterator();
            while (it.hasNext()) {
                String value = new String(it.next().message(), "UTF-8");
                try {
                    bw.write(value);
                    System.out.println(value);
                    bw.newLine();
                    if(value.endsWith("END")){
                        System.out.println("********Written Message to File********");
                        bw.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (consumer != null) {
            consumer.shutdown();
        }
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        String topic = args[0]; //Topic Name
        String path = args[1];
        SimpleConsumer simpleConsumer = new SimpleConsumer("localhost:2181", "testgroup", topic, path);
        simpleConsumer.testConsumer();
    }

}