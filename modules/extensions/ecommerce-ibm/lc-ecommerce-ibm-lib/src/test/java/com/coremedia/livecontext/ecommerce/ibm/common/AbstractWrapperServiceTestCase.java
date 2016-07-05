package com.coremedia.livecontext.ecommerce.ibm.common;

import co.freeside.betamax.Recorder;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.cap.test.xmlrepo.XmlRepoConfiguration;
import com.coremedia.livecontext.ecommerce.ibm.login.LoginService;
import com.coremedia.livecontext.ecommerce.ibm.storeinfo.StoreInfoService;
import com.coremedia.livecontext.ecommerce.user.UserContextProvider;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;

import static com.coremedia.cap.test.xmlrepo.XmlRepoResources.HANDLERS;
import static com.coremedia.livecontext.ecommerce.ibm.common.AbstractWrapperServiceTestCase.LocalConfig.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = AbstractWrapperServiceTestCase.LocalConfig.class)
@ActiveProfiles(PROFILE)
public abstract class AbstractWrapperServiceTestCase {
  @Configuration
  @ImportResource(
          value = {
                  HANDLERS,
          "classpath:/com.coremedia.livecontext.ecommerce.ibm.service/test-commerce-services.xml",
          "classpath:/com.coremedia.livecontext.ecommerce.ibm.service/test-commerce-services-search.xml",
          "classpath:/com.coremedia.livecontext.ecommerce.ibm.service/test-commerce-services-bod-customizers.xml",
          },
          reader = ResourceAwareXmlBeanDefinitionReader.class
  )
  @Import(XmlRepoConfiguration.class)
  @Profile(PROFILE)
  public static class LocalConfig {
    public static final String PROFILE = "AbstractWrapperServiceTest";
  }

  private static final String BEAN_NAME_LOGIN_SERVICE = "userLoginService";
  private static final String BEAN_NAME_USER_CONTEXT_PROVIDER = "userContextProvider";
  private static final String BEAN_NAME_CATALOG_CONNECTOR = "restConnector";
  private static final String BEAN_NAME_TEST_CONFIG = "testConfig";

  public static final String TEST_USER = "gstevens"; // Frequent Buyer, Male Customer

  @Inject
  protected TestConfig testConfig;

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

  public TestConfig getTestConfig() {
    return testConfig;
  }

}
