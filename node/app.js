
var express = require("express");
var app = express();
var stylus = require('stylus');
var nib = require('nib');
var port = 3000;

app.use(express.static(__dirname + '/public'));

function compile(str, path) {
  return stylus(str)
    .set('filename', path)
    .use(nib());
}

app.set('views', __dirname + '/tpl');
app.set('view engine', "jade");
app.engine('jade', require('jade').__express);
app.get("/", function(req, res){
    res.render("index");
});
app.use(stylus.middleware(
  { src: __dirname + '/public'
  , compile: compile
  }
))


//app.listen(port);
var io = require('socket.io').listen(app.listen(port));

console.log("Listening on port " + port);


//var app = require('express')();
//var http = require('http').Server(app);
//var io = require('socket.io')(http);

//app.get('/', function(req, res){
//  send the index.html file for all requests
//  res.sendFile(__dirname + '/index.html');
//});

//http.listen(3000, function(){
//  console.log('listening on *:3000');
//});

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

