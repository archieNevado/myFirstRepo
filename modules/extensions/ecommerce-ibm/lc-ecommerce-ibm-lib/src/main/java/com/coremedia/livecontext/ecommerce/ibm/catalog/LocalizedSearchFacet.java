package com.coremedia.livecontext.ecommerce.ibm.catalog;

import com.coremedia.livecontext.ecommerce.search.SearchFacet;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

/**
 * Helper class for localized search facets.
 */
class LocalizedSearchFacet implements SearchFacet {

  private final SearchFacet delegate;
  private final List<SearchFacet> childFacets;
  private String label;

  LocalizedSearchFacet(@Nonnull SearchFacet searchFacet, @Nonnull String label, @Nonnull List<SearchFacet> childFacets) {
    this.delegate = searchFacet;
    this.label = label;
    this.childFacets = childFacets;
  }

  @Nonnull
  @Override
  public String getLabel() {
    return label;
  }

  @Override
  public int getCount() {
    return delegate.getCount();
  }

  @Override
  public boolean isSelected() {
    return delegate.isSelected();
  }

  @Override
  @Nonnull
  public String getQuery() {
    return delegate.getQuery();
  }

  @Override
  public String getUrl() {
    return delegate.getUrl();
  }

  @Override
  @Nonnull
  public Map<String, Object> getExtendedData() {
    return delegate.getExtendedData();
  }

  @Override
  @Nonnull
  public List<SearchFacet> getChildFacets() {
    return childFacets;
  }
}
