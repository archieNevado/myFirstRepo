package com.coremedia.ecommerce.studio.rest.model;

import com.coremedia.livecontext.ecommerce.common.CommerceObject;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * We are using a faked commerce bean here to support the invalidation of the list of available search facets.
 * Therefore we implement the the interface "CommerceObject" here and use the Store itself
 * as a delegate since the "Facets" only provides methods that are available on the store.
 */
public class SearchFacets implements CommerceObject {

  private final StoreContext context;
  private final String categoryId;

  public SearchFacets(@NonNull StoreContext context, @NonNull String categoryId) {
    this.context = context;
    this.categoryId = categoryId;
  }

  @NonNull
  public StoreContext getContext() {
    return context;
  }

  @NonNull
  public String getCategoryId() {
    return categoryId;
  }

}
