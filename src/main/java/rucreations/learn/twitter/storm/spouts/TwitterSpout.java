package rucreations.learn.twitter.storm.spouts;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;
import org.slf4j.Logger;
import rucreations.learn.twitter.storm.util.Constants;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingDeque;

public class TwitterSpout extends BaseRichSpout {

    Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass().getName());

    private static final long serialVersionUID = -324234207002113362L;

    SpoutOutputCollector collector;
    LinkedBlockingDeque<Status> tweetsQueue;
    private TwitterStream twitterStream;

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("tweet"));
    }

    @Override
    public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
        StatusListener tweetListener = new StatusListener() {

            // Implement the callback function when a tweet arrives
            @Override
            public void onStatus(Status status) {
                // add the tweet into the queue buffer
                //logger.debug("@"+status.getUser().getName() + "-->" + status.getText());
                tweetsQueue.offer(status);
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice sdn) {
            }

            @Override
            public void onTrackLimitationNotice(int i) {
            }

            @Override
            public void onScrubGeo(long l, long l1) {
            }

            @Override
            public void onStallWarning(StallWarning warning) {
            }

            @Override
            public void onException(Exception e) {
                e.printStackTrace();
            }
        };

        this.collector = spoutOutputCollector;
        this.tweetsQueue = new LinkedBlockingDeque<Status>();

        //Twitter stream authentication setup
        final Properties properties = new Properties();
        try {
            properties.load(TwitterSpout.class.getClassLoader().getResourceAsStream(Constants.CONFIG_PROPERTIES_FILE));
        } catch (final IOException ioException) {
            //Should not occur. If it does, we cant continue. So exiting the program!
            logger.error(ioException.getMessage(), ioException);
            System.exit(1);
        }

        final ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setIncludeEntitiesEnabled(true);

        configurationBuilder.setOAuthAccessToken(properties.getProperty(Constants.OAUTH_ACCESS_TOKEN));
        configurationBuilder.setOAuthAccessTokenSecret(properties.getProperty(Constants.OAUTH_ACCESS_TOKEN_SECRET));
        configurationBuilder.setOAuthConsumerKey(properties.getProperty(Constants.OAUTH_CONSUMER_KEY));
        configurationBuilder.setOAuthConsumerSecret(properties.getProperty(Constants.OAUTH_CONSUMER_SECRET));
        this.twitterStream = new TwitterStreamFactory(configurationBuilder.build()).getInstance();
        this.twitterStream.addListener(tweetListener);
        this.twitterStream.sample();

    }

    @Override
    public void nextTuple() {
        Status status = tweetsQueue.poll();
        if (status == null) {
            Utils.sleep(500);
        } else {
            this.collector.emit(new Values(status));
        }
    }

    @Override
    public final void close() {
        this.twitterStream.cleanUp();
        this.twitterStream.shutdown();
    }


    @Override
    public final void ack(final Object id) {
    }

    @Override
    public final void fail(final Object id) {
    }
}
