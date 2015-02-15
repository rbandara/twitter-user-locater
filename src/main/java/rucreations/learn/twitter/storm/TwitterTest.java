package rucreations.learn.twitter.storm;

import rucreations.learn.twitter.storm.util.Constants;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.io.IOException;
import java.util.Properties;

public class TwitterTest {
    public static void main(String[] args) {

        StatusListener statusListener = new StatusListener() {
            @Override
            public void onStatus(Status status) {
                if (status.getGeoLocation() != null)
                    System.out.println("@" + status.getUser().getScreenName() +
                            " - " + status.getText() + " --> "
                            + status.getGeoLocation());
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {

            }

            @Override
            public void onTrackLimitationNotice(int i) {

            }

            @Override
            public void onScrubGeo(long l, long l1) {

            }

            @Override
            public void onStallWarning(StallWarning stallWarning) {

            }

            @Override
            public void onException(Exception e) {

            }
        };

        TwitterStream twitterStream;

        final Properties properties = new Properties();
        try {
            properties.load(TwitterTest.class.getClassLoader()
                    .getResourceAsStream(Constants.CONFIG_PROPERTIES_FILE));
        } catch (final IOException ioException) {
            System.err.println(ioException.getMessage());
            System.exit(1);
        }
        final ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setIncludeEntitiesEnabled(true);

        configurationBuilder.setOAuthAccessToken(properties.getProperty(Constants.OAUTH_ACCESS_TOKEN));
        configurationBuilder.setOAuthAccessTokenSecret(properties.getProperty(Constants.OAUTH_ACCESS_TOKEN_SECRET));
        configurationBuilder.setOAuthConsumerKey(properties.getProperty(Constants.OAUTH_CONSUMER_KEY));
        configurationBuilder.setOAuthConsumerSecret(properties.getProperty(Constants.OAUTH_CONSUMER_SECRET));
        twitterStream = new TwitterStreamFactory(configurationBuilder.build()).getInstance();
        twitterStream.addListener(statusListener);
        twitterStream.sample();

    }
}
