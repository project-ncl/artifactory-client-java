package org.jfrog.artifactory.client;

import org.apache.http.entity.ContentType;
import org.jfrog.artifactory.client.impl.ArtifactoryImpl;
import org.jfrog.artifactory.client.impl.SearchesImpl;
import org.jfrog.artifactory.client.model.AqlBuild;
import org.jfrog.artifactory.client.model.AqlBuildSearchResults;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class SearchesBuildsAqlTest {

    private ArtifactoryImpl artifactory;
    private Searches searches;
    private ArgumentCaptor<String> queryCaptor;

    @BeforeMethod
    public void setUp() throws IOException {
        artifactory = mock(ArtifactoryImpl.class);
        searches = new SearchesImpl(artifactory, "/api");

        AqlBuildSearchResults mockResults = mock(AqlBuildSearchResults.class);
        when(mockResults.getResults()).thenReturn(Collections.singletonList(mock(AqlBuild.class)));
        queryCaptor = ArgumentCaptor.forClass(String.class);
        when(artifactory.post(eq("/api/search/aql"), eq(ContentType.TEXT_PLAIN), queryCaptor.capture(), isNull(), eq(AqlBuildSearchResults.class), isNull()))
                .thenReturn(mockResults);
    }

    @Test
    public void testByName() {
        List<AqlBuild> results = searches.builds().name("org.example.app:sample-app:1.0.0").doSearch();

        assertNotNull(results);
        assertEquals(results.size(), 1);
        String query = queryCaptor.getValue();
        assertTrue(query.startsWith("builds.find("));
        assertTrue(query.contains("\"name\":\"org.example.app:sample-app:1.0.0\""));
        assertTrue(query.contains(".include("));
    }

    @Test
    public void testByNumber() {
        searches.builds().number("build-ABC").doSearch();

        String query = queryCaptor.getValue();
        assertTrue(query.startsWith("builds.find("));
        assertTrue(query.contains("\"number\":\"build-ABC\""));
    }

    @Test
    public void testByNameAndNumber() {
        searches.builds().name("org.example.app:sample-app:1.0.0").number("build-ABC").doSearch();

        String query = queryCaptor.getValue();
        assertTrue(query.contains("\"name\":\"org.example.app:sample-app:1.0.0\""));
        assertTrue(query.contains("\"number\":\"build-ABC\""));
    }

    @Test
    public void testByNamePrefix() {
        searches.builds().namePrefix("org.example.app").doSearch();

        assertTrue(queryCaptor.getValue().contains("\"name\":{\"$match\":\"org.example.app*\"}"));
    }

    @Test
    public void testByNamePrefixWithExistingWildcard() {
        searches.builds().namePrefix("org.example.app*").doSearch();

        assertTrue(queryCaptor.getValue().contains("\"name\":{\"$match\":\"org.example.app*\"}"));
    }

    @Test
    public void testCreatedSince() {
        searches.builds().createdSince(LocalDateTime.of(2024, 1, 15, 10, 0, 0)).doSearch();

        String query = queryCaptor.getValue();
        assertTrue(query.startsWith("builds.find("));
        assertTrue(query.contains("\"created\":{\"$gte\":\"2024-01-15T10:00:00.000Z\"}"));
    }

    @Test
    public void testProjectAddsBuildInfoRepoFilter() {
        searches.builds().project("pnc").number("build-ABC").doSearch();

        String query = queryCaptor.getValue();
        assertTrue(query.contains("\"repo\":\"pnc-build-info\""));
        assertTrue(query.contains("\"number\":\"build-ABC\""));
    }

    @Test
    public void testExplicitRepoOverridesProject() {
        searches.builds().project("pnc").repo("custom-repo").doSearch();

        String query = queryCaptor.getValue();
        assertTrue(query.contains("\"repo\":\"custom-repo\""));
        assertTrue(!query.contains("pnc-build-info"));
    }

    @Test
    public void testPromotionStatus() {
        searches.builds().promotion().status("promoted").done().doSearch();

        String query = queryCaptor.getValue();
        assertTrue(query.startsWith("builds.find("));
        assertTrue(query.contains("\"promotion.status\":\"promoted\""), query);
    }

    @Test
    public void testAllPromotionFiltersCombinedWithBuildFilters() {
        searches.builds()
                .project("pnc")
                .promotion()
                    .status("promoted")
                    .repo("pnc-release")
                    .createdBy("admin")
                    .user("ci-user")
                    .comment("release candidate")
                    .createdSince(LocalDateTime.of(2024, 1, 15, 10, 0, 0))
                    .done()
                .number("build-ABC")
                .doSearch();

        String query = queryCaptor.getValue();
        assertTrue(query.contains("\"repo\":\"pnc-build-info\""), query);
        assertTrue(query.contains("\"number\":\"build-ABC\""), query);
        assertTrue(query.contains("\"promotion.status\":\"promoted\""), query);
        assertTrue(query.contains("\"promotion.repo\":\"pnc-release\""), query);
        assertTrue(query.contains("\"promotion.created_by\":\"admin\""), query);
        assertTrue(query.contains("\"promotion.user\":\"ci-user\""), query);
        assertTrue(query.contains("\"promotion.comment\":\"release candidate\""), query);
        assertTrue(query.contains("\"promotion.created\":{\"$gte\":\"2024-01-15T10:00:00.000Z\"}"), query);
    }

    @Test
    public void testPromotionDoneReturnsSameBuildSearches() {
        BuildSearches builds = searches.builds();

        assertTrue(builds.promotion().status("promoted").done() == builds);
    }

    @Test
    public void testCustomIncludeLimitAndOffset() {
        searches.builds().include("name", "number").offset(5).limit(10).doSearch();

        String query = queryCaptor.getValue();
        assertTrue(query.contains(".include(\"name\",\"number\")"), query);
        assertTrue(query.contains(".offset(5)"), query);
        assertTrue(query.contains(".limit(10)"), query);
    }

    @Test
    public void testByAql() throws IOException {
        String customAql = "builds.find({\"number\":\"build-ABC\"})";

        List<AqlBuild> results = searches.builds().byAql(customAql);

        assertNotNull(results);
        verify(artifactory).post(eq("/api/search/aql"), eq(ContentType.TEXT_PLAIN), eq(customAql), isNull(), eq(AqlBuildSearchResults.class), isNull());
    }
}
