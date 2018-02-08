function MultipartUpload(url) { this.url=url}

MultipartUpload.prototype.uploadFile = function(files) {

    var file = files[0];
    console.log('MultipartUpload Sending '+file.name);
    var xhr = new XMLHttpRequest();
    var formData = new FormData();
    formData.append("file", file);
    xhr.open("POST", this.url);
    xhr.send(formData);
    xhr.onreadystatechange = function() {//Call a function when the state changes.
        if(xhr.readyState == XMLHttpRequest.DONE && xhr.status == 200) {
            console.log('MultipartUpload Uploaded '+file.name, xhr.responseText);
        }
    }
};


window.multipartClient = new MultipartUpload('http://'+window.location.host+'/multipart');