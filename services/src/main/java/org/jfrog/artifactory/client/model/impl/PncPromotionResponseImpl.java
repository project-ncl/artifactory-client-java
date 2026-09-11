package org.jfrog.artifactory.client.model.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jfrog.artifactory.client.model.PncPromotionResponse;

/**
 * Implementation of {@link PncPromotionResponse}.
 *
 * Deserialised from the JSON body returned by the {@code pncPromotion} user plugin on HTTP 200.
 */
public class PncPromotionResponseImpl implements PncPromotionResponse {
    private String message;
    @JsonProperty("promotedArts")
    private int promotedArts;
    @JsonProperty("promotedDeps")
    private int promotedDeps;

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public int getPromotedArts() {
        return promotedArts;
    }

    public void setPromotedArts(int promotedArts) {
        this.promotedArts = promotedArts;
    }

    @Override
    public int getPromotedDeps() {
        return promotedDeps;
    }

    public void setPromotedDeps(int promotedDeps) {
        this.promotedDeps = promotedDeps;
    }
}
