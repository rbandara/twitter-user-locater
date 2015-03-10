# Twitter-user-locater

This will connect to real time twitter feed and display the location of a tweet in a google map.

## Technologies used

### Display the map
* Node.JS
* Socket.IO
* Express.JS

### Processing Tweets
* Java
* Apache Storm
* Redis



To run the java server

`mvn clean package`
`java -jar target/twitter-user-locator-1.0-SNAPSHOT-jar-with-dependencies.jar`

To run the web server

`cd node\`
`node app.js`

This will start the web server which runs port 3000

`http://localhost:3000/`

![Tweet Map](https://github.com/rbandara/twitter-user-locater/blob/master/TweetLocationMap.png)
