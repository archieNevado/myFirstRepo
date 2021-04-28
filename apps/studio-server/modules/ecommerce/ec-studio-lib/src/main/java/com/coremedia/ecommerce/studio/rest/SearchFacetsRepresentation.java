package com.coremedia.ecommerce.studio.rest;

import com.coremedia.livecontext.ecommerce.search.SearchResult;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.List;

/**
 * Search facets representation for JSON.
 */
public class SearchFacetsRepresentation extends CommerceBeanRepresentation {

  private List<SearchResult.Facet> facets = List.of();

  /**
   * Set the search result facets
   *
   * @param facets the search result facets
   */
  public void setFacets(@NonNull List<SearchResult.Facet> facets) {
    this.facets = facets;
  }

  /**
   * Get the search result facets
   *
   * @return the search result facets
   */
  @NonNull
  public List<SearchResult.Facet> getFacets() {
    return facets;
  }
}
