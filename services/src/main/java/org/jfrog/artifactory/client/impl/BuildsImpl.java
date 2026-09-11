package org.jfrog.artifactory.client.impl;

import org.apache.http.entity.ContentType;
import org.jfrog.artifactory.client.Artifactory;
import org.jfrog.artifactory.client.Builds;
import org.jfrog.artifactory.client.impl.util.Util;
import org.jfrog.artifactory.client.model.AllBuilds;
import org.jfrog.artifactory.client.model.BuildPromotionRequest;
import org.jfrog.artifactory.client.model.BuildPromotionResponse;
import org.jfrog.artifactory.client.model.BuildRuns;
import org.jfrog.artifactory.client.model.PncPromotionRequest;
import org.jfrog.artifactory.client.model.PncPromotionResponse;
import org.jfrog.artifactory.client.model.impl.AllBuildsImpl;
import org.jfrog.artifactory.client.model.impl.BuildPromotionResponseImpl;
import org.jfrog.artifactory.client.model.impl.BuildRunsImpl;
import org.jfrog.artifactory.client.model.impl.PncPromotionResponseImpl;
import org.jfrog.build.api.Build;

import java.io.IOException;

/**
 * @author yahavi
 **/
public class BuildsImpl implements Builds {
    private final Artifactory artifactory;
    private final String baseApiPath;

    public BuildsImpl(Artifactory artifactory, String baseApiPath) {
        this.artifactory = artifactory;
        this.baseApiPath = baseApiPath;
    }

    @Override
    public AllBuilds getAllBuilds() throws IOException {
        return artifactory.get(getBuilderApi(), AllBuildsImpl.class, AllBuilds.class);
    }

    @Override
    public BuildRuns getBuildRuns(String buildName) throws IOException {
        return artifactory.get(getBuilderApi() + buildName, BuildRunsImpl.class, BuildRuns.class);
    }

    @Override
    public void uploadBuild(Build build) throws IOException {
        uploadBuild(build, null);
    }

    @Override
    public void uploadBuild(Build build, String project) throws IOException {
        String apiPath = getBuilderApi();
        if (project != null && !project.isEmpty()) {
            apiPath += "?project=" + Util.encodeParams(project);
        }
        artifactory.put(apiPath, ContentType.APPLICATION_JSON,
                Util.getStringFromObject(build), null, null, -1,
                String.class, null);
    }

    @Override
    public BuildPromotionResponse promoteBuild(String buildName, String buildNumber,
            BuildPromotionRequest promotionRequest) throws IOException {
        return promoteBuild(buildName, buildNumber, promotionRequest, null);
    }

    @Override
    public BuildPromotionResponse promoteBuild(String buildName, String buildNumber,
            BuildPromotionRequest promotionRequest, String project) throws IOException {
        String apiPath = getBuilderApi() + "promote/" + buildName + "/" + buildNumber;
        if (project != null && !project.isEmpty()) {
            apiPath += "?project=" + Util.encodeParams(project);
        }
        return artifactory.post(apiPath, ContentType.APPLICATION_JSON,
                Util.getStringFromObject(promotionRequest), null,
                BuildPromotionResponseImpl.class, BuildPromotionResponse.class);
    }

    @Override
    public PncPromotionResponse promotePNCBuild(String buildName, String buildNumber,
            PncPromotionRequest request) throws IOException {
        return promotePNCBuild(buildName, buildNumber, request, null);
    }

    @Override
    public PncPromotionResponse promotePNCBuild(String buildName, String buildNumber,
            PncPromotionRequest request, String project) throws IOException {
        if (request.getTargetRepository() == null || request.getTargetRepository().isEmpty()) {
            throw new IllegalArgumentException("targetRepository is mandatory");
        }
        if (request.getBuildInfoRepo() == null || request.getBuildInfoRepo().isEmpty()) {
            throw new IllegalArgumentException("buildInfoRepo is mandatory");
        }

        String apiPath = baseApiPath + "/plugins/build/promote/pncPromotion/"
                + Util.encodeParams(buildName) + "/"
                + Util.encodeParams(buildNumber);

        String separator = "?";
        if (project != null && !project.isEmpty()) {
            apiPath += separator + "project=" + Util.encodeParams(project);
            separator = "&";
        }
        String encodedParams = encodePncParams(request);
        if (!encodedParams.isEmpty()) {
            apiPath += separator + "params=" + encodedParams;
        }

        return artifactory.post(apiPath, ContentType.APPLICATION_JSON,
                null, null, PncPromotionResponseImpl.class, PncPromotionResponse.class);
    }

    /**
     * Encodes {@link PncPromotionRequest} values into the Artifactory plugin params wire format.
     * Uses semicolon (;) as the key=value pair separator, as recommended by JFrog
     * for the /api/plugins/ API family. Format: {@code p1=v1;p2=v2}
     */
    private static String encodePncParams(PncPromotionRequest r) throws IOException {
        StringBuilder sb = new StringBuilder();
        appendParam(sb, "targetRepository", r.getTargetRepository());
        appendParam(sb, "buildInfoRepo",    r.getBuildInfoRepo());
        appendParam(sb, "buildStartTime",   r.getBuildStartTime());
        appendParam(sb, "comment",          r.getComment());
        appendParam(sb, "status",           r.getStatus());
        if (r.getArtifacts() != null)    appendParam(sb, "artifacts",    r.getArtifacts().toString());
        if (r.getDependencies() != null) appendParam(sb, "dependencies", r.getDependencies().toString());
        if (r.getCopy() != null)         appendParam(sb, "copy",         r.getCopy().toString());
        return sb.toString();
    }

    private static void appendParam(StringBuilder sb, String key, String value) throws IOException {
        if (value != null) {
            if (sb.length() > 0) sb.append(";");
            sb.append(key).append("=").append(Util.encodeParams(value));
        }
    }

    public String getBuilderApi() {
        return baseApiPath + "/build/";
    }
}
