package com.coremedia.ecommerce.studio.rest.model;

import com.coremedia.livecontext.ecommerce.common.CommerceObject;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import edu.umd.cs.findbugs.annotations.NonNull;

public abstract class StoreContextCommerceObject implements CommerceObject {
  private final StoreContext context;
  private final String idPrefix;

  protected StoreContextCommerceObject(@NonNull StoreContext context, @NonNull String idPrefix) {
    this.context = context;
    this.idPrefix = idPrefix;
  }

  @NonNull
  public String getId() {
    return idPrefix + "-" + context.getStoreName();
  }

  @NonNull
  public StoreContext getContext() {
    return context;
  }
}
