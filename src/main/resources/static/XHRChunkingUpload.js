function XHRChunkingUploder(url) { this.url=url}

XHRChunkingUploder.prototype.uploadFile = function(files) {

    var file = files[0];
    console.log('XHRChunkingUploder Sending '+file.name);
    this.uploadChunk(file);

};

XHRChunkingUploder.prototype.uploadChunk = function(file, metadata) {
    metadata = metadata || { fileName: file.name, seq: 0, start: 0, chunkSize: Math.min(4096, file.size), totalSent: 0, sessionId: getUniqueSessionId(file.name)};
    var end = metadata.start + metadata.chunkSize - 1;
    var chunk = file.slice(metadata.start, end);

    var isLast = (file.size === end);

    var xhr = new XMLHttpRequest();
    xhr.open("POST", this.url);
    xhr.setRequestHeader("X-File-Name", metadata.fileName);
    xhr.setRequestHeader("X-Upload-Session-id", metadata.sessionId);
    xhr.setRequestHeader("X-File-Chunk-Sequence", metadata.seq+"");
    xhr.setRequestHeader("X-File-Last", isLast+"");
    xhr.send(chunk);
    xhr.onreadystatechange = function() {//Call a function when the state changes.
        if(xhr.readyState == XMLHttpRequest.DONE && xhr.status == 200) {
            console.log('Chunk Uploaded '+file.name, xhr.responseText);
        }
    };

    console.log(metadata.start, end, metadata);

    if (!isLast) {
        metadata.totalSent+=chunk.size;
        metadata.start = metadata.start +  metadata.chunkSize;
        metadata.chunkSize = Math.min(metadata.chunkSize, file.size - metadata.totalSent);
        metadata.seq++;
        this.uploadChunk(file, metadata);
    }


};

window.xhrChunker = new XHRChunkingUploder('http://'+window.location.host+'/chunk-upload');