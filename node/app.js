var app = require('express')();
var http = require('http').Server(app);
var io = require('socket.io')(http);

app.get('/', function(req, res){
  //send the index.html file for all requests
  res.sendFile(__dirname + '/index.html');
});

http.listen(3000, function(){
  console.log('listening on *:3000');
});

// redis code
var redis = require("redis");
var subscriber = redis.createClient();

subscriber.subscribe("TweetLocationQueue");

console.log('Subscribed to redis queue \'TweetLocationQueue\'')

subscriber.on("error", function (err) {
    console.log("Error %s", err);
});

subscriber.on("message", function (channel, message) {
    console.log("Location '" + message)
    io.sockets.emit('message', { tweet: message });
});

