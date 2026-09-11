package org.jfrog.artifactory.client.model.impl;

import org.jfrog.artifactory.client.model.PncPromotionRequest;

/**
 * Implementation of {@link PncPromotionRequest}.
 *
 * Fields are encoded to the Artifactory plugin params wire format (query string),
 * not serialised to JSON — no {@code @JsonProperty} annotations are needed.
 */
public class PncPromotionRequestImpl implements PncPromotionRequest {
    private String targetRepository;
    private String buildInfoRepo;
    private String buildStartTime;
    private String comment;
    private String status;
    private Boolean artifacts;
    private Boolean dependencies;
    private Boolean copy;

    @Override
    public String getTargetRepository() {
        return targetRepository;
    }

    public void setTargetRepository(String targetRepository) {
        this.targetRepository = targetRepository;
    }

    @Override
    public String getBuildInfoRepo() {
        return buildInfoRepo;
    }

    public void setBuildInfoRepo(String buildInfoRepo) {
        this.buildInfoRepo = buildInfoRepo;
    }

    @Override
    public String getBuildStartTime() {
        return buildStartTime;
    }

    public void setBuildStartTime(String buildStartTime) {
        this.buildStartTime = buildStartTime;
    }

    @Override
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public Boolean getArtifacts() {
        return artifacts;
    }

    public void setArtifacts(Boolean artifacts) {
        this.artifacts = artifacts;
    }

    @Override
    public Boolean getDependencies() {
        return dependencies;
    }

    public void setDependencies(Boolean dependencies) {
        this.dependencies = dependencies;
    }

    @Override
    public Boolean getCopy() {
        return copy;
    }

    public void setCopy(Boolean copy) {
        this.copy = copy;
    }
}
