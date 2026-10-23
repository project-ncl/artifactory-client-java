package org.jfrog.artifactory.client.model.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jfrog.artifactory.client.model.PncRollbackResponse;

/**
 * Implementation of {@link PncRollbackResponse}.
 *
 * Deserialised from the JSON body returned by the {@code pncRollback} user plugin on HTTP 200.
 */
public class PncRollbackResponseImpl implements PncRollbackResponse {
    private String message;
    @JsonProperty("rolledBackArts")
    private int rolledBackArts;
    @JsonProperty("rolledBackDeps")
    private int rolledBackDeps;
    @JsonProperty("skippedArts")
    private int skippedArts;
    @JsonProperty("skippedDeps")
    private int skippedDeps;

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public int getRolledBackArts() {
        return rolledBackArts;
    }

    public void setRolledBackArts(int rolledBackArts) {
        this.rolledBackArts = rolledBackArts;
    }

    @Override
    public int getRolledBackDeps() {
        return rolledBackDeps;
    }

    public void setRolledBackDeps(int rolledBackDeps) {
        this.rolledBackDeps = rolledBackDeps;
    }

    @Override
    public int getSkippedArts() {
        return skippedArts;
    }

    public void setSkippedArts(int skippedArts) {
        this.skippedArts = skippedArts;
    }

    @Override
    public int getSkippedDeps() {
        return skippedDeps;
    }

    public void setSkippedDeps(int skippedDeps) {
        this.skippedDeps = skippedDeps;
    }
}
