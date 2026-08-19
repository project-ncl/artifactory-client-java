package org.jfrog.artifactory.client.impl;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicStatusLine;
import org.jfrog.artifactory.client.model.impl.BuildPromotionResponseImpl;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Pure unit tests for HTTP error handling in {@link ArtifactoryImpl}.
 *
 * These tests verify that post() and patch() (which previously had no status-code check)
 * now throw {@link HttpResponseException} for non-2xx responses rather than propagating a
 * JsonParseException from Jackson trying to parse an HTML error page.
 *
 * No live Artifactory instance is required — CloseableHttpClient is mocked.
 */
public class ArtifactoryImplHttpErrorTest {

    private static final String BASE_URL = "http://artifactory.example.com/artifactory";
    private static final String HTML_BODY =
            "<!DOCTYPE html><html><body><h1>401 Unauthorized</h1></body></html>";
    private static final String JSON_409_BODY =
            "{\"errors\":[{\"status\":409,\"message\":\"Build already exists\"}]}";
    private static final String VALID_PROMOTE_JSON =
            "{\"messages\":[]}";
    private static final String POST_PATH = "/api/build/promote/my-build/42";

    @Mock
    private CloseableHttpClient mockHttpClient;

    private ArtifactoryImpl artifactory;
    private AutoCloseable mocks;

