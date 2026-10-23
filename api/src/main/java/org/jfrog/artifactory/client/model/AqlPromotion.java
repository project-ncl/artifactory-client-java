package org.jfrog.artifactory.client.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AqlPromotion {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    @JsonProperty("build.promotion.created")
    @JsonAlias("created")
    private Date created;

    @JsonProperty("build.promotion.created_by")
    @JsonAlias("created_by")
    private String createdBy;

    @JsonProperty("build.promotion.status")
    @JsonAlias("status")
    private String status;

    @JsonProperty("build.promotion.repo")
    @JsonAlias("repo")
    private String repo;

    @JsonProperty("build.promotion.comment")
    @JsonAlias("comment")
    private String comment;

    @JsonProperty("build.promotion.user")
    @JsonAlias("user")
    private String user;

    public Date getCreated() {
        return created;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getStatus() {
        return status;
    }

    public String getRepo() {
        return repo;
    }

    public String getComment() {
        return comment;
    }

    public String getUser() {
        return user;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("AqlPromotion [");
        appendIfNotNull(sb, "created",   created);
        appendIfNotNull(sb, "createdBy", createdBy);
        appendIfNotNull(sb, "status",    status);
        appendIfNotNull(sb, "repo",      repo);
        appendIfNotNull(sb, "comment",   comment);
        appendIfNotNull(sb, "user",      user);
        return sb.append("]").toString();
    }

    private void appendIfNotNull(StringBuilder sb, String fieldName, Object value) {
        if (value == null) {
            return;
        }
        if (sb.length() > "AqlPromotion [".length()) {
            sb.append(", ");
        }
        sb.append(fieldName).append("=").append(value);
    }
}
