package com.coremedia.livecontext.ecommerce.ibm.event;

import co.freeside.betamax.Betamax;
import co.freeside.betamax.MatchRule;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.ibm.IbmServiceTestBase;
import com.coremedia.livecontext.ecommerce.ibm.common.WcRestConnector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import javax.inject.Inject;
import java.util.Map;

import static com.coremedia.blueprint.lc.test.BetamaxTestHelper.useBetamaxTapes;
import static org.junit.Assert.assertNotNull;

@ContextConfiguration(classes = {
        WcInvalidationWrapperServiceIT.LocalConfig.class,
        IbmServiceTestBase.LocalConfig.class
})
@ActiveProfiles({
        WcInvalidationWrapperServiceIT.PROFILE,
        IbmServiceTestBase.LocalConfig.PROFILE
})
public class WcInvalidationWrapperServiceIT extends IbmServiceTestBase {
  private static long TIME_STAMP = 1399641181799L;
  static final String PROFILE = "WcCacheWrapperServiceIT";

  @Configuration
  static class LocalConfig {

    @Bean
    @Autowired
    WcInvalidationWrapperService cacheWrapperService(WcRestConnector wcRestConnector) {
      WcInvalidationWrapperService wcCacheWrapperService = new WcInvalidationWrapperService();
      wcCacheWrapperService.setRestConnector(wcRestConnector);
      return wcCacheWrapperService;
    }
  }

  @Inject
  WcInvalidationWrapperService testling;

  String origServiceEndpoint;

  @Before
  @Override
  public void setup() {
    super.setup();
    origServiceEndpoint = testling.getRestConnector().getServiceEndpoint(storeContext);
  }

  @After
  public void reset() {
    testling.getRestConnector().setServiceEndpoint(origServiceEndpoint);
  }

  @Test
  public void testGetCacheInvalidations() {
    if (useBetamaxTapes()) {
      return;
    }

    Map<String, Object> cacheInvalidations = testling.getCacheInvalidations(TIME_STAMP, 20000, 500, storeContext);
    assertNotNull(cacheInvalidations);
  }

  @Betamax(tape = "cache_testGetCacheInvalidationsUnknownHost", match = {MatchRule.path, MatchRule.query})
  @Test(expected = CommerceException.class)
  public void testGetCacheInvalidationsUnknownHost() {
    testling.getRestConnector().setServiceEndpoint("http://does.not.exists/blub");
    testling.getCacheInvalidations(TIME_STAMP, 20000, 500, storeContext);
  }

  @Betamax(tape = "cache_testGetCacheInvalidationsError404", match = {MatchRule.path, MatchRule.query})
  @Test(expected = CommerceException.class)
  public void testGetCacheInvalidationsError404() {
    testling.getRestConnector().setServiceEndpoint(testling.getRestConnector().getServiceEndpoint(storeContext) + "/blub");
    testling.getCacheInvalidations(TIME_STAMP, 20000, 500, this.storeContext);
  }
}
