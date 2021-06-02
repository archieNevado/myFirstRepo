package com.coremedia.blueprint.lc.test;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentStoreContext;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentUserContext;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.user.UserContextProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

/**
 * Abstract base class for LiveContext tests.
 *
 * @deprecated This class is part of the legacy Blueprint commerce integration and has been deprecated
 * in favour of the Commerce Hub integration.
 */
@Deprecated(since = "2104.2", forRemoval = true)
@SuppressWarnings("removal")
@TestPropertySource(properties = "livecontext.cache.invalidation.enabled:false")
public abstract class AbstractServiceTest {

  @Autowired
  protected UserContextProvider userContextProvider;

  @Autowired
  protected TestConfig testConfig;

  @Autowired
  protected CommerceCache commerceCache;

  protected CommerceConnection connection;

  protected StoreContext storeContext;

  @BeforeEach
  public void setup() {
    connection = testConfig.getCommerceConnection();

    storeContext = testConfig.getStoreContext(connection);
    connection.setInitialStoreContext(storeContext);
    CurrentStoreContext.set(connection.getStoreContext());

    commerceCache.getCache().clear();
  }

  @AfterEach
  public void teardown() {
    CurrentStoreContext.remove();
    CurrentUserContext.remove();
  }

  public TestConfig getTestConfig() {
    return testConfig;
  }
}
