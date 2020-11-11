package com.coremedia.livecontext.ecommerce.ibm.common;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceConnection;

import java.util.Optional;

/**
 * An IBM specific connection class. Manages the IBM vendor specific properties.
 *
 * @deprecated This class is part of the legacy Blueprint commerce integration and has been deprecated
 * in favour of the Commerce Hub integration.
 */
@Deprecated
public class CommerceConnectionImpl extends BaseCommerceConnection {

  public CommerceConnectionImpl() {
    setVendor(IbmCommerceIdProvider.IBM);
  }

  @Override
  public String getVendorVersion() {
    return Optional.ofNullable(getStoreContext())
            .map(StoreContextHelper::getWcsVersion)
            .map(WcsVersion::toVersionString)
            .orElseGet(super::getVendorVersion);
  }
}
