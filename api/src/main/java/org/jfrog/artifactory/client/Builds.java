package org.jfrog.artifactory.client;

import org.jfrog.artifactory.client.model.AllBuilds;
import org.jfrog.artifactory.client.model.BuildInfo;
import org.jfrog.artifactory.client.model.BuildPromotionRequest;
import org.jfrog.artifactory.client.model.BuildPromotionResponse;
import org.jfrog.artifactory.client.model.BuildRuns;

import java.io.IOException;

/**
 * @author yahavi
 */
public interface Builds {
    AllBuilds getAllBuilds() throws IOException;

    BuildRuns getBuildRuns(String buildName) throws IOException;

    /**
     * Upload a build to Artifactory
     *
     * @param buildInfo the build info
     * @throws IOException if the upload fails
     */
    void uploadBuild(BuildInfo buildInfo) throws IOException;

    /**
     * Upload a build to Artifactory with a project parameter
     *
     * @param buildInfo the build info
     * @param project the project name to limit the build to
     * @throws IOException if the upload fails
     */
    void uploadBuild(BuildInfo buildInfo, String project) throws IOException;

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
}
