package com.oneandone.typedrest;

import java.util.List;
import lombok.Data;

/**
 * Represents a subset of a set of elements.
 *
 * @param <TEntity> The type of element the response contains.
 */
@Data
public class PartialResponse<TEntity> {

    /**
     * The returned elements.
     */
    private final List<TEntity> elements;

    /**
     * The index of the first element. <code>null</code> if unset.
     */
    private final Long from;

    /**
     * The index of the last element. <code>null</code> if unset.
     */
    private final Long to;

    /**
     * The total count of elements available on the server. <code>null</code> if
     * unset.
     */
    private final Long length;

    /**
     * Indicates whether the response reaches the end of the elements available
     * on the server.
     *
     * @return <code>true</code> if there are no more elements.
     */
    public boolean isEndReached() {
        if (to == null || from == null) {
            // No range specified, must be complete response
            return true;
        }

        if (length == null) {
            // No lenth specified, can't be end
            return false;
        }

        return to == length - 1;
    }
}
