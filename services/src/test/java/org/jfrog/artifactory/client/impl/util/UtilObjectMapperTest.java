package org.jfrog.artifactory.client.impl.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicStatusLine;
import org.jfrog.artifactory.client.model.AllBuilds;
import org.jfrog.artifactory.client.model.BuildPromotionResponse;
import org.jfrog.artifactory.client.model.BuildRuns;
import org.jfrog.artifactory.client.model.impl.BuildPromotionRequestImpl;
import org.jfrog.artifactory.client.model.impl.BuildPromotionResponseImpl;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;

/**
 * Verifies the shared {@link Util#CONFIGURED_MAPPER} singleton is:
 * <ul>
 *   <li>a true singleton (same reference on every access)</li>
 *   <li>correctly pre-configured with all known interface→impl abstract-type mappings</li>
 *   <li>thread-safe for concurrent read operations</li>
 * </ul>
 */
public class UtilObjectMapperTest {

    // ── singleton ─────────────────────────────────────────────────────────────

    @Test
    public void configuredMapper_isSingleton() {
        ObjectMapper first = Util.CONFIGURED_MAPPER;
        ObjectMapper second = Util.CONFIGURED_MAPPER;
        assertSame(first, second, "CONFIGURED_MAPPER must be the same instance on every access");
    }

    // ── interface→impl mappings pre-registered ────────────────────────────────

    @Test
    public void responseToObject_buildPromotionResponse_resolvedToImpl() throws Exception {
        String json = "{\"messages\":[]}";
        HttpResponse response = mockJsonResponse(json);

        Object result = Util.responseToObject(response, BuildPromotionResponseImpl.class,
                BuildPromotionResponse.class);

        assertNotNull(result);
        // The concrete type must be the impl — proves the mapping is registered
        assertEquals(result.getClass(), BuildPromotionResponseImpl.class);
    }

    @Test
    public void responseToObject_allBuilds_resolvedToImpl() throws Exception {
        // Minimal AllBuilds JSON
        String json = "{\"uri\":\"/api/build\",\"builds\":[]}";
        HttpResponse response = mockJsonResponse(json);

        // Use the interface class to show the mapping resolves it correctly
        AllBuilds result = Util.responseToObject(response,
                org.jfrog.artifactory.client.model.impl.AllBuildsImpl.class, AllBuilds.class);

        assertNotNull(result);
        assertEquals(result.getUri(), "/api/build");
    }

    @Test
    public void responseToObject_buildRuns_resolvedToImpl() throws Exception {
        String json = "{\"uri\":\"/api/build/MyBuild\",\"buildsNumbers\":[]}";
        HttpResponse response = mockJsonResponse(json);

        BuildRuns result = Util.responseToObject(response,
                org.jfrog.artifactory.client.model.impl.BuildRunsImpl.class, BuildRuns.class);

        assertNotNull(result);
    }

    // ── round-trip serialisation ──────────────────────────────────────────────

    @Test
    public void getStringFromObject_roundtrip_buildPromotionRequest() throws Exception {
        BuildPromotionRequestImpl request = new BuildPromotionRequestImpl();
        request.setStatus("Released");
        request.setComment("automated");
        request.setTargetRepo("releases");
        request.setDryRun(false);

        String json = Util.getStringFromObject(request);
        assertNotNull(json);

        // Deserialise back and verify round-trip fidelity
        BuildPromotionRequestImpl restored = Util.CONFIGURED_MAPPER.readValue(json,
                BuildPromotionRequestImpl.class);
        assertEquals(restored.getStatus(), "Released");
        assertEquals(restored.getComment(), "automated");
        assertEquals(restored.getTargetRepo(), "releases");
        assertEquals(restored.getDryRun(), Boolean.FALSE);
    }

    @Test
    public void getStringFromObject_nullInput_returnsNull() throws Exception {
        assertNull(Util.getStringFromObject(null));
    }

    // ── unknown-property tolerance ────────────────────────────────────────────

    @Test
    public void parseText_unknownProperties_doesNotThrow() throws Exception {
        // FAIL_ON_UNKNOWN_PROPERTIES=false means extra fields are silently ignored
        String json = "{\"uri\":\"/api/build\",\"builds\":[],\"unknownField\":\"ignored\"}";
        org.jfrog.artifactory.client.model.impl.AllBuildsImpl result =
                Util.parseText(json, org.jfrog.artifactory.client.model.impl.AllBuildsImpl.class);
        assertNotNull(result);
        assertEquals(result.getUri(), "/api/build");
    }

    // ── thread safety ─────────────────────────────────────────────────────────

    @Test
    public void configuredMapper_isThreadSafe_concurrentReads() throws Exception {
        final int THREADS = 20;
        final String json = "{\"messages\":[]}";
        ExecutorService pool = Executors.newFixedThreadPool(THREADS);
        List<Callable<BuildPromotionResponseImpl>> tasks = new ArrayList<>();

        for (int i = 0; i < THREADS; i++) {
            tasks.add(() -> {
                HttpResponse response = mockJsonResponse(json);
                return Util.responseToObject(response, BuildPromotionResponseImpl.class, null);
            });
        }

        List<Future<BuildPromotionResponseImpl>> futures = pool.invokeAll(tasks);
        pool.shutdown();

        for (Future<BuildPromotionResponseImpl> future : futures) {
            try {
                assertNotNull(future.get(), "Each concurrent call must return a non-null result");
            } catch (ExecutionException e) {
                throw new AssertionError("Concurrent call threw an exception: " + e.getCause(), e.getCause());
            }
        }
    }

    // ── helper ────────────────────────────────────────────────────────────────

    private static HttpResponse mockJsonResponse(String body) throws IOException {
        HttpResponse response = mock(HttpResponse.class);
        BasicStatusLine statusLine = new BasicStatusLine(
                HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
        when(response.getStatusLine()).thenReturn(statusLine);
        StringEntity entity = new StringEntity(body, ContentType.APPLICATION_JSON);
        when(response.getEntity()).thenReturn(entity);
        return response;
    }
}
