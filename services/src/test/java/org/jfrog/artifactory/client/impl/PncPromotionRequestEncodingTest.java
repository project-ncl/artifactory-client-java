package org.jfrog.artifactory.client.impl;

import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicStatusLine;
import org.jfrog.artifactory.client.model.PncPromotionResponse;
import org.jfrog.artifactory.client.model.impl.PncPromotionRequestImpl;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Pure unit tests for {@link BuildsImpl#executePncPromotion}.
 *
 * Verifies URL construction, parameter encoding, and mandatory-field validation.
 * No live Artifactory instance is required — {@link CloseableHttpClient} is mocked.
 */
public class PncPromotionRequestEncodingTest {

    private static final String BASE_URL = "http://artifactory.example.com/artifactory";
    private static final String SUCCESS_MESSAGE = "Build my-build/42 has been successfully promoted";
    private static final String SUCCESS_BODY =
            "{\"message\":\"" + SUCCESS_MESSAGE + "\",\"promotedArts\":5,\"promotedDeps\":2}";

    @Mock
    private CloseableHttpClient mockHttpClient;

    private ArtifactoryImpl artifactory;
    private BuildsImpl builds;
    private AutoCloseable mocks;

    @BeforeMethod
    public void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        artifactory = new ArtifactoryImpl(mockHttpClient, BASE_URL, "test-agent", "admin", null);
        builds = new BuildsImpl(artifactory, "/api");
    }

    @AfterMethod
    public void tearDown() throws Exception {
        mocks.close();
    }

    // ── mandatory field validation ─────────────────────────────────────────────

    @Test(expectedExceptions = IllegalArgumentException.class,
          expectedExceptionsMessageRegExp = "targetRepository is mandatory")
    public void executePncPromotion_nullTargetRepository_throwsIllegalArgument() throws IOException {
        PncPromotionRequestImpl req = mandatoryRequest();
        req.setTargetRepository(null);
        builds.promotePNCBuild("build", "1", req);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
          expectedExceptionsMessageRegExp = "targetRepository is mandatory")
    public void executePncPromotion_blankTargetRepository_throwsIllegalArgument() throws IOException {
        PncPromotionRequestImpl req = mandatoryRequest();
        req.setTargetRepository("");
        builds.promotePNCBuild("build", "1", req);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
          expectedExceptionsMessageRegExp = "buildInfoRepo is mandatory")
    public void executePncPromotion_nullBuildInfoRepo_throwsIllegalArgument() throws IOException {
        PncPromotionRequestImpl req = mandatoryRequest();
        req.setBuildInfoRepo(null);
        builds.promotePNCBuild("build", "1", req);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
          expectedExceptionsMessageRegExp = "buildInfoRepo is mandatory")
    public void executePncPromotion_blankBuildInfoRepo_throwsIllegalArgument() throws IOException {
        PncPromotionRequestImpl req = mandatoryRequest();
        req.setBuildInfoRepo("");
        builds.promotePNCBuild("build", "1", req);
    }

    // ── URL construction ───────────────────────────────────────────────────────

    @Test
    public void executePncPromotion_mandatoryFieldsOnly_buildsCorrectUrl() throws Exception {
        ArgumentCaptor<org.apache.http.client.methods.HttpUriRequest> requestCaptor =
                ArgumentCaptor.forClass(org.apache.http.client.methods.HttpUriRequest.class);
        stubHttpClient(HttpStatus.SC_OK, SUCCESS_BODY, requestCaptor);

        PncPromotionRequestImpl req = mandatoryRequest();
        PncPromotionResponse result = builds.promotePNCBuild("my-build", "42", req);

        assertEquals(result.getMessage(), SUCCESS_MESSAGE);
        assertEquals(result.getPromotedArts(), 5);
        assertEquals(result.getPromotedDeps(), 2);
        String uri = requestCaptor.getValue().getURI().toString();

        // path segments
        assertTrue(uri.contains("/plugins/build/promote/pncPromotion/my-build/42"),
                "Expected pncPromotion path in: " + uri);

        assertTrue(uri.contains("?params=targetRepository=pnc-target;buildInfoRepo=pnc-build-info"),
                "Expected literal semicolon-separated params in: " + uri);
    }

    @Test
    public void executePncPromotion_withProject_projectAppearsBeforeParams() throws Exception {
        ArgumentCaptor<org.apache.http.client.methods.HttpUriRequest> requestCaptor =
                ArgumentCaptor.forClass(org.apache.http.client.methods.HttpUriRequest.class);
        stubHttpClient(HttpStatus.SC_OK, SUCCESS_BODY, requestCaptor);

        builds.promotePNCBuild("my-build", "42", mandatoryRequest(), "pnc-devel");

        String uri = requestCaptor.getValue().getURI().toString();
        assertTrue(uri.contains("?project=pnc-devel&params="),
                "Expected ?project=pnc-devel&params= in: " + uri);
    }

    @Test
    public void executePncPromotion_nullProject_noProjectParam() throws Exception {
        ArgumentCaptor<org.apache.http.client.methods.HttpUriRequest> requestCaptor =
                ArgumentCaptor.forClass(org.apache.http.client.methods.HttpUriRequest.class);
        stubHttpClient(HttpStatus.SC_OK, SUCCESS_BODY, requestCaptor);

        builds.promotePNCBuild("my-build", "42", mandatoryRequest(), null);

        String uri = requestCaptor.getValue().getURI().toString();
        assertTrue(!uri.contains("project="), "project param should be absent in: " + uri);
        assertTrue(uri.contains("?params="),  "params query should still be present in: " + uri);
    }

    @Test
    public void executePncPromotion_emptyProject_noProjectParam() throws Exception {
        ArgumentCaptor<org.apache.http.client.methods.HttpUriRequest> requestCaptor =
                ArgumentCaptor.forClass(org.apache.http.client.methods.HttpUriRequest.class);
        stubHttpClient(HttpStatus.SC_OK, SUCCESS_BODY, requestCaptor);

        builds.promotePNCBuild("my-build", "42", mandatoryRequest(), "");

        String uri = requestCaptor.getValue().getURI().toString();
        assertTrue(!uri.contains("project="), "project param should be absent in: " + uri);
    }

    @Test
    public void executePncPromotion_optionalFieldsNull_omittedFromParams() throws Exception {
        ArgumentCaptor<org.apache.http.client.methods.HttpUriRequest> requestCaptor =
                ArgumentCaptor.forClass(org.apache.http.client.methods.HttpUriRequest.class);
        stubHttpClient(HttpStatus.SC_OK, SUCCESS_BODY, requestCaptor);

        PncPromotionRequestImpl req = mandatoryRequest(); // all optionals are null
        builds.promotePNCBuild("my-build", "42", req);

        String uri = requestCaptor.getValue().getURI().toString();
        // none of the optional keys should appear
        String paramsValue = uri.substring(uri.indexOf("?params=") + "?params=".length());
        String decoded = java.net.URLDecoder.decode(paramsValue, "UTF-8");
        assertTrue(!decoded.contains("buildStartTime"), "buildStartTime should be absent: " + decoded);
        assertTrue(!decoded.contains("comment"),        "comment should be absent: " + decoded);
        assertTrue(!decoded.contains("status"),         "status should be absent: " + decoded);
        assertTrue(!decoded.contains("artifacts"),      "artifacts should be absent: " + decoded);
        assertTrue(!decoded.contains("dependencies"),   "dependencies should be absent: " + decoded);
        assertTrue(!decoded.contains("copy"),           "copy should be absent: " + decoded);
    }

    @Test
    public void executePncPromotion_optionalFieldsSet_includedInParams() throws Exception {
        ArgumentCaptor<org.apache.http.client.methods.HttpUriRequest> requestCaptor =
                ArgumentCaptor.forClass(org.apache.http.client.methods.HttpUriRequest.class);
        stubHttpClient(HttpStatus.SC_OK, SUCCESS_BODY, requestCaptor);

        PncPromotionRequestImpl req = mandatoryRequest();
        req.setBuildStartTime("2024-01-15T10:30:00.000+0000");
        req.setComment("release candidate");
        req.setStatus("released");
        req.setArtifacts(true);
        req.setDependencies(false);

        builds.promotePNCBuild("my-build", "42", req);

        String uri = requestCaptor.getValue().getURI().toString();
        String paramsValue = uri.substring(uri.indexOf("?params=") + "?params=".length());
        String decoded = java.net.URLDecoder.decode(paramsValue, "UTF-8");

        assertTrue(decoded.contains("buildStartTime=2024-01-15T10:30:00.000+0000"), "buildStartTime missing: " + decoded);
        assertTrue(decoded.contains("comment=release candidate"), "comment missing: " + decoded);
        assertTrue(decoded.contains("status=released"),           "status missing: " + decoded);
        assertTrue(decoded.contains("artifacts=true"),            "artifacts missing: " + decoded);
        assertTrue(decoded.contains("dependencies=false"),        "dependencies missing: " + decoded);
    }

    @Test
    public void executePncPromotion_paramValuesAreUrlEncoded() throws Exception {
        ArgumentCaptor<org.apache.http.client.methods.HttpUriRequest> requestCaptor =
                ArgumentCaptor.forClass(org.apache.http.client.methods.HttpUriRequest.class);
        stubHttpClient(HttpStatus.SC_OK, SUCCESS_BODY, requestCaptor);

        PncPromotionRequestImpl req = mandatoryRequest();
        req.setComment("release;candidate&approved=true");
        builds.promotePNCBuild("my-build", "42", req);

        String uri = requestCaptor.getValue().getURI().toString();
        assertTrue(uri.contains("comment=release%3Bcandidate%26approved%3Dtrue"),
                "Expected encoded comment value in: " + uri);
        assertTrue(uri.contains(";comment="), "Expected literal parameter separator in: " + uri);
    }

    @Test
    public void executePncPromotion_copyFalse_appearsInParams() throws Exception {
        ArgumentCaptor<org.apache.http.client.methods.HttpUriRequest> requestCaptor =
                ArgumentCaptor.forClass(org.apache.http.client.methods.HttpUriRequest.class);
        stubHttpClient(HttpStatus.SC_OK, SUCCESS_BODY, requestCaptor);

        PncPromotionRequestImpl req = mandatoryRequest();
        req.setCopy(false);
        builds.promotePNCBuild("my-build", "42", req);

        String uri = requestCaptor.getValue().getURI().toString();
        String decoded = java.net.URLDecoder.decode(
                uri.substring(uri.indexOf("?params=") + "?params=".length()), "UTF-8");
        assertTrue(decoded.contains("copy=false"), "copy=false missing: " + decoded);
    }

    @Test
    public void executePncPromotion_copyTrue_appearsInParams() throws Exception {
        ArgumentCaptor<org.apache.http.client.methods.HttpUriRequest> requestCaptor =
                ArgumentCaptor.forClass(org.apache.http.client.methods.HttpUriRequest.class);
        stubHttpClient(HttpStatus.SC_OK, SUCCESS_BODY, requestCaptor);

        PncPromotionRequestImpl req = mandatoryRequest();
        req.setCopy(true);
        builds.promotePNCBuild("my-build", "42", req);

        String uri = requestCaptor.getValue().getURI().toString();
        String decoded = java.net.URLDecoder.decode(
                uri.substring(uri.indexOf("?params=") + "?params=".length()), "UTF-8");
        assertTrue(decoded.contains("copy=true"), "copy=true missing: " + decoded);
    }

    @Test
    public void executePncPromotion_copyNull_absentFromParams() throws Exception {
        ArgumentCaptor<org.apache.http.client.methods.HttpUriRequest> requestCaptor =
                ArgumentCaptor.forClass(org.apache.http.client.methods.HttpUriRequest.class);
        stubHttpClient(HttpStatus.SC_OK, SUCCESS_BODY, requestCaptor);

        builds.promotePNCBuild("my-build", "42", mandatoryRequest()); // copy not set

        String uri = requestCaptor.getValue().getURI().toString();
        String decoded = java.net.URLDecoder.decode(
                uri.substring(uri.indexOf("?params=") + "?params=".length()), "UTF-8");
        assertTrue(!decoded.contains("copy"), "copy should be absent when null: " + decoded);
    }

    @Test
    public void executePncPromotion_buildNameAndNumberUrlEncoded() throws Exception {
        ArgumentCaptor<org.apache.http.client.methods.HttpUriRequest> requestCaptor =
                ArgumentCaptor.forClass(org.apache.http.client.methods.HttpUriRequest.class);
        stubHttpClient(HttpStatus.SC_OK, SUCCESS_BODY, requestCaptor);

        builds.promotePNCBuild("my build/name", "1.0+rc1", mandatoryRequest());

        String uri = requestCaptor.getValue().getURI().toString();
        // spaces → +, / → %2F, + → %2B
        assertTrue(uri.contains("my+build%2Fname"), "Build name should be URL-encoded in: " + uri);
        assertTrue(uri.contains("1.0%2Brc1"),       "Build number should be URL-encoded in: " + uri);
    }

    // ── helper ─────────────────────────────────────────────────────────────────

    /** Returns a request with all mandatory fields populated and all optionals null. */
    private static PncPromotionRequestImpl mandatoryRequest() {
        PncPromotionRequestImpl req = new PncPromotionRequestImpl();
        req.setTargetRepository("pnc-target");
        req.setBuildInfoRepo("pnc-build-info");  // BuildInfo repository, not artifact source repo
        return req;
    }

    private void stubHttpClient(int statusCode, String body,
            ArgumentCaptor<org.apache.http.client.methods.HttpUriRequest> captor) throws IOException {
        CloseableHttpResponse response = mock(CloseableHttpResponse.class);
        BasicStatusLine statusLine = new BasicStatusLine(
                HttpVersion.HTTP_1_1, statusCode,
                org.apache.http.impl.EnglishReasonPhraseCatalog.INSTANCE.getReason(statusCode, null));
        when(response.getStatusLine()).thenReturn(statusLine);
        StringEntity entity = new StringEntity(body, ContentType.APPLICATION_JSON);
        when(response.getEntity()).thenReturn(entity);
        when(mockHttpClient.execute(
                captor.capture(),
                any(org.apache.http.protocol.HttpContext.class)))
                .thenReturn(response);
    }
}
