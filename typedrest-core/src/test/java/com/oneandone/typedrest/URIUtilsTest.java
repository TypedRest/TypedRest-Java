package com.oneandone.typedrest;

import static com.oneandone.typedrest.URIUtils.*;
import java.net.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import org.junit.*;

public class URIUtilsTest {

    @Test
    public void testEnsureTrailingSlashRelative() {
        assertThat(ensureTrailingSlash(URI.create("test")),
                is(equalTo(URI.create("test/"))));
    }

    @Test
    public void testEnsureTrailingSlashAbsolute() {
        assertThat(ensureTrailingSlash(URI.create("http://localhost/test")),
                is(equalTo(URI.create("http://localhost/test/"))));
    }
}
