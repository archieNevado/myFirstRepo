package com.coremedia.livecontext.ecommerce.ibm.common;

import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.workspace.WorkspaceId;

import java.util.Currency;

abstract class StoreContextHelperTestBase {

  protected static final String SITE_ID = "awesome-site";
  protected static final String STORE_NAME = "toko";
  protected static final String STORE_ID = "4711";
  protected static final CatalogId CATALOG_ID = CatalogId.of("0815");
  protected static final String LOCALE = "en_US";
  protected static final Currency CURRENCY = Currency.getInstance("USD");
  protected static final WorkspaceId WORKSPACE_ID = WorkspaceId.of("ws42");

  protected StoreContext createContext() {
    return StoreContextHelper.createContext(SITE_ID, STORE_ID, STORE_NAME, CATALOG_ID, LOCALE, CURRENCY);
  }
}
