package org.jfrog.artifactory.client.model.repository.settings.impl;

import org.jfrog.artifactory.client.model.PackageType;
import org.jfrog.artifactory.client.model.impl.PackageTypeImpl;
import org.jfrog.artifactory.client.model.repository.settings.AbstractRepositorySettings;
import org.jfrog.artifactory.client.model.repository.settings.GenericRepositorySettings;

/**
 * @author Ivan Vasylivskyi (ivanvas@jfrog.com)
 */
public class GenericRepositorySettingsImpl extends AbstractRepositorySettings implements GenericRepositorySettings {
    public static String defaultLayout = "simple-default";
    private Boolean listRemoteFolderItems;
    private Boolean propagateQueryParams;

    public GenericRepositorySettingsImpl() {
        super(defaultLayout);
    }

    public PackageType getPackageType() {
        return PackageTypeImpl.generic;
    }

    public Boolean getListRemoteFolderItems() {
        return listRemoteFolderItems;
    }

    public void setListRemoteFolderItems(Boolean listRemoteFolderItems) {
        this.listRemoteFolderItems = listRemoteFolderItems;
    }

    public Boolean getPropagateQueryParams() {
        return propagateQueryParams;
    }

    public void setPropagateQueryParams(Boolean propagateQueryParams) {
        this.propagateQueryParams = propagateQueryParams;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GenericRepositorySettingsImpl)) return false;

        GenericRepositorySettingsImpl that = (GenericRepositorySettingsImpl) o;

        if (listRemoteFolderItems != null ? !listRemoteFolderItems.equals(that.listRemoteFolderItems) : that.listRemoteFolderItems != null)
            return false;
        return propagateQueryParams != null ? propagateQueryParams.equals(that.propagateQueryParams) : that.propagateQueryParams == null;
    }

    @Override
    public int hashCode() {
        int result = listRemoteFolderItems != null ? listRemoteFolderItems.hashCode() : 0;
        result = 31 * result + (propagateQueryParams != null ? propagateQueryParams.hashCode() : 0);
        return result;
    }
}
