package com.coremedia.livecontext.ecommerce.ibm.common;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractStoreContextProvider;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;

/**
 * An IBM specific connection class. Manages the IBM vendor specific properties.
 */
public class CommerceConnectionImpl extends BaseCommerceConnection {
  @Override
  public String getVendorVersion() {
    StoreContext storeContext = getStoreContext();
    if (storeContext != null) {
      Object wcsVersion = storeContext.get(AbstractStoreContextProvider.CONFIG_KEY_WCS_VERSION);
      if (wcsVersion != null)
        return Float.toString((Float) wcsVersion);
    }
    return super.getVendorVersion();
  }
}
