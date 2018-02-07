function BinaryWSClient(url) {
    this.url = url;
}

BinaryWSClient.prototype.connect = function() {
    var ws = new WebSocket(this.url);
    this.ws = ws;
    ws.onmessage = this.onMessageFn;

    this.setConnected(true);
};
BinaryWSClient.prototype.setOnMessage = function(onMessageFn) {
    this.onMessageFn =   onMessageFn;
};

BinaryWSClient.prototype.disconnect = function() {
    if (this.ws != null) {
        this.ws.close();
    }
    this.setConnected(false);
    console.log("Disconnected");
};

BinaryWSClient.prototype.setConnected = function(connected) {
    console.log(connected);
};
BinaryWSClient.prototype.uploadFile = function(files) {
    var file = files[0];

    if (!this.ws) {
        this.connect();
    }
    console.log('Sending '+file.name);
    // if (!window.textWS.ws) {
    //     window.textWS.connect();
    // }
    // window.textWS.ws.send('Sending ' +file.name);
    // this.ws.send('Sending ' +file.name);
    this.ws.send(file);


};

function showGreetings(data) {
    console.log(data.data + "");
};



window.binaryWS = new BinaryWSClient('ws://'+window.location.host+'/binary');
window.binaryWS.setOnMessage(showGreetings);