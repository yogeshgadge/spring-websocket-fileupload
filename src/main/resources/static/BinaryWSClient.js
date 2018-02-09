
function uploadFileBinaryWS(files){
    var file = files[0];
    console.log('WSClient Sending '+file.name);
    var uploadSessionId = getUniqueSessionId(file.name);
    var ws = new WebSocket('ws://'+window.location.host+'/binary?uploadSessionId='+uploadSessionId);
    ws.onmessage = function(response){
        console.log('WSClient',response);
        ws.close();
    };

    ws.onopen = function() {
        ws.send(file);
        console.log('WSClient Finished sending '+file.name);
    };


}

