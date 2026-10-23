package org.jfrog.artifactory.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.jfrog.artifactory.client.model.AqlBuild;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Fluent builder for AQL-based build and promotion searches.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public interface BuildSearches {

    /**
     * Scope search to a project. Sets the build repository filter to "${project}-build-info"
     * (e.g. project("pnc") sets repository filter to "pnc-build-info").
     * <p>
     * <b>Note:</b> {@code project()} and {@link #repo(String)} both set the same single
     * repository filter and are mutually exclusive. If both are called on the same builder,
     * the one called last silently overrides the other. Use only one of them per search.
     *
     * @param project the project key
     * @return this builder
     * @see #repo(String)
     */
    BuildSearches project(String project);

    /**
     * Explicit repository filter for build search.
     * <p>
     * <b>Note:</b> {@code repo()} and {@link #project(String)} both set the same single
     * repository filter and are mutually exclusive. If both are called on the same builder,
     * the one called last silently overrides the other. Use only one of them per search.
     *
     * @param repo the repository name
     * @return this builder
     * @see #project(String)
     */
    BuildSearches repo(String repo);

    /**
     * Filter by build name.
     *
     * @param name the build name
     * @return this builder
     */
    BuildSearches name(String name);

    /**
     * Filter by build name prefix (matches "prefix*").
     *
     * @param namePrefix the prefix of the build name
     * @return this builder
     */
    BuildSearches namePrefix(String namePrefix);

    /**
     * Filter by build number.
     *
     * @param number the build number
     * @return this builder
     */
    BuildSearches number(String number);

    /**
     * Filter builds created since the given timestamp ($gte).
     *
     * @param since the lower bound creation timestamp
     * @return this builder
     */
    BuildSearches createdSince(LocalDateTime since);

    /**
     * Enters the promotion filters (AQL fields {@code promotion.*}) of this search.
     * Call {@link PromotionFilters#done()} to return to this builder.
     *
     * @return promotion filters bound to this builder
     */
    PromotionFilters promotion();

    /**
     * Custom include fields for AQL query.
     * Defaults to all standard build fields and promotions if not specified.
     *
     * @param fields the fields to include
     * @return this builder
     */
    BuildSearches include(String... fields);

    /**
     * Limit the number of search results returned.
     *
     * @param limit maximum results to return
     * @return this builder
     */
    BuildSearches limit(int limit);

    /**
     * Offset the starting position of results returned.
     *
     * @param offset the 0-based offset
     * @return this builder
     */
    BuildSearches offset(int offset);

    /**
     * Executes the built AQL query and returns matching builds.
     *
     * @return list of matching {@link AqlBuild} objects
     */
    List<AqlBuild> doSearch();

    /**
     * Executes a raw custom AQL builds query directly.
     *
     * @param aqlQuery the AQL query string (e.g. {@code builds.find(...)})
     * @return list of matching {@link AqlBuild} objects
     */
    List<AqlBuild> byAql(String aqlQuery);
}
