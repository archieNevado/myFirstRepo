package com.coremedia.blueprint.localization;

import com.coremedia.blueprint.localization.configuration.TaxonomyLocalizationStrategyConfiguration;
import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.test.xmlrepo.XmlRepoConfiguration;
import com.coremedia.cap.test.xmlrepo.XmlUapiConfig;
import org.apache.commons.lang3.LocaleUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import javax.inject.Inject;

import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assume.assumeTrue;
import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_SINGLETON;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TaxonomyLocalizationStrategyTest.LocalConfig.class)
@ActiveProfiles(TaxonomyLocalizationStrategyTest.LocalConfig.PROFILE)
public class TaxonomyLocalizationStrategyTest {

  @Configuration(proxyBeanMethods = false)
  @Import({XmlRepoConfiguration.class, TaxonomyLocalizationStrategyConfiguration.class})
  @Profile(LocalConfig.PROFILE)
  public static class LocalConfig {
    static final String PROFILE = "LocalizationServiceTest";
    private static final String CONTENT = "classpath:com/coremedia/blueprint/localization/taxonomies/content.xml";

    @Bean
    @Scope(SCOPE_SINGLETON)
    public XmlUapiConfig xmlUapiConfig() {
      return XmlUapiConfig.builder()
              .withContent(CONTENT)
              .withContentTypes("classpath:com/coremedia/blueprint/localization/taxonomies/test-doctypes.xml")
              .build();
    }
  }

  @SuppressWarnings("SpringJavaAutowiringInspection")
  @Inject
  private ContentRepository contentRepository;

  @SuppressWarnings("SpringJavaAutowiringInspection")
  @Resource(name="taxonomyLocalizationStrategy")
  private TaxonomyLocalizationStrategyImpl testling;

  @Before
  public void checkWiring() {
    assumeTrue("TaxonomyLocalizationStrategyImpl not found in TaxonomyLocalizationStrategyConfiguration, these tests will fail.", testling!=null);
  }


  // --- contract ---------------------------------------------------

  @Test
  public void testStrategy() {
    List<Locale> supportedLocales = testling.getSupportedLocales();
    assertFalse("supportedLocale", supportedLocales.isEmpty());
    assertEquals(Locale.ENGLISH,testling.getDefaultLocale());

    assertEquals("Artikeltyp (de)", testling.getDisplayName(content(4), Locale.GERMAN));
    assertEquals("Artikeltyp (de_DE)", testling.getDisplayName(content(4), Locale.GERMANY));
    assertEquals("Artikeltyp (en)", testling.getDisplayName(content(4), Locale.ENGLISH));
    assertEquals("Artikeltyp (en_US)", testling.getDisplayName(content(4), Locale.US));
    assertEquals("Artikeltyp (en)", testling.getDisplayName(content(4), Locale.CANADA));
    assertEquals("Artikeltyp (en_GB)", testling.getDisplayName(content(4), Locale.UK));
  }

  private Content content(int id) {
    return contentRepository.getContent(IdHelper.formatContentId(id));
  }
}
