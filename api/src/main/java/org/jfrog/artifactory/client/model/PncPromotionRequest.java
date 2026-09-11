package org.jfrog.artifactory.client.model;

/**
 * Parameters for the pncPromotion user-plugin endpoint.
 *
 * @see <a href="https://jfrog.com/help/r/jfrog-artifactory-documentation/user-plugins">User Plugins</a>
 */
public interface PncPromotionRequest {
    /** Destination repository key. Mandatory. */
    String getTargetRepository();

    /**
     * The BuildInfo repository key — the Artifactory repository that stores build metadata
     * (e.g. {@code artifactory-build-info}). Mandatory.
     *
     * <p>This is <em>not</em> the repository where the build's artifacts are stored;
     * artifact locations are resolved from the build info itself by the plugin.
     */
    String getBuildInfoRepo();

    /** Disambiguates builds with the same name/number. Optional. */
    String getBuildStartTime();

    /** Free-text comment stored in the release status. Optional. */
    String getComment();

    /** Release status string. Optional, defaults to {@code "promoted"} in the plugin. */
    String getStatus();

    /** Whether to promote build artifacts. Optional, defaults to {@code true} in the plugin. */
    Boolean getArtifacts();

    /** Whether to promote build dependencies. Optional, defaults to {@code false} in the plugin. */
    Boolean getDependencies();

    /**
     * Whether to copy ({@code true}) or move ({@code false}) artifacts during promotion.
     * Optional, defaults to {@code false} (move) in the plugin.
     */
    Boolean getCopy();
}
