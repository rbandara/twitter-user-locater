
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
    res.render("page");
});
app.use(stylus.middleware(
  { src: __dirname + '/public'
  , compile: compile
  }
))


//app.listen(port);
var io = require('socket.io').listen(app.listen(port));

console.log("Listening on port " + port);


// redis code
var redis = require("redis");
var subscriber = redis.createClient();

subscriber.subscribe("TweetLocationQueue");

console.log('Subscribed to redis queue \'TweetLocationQueue\'')

subscriber.on("error", function (err) {
    console.log("Error %s", err);
});

subscriber.on("message", function (channel, message) {
    console.log("Location '" + message);
    var lat = message.split('|')[0];
    var lng = message.split('|')[1];
    console.log('lat ' + lat);
    console.log('lng ' + lng);
    io.sockets.emit('message', {
                        'lat': lat,
                        'lng' : lng });
});

