package com.coremedia.livecontext.ecommerce.ibm.common;

import co.freeside.betamax.Recorder;
import com.coremedia.blueprint.lc.test.BetamaxTestHelper;
import com.coremedia.livecontext.ecommerce.ibm.IbmServiceTestBase;
import com.coremedia.livecontext.ecommerce.ibm.login.LoginService;
import com.coremedia.livecontext.ecommerce.ibm.storeinfo.StoreInfoService;
import com.coremedia.livecontext.ecommerce.user.UserContextProvider;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {IbmServiceTestBase.LocalConfig.class, AbstractWrapperServiceTestCase.LocalConfig.class})
@ActiveProfiles(IbmServiceTestBase.LocalConfig.PROFILE)
public abstract class AbstractWrapperServiceTestCase {
  @Configuration
  @ImportResource(
          value = {
                  "classpath:/com.coremedia.livecontext.ecommerce.ibm.service/test-commerce-services-bod-customizers.xml",
          },
          reader = ResourceAwareXmlBeanDefinitionReader.class
  )
  public static class LocalConfig {
  }

  public static final String TEST_USER = "gstevens"; // Frequent Buyer, Male Customer

  @Inject
  protected IbmTestConfig testConfig;

  @Rule
  public Recorder recorder = new Recorder(BetamaxTestHelper.updateSystemPropertiesWithBetamaxConfig());

  @Inject
  protected LoginService loginService;
  @Inject
  protected UserContextProvider userContextProvider;
  @Inject
  protected WcRestConnector wcRestConnector;
  @Inject
  protected StoreInfoService storeInfoService;

  public IbmTestConfig getTestConfig() {
    return testConfig;
  }

}
