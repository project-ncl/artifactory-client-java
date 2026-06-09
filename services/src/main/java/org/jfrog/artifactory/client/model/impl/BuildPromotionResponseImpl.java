package org.jfrog.artifactory.client.model.impl;

import org.jfrog.artifactory.client.model.BuildPromotionResponse;
import org.jfrog.artifactory.client.model.PromotionMessage;

import java.util.List;

/**
 * Implementation of BuildPromotionResponse
 * 
 * @author rnc
 */
public class BuildPromotionResponseImpl implements BuildPromotionResponse {
    private List<PromotionMessage> messages;

    @Override
    public List<PromotionMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<PromotionMessage> messages) {
        this.messages = messages;
    }
}

// Made with Bob
