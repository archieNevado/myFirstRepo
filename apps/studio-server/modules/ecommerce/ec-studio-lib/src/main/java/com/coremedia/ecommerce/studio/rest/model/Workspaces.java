package com.coremedia.ecommerce.studio.rest.model;

import com.coremedia.livecontext.ecommerce.common.StoreContext;

/**
 * We are using a faked commerce bean here to support the invalidation of the list of available segments
 * Therefore we implement the the interface "CommerceObject" here and use the Store itself
 * as a delegate since the "Workspaces" only provides methods that are available on the store.
 *
 * @deprecated This class is part of the commerce integration "workspaces support" that is not
 * supported by the Commerce Hub architecture. It will be removed or changed in the future.
 */
@Deprecated
public class Workspaces extends StoreContextCommerceObject {
  public Workspaces(StoreContext context) {
    super(context, "workspaces");
  }
}
