var http = require('http'),
    fs = require('fs'),
    // NEVER use a Sync function except at start-up!
    index = fs.readFileSync(__dirname + '/index.html');

// Send index.html to all requests
var app = http.createServer(function(req, res) {
    res.writeHead(200, {'Content-Type': 'text/html'});
    res.end(index);
});

// Socket.io server listens to our app
var io = require('socket.io').listen(app);
app.listen(3000);

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

