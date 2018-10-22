package com.coremedia.livecontext.ecommerce.ibm.common;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl;
import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.workspace.WorkspaceId;

import java.util.Currency;
import java.util.Locale;

abstract class StoreContextHelperTestBase {

  protected static final String SITE_ID = "awesome-site";
  protected static final String STORE_ID = "4711";
  protected static final String STORE_NAME = "toko";
  protected static final CatalogId CATALOG_ID = CatalogId.of("0815");
  protected static final Currency CURRENCY = Currency.getInstance("USD");
  protected static final Locale LOCALE = Locale.US;
  protected static final WorkspaceId WORKSPACE_ID = WorkspaceId.of("ws42");

  protected IbmStoreContextBuilder buildContext() {
    return IbmStoreContextBuilder
            .from(StoreContextImpl.builder(SITE_ID))
            .withStoreId(STORE_ID)
            .withStoreName(STORE_NAME)
            .withCatalogId(CATALOG_ID)
            .withCurrency(CURRENCY)
            .withLocale(LOCALE);
  }
}
