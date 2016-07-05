package com.coremedia.livecontext.ecommerce.ibm.common;

import co.freeside.betamax.Recorder;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.cap.test.xmlrepo.XmlRepoConfiguration;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.ibm.login.LoginService;
import com.coremedia.livecontext.ecommerce.ibm.storeinfo.StoreInfoService;
import com.coremedia.livecontext.ecommerce.ibm.user.UserContextProviderImpl;
import com.coremedia.livecontext.ecommerce.user.UserService;
import com.coremedia.livecontext.ecommerce.user.UserSessionService;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;

import static com.coremedia.cap.test.xmlrepo.XmlRepoResources.HANDLERS;
import static com.coremedia.livecontext.ecommerce.ibm.common.AbstractServiceTest.LocalConfig.PROFILE;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public abstract class AbstractServiceTest {
  @Configuration
  @ImportResource(
          value = {
                  HANDLERS,
                  "classpath:/com.coremedia.livecontext.ecommerce.ibm.service/test-commerce-services.xml",
                  "classpath:/com.coremedia.livecontext.ecommerce.ibm.service/test-commerce-tree-relation.xml",
                  "classpath:/com.coremedia.livecontext.ecommerce.ibm.service/test-commerce-services-search.xml",
                  "classpath:/com.coremedia.livecontext.ecommerce.ibm.service/test-commerce-services-bod-customizers.xml",
                  "classpath:/com/coremedia/blueprint/base/multisite/bpbase-multisite-services.xml",
          },
          reader = ResourceAwareXmlBeanDefinitionReader.class
  )
  @Import(XmlRepoConfiguration.class)
  @Profile(PROFILE)
  public static class LocalConfig {
    public static final String PROFILE = "AbstractServiceTest";
  }

  @Inject
  protected Commerce commerce;

  @Inject
  protected UserContextProviderImpl userContextProvider;

  @Inject
  protected WcRestConnector restConnector;

  @Inject
  protected TestConfig testConfig;

  @Inject
  protected UserService commerceUserService;

  @Inject
  protected UserSessionService commerceUserSessionService;

  @Inject
  protected LoginService loginService;

  @Inject
  protected CommerceCache commerceCache;

  @Inject
  protected StoreInfoService storeInfoService;

  @Rule
  public Recorder recorder = new Recorder(BetamaxTestHelper.updateSystemPropertiesWithBetamaxConfig());

  @Before
  public void setup() {
    CommerceConnection connection = commerce.getConnection("wcs1");
    String wcsVersion = storeInfoService.getWcsVersion();
    if (wcsVersion != null)
      testConfig.setWcsVersion(Float.parseFloat(wcsVersion));
    connection.setStoreContext(testConfig.getStoreContext());
    Commerce.setCurrentConnection(connection);

    userContextProvider.clearCurrentContext();
    userContextProvider.setUserSessionService(commerceUserSessionService);
    loginService.clearIdentityCache();

    commerceCache.setEnabled(false);
    commerceCache.getCache().clear();
  }

  public TestConfig getTestConfig() {
    return testConfig;
  }

}
