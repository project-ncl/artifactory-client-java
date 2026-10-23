package org.jfrog.artifactory.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AqlBuildSearchResults {
    private List<AqlBuild> results;

    public List<AqlBuild> getResults() {
        return this.results;
    }
}
