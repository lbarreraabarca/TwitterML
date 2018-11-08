var express = require('express'),
    app = express(),
    server = require('http').createServer(app)

app.use(express.static(__dirname + '/App-Twitter'));

var ip = 'localhost';
var port = '7070';

server.listen(port, ip);

server.on('listening', function () {
    console.log('Server listening on http://localhost:%d', this.address().port);
});
