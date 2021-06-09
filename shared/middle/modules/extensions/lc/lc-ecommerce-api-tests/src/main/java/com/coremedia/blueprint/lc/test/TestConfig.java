package com.coremedia.blueprint.lc.test;

import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Configuration class for LiveContext tests.
 *
 * @deprecated This class is part of the legacy Blueprint commerce integration and has been deprecated
 * in favour of the Commerce Hub integration.
 */
@Deprecated(since = "2101.4", forRemoval = true)
public interface TestConfig {

  CommerceConnection getCommerceConnection();

  StoreContext getStoreContext(@NonNull CommerceConnection connection);

  StoreContext getGermanStoreContext(@NonNull CommerceConnection connection);

  String getCatalogName();
}
