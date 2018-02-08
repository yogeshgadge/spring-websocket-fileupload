
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




function uploadFileBinaryWS(files){
    var file = files[0];
    var uploadSessionId = btoa(guid()+'\\'+file.name);
    var ws = new WebSocket('ws://'+window.location.host+'/binary?uploadSessionId='+uploadSessionId);
    ws.onmessage = function(response){
        console.log('BinaryWSClient',response);
        ws.close();
    };

    ws.onopen = function() {
        console.log('Sending '+file.name);
        ws.send(file);
        console.log('Finished sending '+file.name);
    };


}

