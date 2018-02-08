function FetchUpload(url) { this.url=url}

FetchUpload.prototype.uploadFile = function(files) {
    var file = files[0];

    console.log('Fetch Sending '+file.name);
    fetch(this.url, {
        method: 'POST',
        body: file,
        headers: new Headers({
            'X-File-Name': file.name
        })
    }).then(function(response){
        return response.json();
    }).then(function(respObject){
        console.log('Fetch Uploaded '+file.name, respObject);
    });
};


window.fetchClient = new FetchUpload('http://'+window.location.host+'/fetch');