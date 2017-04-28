var express = require('express');
var fs = require('fs');
var bodyParser = require('body-parser');

var app = express();
var hostname = process.env.HOSTNAME || 'localhost';
var port = parseInt(process.env.PORT, 10) || 8080;
var mockDir = __dirname + '/web/';

app.use(express.static(__dirname + '/web'));
app.use(bodyParser.json()); // for parsing application/json

app.all('/', function(req, res) {
	res.send(fs.readFileSync(mockDir + 'index.html', 'utf8'));
});

app.all('/users/search/*', function(req, res) {
	res.json(JSON.parse(fs.readFileSync(mockDir + 'mocks/search.json', 'utf8')));
});

app.all('/users/load', function(req, res) {
	res.json(JSON.parse(fs.readFileSync(mockDir + 'mocks/load.json', 'utf8')));
});

app.all('/users/get', function(req, res) {
	res.json(JSON.parse(fs.readFileSync(mockDir + 'mocks/get.json', 'utf8')));
});

app.all('/users/apply', function (req,res) {
	res.json(req.body);
});

app.all('/users/remove', function (req,res) {
	res.json(req.body);
});

var server = app.listen(port, hostname, function() {
	var _host = server.address().address;
	var _port = server.address().port;
	console.log('Example app listening at http://%s:%s', _host, _port);
});
