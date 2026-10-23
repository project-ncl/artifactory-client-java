package org.jfrog.artifactory.client.aql;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jfrog.artifactory.client.impl.util.Util;
import org.jfrog.artifactory.client.model.AqlBuild;
import org.jfrog.artifactory.client.model.AqlBuildSearchResults;
import org.jfrog.artifactory.client.model.AqlPromotion;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class AqlBuildTest {

    private final ObjectMapper mapper = Util.CONFIGURED_MAPPER;

    @Test
    public void testDeserializePrefixedAqlBuildJson() throws Exception {
        String json = "{\n" +
                "\"results\" : [ {\n" +
                "  \"build.name\" : \"org.example.app:sample-app:1.0.0\",\n" +
                "  \"build.number\" : \"build-ABC\",\n" +
                "  \"build.created\" : \"2024-01-15T10:00:00.000Z\",\n" +
                "  \"build.created_by\" : \"builder-user\",\n" +
                "  \"build.modified\" : \"2024-01-15T10:05:00.000Z\",\n" +
                "  \"build.modified_by\" : \"modifier-user\",\n" +
                "  \"build.repo\" : \"pnc-repo\",\n" +
                "  \"build.promotions\" : [ {\n" +
                "    \"build.promotion.comment\" : \"Promoted to release repository\",\n" +
                "    \"build.promotion.created\" : \"2024-01-15T10:10:00.000Z\",\n" +
                "    \"build.promotion.created_by\" : \"admin-user\",\n" +
                "    \"build.promotion.repo\" : \"pnc-repo\",\n" +
                "    \"build.promotion.status\" : \"promoted\",\n" +
                "    \"build.promotion.user\" : \"ci-user\"\n" +
                "  } ],\n" +
                "  \"build.started\" : \"2024-01-15T10:02:00.000Z\"\n" +
                "} ],\n" +
                "\"range\" : {\n" +
                "  \"start_pos\" : 0,\n" +
                "  \"end_pos\" : 1,\n" +
                "  \"total\" : 1\n" +
                "}\n" +
                "}";

        AqlBuildSearchResults searchResults = mapper.readValue(json, AqlBuildSearchResults.class);
        assertNotNull(searchResults);
        List<AqlBuild> builds = searchResults.getResults();
        assertNotNull(builds);
        assertEquals(builds.size(), 1);

        AqlBuild build = builds.get(0);
        assertEquals(build.getName(), "org.example.app:sample-app:1.0.0");
        assertEquals(build.getNumber(), "build-ABC");
        assertEquals(build.getCreatedBy(), "builder-user");
        assertEquals(build.getModifiedBy(), "modifier-user");
        assertEquals(build.getRepo(), "pnc-repo");
        assertNotNull(build.getCreated());
        assertNotNull(build.getModified());
        assertNotNull(build.getStarted());

        List<AqlPromotion> promotions = build.getPromotions();
        assertNotNull(promotions);
        assertEquals(promotions.size(), 1);

        AqlPromotion promotion = promotions.get(0);
        assertEquals(promotion.getComment(), "Promoted to release repository");
        assertEquals(promotion.getCreatedBy(), "admin-user");
        assertEquals(promotion.getRepo(), "pnc-repo");
        assertEquals(promotion.getStatus(), "promoted");
        assertEquals(promotion.getUser(), "ci-user");
        assertNotNull(promotion.getCreated());

        String buildString = build.toString();
        assertTrue(buildString.contains("name=org.example.app:sample-app:1.0.0"));
        assertTrue(buildString.contains("number=build-ABC"));

        String promotionString = promotion.toString();
        assertTrue(promotionString.contains("status=promoted"));
        assertTrue(promotionString.contains("repo=pnc-repo"));
    }

    @Test
    public void testDeserializeUnprefixedAqlBuildJson() throws Exception {
        String json = "{\n" +
                "\"results\" : [ {\n" +
                "  \"name\" : \"org.example.app:sample-app:1.0.0\",\n" +
                "  \"number\" : \"build-ABC\",\n" +
                "  \"created_by\" : \"builder\",\n" +
                "  \"modified_by\" : \"modifier\",\n" +
                "  \"repo\" : \"pnc-repo\",\n" +
                "  \"promotions\" : [ {\n" +
                "    \"comment\" : \"Release promotion\",\n" +
                "    \"created_by\" : \"admin-user\",\n" +
                "    \"status\" : \"promoted\",\n" +
                "    \"repo\" : \"pnc-repo\",\n" +
                "    \"user\" : \"ci-user\"\n" +
                "  } ]\n" +
                "} ]\n" +
                "}";

        AqlBuildSearchResults searchResults = mapper.readValue(json, AqlBuildSearchResults.class);
        assertNotNull(searchResults);
        assertEquals(searchResults.getResults().size(), 1);

        AqlBuild build = searchResults.getResults().get(0);
        assertEquals(build.getName(), "org.example.app:sample-app:1.0.0");
        assertEquals(build.getNumber(), "build-ABC");
        assertEquals(build.getCreatedBy(), "builder");
        assertEquals(build.getModifiedBy(), "modifier");
        assertEquals(build.getRepo(), "pnc-repo");

        assertNotNull(build.getPromotions());
        assertEquals(build.getPromotions().size(), 1);
        AqlPromotion promotion = build.getPromotions().get(0);
        assertEquals(promotion.getComment(), "Release promotion");
        assertEquals(promotion.getCreatedBy(), "admin-user");
        assertEquals(promotion.getStatus(), "promoted");
        assertEquals(promotion.getRepo(), "pnc-repo");
        assertEquals(promotion.getUser(), "ci-user");
    }
}
