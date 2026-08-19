package org.jfrog.artifactory.client.aql;

import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Verifies that {@link AqlQueryBuilder} produces correctly structured AQL query strings.
 * The PLAIN_MAPPER inside AqlQueryBuilder must serialise plain values correctly without any
 * Repository mix-ins leaking into the output.
 */
public class AqlQueryBuilderObjectMapperTest {

    @Test
    public void build_withItem_producesItemsFindStructure() {
        // AqlItem.aqlItem() is the factory method for a key=value predicate
        String aql = new AqlQueryBuilder()
                .item(AqlItem.aqlItem("repo", "libs-release"))
                .build();
        assertTrue(aql.startsWith("items.find("), "AQL must start with items.find(");
        assertTrue(aql.contains("libs-release"), "AQL must contain the repo value");
        assertFalse(aql.contains("RepositoryMixIn"),
                "AQL output must not contain Repository mix-in artefacts");
    }

    @Test
    public void build_emptyQuery_producesMinimalAql() {
        String aql = new AqlQueryBuilder().build();
        assertTrue(aql.contains("items.find("), "Must contain items.find(");
    }

    @Test
    public void build_withSort_producesCorrectSortSuffix() {
        String aql = new AqlQueryBuilder()
                .item(AqlItem.aqlItem("repo", "libs-release"))
                .asc("name", "path")
                .build();
        assertTrue(aql.contains(".sort("), "AQL with sort must contain .sort(");
        assertTrue(aql.contains("$asc"), "Ascending sort must include $asc");
    }

    @Test
    public void build_withLimit_producesLimitSuffix() {
        String aql = new AqlQueryBuilder()
                .item(AqlItem.aqlItem("repo", "libs-release"))
                .limit(10)
                .build();
        assertTrue(aql.contains(".limit(10)"), "AQL with limit must contain .limit(10)");
    }

    @Test
    public void build_withOffset_producesOffsetSuffix() {
        String aql = new AqlQueryBuilder()
                .item(AqlItem.aqlItem("repo", "libs-release"))
                .offset(5)
                .build();
        assertTrue(aql.contains(".offset(5)"), "AQL with offset must contain .offset(5)");
    }

    @Test
    public void build_withLimitAndOffset_producesCorrectOrder() {
        // Suffix order must be: find(...) + include + sort + offset + limit
        String aql = new AqlQueryBuilder()
                .item(AqlItem.aqlItem("repo", "libs-release"))
                .limit(10)
                .offset(5)
                .build();
        int offsetIdx = aql.indexOf(".offset(5)");
        int limitIdx  = aql.indexOf(".limit(10)");
        assertTrue(offsetIdx < limitIdx,
                ".offset() must appear before .limit() in the AQL string");
    }

    @Test
    public void build_withInclude_producesIncludeSuffix() {
        String aql = new AqlQueryBuilder()
                .item(AqlItem.aqlItem("repo", "libs-release"))
                .include("name", "repo", "path")
                .build();
        assertTrue(aql.contains(".include("), "AQL with include must contain .include(");
    }

    @Test
    public void build_withMatch_producesMatchPredicate() {
        String aql = new AqlQueryBuilder()
                .match("repo", "libs-*")
                .build();
        assertTrue(aql.contains("libs-*"), "match pattern must appear in AQL output");
    }
}
