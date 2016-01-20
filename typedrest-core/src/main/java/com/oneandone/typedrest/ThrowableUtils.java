package com.oneandone.typedrest;

/**
 * Provides utility methods for {@link Throwable}.
 */
public final class ThrowableUtils {

    private ThrowableUtils() {
    }

    /**
     * Concatenates the {@link Throwable#getLocalizedMessage()}s of the entire
     * {@link Throwable#getCause()} tree.
     *
     * @param throwable The top-level throwable to walk for messages.
     * @return A newline separated list of messages.
     */
    @SuppressWarnings("ThrowableResultIgnored")
    public static String getFullMessage(Throwable throwable) {
        StringBuilder builder = new StringBuilder();
        do {
            builder.append(throwable.getLocalizedMessage()).append("\n");
            throwable = throwable.getCause();
        } while (throwable != null);
        return builder.toString();
    }
}
