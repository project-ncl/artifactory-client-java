package org.jfrog.artifactory.client.model;

/**
 * Parameters for the pncRollback user-plugin endpoint.
 *
 * @see <a href="https://jfrog.com/help/r/jfrog-artifactory-documentation/user-plugins">User Plugins</a>
 */
public interface PncRollbackRequest {
    /**
     * The repository that received the promotion (rollback source). Mandatory.
     */
    String getTargetRepository();

    /**
     * The BuildInfo repository key — the Artifactory repository that stores build metadata.
     * Mandatory.
     *
     * <p>This is <em>not</em> the repository where the build's artifacts are stored;
     * artifact locations are resolved from the build info itself by the plugin.
     */
    String getBuildInfoRepo();

    /** Disambiguates builds with the same name/number. Optional. */
    String getBuildStartTime();

    /** Free-text comment stored in the release status. Optional. */
    String getComment();

    /**
     * Release status string. Optional, defaults to {@code "rolledback"} in the plugin.
     */
    String getStatus();

    /** Whether to rollback build artifacts. Optional, defaults to {@code true} in the plugin. */
    Boolean getArtifacts();

    /** Whether to rollback build dependencies. Optional, defaults to {@code false} in the plugin. */
    Boolean getDependencies();

    /**
     * Must match the {@code copy} flag used during promotion.
     * If {@code true}, artifacts are deleted from {@code targetRepository}.
     * If {@code false} (move), artifacts are moved back to their original location.
     * Optional, defaults to {@code false} in the plugin.
     */
    Boolean getCopy();
}
