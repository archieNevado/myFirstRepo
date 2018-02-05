package com.coremedia.livecontext.ecommerce.ibm.catalog;

import com.coremedia.livecontext.ecommerce.ibm.common.DataMapHelper;
import com.coremedia.livecontext.ecommerce.search.SearchFacet;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public class SearchFacetImpl implements SearchFacet {

  protected Map<String, Object> delegate;

  public SearchFacetImpl(@Nonnull Map<String, Object> delegate) {
    this.delegate = delegate;
  }

  @Override
  public int getCount() {
    return DataMapHelper.findValue(delegate, "count", Integer.class).orElse(0);
  }

  @Nonnull
  @Override
  public String getLabel() {
    return DataMapHelper.findStringValue(delegate, "label")
            .orElseGet(() -> DataMapHelper.findStringValue(delegate, "name").orElse(null));
  }

  @Override
  public boolean isSelected() {
    return DataMapHelper.findValue(delegate, "selected", Boolean.class).orElse(false);
  }

  @Nonnull
  @Override
  public String getQuery() {
    return DataMapHelper.findStringValue(delegate, "value").orElse(null);
  }

  @Override
  public String getUrl() {
    return null;
  }

  @Nonnull
  @Override
  public Map<String, Object> getExtendedData() {
    return DataMapHelper.findValue(delegate, "extendedData", Map.class)
            .orElseGet(Collections::emptyMap);
  }

  @Nonnull
  @Override
  public List<SearchFacet> getChildFacets() {
    List<Map<String, Object>> entry = DataMapHelper.getListValue(delegate, "entry");

    return entry.stream().map(SearchFacetImpl::new).collect(toList());
  }

  public void setDelegate(Map<String, Object> delegate) {
    this.delegate = delegate;
  }
}
