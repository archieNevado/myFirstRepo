package com.coremedia.livecontext.ecommerce.ibm;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionInitializer;
import com.coremedia.blueprint.lc.test.AbstractServiceTest;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.test.xmlrepo.XmlRepoConfiguration;
import com.coremedia.cap.test.xmlrepo.XmlUapiConfig;
import com.coremedia.livecontext.ecommerce.ibm.common.IbmTestConfig;
import com.coremedia.livecontext.ecommerce.ibm.login.LoginService;
import com.coremedia.livecontext.ecommerce.ibm.storeinfo.StoreInfoService;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;
import java.util.Optional;

import static com.coremedia.cap.test.xmlrepo.XmlRepoResources.HANDLERS;
import static com.coremedia.livecontext.ecommerce.ibm.IbmServiceTestBase.LocalConfig.PROFILE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public abstract class IbmServiceTestBase extends AbstractServiceTest {
  @Configuration
  @PropertySource(
          value = {
                  "classpath:/framework/spring/lc-ecommerce-ibm.properties",
                  "classpath:/com.coremedia.livecontext.ecommerce.ibm.service/test-commerce-services.properties",
                  "classpath:/com.coremedia.livecontext.ecommerce.ibm.service/ibm-example-catalog.properties"
          }
  )
  @ImportResource(
          value = {
                  HANDLERS,
                  "classpath:/com.coremedia.livecontext.ecommerce.ibm.service/test-commerce-services.xml",
                  "classpath:/framework/spring/lc-ecommerce-connection.xml",
                  "classpath:/framework/spring/livecontext-connection.xml",
                  "classpath:/framework/spring/livecontext-services.xml",
                  "classpath:/framework/spring/livecontext-commercebeans.xml",
                  "classpath:/com/coremedia/cae/contentbean-services.xml",
                  "classpath:/framework/spring/search/solr-search.xml",
                  "classpath:/com/coremedia/blueprint/base/multisite/bpbase-multisite-services.xml"
          },
          reader = ResourceAwareXmlBeanDefinitionReader.class
  )
  @Import(XmlRepoConfiguration.class)
  @Profile(PROFILE)
  public static class LocalConfig {
    public static final String PROFILE = "IbmServiceTestBase";

    @Bean
    public XmlUapiConfig xmlUapiConfig() {
      return new XmlUapiConfig("classpath:/content/testcontent.xml");
    }
  }

  @MockBean
  private CommerceConnectionInitializer commerceConnectionInitializer;

  @Inject
  protected StoreInfoService storeInfoService;

  @Inject
  protected LoginService loginService;

  @Inject
  protected IbmTestConfig testConfig;

  @Override
  @Before
  public void setup() {
    doAnswer(invocationOnMock -> Optional.of(connection)).when(commerceConnectionInitializer).findConnectionForSite(any(Site.class));
    storeInfoService.getWcsVersion().ifPresent(testConfig::setWcsVersion);
    super.setup();
    loginService.clearIdentityCache();
  }
}
