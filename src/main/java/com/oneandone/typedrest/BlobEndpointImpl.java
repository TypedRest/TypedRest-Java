package com.oneandone.typedrest;

import java.io.*;
import java.net.URI;
import javax.naming.OperationNotSupportedException;
import org.apache.http.*;
import org.apache.http.client.fluent.*;
import org.apache.http.entity.FileEntity;

/**
 * REST endpoint that represents a single binary blob that can downloaded and uploaded.
 */
public class BlobEndpointImpl extends AbstractEndpoint implements BlobEndpoint {

    public BlobEndpointImpl(Endpoint parent, URI relativeUri) {
        super(parent, relativeUri);
    }

    public BlobEndpointImpl(Endpoint parent, String relativeUri) {
        super(parent, relativeUri);
    }

    @Override
    public void downloadTo(OutputStream stream) throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, OperationNotSupportedException, HttpException {
        HttpResponse response = execute(Request.Get(uri));
        response.getEntity().writeTo(stream);
    }

    @Override
    public void uploadFrom(File file) throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, OperationNotSupportedException, HttpException {
        execute(Request.Put(uri).body(new FileEntity(file)));
    }
}
