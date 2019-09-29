package com.coremedia.blueprint.lc.test;

import co.freeside.betamax.Recorder;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.user.UserContextProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(properties = "livecontext.cache.invalidation.enabled:false")
public abstract class AbstractServiceTest {

  @Inject
  protected Commerce commerce;

  @Inject
  protected UserContextProvider userContextProvider;

  @Inject
  protected TestConfig testConfig;

  @Inject
  protected CommerceCache commerceCache;

  @Rule
  public Recorder recorder = new Recorder(BetamaxTestHelper.updateSystemPropertiesWithBetamaxConfig());

  protected CommerceConnection connection;

  protected StoreContext storeContext;

  @Before
  public void setup() {
    connection = commerce.findConnection(testConfig.getConnectionId())
            .orElseThrow(() -> new IllegalStateException("Could not obtain commerce connection."));

    storeContext = testConfig.getStoreContext(connection);
    connection.setStoreContext(storeContext);
    CurrentCommerceConnection.set(connection);

    userContextProvider.clearCurrentContext();

    commerceCache.setEnabled(false);
    commerceCache.getCache().clear();
  }

  @After
  public void teardown() {
    CurrentCommerceConnection.remove();
  }

  public TestConfig getTestConfig() {
    return testConfig;
  }
}
