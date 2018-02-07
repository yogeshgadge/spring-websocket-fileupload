function FetchUpload(url) { this.url=url}

FetchUpload.prototype.uploadFile = function(files) {

    fetch(this.url, {
        method: 'POST',
        body: files[0],
        headers: new Headers({
            'X-File-Name': files[0].name
        })
    });
};


window.fetchClient = new FetchUpload('http://'+window.location.host+'/fetch/upload');