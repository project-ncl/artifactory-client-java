package org.jfrog.artifactory.client.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AqlBuild {

    @JsonProperty("build.name")
    @JsonAlias("name")
    private String name;

    @JsonProperty("build.number")
    @JsonAlias("number")
    private String number;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    @JsonProperty("build.created")
    @JsonAlias("created")
    private Date created;

    @JsonProperty("build.created_by")
    @JsonAlias("created_by")
    private String createdBy;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    @JsonProperty("build.modified")
    @JsonAlias("modified")
    private Date modified;

    @JsonProperty("build.modified_by")
    @JsonAlias("modified_by")
    private String modifiedBy;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    @JsonProperty("build.started")
    @JsonAlias("started")
    private Date started;

    @JsonProperty("build.repo")
    @JsonAlias("repo")
    private String repo;

    @JsonProperty("build.promotions")
    @JsonAlias({"promotions", "build.promotion"})
    private List<AqlPromotion> promotions;

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public Date getCreated() {
        return created;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Date getModified() {
        return modified;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public Date getStarted() {
        return started;
    }

    public String getRepo() {
        return repo;
    }

    public List<AqlPromotion> getPromotions() {
        return promotions;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("AqlBuild [");
        appendIfNotNull(sb, "name",       name);
        appendIfNotNull(sb, "number",     number);
        appendIfNotNull(sb, "created",    created);
        appendIfNotNull(sb, "createdBy",  createdBy);
        appendIfNotNull(sb, "modified",   modified);
        appendIfNotNull(sb, "modifiedBy", modifiedBy);
        appendIfNotNull(sb, "started",    started);
        appendIfNotNull(sb, "repo",       repo);
        appendIfNotNull(sb, "promotions", promotions);
        return sb.append("]").toString();
    }

    private void appendIfNotNull(StringBuilder sb, String fieldName, Object value) {
        if (value == null) {
            return;
        }
        if (sb.length() > "AqlBuild [".length()) {
            sb.append(", ");
        }
        sb.append(fieldName).append("=").append(value);
    }
}
