package org.jfrog.artifactory.client.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AqlItem {
    private static final String CACHE_SUFFIX = "-cache";

    private String repo;
    @JsonIgnore
    private String repoCache;
    private String path;
    private String name;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date created;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date modified;
    private AqlItemType type;
    @JsonProperty("actual_md5")
    private String actualMd5;
    @JsonProperty("original_md5")
    private String originalMd5;
    @JsonProperty("actual_sha1")
    private String actualSha1;
    @JsonProperty("sha256")
    private String sha256;
    @JsonProperty("created_by")
    private String createdBy;
    @JsonProperty("modified_by")
    private String modifiedBy;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date updated;
    private Long size;
    private List<Property> properties;

    public String getRepo() {
        return repo;
    }

    /**
     * If the original repository was a cache that is stored in here while
     * the original repository was updated to be without the cache to allow
     * calls to be made upon it.
     */
    public String getRepoCache() {
        return repoCache;
    }

    /**
     * If {@code repo} ends with {@value #CACHE_SUFFIX}, strips the suffix from {@code repo}
     * and records the original value in {@code repoCache}. No-op otherwise.
     */
    void resolveRepoCache() {
        if (repo != null && repo.endsWith(CACHE_SUFFIX)) {
            this.repoCache = this.repo;
            this.repo = this.repo.substring(0, this.repo.length() - CACHE_SUFFIX.length());
        }
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public Date getCreated() {
        return created;
    }

    public Date getModified() {
        return modified;
    }

    public AqlItemType getType() {
        return type;
    }

    public String getActualMd5() {
        return actualMd5;
    }

    public String getOriginalMd5() {
        return originalMd5;
    }

    public String getActualSha1() {
        return actualSha1;
    }

    public String getSha256() {
        return sha256;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public Date getUpdated() {
        return updated;
    }

    public Long getSize() {
        return size;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public static class Property {
        @JsonProperty("key")
        private String key;
        @JsonProperty("value")
        private String value;

        public String getkey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("Property [");
            if (key   != null) { sb.append("key=").append(key);       }
            if (value != null) {
                if (sb.length() > "Property [".length()) { sb.append(", "); }
                sb.append("value=").append(value);
            }
            return sb.append("]").toString();
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("AqlItem [");
        appendIfNotNull(sb, "repo",        repo);
        appendIfNotNull(sb, "repoCache",   repoCache);
        appendIfNotNull(sb, "path",        path);
        appendIfNotNull(sb, "name",        name);
        appendIfNotNull(sb, "created",     created);
        appendIfNotNull(sb, "modified",    modified);
        appendIfNotNull(sb, "type",        type);
        appendIfNotNull(sb, "actualMd5",   actualMd5);
        appendIfNotNull(sb, "originalMd5", originalMd5);
        appendIfNotNull(sb, "actualSha1",  actualSha1);
        appendIfNotNull(sb, "sha256",      sha256);
        appendIfNotNull(sb, "createdBy",   createdBy);
        appendIfNotNull(sb, "modifiedBy",  modifiedBy);
        appendIfNotNull(sb, "updated",     updated);
        appendIfNotNull(sb, "size",        size);
        appendIfNotNull(sb, "properties",  properties);
        return sb.append("]").toString();
    }

    private void appendIfNotNull(StringBuilder sb, String fieldName, Object value) {
        if (value == null) {
            return;
        }
        // add separator only when there is already at least one field present
        if (sb.length() > "AqlItem [".length()) {
            sb.append(", ");
        }
        sb.append(fieldName).append("=").append(value);
    }
}
