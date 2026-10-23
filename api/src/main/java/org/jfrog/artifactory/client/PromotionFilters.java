package org.jfrog.artifactory.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;

/**
 * Filters for the AQL <b>promotion</b> domain of a build search (fields {@code promotion.*}).
 * <p>
 * Obtained via {@link BuildSearches#promotion()}. All filters are combined with the filters of the
 * owning {@link BuildSearches} using logical AND. Calling the same filter twice overrides the previous value.
 * Call {@link #done()} to return to the owning {@link BuildSearches} and continue chaining or execute the search.
 * <p>
 * Example:
 * <pre>{@code
 * artifactory.searches().builds()
 *     .project("pnc")
 *     .promotion()
 *         .status("promoted")
 *         .repo("pnc-release")
 *         .done()
 *     .doSearch();
 * }</pre>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public interface PromotionFilters {

    /**
     * Filter by promotion status (AQL field {@code promotion.status}), e.g. "promoted".
     *
     * @param status the promotion status
     * @return this filter
     */
    PromotionFilters status(String status);

    /**
     * Filter by the repository the build was promoted to (AQL field {@code promotion.repo}).
     *
     * @param repo the target repository name
     * @return this filter
     */
    PromotionFilters repo(String repo);

    /**
     * Filter by the Artifactory user that promoted the build (AQL field {@code promotion.created_by}).
     *
     * @param createdBy the Artifactory user name
     * @return this filter
     */
    PromotionFilters createdBy(String createdBy);

    /**
     * Filter by the CI server user that promoted the build (AQL field {@code promotion.user}).
     *
     * @param user the CI server user name
     * @return this filter
     */
    PromotionFilters user(String user);

    /**
     * Filter by the promotion comment (AQL field {@code promotion.comment}), exact match.
     *
     * @param comment the comment text
     * @return this filter
     */
    PromotionFilters comment(String comment);

    /**
     * Filter builds promoted since the given timestamp (AQL field {@code promotion.created}, {@code $gte}).
     *
     * @param since the lower bound promotion timestamp
     * @return this filter
     */
    PromotionFilters createdSince(LocalDateTime since);

    /**
     * Returns to the owning {@link BuildSearches}, which now carries the promotion filters configured so far.
     *
     * @return the owning builder
     */
    BuildSearches done();
}
