package rucreations.learn.twitter.storm.bolt;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import twitter4j.GeoLocation;
import twitter4j.Status;

import java.util.Map;

public class UserLocatorBolt extends BaseRichBolt {

    private OutputCollector outputCollector;

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("location", "tweet"));
    }

    @Override
    public void execute(Tuple tuple) {
        final Status status = (Status) tuple.getValueByField("tweet");
        GeoLocation location = getLocation(status);
        if (location != null)
            this.outputCollector.emit(new Values(location, status.getText()));
    }

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.outputCollector = outputCollector;
    }

    /**
     * Extracts the location from the tweet
     *
     * @param status
     * @return
     */
    private GeoLocation getLocation(Status status) {
        if (status.getGeoLocation() != null) {
            GeoLocation geoLocation = status.getGeoLocation();
            return geoLocation;
        } else {
            return null;
        }
    }
}
