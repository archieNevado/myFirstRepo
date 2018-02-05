package com.coremedia.livecontext.ecommerce.ibm.common;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;

/**
 * An IBM specific connection class. Manages the IBM vendor specific properties.
 */
public class CommerceConnectionImpl extends BaseCommerceConnection {

  public CommerceConnectionImpl() {
    setVendor(IbmCommerceIdProvider.IBM);
  }

  @Override
  public String getVendorVersion() {
    StoreContext storeContext = getStoreContext();
    if (storeContext != null) {
      return StoreContextHelper.getWcsVersion(storeContext).toVersionString();
    }
    return super.getVendorVersion();
  }
}
