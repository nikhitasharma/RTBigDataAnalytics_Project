import org.apache.log4j.BasicConfigurator;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.kafka.KafkaSpout;
import org.apache.storm.kafka.SpoutConfig;
import org.apache.storm.kafka.StringScheme;
import org.apache.storm.kafka.ZkHosts;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.topology.TopologyBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class StormKafkaMain {
    private static final String KAFKA_TOPIC ="auditest";
    private static final Logger LOG = LoggerFactory.getLogger(StormKafkaMain.class);
    //private static final String KAFKA_TOPIC_B ="testb";
    public static void main(String[] args) {
        LOG.info("Inside main");
        // TODO Auto-generated method stub
        BasicConfigurator.configure();

        if (args != null && args.length > 0)
        {
            try {
                StormSubmitter.submitTopology(
                        args[0],
                        createConfig(false),
                        createTopology());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        else
        {
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology(
                    "Storm_Kafka_Sample",
                    createConfig(true),
                    createTopology());
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            cluster.shutdown();
        }
    }

    private static StormTopology createTopology()
    {
        SpoutConfig kafkaConf = new SpoutConfig(
                new ZkHosts("localhost:2181"),
                KAFKA_TOPIC,
                "/kafka",
                "KafkaSpout");
        kafkaConf.scheme = new SchemeAsMultiScheme(new StringScheme());
        TopologyBuilder topology = new TopologyBuilder();

        topology.setSpout("spout_audioFeatures", new KafkaSpout(kafkaConf), 4);

//        topology.setBolt("DataExtraction", new DataExtractionBolt(), 4)
//                .shuffleGrouping("kafka_spout");
         topology.setBolt("AudioBolt", new AudioBolt(), 4)
                .shuffleGrouping("spout_audioFeatures");
        topology.setBolt("SpeechBolt", new SpeechBolt(), 4)
                .shuffleGrouping("spout_audioFeatures");
        topology.setBolt("OtherBolt", new OtherBolt(), 4)
                .shuffleGrouping("spout_audioFeatures");
//        topology.setBolt("LPC",new LPCBolt(),4)
//                .shuffleGrouping("kafka_spout");
        //topology.setBolt("TrainBolt",new TrainBolt(),4).shuffleGrouping("ZC").shuffleGrouping("LPC");
        return topology.createTopology();
    }


    private static Config createConfig(boolean local)
    {
        int workers = 1;
        Config conf = new Config();
        conf.setDebug(true);
        if (local)
            conf.setMaxTaskParallelism(workers);
        else
            conf.setNumWorkers(workers);
        return conf;
    }
}
