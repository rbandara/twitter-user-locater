package rucreations.learn.twitter.storm.bolt;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;
import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisConnection;
import twitter4j.GeoLocation;
import twitter4j.Status;

import java.util.Map;

public class ReportBolt extends BaseRichBolt {

    // place holder to keep the connection to redis
    private RedisConnection<String, String> redis;

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {

        // instantiate a redis connection
        RedisClient client = new RedisClient("localhost", 6379);

        // initiate the actual connection
        redis = client.connect();
    }

    @Override
    public void execute(Tuple tuple) {

        // extracted location
        GeoLocation location = (GeoLocation) tuple.getValueByField("location");
        System.out.println(location.toString());

        // tweet text
        String text = tuple.getStringByField("tweet");

        String latLong = location.getLatitude() + "|" + location.getLongitude();
        redis.publish("TweetLocationQueue", latLong);

        System.out.println(">> Redis :  " + latLong + " : " + text);
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        // nothing to add - since it is the final bolt
    }
}