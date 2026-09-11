package org.jfrog.artifactory.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Response from the pncPromotion user-plugin endpoint.
 *
 * <p>On HTTP 200 the plugin returns:
 * <pre>
 * {
 *     "message":      "Build {buildName}/{buildNumber} has been successfully promoted",
 *     "promotedArts": &lt;number of promoted artifacts&gt;,
 *     "promotedDeps": &lt;number of promoted dependencies&gt;
 * }
 * </pre>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public interface PncPromotionResponse {
    /** Human-readable success message from the plugin. */
    String getMessage();

    /** Number of artifacts promoted during this operation. */
    int getPromotedArts();

    /** Number of dependencies promoted during this operation. */
    int getPromotedDeps();
}
