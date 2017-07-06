package com.coremedia.livecontext.ecommerce.ibm.catalog;

import com.coremedia.livecontext.ecommerce.ibm.common.DataMapHelper;
import com.coremedia.livecontext.ecommerce.search.SearchFacet;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SearchFacetImpl implements SearchFacet {

  public SearchFacetImpl(@Nonnull Map<String, Object> delegate) {
    this.delegate = delegate;
  }

  protected Map<String, Object> delegate;

  @Override
  public int getCount() {
    return DataMapHelper.getValueForKey(delegate, "count", 0);
  }

  @Nonnull
  @Override
  public String getLabel() {
    return DataMapHelper.getValueForKey(delegate, "label", String.class);
  }

  @Override
  public boolean isSelected() {
    return DataMapHelper.getValueForKey(delegate, "selected", Boolean.FALSE);
  }

  @Nonnull
  @Override
  public String getQuery() {
    return DataMapHelper.getValueForKey(delegate, "value", String.class);
  }

  @Override
  public String getUrl() {
    return null;
  }

  @Nonnull
  @Override
  public Map<String, Object> getExtendedData() {
    return DataMapHelper.getValueForKey(delegate, "extendedData", Map.class);
  }

  @Nonnull
  @Override
  public List<SearchFacet> getChildFacets() {
    return Collections.emptyList();
  }

  public void setDelegate(Map<String, Object> delegate) {
    this.delegate = delegate;
  }
}
