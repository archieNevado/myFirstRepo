package com.coremedia.ecommerce.studio.rest;

import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.List;
import java.util.Map;

/**
 * Facets representation for JSON.
 * @deprecated use {@link SearchFacetsRepresentation} instead
 */
@Deprecated(since = "2104.1", forRemoval = true)
public class FacetsRepresentation extends CommerceBeanRepresentation {

  private Map<String, List<Facet>> facets = Map.of();

  @NonNull
  public Map<String, List<Facet>> getFacets() {
    return facets;
  }

  public void setFacets(@NonNull Map<String, List<Facet>> facets) {
    this.facets = facets;
  }

}
