package com.themodernizers.misc.upload;

import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class ChunkedFileUploadInFlightTest {

    private ChunkedFileUploadInFlight uploadInFlight = null;

    @Before
    public void setUp() {
        this.uploadInFlight = new ChunkedFileUploadInFlight("test.jpg", UUID.randomUUID().toString());
    }


    @Test
    public void getAllBytes() {
        this.uploadInFlight.addChunk(2, "pl".getBytes());
        this.uploadInFlight.addChunk(0, "A".getBytes());
        this.uploadInFlight.addChunk(3, "e".getBytes());
        this.uploadInFlight.addChunk(1, "p".getBytes());
        assertEquals( "Apple", new String(uploadInFlight.getAllBytes()));
    }
}