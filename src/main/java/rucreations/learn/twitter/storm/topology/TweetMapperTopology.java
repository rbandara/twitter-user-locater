package rucreations.learn.twitter.storm.topology;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import backtype.storm.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rucreations.learn.twitter.storm.bolt.UserLocatorBolt;
import rucreations.learn.twitter.storm.spouts.TwitterSpout;
import rucreations.learn.twitter.storm.util.Constants;
import rucreations.learn.twitter.storm.bolt.ReportBolt;

public class TweetMapperTopology {
    static Logger logger = org.slf4j.LoggerFactory.getLogger("TweetMapperTopology");

    public static void main(String[] args) throws AlreadyAliveException, InvalidTopologyException {

        // What does this do?
        final Config config = new Config();
        config.setMessageTimeoutSecs(120);
        config.setDebug(false);

        final TopologyBuilder topologyBuilder = new TopologyBuilder();

        // spout for getting tweets
        topologyBuilder.setSpout("twitterspout", new TwitterSpout());

        //bolt to locate the user location
        topologyBuilder.setBolt("userlocatorbolt", new UserLocatorBolt()).shuffleGrouping("twitterspout");

        topologyBuilder.setBolt("reporterbolt", new ReportBolt()).globalGrouping("userlocatorbolt");

        logger.debug("");

        //Submit it to the cluster, or submit it locally
        if (null != args && 0 < args.length) {
            config.setNumWorkers(3);
            StormSubmitter.submitTopology(args[0], config, topologyBuilder.createTopology());
        } else {
            config.setMaxTaskParallelism(10);
            final LocalCluster localCluster = new LocalCluster();
            localCluster.submitTopology(Constants.TOPOLOGY_NAME, config, topologyBuilder.createTopology());
            //Run this topology for 120 seconds so that we can complete processing of decent # of tweets.
            Utils.sleep(120 * 1000);

            logger.info("Shutting down the cluster...");
            localCluster.killTopology(Constants.TOPOLOGY_NAME);
            localCluster.shutdown();

            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    logger.info("Shutting down the cluster...");
                    localCluster.killTopology(Constants.TOPOLOGY_NAME);
                    localCluster.shutdown();
                }
            });
        }
    }
}
