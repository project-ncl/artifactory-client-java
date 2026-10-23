package org.jfrog.artifactory.client.impl

import org.jfrog.artifactory.client.BuildSearches
import org.jfrog.artifactory.client.PromotionFilters
import org.jfrog.artifactory.client.aql.AqlItem as AqlItemPredicate
import org.jfrog.artifactory.client.aql.AqlQueryBuilder

import java.time.LocalDateTime

/**
 * Adds 'promotion.*' filters directly to the {@link AqlQueryBuilder} shared with the owning {@link BuildSearches}.
 */
class PromotionFiltersImpl implements PromotionFilters {

    private final AqlQueryBuilder queryBuilder
    private final BuildSearches buildSearches

    PromotionFiltersImpl(AqlQueryBuilder queryBuilder, BuildSearches buildSearches) {
        this.queryBuilder = queryBuilder
        this.buildSearches = buildSearches
    }

    @Override
    PromotionFilters status(String status) {
        eq("promotion.status", status)
    }

    @Override
    PromotionFilters repo(String repo) {
        eq("promotion.repo", repo)
    }

    @Override
    PromotionFilters createdBy(String createdBy) {
        eq("promotion.created_by", createdBy)
    }

    @Override
    PromotionFilters user(String user) {
        eq("promotion.user", user)
    }

    @Override
    PromotionFilters comment(String comment) {
        eq("promotion.comment", comment)
    }

    @Override
    PromotionFilters createdSince(LocalDateTime since) {
        queryBuilder.gte("promotion.created", since.format(BuildSearchesImpl.ISO_FORMATTER))
        this
    }

    @Override
    BuildSearches done() {
        buildSearches
    }

    private PromotionFilters eq(String field, String value) {
        queryBuilder.item(AqlItemPredicate.aqlItem(field, value))
        this
    }
}
