package org.jfrog.artifactory.client;

import org.jfrog.artifactory.client.model.AllBuilds;
import org.jfrog.artifactory.client.model.BuildPromotionRequest;
import org.jfrog.artifactory.client.model.BuildPromotionResponse;
import org.jfrog.artifactory.client.model.BuildRuns;
import org.jfrog.artifactory.client.model.PncPromotionRequest;
import org.jfrog.artifactory.client.model.PncPromotionResponse;
import org.jfrog.build.api.Build;

import java.io.IOException;

/**
 * @author yahavi
 */
public interface Builds {
    AllBuilds getAllBuilds() throws IOException;

    BuildRuns getBuildRuns(String buildName) throws IOException;

    /**
     * Upload a build to Artifactory using the official build-info API
     *
     * @param build the build info from org.jfrog.build.api.Build
     * @throws IOException if the upload fails
     */
    void uploadBuild(Build build) throws IOException;

    /**
     * Upload a build to Artifactory with a project parameter using the official build-info API
     *
     * @param build the build info from org.jfrog.build.api.Build
     * @param project the project name to limit the build to
     * @throws IOException if the upload fails
     */
    void uploadBuild(Build build, String project) throws IOException;

    /**
     * Promote a build in Artifactory
     *
     * @param buildName the name of the build to promote
     * @param buildNumber the number of the build to promote
     * @param promotionRequest the promotion request details
     * @return the promotion response with messages
     * @throws IOException if the promotion fails
     */
    BuildPromotionResponse promoteBuild(String buildName, String buildNumber, BuildPromotionRequest promotionRequest) throws IOException;

    /**
     * Promote a build in Artifactory with a project parameter
     *
     * @param buildName the name of the build to promote
     * @param buildNumber the number of the build to promote
     * @param promotionRequest the promotion request details
     * @param project the project name
     * @return the promotion response with messages
     * @throws IOException if the promotion fails
     */
    BuildPromotionResponse promoteBuild(String buildName, String buildNumber, BuildPromotionRequest promotionRequest, String project) throws IOException;

    /**
     * Promote a build via the pncPromotion user plugin.
     *
     * @param buildName   the build name
     * @param buildNumber the build number
     * @param request     the promotion parameters
     * @return the promotion response containing message, artifact count, and dependency count
     * @throws IOException if the request fails
     */
    PncPromotionResponse promotePNCBuild(String buildName, String buildNumber,
            PncPromotionRequest request) throws IOException;

    /**
     * Promote a build via the pncPromotion user plugin, scoped to a project.
     *
     * @param buildName   the build name
     * @param buildNumber the build number
     * @param request     the promotion parameters
     * @param project     the project key (passed as {@code ?project=} query parameter); may be null
     * @return the promotion response containing message, artifact count, and dependency count
     * @throws IOException if the request fails
     */
    PncPromotionResponse promotePNCBuild(String buildName, String buildNumber,
            PncPromotionRequest request, String project) throws IOException;
}
