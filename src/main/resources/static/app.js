


// --------------------------

function TextWSClient(url) {
    this.url = url;
}

TextWSClient.prototype.connect = function() {
    var ws = new WebSocket(this.url);
    this.ws = ws;
    ws.onmessage = this.onMessageFn;

    this.setConnected(true);
};
TextWSClient.prototype.setOnMessage = function(onMessageFn) {
  this.onMessageFn =   onMessageFn;
};

TextWSClient.prototype.disconnect = function() {
    if (this.ws != null) {
        this.ws.close();
    }
    this.setConnected(false);
    console.log("Disconnected");
};

TextWSClient.prototype.setConnected = function(connected) {
    console.log(connected);
};
TextWSClient.prototype.sendMessage = function() {
    var message = 'Browser message' + Math.random() * 1000;
    console.log('Sending '+message);
    this.ws.send(message);
};

function showGreetings(data) {
    console.log(data.data + "");
};

function uploadFile(files) {
 console.log(files);
  this.ws.send( files[0]);
}

window.textWS = new TextWSClient('ws://'+window.location.host+'/text');
window.textWS.setOnMessage(showGreetings);
