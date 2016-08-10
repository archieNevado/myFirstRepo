package com.coremedia.livecontext.ecommerce.ibm.common;

import com.coremedia.livecontext.ecommerce.common.StoreContext;

abstract class StoreContextHelperTestBase {

  protected static final String STORE_CONFIG_ID = "configId";
  protected static final String STORE_NAME = "toko";
  protected static final String STORE_ID = "4711";
  protected static final String CATALOG_ID = "0815";
  protected static final String LOCALE = "en_US";
  protected static final String CURRENCY = "USD";
  protected static final String WORKSPACE = "ws42";

  protected StoreContext createContext() {
    return StoreContextHelper.createContext(STORE_CONFIG_ID, STORE_ID, STORE_NAME, CATALOG_ID, LOCALE, CURRENCY);
  }
}
