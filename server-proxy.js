var express = require('express');
var httpProxy = require('http-proxy');

var proxy = httpProxy.createProxyServer({
	changeOrigin: true,
});
var app = express();

app.use(express.static(__dirname + '/web'));

app.all('/', function(req, res) {
	res.send(fs.readFileSync(mockDir + 'index.html', 'utf8'));
});

app.all('/users/*', function (req, res) {
	proxy.web(req, res, {
		// target: 'http://idmdev4-internal.roche.com/',
		target: 'http://10.22.216.54:8080',
	});
});

app.listen(8080, function () {
	console.log('Example app listening on port 8080!');
});
