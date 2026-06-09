package org.jfrog.artifactory.client.model.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.jfrog.artifactory.client.model.BuildInfo;

import java.util.List;
import java.util.Map;

/**
 * Implementation of BuildInfo
 * 
 * @author rnc
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BuildInfoImpl implements BuildInfo {
    private String version;
    private String name;
    private String number;
    private String type;
    private BuildAgent buildAgent;
    private Agent agent;
    private String started;
    private String artifactoryPluginVersion;
    private Long durationMillis;
    private String artifactoryPrincipal;
    private String url;
    private String vcsRevision;
    private String vcsUrl;
    private List<VcsInfo> vcs;
    private LicenseControl licenseControl;
    private BuildRetention buildRetention;
    private List<BuildModule> modules;
    private Issues issues;
    private Map<String, Object> governance;
    private Map<String, Object> properties;

    @Override
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public BuildAgent getBuildAgent() {
        return buildAgent;
    }

    public void setBuildAgent(BuildAgent buildAgent) {
        this.buildAgent = buildAgent;
    }

    @Override
    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    @Override
    public String getStarted() {
        return started;
    }

    public void setStarted(String started) {
        this.started = started;
    }

    @Override
    public String getArtifactoryPluginVersion() {
        return artifactoryPluginVersion;
    }

    public void setArtifactoryPluginVersion(String artifactoryPluginVersion) {
        this.artifactoryPluginVersion = artifactoryPluginVersion;
    }

    @Override
    public Long getDurationMillis() {
        return durationMillis;
    }

    public void setDurationMillis(Long durationMillis) {
        this.durationMillis = durationMillis;
    }

    @Override
    public String getArtifactoryPrincipal() {
        return artifactoryPrincipal;
    }

    public void setArtifactoryPrincipal(String artifactoryPrincipal) {
        this.artifactoryPrincipal = artifactoryPrincipal;
    }

    @Override
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String getVcsRevision() {
        return vcsRevision;
    }

    public void setVcsRevision(String vcsRevision) {
        this.vcsRevision = vcsRevision;
    }

    @Override
    public String getVcsUrl() {
        return vcsUrl;
    }

    public void setVcsUrl(String vcsUrl) {
        this.vcsUrl = vcsUrl;
    }

    @Override
    public List<VcsInfo> getVcs() {
        return vcs;
    }

    public void setVcs(List<VcsInfo> vcs) {
        this.vcs = vcs;
    }

    @Override
    public LicenseControl getLicenseControl() {
        return licenseControl;
    }

    public void setLicenseControl(LicenseControl licenseControl) {
        this.licenseControl = licenseControl;
    }

    @Override
    public BuildRetention getBuildRetention() {
        return buildRetention;
    }

    public void setBuildRetention(BuildRetention buildRetention) {
        this.buildRetention = buildRetention;
    }

    @Override
    public List<BuildModule> getModules() {
        return modules;
    }

    public void setModules(List<BuildModule> modules) {
        this.modules = modules;
    }

    @Override
    public Issues getIssues() {
        return issues;
    }

    public void setIssues(Issues issues) {
        this.issues = issues;
    }

    @Override
    public Map<String, Object> getGovernance() {
        return governance;
    }

    public void setGovernance(Map<String, Object> governance) {
        this.governance = governance;
    }

    @Override
    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
}

// Made with Bob
