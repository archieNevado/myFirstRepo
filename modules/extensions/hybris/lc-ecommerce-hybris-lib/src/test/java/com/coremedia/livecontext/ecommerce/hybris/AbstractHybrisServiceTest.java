package com.coremedia.livecontext.ecommerce.hybris;

import co.freeside.betamax.Recorder;
import com.coremedia.blueprint.lc.test.AbstractServiceTest;
import com.coremedia.blueprint.lc.test.BetamaxTestHelper;
import org.junit.Before;
import org.junit.Rule;

import javax.inject.Inject;

public abstract class AbstractHybrisServiceTest extends AbstractServiceTest {

  @Inject
  HybrisTestConfig testConfig;

  @Rule
  public Recorder recorder = new Recorder(BetamaxTestHelper.updateSystemPropertiesWithBetamaxConfig());

  @Before
  @Override
  public void setup() {
    super.setup();
  }
}
