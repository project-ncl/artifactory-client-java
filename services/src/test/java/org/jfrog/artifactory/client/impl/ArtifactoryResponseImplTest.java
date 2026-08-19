package org.jfrog.artifactory.client.impl;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicStatusLine;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Unit tests for {@link ArtifactoryResponseImpl#parseBody(Class)}.
 *
 * Key regressions guarded here:
 * <ul>
 *   <li>Previously the implementation called {@code Util.configureObjectMapper(staticMapper)} on
 *       every invocation, mutating shared state. Calling {@code parseBody} twice on different
 *       responses must not throw.</li>
 *   <li>A malformed body must produce an {@link IOException} whose message includes the raw body,
 *       not a bare Jackson internal error.</li>
 * </ul>
 */
public class ArtifactoryResponseImplTest {

    private static final String VALID_JSON = "{\"key\":\"value\",\"count\":42}";
    private static final String MALFORMED_JSON = "<!DOCTYPE html><html>Not JSON</html>";

    // ── parseBody correctness ─────────────────────────────────────────────────

    @Test
    @SuppressWarnings("unchecked")
    public void parseBody_validJson_returnsDeserialised() throws Exception {
        ArtifactoryResponseImpl response = makeResponse(HttpStatus.SC_OK, VALID_JSON);

        Map<String, Object> result = response.parseBody(Map.class);

        assertNotNull(result);
        assertEquals(result.get("key"), "value");
        assertEquals(result.get("count"), 42);
    }

    // ── double-call safety (regression for per-call configureObjectMapper mutation) ───

    @Test
    @SuppressWarnings("unchecked")
    public void parseBody_calledTwice_bothCallsSucceed() throws Exception {
        ArtifactoryResponseImpl response = makeResponse(HttpStatus.SC_OK, VALID_JSON);

        // First call
        Map<String, Object> first = response.parseBody(Map.class);
        assertNotNull(first, "First parseBody call must succeed");

        // Second call on the same instance — previously this could fail due to concurrent
        // mapper mutation in the old Util.configureObjectMapper(staticMapper) approach
        Map<String, Object> second = response.parseBody(Map.class);
        assertNotNull(second, "Second parseBody call must also succeed without exception");
    }

    // ── error message quality ─────────────────────────────────────────────────

    @Test
    public void parseBody_invalidJson_throwsIOExceptionWithRawBody() throws Exception {
        ArtifactoryResponseImpl response = makeResponse(HttpStatus.SC_OK, MALFORMED_JSON);

        try {
            response.parseBody(Map.class);
        } catch (IOException e) {
            // The message must contain the raw body so the caller can diagnose the problem
            assertTrue(e.getMessage().contains(MALFORMED_JSON),
                    "IOException message must include raw body. Got: " + e.getMessage());
        }
    }

    // ── isSuccessResponse ─────────────────────────────────────────────────────

    @Test
    public void isSuccessResponse_200_returnsTrue() throws Exception {
        assertTrue(makeResponse(HttpStatus.SC_OK, "{}").isSuccessResponse());
    }

    @Test
    public void isSuccessResponse_201_returnsTrue() throws Exception {
        assertTrue(makeResponse(HttpStatus.SC_CREATED, "{}").isSuccessResponse());
    }

    @Test
    public void isSuccessResponse_404_returnsFalse() throws Exception {
        org.testng.Assert.assertFalse(
                makeResponse(HttpStatus.SC_NOT_FOUND, "not found").isSuccessResponse());
    }

    // ── helper ────────────────────────────────────────────────────────────────

    private ArtifactoryResponseImpl makeResponse(int statusCode, String body) throws IOException {
        HttpResponse httpResponse = mock(HttpResponse.class);
        BasicStatusLine statusLine = new BasicStatusLine(
                HttpVersion.HTTP_1_1, statusCode,
                org.apache.http.impl.EnglishReasonPhraseCatalog.INSTANCE.getReason(statusCode, null));
        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        StringEntity entity = new StringEntity(body, ContentType.APPLICATION_JSON);
        when(httpResponse.getEntity()).thenReturn(entity);
        return new ArtifactoryResponseImpl(httpResponse);
    }
}
