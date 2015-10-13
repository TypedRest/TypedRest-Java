package com.oneandone.typedrest;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import javax.naming.OperationNotSupportedException;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.type.JavaType;

/**
 * REST endpoint that represents a set of elements that can be retrieved
 * partially (pagination). Uses the HTTP Range header.
 *
 * @param <TElement> The type of elements the endpoint represents.
 */
public class PaginationEndpointImpl<TElement>
        extends AbstractEndpoint implements PaginationEndpoint<TElement> {

    protected final Class<TElement> elementType;

    /**
     * Creates a new pagination endpoint.
     *
     * @param parent The parent endpoint containing this one.
     * @param relativeUri The URI of this endpoint relative to the
     * <code>parent</code>'s.
     * @param elementType The type of elements the endpoint represents.
     */
    public PaginationEndpointImpl(Endpoint parent, URI relativeUri, Class<TElement> elementType) {
        super(parent, relativeUri);
        this.elementType = elementType;
    }

    /**
     * Creates a new pagination endpoint.
     *
     * @param parent The parent endpoint containing this one.
     * @param relativeUri The URI of this endpoint relative to the
     * <code>parent</code>'s.
     * @param elementType The type of elements the endpoint represents.
     */
    public PaginationEndpointImpl(Endpoint parent, String relativeUri, Class<TElement> elementType) {
        super(parent, relativeUri);
        this.elementType = elementType;
    }

    @Override
    public Collection<TElement> readAll()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, OperationNotSupportedException, HttpException {
        HttpResponse response = execute(Request.Get(uri));
        JavaType collectionType = json.getTypeFactory().constructCollectionType(List.class, elementType);
        return json.readValue(EntityUtils.toString(response.getEntity()), collectionType);
    }

    /**
     * The value used for the Range header unit.
     */
    public static final String RANGE_UNIT = "elements";

    @Override
    public PartialResponse<TElement> readPartial(Long from, Long to)
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, OperationNotSupportedException, IndexOutOfBoundsException, HttpException {
        String range = RANGE_UNIT + "="
                + (from == null ? "" : from.toString()) + "-"
                + (to == null ? "" : to.toString());

        HttpResponse response = execute(Request.Get(uri).addHeader("Range", range));

        JavaType collectionType = json.getTypeFactory().constructCollectionType(List.class, elementType);
        List<TElement> elements = json.readValue(EntityUtils.toString(response.getEntity()), collectionType);

        String contentRange = response.getFirstHeader("Content-Range").getValue();
        String[] split = contentRange.split(" ");
        split = split[1].split("/");
        Long contentLength = (split[1].equals("*")) ? null : Long.parseLong(split[1]);
        split = split[0].split("-");
        Long contentFrom = (split[0].isEmpty() ? null : Long.parseLong(split[0]));
        Long contentTo = (split[1].isEmpty() ? null : Long.parseLong(split[1]));

        return new PartialResponse<>(elements, contentFrom, contentTo, contentLength);
    }
}
