package com.oneandone.typedrest;

import java.io.*;
import java.util.Collection;
import javax.naming.OperationNotSupportedException;
import org.apache.http.*;

/**
 * REST endpoint that represents a set of elements that can be retrieved
 * partially (pagination).
 *
 * @param <TElement> The type of elements the endpoint represents.
 */
public interface PaginationEndpoint<TElement> extends Endpoint {

    /**
     * Returns all <code>TElement</code>s currently in the stream.
     *
     * @return All <code>TElement</code>s.
     * @throws IOException Network communication failed.
     * @throws IllegalArgumentException {@link HttpStatus#SC_BAD_REQUEST}
     * @throws IllegalAccessException {@link HttpStatus#SC_UNAUTHORIZED} or
     * {@link HttpStatus#SC_FORBIDDEN}
     * @throws FileNotFoundException {@link HttpStatus#SC_NOT_FOUND} or
     * {@link HttpStatus#SC_GONE}
     * @throws OperationNotSupportedException {@link HttpStatus#SC_CONFLICT}
     * @throws HttpException Other non-success status code.
     */
    Collection<TElement> readAll() throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, OperationNotSupportedException, HttpException;

    /**
     * Returns all  <code>TElement</code>s within a specific range of the set.
     *
     * @param from The index of the first element to return. <code>null</code>
     * to use <code>to</code> to specify a start point counting from the end of
     * the set.
     * @param to The index of the last element to return. Alternatively the
     * index of the first element to return counting from the end of the set if
     * <code>from</code> is <code>null</code>. <code>null</code> to read to the
     * end.
     * @return A subset of the <code>TElement</code>s and the range they come
     * from. May not exactly match the request range.
     * @throws IOException Network communication failed.
     * @throws IllegalArgumentException {@link HttpStatus#SC_BAD_REQUEST}
     * @throws IllegalAccessException {@link HttpStatus#SC_UNAUTHORIZED} or
     * {@link HttpStatus#SC_FORBIDDEN}
     * @throws FileNotFoundException {@link HttpStatus#SC_NOT_FOUND} or
     * {@link HttpStatus#SC_GONE}
     * @throws OperationNotSupportedException {@link HttpStatus#SC_CONFLICT}
     * @throws IndexOutOfBoundsException
     * {@link HttpStatus#SC_REQUESTED_RANGE_NOT_SATISFIABLE}
     * @throws HttpException Other non-success status code.
     */
    PartialResponse<TElement> readPartial(Long from, Long to)
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, OperationNotSupportedException, IndexOutOfBoundsException, HttpException;
}