    @BeforeMethod
    public void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        artifactory = new ArtifactoryImpl(mockHttpClient, BASE_URL, "test-agent", "admin", null);
    }

    @AfterMethod
    public void tearDown() throws Exception {
        mocks.close();
    }

    // ── post() tests ──────────────────────────────────────────────────────────

    @Test(expectedExceptions = HttpResponseException.class)
    public void post_withHtmlBody_401_throwsHttpResponseException() throws Exception {
        stubHttpClient(HttpStatus.SC_UNAUTHORIZED, HTML_BODY);
        artifactory.post(POST_PATH, ContentType.APPLICATION_JSON, "{}", new HashMap<>(),
                BuildPromotionResponseImpl.class, null);
    }

    @Test
    public void post_with401_exceptionContainsStatusCode() throws Exception {
        stubHttpClient(HttpStatus.SC_UNAUTHORIZED, HTML_BODY);
        try {
            artifactory.post(POST_PATH, ContentType.APPLICATION_JSON, "{}", new HashMap<>(),
                    BuildPromotionResponseImpl.class, null);
        } catch (HttpResponseException e) {
            assertEquals(e.getStatusCode(), HttpStatus.SC_UNAUTHORIZED);
            // message should contain the response body, not a Jackson error
            assertNotNull(e.getMessage());
        }
    }

    @Test(expectedExceptions = HttpResponseException.class)
    public void post_withHtmlBody_503_throwsHttpResponseException() throws Exception {
        stubHttpClient(HttpStatus.SC_SERVICE_UNAVAILABLE, HTML_BODY);
        artifactory.post(POST_PATH, ContentType.APPLICATION_JSON, "{}", new HashMap<>(),
                BuildPromotionResponseImpl.class, null);
    }

    @Test
    public void post_with503_exceptionContainsStatusCode() throws Exception {
        stubHttpClient(HttpStatus.SC_SERVICE_UNAVAILABLE, HTML_BODY);
        try {
            artifactory.post(POST_PATH, ContentType.APPLICATION_JSON, "{}", new HashMap<>(),
                    BuildPromotionResponseImpl.class, null);
        } catch (HttpResponseException e) {
            assertEquals(e.getStatusCode(), HttpStatus.SC_SERVICE_UNAVAILABLE);
        }
    }

    @Test(expectedExceptions = HttpResponseException.class)
    public void post_withJsonErrorBody_409_throwsHttpResponseException() throws Exception {
        stubHttpClient(HttpStatus.SC_CONFLICT, JSON_409_BODY);
        artifactory.post(POST_PATH, ContentType.APPLICATION_JSON, "{}", new HashMap<>(),
                BuildPromotionResponseImpl.class, null);
    }

    @Test
    public void post_with409_exceptionMessageContainsServerError() throws Exception {
        stubHttpClient(HttpStatus.SC_CONFLICT, JSON_409_BODY);
        try {
            artifactory.post(POST_PATH, ContentType.APPLICATION_JSON, "{}", new HashMap<>(),
                    BuildPromotionResponseImpl.class, null);
        } catch (HttpResponseException e) {
            assertEquals(e.getStatusCode(), HttpStatus.SC_CONFLICT);
            assertNotNull(e.getMessage(), "Exception message should contain the server error body");
        }
    }

    @Test
    public void post_withValidJson_200_returnsDeserialisedObject() throws Exception {
        stubHttpClient(HttpStatus.SC_OK, VALID_PROMOTE_JSON);
        BuildPromotionResponseImpl result = artifactory.post(POST_PATH,
                ContentType.APPLICATION_JSON, "{}", new HashMap<>(),
                BuildPromotionResponseImpl.class, null);
        assertNotNull(result);
    }

    @Test
    public void post_withValidJson_201_returnsDeserialisedObject() throws Exception {
        stubHttpClient(HttpStatus.SC_CREATED, VALID_PROMOTE_JSON);
        BuildPromotionResponseImpl result = artifactory.post(POST_PATH,
                ContentType.APPLICATION_JSON, "{}", new HashMap<>(),
                BuildPromotionResponseImpl.class, null);
        assertNotNull(result);
    }

    // ── patch() tests ─────────────────────────────────────────────────────────

    @Test(expectedExceptions = HttpResponseException.class)
    public void patch_withHtmlBody_401_throwsHttpResponseException() throws Exception {
        stubHttpClient(HttpStatus.SC_UNAUTHORIZED, HTML_BODY);
        artifactory.patch("/api/system/configuration", ContentType.APPLICATION_XML,
                "<config/>", new HashMap<>(), String.class, null);
    }

    @Test
    public void patch_with401_exceptionContainsStatusCode() throws Exception {
        stubHttpClient(HttpStatus.SC_UNAUTHORIZED, HTML_BODY);
        try {
            artifactory.patch("/api/system/configuration", ContentType.APPLICATION_XML,
                    "<config/>", new HashMap<>(), String.class, null);
        } catch (HttpResponseException e) {
            assertEquals(e.getStatusCode(), HttpStatus.SC_UNAUTHORIZED);
        }
    }

    @Test
    public void patch_withValidResponse_200_returnsString() throws Exception {
        stubHttpClient(HttpStatus.SC_OK, "OK");
        String result = artifactory.patch("/api/system/configuration",
                ContentType.APPLICATION_XML, "<config/>", new HashMap<>(), String.class, null);
        assertNotNull(result);
    }

    // ── get() regression tests — was already correct, verify it still is ─────

    @Test(expectedExceptions = HttpResponseException.class)
    public void get_withHtmlBody_401_throwsHttpResponseException() throws Exception {
        stubHttpClient(HttpStatus.SC_UNAUTHORIZED, HTML_BODY);
        artifactory.get("/api/build/", String.class, null);
    }

    @Test
    public void get_with401_exceptionContainsStatusCode() throws Exception {
        stubHttpClient(HttpStatus.SC_UNAUTHORIZED, HTML_BODY);
        try {
            artifactory.get("/api/build/", String.class, null);
        } catch (HttpResponseException e) {
            assertEquals(e.getStatusCode(), HttpStatus.SC_UNAUTHORIZED);
        }
    }

    @Test
    public void get_withValidResponse_200_returnsString() throws Exception {
        stubHttpClient(HttpStatus.SC_OK, "pong");
        String result = artifactory.get("/api/system/ping", String.class, null);
        assertEquals(result, "pong");
    }

    // ── delete() regression tests ─────────────────────────────────────────────

    @Test(expectedExceptions = HttpResponseException.class)
    public void delete_with404_throwsHttpResponseException() throws Exception {
        stubHttpClient(HttpStatus.SC_NOT_FOUND, "Not Found");
        artifactory.delete("/api/build/no-such-build");
    }

    @Test
    public void delete_with200_returnsResponseBody() throws Exception {
        stubHttpClient(HttpStatus.SC_OK, "");
        // Should not throw
        artifactory.delete("/api/build/my-build");
    }

    // ── helper ────────────────────────────────────────────────────────────────

    /**
     * Configures the mock {@link CloseableHttpClient} to return an {@link HttpResponse} with the
     * given status code and body for any request.
     */
    private void stubHttpClient(int statusCode, String body) throws IOException {
        CloseableHttpResponse response = mock(CloseableHttpResponse.class);
        BasicStatusLine statusLine = new BasicStatusLine(
                HttpVersion.HTTP_1_1, statusCode,
                org.apache.http.impl.EnglishReasonPhraseCatalog.INSTANCE.getReason(statusCode, null));
        when(response.getStatusLine()).thenReturn(statusLine);
        StringEntity entity = new StringEntity(body, ContentType.TEXT_PLAIN);
        when(response.getEntity()).thenReturn(entity);
        when(mockHttpClient.execute(any(), any(org.apache.http.protocol.HttpContext.class)))
                .thenReturn(response);
    }
}
