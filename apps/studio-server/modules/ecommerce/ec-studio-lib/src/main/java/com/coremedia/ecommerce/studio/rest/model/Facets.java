package com.coremedia.ecommerce.studio.rest.model;

import com.coremedia.livecontext.ecommerce.common.StoreContext;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * We are using a faked commerce bean here to support the invalidation of the list of available facets.
 * Therefore we implement the the interface "CommerceObject" here and use the Store itself
 * as a delegate since the "Facets" only provides methods that are available on the store.
 * @deprecated use {@link SearchFacets} instead
 */
@Deprecated(since = "2104.1", forRemoval = true)
public class Facets extends StoreContextCommerceObject {

  private String id = "unknown";

  public Facets(StoreContext context) {
    super(context, "facets");
  }

  @NonNull
  @Override
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

}
