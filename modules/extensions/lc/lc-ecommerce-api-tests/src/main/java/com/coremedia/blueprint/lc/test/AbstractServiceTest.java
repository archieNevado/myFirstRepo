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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkState;

@RunWith(SpringJUnit4ClassRunner.class)
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

  @Before
  public void setup() {
    CommerceConnection connection = commerce.getConnection(testConfig.getConnectionId());
    checkState(connection != null, "Commerce connection could not be obtained.");

    connection.setStoreContext(testConfig.getStoreContext());
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

  protected StoreContext getStoreContext() {
    return testConfig.getStoreContext();
  }


}
