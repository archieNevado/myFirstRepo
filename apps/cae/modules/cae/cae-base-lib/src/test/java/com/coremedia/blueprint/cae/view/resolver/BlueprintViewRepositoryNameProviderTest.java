package com.coremedia.blueprint.cae.view.resolver;

import com.coremedia.blueprint.cae.config.BlueprintViewsCaeBaseLibConfiguration;
import com.coremedia.blueprint.cae.web.links.NavigationLinkSupport;
import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import com.coremedia.blueprint.testing.ContentTestConfiguration;
import com.coremedia.blueprint.testing.ContentTestHelper;
import com.coremedia.cap.test.xmlrepo.XmlUapiConfig;
import com.coremedia.cms.delivery.configuration.DeliveryConfigurationProperties;
import com.coremedia.springframework.customizer.Customize;
import com.coremedia.springframework.customizer.CustomizerConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.Order;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.coremedia.blueprint.cae.view.resolver.BlueprintViewRepositoryNameProviderTest.LocalConfig.PROFILE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_SINGLETON;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = BlueprintViewRepositoryNameProviderTest.LocalConfig.class)
@ActiveProfiles(PROFILE)
public class BlueprintViewRepositoryNameProviderTest {
  @Configuration(proxyBeanMethods = false)
  @EnableConfigurationProperties({
          DeliveryConfigurationProperties.class,
  })
  @Import({
          BlueprintViewsCaeBaseLibConfiguration.class,
          ContentTestConfiguration.class,
          CustomizerConfiguration.class,
  })
  @Profile(PROFILE)
  public static class LocalConfig {
    public static final String PROFILE = "BlueprintViewRepositoryNameProviderTest";
    private static final String CONTENT_REPOSITORY = "classpath:/com/coremedia/testing/contenttest.xml";

    @Bean
    @Scope(SCOPE_SINGLETON)
    public XmlUapiConfig xmlUapiConfig() {
      return new XmlUapiConfig(CONTENT_REPOSITORY);
    }

    @Bean
    @Order(900)
    @Customize(value = "viewRepositories", mode = Customize.Mode.PREPEND)
    String yetAnotherRepositoryName() {
      return "yetAnotherRepository";
    }

    @Bean
    @Customize(value = "viewRepositories", mode = Customize.Mode.PREPEND)
    @Order(1000)
    String basicRepositoryName() {
      return "basic";
    }

  }

  @Inject
  private BlueprintViewRepositoryNameProvider blueprintViewRepositoryNameProvider;

  @Inject
  private ContentTestHelper contentTestHelper;

  @Inject
  private MockHttpServletRequest request;

  private CMChannel channel;

  @Before
  public void setUp() {
    channel = contentTestHelper.getContentBean(10);
  }

  @Test
  public void testGetViewRepositoryNames() {
    request.setAttribute("com.coremedia.blueprint.viewrepositorynames", null);
    Map<String, CMLinkable> map = new HashMap<>();
    map.put(NavigationLinkSupport.ATTR_NAME_CMNAVIGATION, channel);
    List<String> repositoryNames = blueprintViewRepositoryNameProvider.getViewRepositoryNames("view", map, null, request);
    assertThat(repositoryNames).containsExactly(
            "media",
            "notMedia",
            "againNotMedia",
            "basic",
            "common",
            "yetAnotherRepository",
            "error"
    );
  }
}
