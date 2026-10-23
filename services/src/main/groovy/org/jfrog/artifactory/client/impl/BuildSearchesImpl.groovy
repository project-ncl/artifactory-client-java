package org.jfrog.artifactory.client.impl

import org.apache.http.entity.ContentType
import org.jfrog.artifactory.client.Artifactory
import org.jfrog.artifactory.client.BuildSearches
import org.jfrog.artifactory.client.PromotionFilters
import org.jfrog.artifactory.client.aql.AqlItem as AqlItemPredicate
import org.jfrog.artifactory.client.aql.AqlQueryBuilder
import org.jfrog.artifactory.client.model.AqlBuild
import org.jfrog.artifactory.client.model.AqlBuildSearchResults

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Fluent builder for AQL searches in the 'builds' domain.
 * <p>
 * Every call modifies the underlying {@link AqlQueryBuilder} immediately. Filters that target the same
 * AQL field (e.g. project/repo, name/namePrefix) therefore override each other - the last call wins.
 */
class BuildSearchesImpl implements BuildSearches {

    static final String BUILD_INFO_REPO_SUFFIX = "-build-info"
    static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

    private static final String[] DEFAULT_INCLUDE =
            ["name", "number", "created", "created_by", "modified", "modified_by", "started", "repo", "promotion"]

    private final ArtifactoryImpl artifactory
    private final String baseApiPath
    private final AqlQueryBuilder queryBuilder = AqlQueryBuilder.builds().include(DEFAULT_INCLUDE)
    private final PromotionFilters promotionFilters

    BuildSearchesImpl(Artifactory artifactory, String baseApiPath) {
        this.artifactory = artifactory as ArtifactoryImpl
        this.baseApiPath = baseApiPath
        this.promotionFilters = new PromotionFiltersImpl(queryBuilder, this)
    }

    @Override
    BuildSearches project(String project) {
        repo(project + BUILD_INFO_REPO_SUFFIX)
    }

    @Override
    BuildSearches repo(String repo) {
        queryBuilder.item(AqlItemPredicate.aqlItem("repo", repo))
        this
    }

    @Override
    BuildSearches name(String name) {
        queryBuilder.item(AqlItemPredicate.aqlItem("name", name))
        this
    }

    @Override
    BuildSearches namePrefix(String namePrefix) {
        queryBuilder.match("name", namePrefix.endsWith("*") ? namePrefix : namePrefix + "*")
        this
    }

    @Override
    BuildSearches number(String number) {
        queryBuilder.item(AqlItemPredicate.aqlItem("number", number))
        this
    }

    @Override
    BuildSearches createdSince(LocalDateTime since) {
        queryBuilder.gte("created", since.format(ISO_FORMATTER))
        this
    }

    @Override
    PromotionFilters promotion() {
        promotionFilters
    }

    @Override
    BuildSearches include(String... fields) {
        queryBuilder.include(fields)
        this
    }

    @Override
    BuildSearches limit(int limit) {
        queryBuilder.limit(limit)
        this
    }

    @Override
    BuildSearches offset(int offset) {
        queryBuilder.offset(offset)
        this
    }

    @Override
    List<AqlBuild> doSearch() {
        return byAql(queryBuilder.build())
    }

    @Override
    List<AqlBuild> byAql(String aqlQuery) {
        String url = "${baseApiPath}/search/aql"
        AqlBuildSearchResults searchResults = artifactory.post(url, ContentType.TEXT_PLAIN, aqlQuery, null, AqlBuildSearchResults, null)
        return searchResults?.getResults() ?: new ArrayList<AqlBuild>()
    }
}
