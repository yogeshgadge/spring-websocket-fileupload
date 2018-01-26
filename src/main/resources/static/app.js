function connect() {
    ws = new WebSocket('ws://'+window.location.host+'/name');
    ws.onmessage = function(data){
        showGreeting(data.data);
    }
    setConnected(true);
}

function disconnect() {
    if (ws != null) {
        ws.close();
    }
    setConnected(false);
    console.log("Disconnected");
}

function setConnected(connected) {
    console.log(connected);
}
function sendMessage() {
    var message = 'Browser message' + Math.random() * 1000;
    console.log('Sending '+message);
    ws.send(message);
}

function showGreeting(message) {
    console.log(message + "");
}