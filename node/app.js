var http = require('http');
//    fs = require('fs'),
var static = require('node-static');

// make html, js & css files accessible
var files = new static.Server('./public');

// serve files on request
function handler(request, response) {
	request.addListener('end', function() {
		files.serve(request, response);
	});
}

var app = http.createServer(handler);

// Socket.io server listens to our app
var io = require('socket.io').listen(app);
app.listen(3000);

// use redis to subscribe
var redis = require("redis");
var subscriber = redis.createClient();

subscriber.subscribe("TweetQueue");

console.log('Subscribed to redis queue \'TweetQueue\'')

subscriber.on("error", function (err) {
    console.log("Error %s", err);
});

subscriber.on("message", function (channel, message) {
    console.log("Location '" + message)
    io.sockets.emit('message', { tweet: message });
});

