package com.coremedia.blueprint.lc.test.services;

import com.coremedia.blueprint.base.livecontext.client.common.GenericCommerceConnection;
import com.coremedia.blueprint.base.livecontext.client.common.RequiresGenericCommerceConnection;
import com.coremedia.blueprint.base.livecontext.client.data.DataClient;
import com.coremedia.livecontext.ecommerce.common.ForVendor;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Example implementation of a custom commerce connection for the generic client.
 */
@ForVendor("test")
abstract class TestOverrideGenericCommerceConnection implements GenericCommerceConnection, RequiresGenericCommerceConnection {

  private GenericCommerceConnection delegate;

  @Override
  public void setGenericCommerceConnection(GenericCommerceConnection genericCommerceConnection) {
    delegate = genericCommerceConnection;
  }

  @Override
  public DataClient getDataClient() {
    return delegate.getDataClient();
  }

  @Override
  @NonNull
  public StoreContextProvider getStoreContextProvider() {
    return delegate.getStoreContextProvider();
  }

  @Override
  public StoreContext getInitialStoreContext() {
    return delegate.getInitialStoreContext();
  }

  @Override
  public void setInitialStoreContext(StoreContext storeContext) {
    delegate.setInitialStoreContext(storeContext);
  }

}
