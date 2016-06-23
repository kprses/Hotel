var app = require('express')();
var http = require('http').Server(app);
var io = require('socket.io')(http);

var os=require('os');
var net=require('net');

var networkInterfaces = os.networkInterfaces();

var port = 8081;

var _dirName = "C:\\wamp\\www\\Android";

app.get('/', function(req, res){
  res.sendFile(_dirName + '/index.html');
});

io.on('connection', function(socket){
  socket.on('slanjePoruke', function(msg){
    io.emit('serverSlanjePoruke', msg);
  });
});

http.listen(3000, function(){
  console.log('HTTP listening on *:3000');
});

function callback_server_connection(socket){
	
    var remoteAddress = socket.remoteAddress;
    var remotePort = socket.remotePort;
    socket.setNoDelay(true);
    console.log("connected: ", remoteAddress, " : ", remotePort);
	
	//io.emit('serverSlanjePoruke', "Konektovo se: " + remoteAddress.toString() + " r. port: " + remotePort.toString());
    
	socket.on('data', function(data) { 
		console.log("Došli podaci na server: ");
		
		var podaci = JSON.parse(data);
		
		console.log(podaci["Scena"]);
		
		// NA WEB ŠALJEMO JSON KOJI SADRŽI SVE POTREBNE PODATKE ZA UPRAVLJANJE OBJEKTIMA
		
		io.emit('serverSlanjePoruke', podaci);
		socket.write(data);
	});
	
    socket.on('end', function (poruka) {
		//io.emit('serverSlanjePoruke', "Diskonektovo se: " + remoteAddress.toString() + " r. port: " + remotePort.toString());
        console.log("ended: ", remoteAddress, " : ", remotePort);
    });
}

console.log("node.js SOCEKT listening on 8081");
for (var interface in networkInterfaces) {

    networkInterfaces[interface].forEach(function(details){
        
        if ((details.family=='IPv4') && !details.internal) {
            console.log(interface, details.address);  
        }
    });
}

var netServer = net.createServer(callback_server_connection);
netServer.listen(port);