package org.jfrog.artifactory.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

/**
 * Build Info structure for uploading to Artifactory
 * 
 * @author rnc
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public interface BuildInfo {
    /**
     * Build Info schema version
     * @return the version
     */
    String getVersion();

    /**
     * Build name
     * @return the build name
     */
    String getName();

    /**
     * Build number
     * @return the build number
     */
    String getNumber();

    /**
     * Build type (MAVEN, GRADLE, ANT, IVY, GENERIC)
     * @return the build type
     */
    String getType();

    /**
     * Build agent information (build tool)
     * @return the build agent
     */
    BuildAgent getBuildAgent();

    /**
     * CI agent information (CI server)
     * @return the agent
     */
    Agent getAgent();

    /**
     * Build start time in ISO 8601 format (yyyy-MM-dd'T'HH:mm:ss.SSSZ)
     * @return the start time
     */
    String getStarted();

    /**
     * Artifactory plugin version
     * @return the plugin version
     */
    String getArtifactoryPluginVersion();

    /**
     * Build duration in milliseconds
     * @return the duration
     */
    Long getDurationMillis();

    /**
     * Artifactory principal (the Artifactory user used for deployment)
     * @return the principal
     */
    String getArtifactoryPrincipal();

    /**
     * CI server URL
     * @return the URL
     */
    String getUrl();

    /**
     * VCS revision
     * @return the VCS revision
     */
    String getVcsRevision();

    /**
     * VCS URL
     * @return the VCS URL
     */
    String getVcsUrl();

    /**
     * VCS information list
     * @return the VCS list
     */
    List<VcsInfo> getVcs();

    /**
     * License control settings
     * @return the license control
     */
    LicenseControl getLicenseControl();

    /**
     * Build retention settings
     * @return the build retention
     */
    BuildRetention getBuildRetention();

    /**
     * Build modules
     * @return the modules
     */
    List<BuildModule> getModules();

    /**
     * Issues information
     * @return the issues
     */
    Issues getIssues();

    /**
     * Governance information
     * @return the governance
     */
    Map<String, Object> getGovernance();

    /**
     * Environment variables and properties
     * @return the properties
     */
    Map<String, Object> getProperties();

    /**
     * Build agent (build tool) information
     */
    interface BuildAgent {
        String getName();
        String getVersion();
    }

    /**
     * CI agent (CI server) information
     */
    interface Agent {
        String getName();
        String getVersion();
    }

    /**
     * VCS information
     */
    interface VcsInfo {
        String getRevision();
        String getMessage();
        String getBranch();
        String getUrl();
    }

    /**
     * License control settings
     */
    interface LicenseControl {
        Boolean getRunChecks();
        Boolean getIncludePublishedArtifacts();
        Boolean getAutoDiscover();
        String getScopesList();
        String getLicenseViolationsRecipientsList();
    }

    /**
     * Build retention settings
     */
    interface BuildRetention {
        Boolean getDeleteBuildArtifacts();
        Integer getCount();
        Long getMinimumBuildDate();
        List<String> getBuildNumbersNotToBeDiscarded();
    }

    /**
     * Build module
     */
    interface BuildModule {
        Map<String, Object> getProperties();
        String getId();
        String getType();
        List<Artifact> getArtifacts();
        List<Dependency> getDependencies();
    }

    /**
     * Build artifact
     */
    interface Artifact {
        String getType();
        String getSha1();
        String getSha256();
        String getMd5();
        String getName();
        String getPath();
        String getOriginalDeploymentRepo();
    }

    /**
     * Build dependency
     */
    interface Dependency {
        String getType();
        String getSha1();
        String getSha256();
        String getMd5();
        String getId();
        List<String> getScopes();
        List<List<String>> getRequestedBy();
    }

    /**
     * Issues information
     */
    interface Issues {
        Tracker getTracker();
        Boolean getAggregateBuildIssues();
        String getAggregationBuildStatus();
        List<AffectedIssue> getAffectedIssues();
    }

    /**
     * Issue tracker information
     */
    interface Tracker {
        String getName();
        String getVersion();
    }

    /**
     * Affected issue
     */
    interface AffectedIssue {
        String getKey();
        String getUrl();
        String getSummary();
        Boolean getAggregated();
    }
}

// Made with Bob
