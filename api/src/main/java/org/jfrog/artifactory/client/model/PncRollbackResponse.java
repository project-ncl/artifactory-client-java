package org.jfrog.artifactory.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Response from the pncRollback user-plugin endpoint.
 *
 * <p>On HTTP 200 the plugin returns:
 * <pre>
 * {
 *     "message":        "Build {buildName}/{buildNumber} has been successfully rolled back",
 *     "rolledBackArts": &lt;number of rolled-back artifacts&gt;,
 *     "rolledBackDeps": &lt;number of rolled-back dependencies&gt;,
 *     "skippedArts":    &lt;number of artifacts skipped (already absent)&gt;,
 *     "skippedDeps":    &lt;number of dependencies skipped (already absent)&gt;
 * }
 * </pre>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public interface PncRollbackResponse {
    /** Human-readable success message from the plugin. */
    String getMessage();

    /** Number of artifacts rolled back during this operation. */
    int getRolledBackArts();

    /** Number of dependencies rolled back during this operation. */
    int getRolledBackDeps();

    /** Number of artifacts skipped because they were already absent. */
    int getSkippedArts();

    /** Number of dependencies skipped because they were already absent. */
    int getSkippedDeps();
}
