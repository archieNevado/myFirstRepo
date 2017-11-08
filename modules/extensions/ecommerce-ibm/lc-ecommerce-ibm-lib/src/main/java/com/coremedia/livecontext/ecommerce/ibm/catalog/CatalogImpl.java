package com.coremedia.livecontext.ecommerce.ibm.catalog;

import com.coremedia.livecontext.ecommerce.catalog.Catalog;
import com.coremedia.livecontext.ecommerce.catalog.CatalogName;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.ibm.common.AbstractIbmCommerceBean;
import com.coremedia.livecontext.ecommerce.ibm.common.DataMapHelper;

import javax.annotation.Nonnull;
import java.util.Map;

public class CatalogImpl extends AbstractIbmCommerceBean implements Catalog {

  private Map<String, Object> delegate;

  protected Map<String, Object> getDelegate() {
    if (delegate == null) {
      throw new IllegalStateException("Should not happen");
    }
    return delegate;
  }

  @Override
  public void setDelegate(Object delegate) {
    this.delegate = (Map<String, Object>) delegate;
  }

  @Override
  public String getExternalId() {
    return DataMapHelper.getValueForKey(getDelegate(), "catalogId", String.class);
  }

  @Override
  public String getExternalTechId() {
    return getExternalId();
  }

  @Override
  public CatalogName getName() {
    return DataMapHelper.findValueForKey(getDelegate(), "catalogIdentifier", String.class)
            .map(CatalogName::of)
            .orElse(null);
  }

  @Override
  public boolean isDefaultCatalog() {
    return DataMapHelper.findValueForKey(getDelegate(), "default", Boolean.class).orElse(false);
  }

  @Override
  public boolean isMasterCatalog() {
    return DataMapHelper.findValueForKey(getDelegate(), "primary", Boolean.class).orElse(false);
  }

  @Nonnull
  @Override
  public Category getRootCategory() {
    return getCatalogService().findRootCategory(getCatalogAlias(), getContext());
  }
}
