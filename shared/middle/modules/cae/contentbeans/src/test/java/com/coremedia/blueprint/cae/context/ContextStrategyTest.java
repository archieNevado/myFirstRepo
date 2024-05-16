package com.coremedia.blueprint.cae.context;

import com.coremedia.blueprint.base.navigation.context.ContextStrategy;
import com.coremedia.blueprint.common.contentbeans.CMArticle;
import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.blueprint.testing.ContentTestConfiguration;
import com.coremedia.blueprint.testing.ContentTestHelper;
import com.coremedia.cap.test.xmlrepo.XmlRepoConfiguration;
import com.coremedia.cap.test.xmlrepo.XmlUapiConfig;
import com.coremedia.cms.delivery.configuration.DeliveryConfigurationProperties;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ContextStrategyTest.LocalConfig.class, XmlRepoConfiguration.class})
public class ContextStrategyTest {

  @Configuration(proxyBeanMethods = false)
  @EnableConfigurationProperties({
          DeliveryConfigurationProperties.class
  })
  @ImportResource(value = {"classpath:/framework/spring/blueprint-contentbeans.xml", "classpath:/framework/spring/blueprint-contextstrategy.xml"},
          reader = ResourceAwareXmlBeanDefinitionReader.class)
  @Import({ContentTestConfiguration.class, XmlRepoConfiguration.class})
  public static class LocalConfig {
    @Bean
    public XmlUapiConfig xmlUapiConfig() {
      return new XmlUapiConfig("classpath:/com/coremedia/blueprint/cae/context/context.xml");
    }
  }

  @Inject
  private ContentTestHelper contentTestHelper;

  @Inject
  private ContextStrategy contextStrategy;

  @Test
  public void testDefaultContextStrategyWithLocaleJump() {
    CMArticle germanArticle = contentTestHelper.getContentBean(82);
    CMArticle englishArticle = contentTestHelper.getContentBean(92);
    CMChannel germanChannelOnThirdLevel = contentTestHelper.getContentBean(10030);
    CMChannel englishChannelOnThirdLevel = contentTestHelper.getContentBean(20030);

    // Assume correct Locales
    assertThat(germanArticle.getLocale()).isEqualTo(Locale.GERMAN);
    assertThat(englishArticle.getLocale()).isEqualTo(Locale.ENGLISH);

    // Assert Master-Derived Relationship
    assertThat(englishArticle).isEqualTo(germanArticle.getVariantsByLocale().get(Locale.ENGLISH));
    assertThat(germanArticle).isEqualTo(englishArticle.getMaster());

    // Assert Master-Derived Relationship of Context
    assertThat(englishChannelOnThirdLevel).isEqualTo(germanChannelOnThirdLevel.getVariantsByLocale().get(Locale.ENGLISH));
    assertThat(germanChannelOnThirdLevel).isEqualTo(englishChannelOnThirdLevel.getMaster());

    // Assert that our expected target is amongst candidates
    assertThat(englishArticle.getContexts()).contains(englishChannelOnThirdLevel);
    assertThat(germanArticle.getContexts()).contains(germanChannelOnThirdLevel);

    // Compute target Context
    Object computedContext = contextStrategy.findAndSelectContextFor(englishArticle, germanChannelOnThirdLevel);

    // Final Assert
    assertThat(computedContext).isEqualTo(englishChannelOnThirdLevel);
  }
}
