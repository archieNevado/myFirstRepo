package com.coremedia.livecontext.ecommerce.ibm.common;

import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.cap.test.xmlrepo.XmlRepoConfiguration;
import com.coremedia.cap.test.xmlrepo.XmlUapiConfig;
import com.coremedia.livecontext.ecommerce.common.InvalidContextException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Currency;
import java.util.Locale;
import java.util.Map;

import static com.coremedia.livecontext.ecommerce.ibm.common.IbmStoreContextProviderTest.LocalConfig.PROFILE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_SINGLETON;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = IbmStoreContextProviderTest.LocalConfig.class)
@ActiveProfiles(PROFILE)
public class IbmStoreContextProviderTest {
  @Configuration
  @ImportResource(
          value = {
                  "classpath:/com.coremedia.livecontext.ecommerce.ibm.service/test-commerce-services.xml",
                  "classpath:/com/coremedia/blueprint/base/multisite/bpbase-multisite-services.xml",
          },
          reader = ResourceAwareXmlBeanDefinitionReader.class
  )
  @Import(XmlRepoConfiguration.class)
  @Profile(PROFILE)
  public static class LocalConfig {
    public static final String PROFILE = "IbmStoreContextProviderTest";
    private static final String CONTENT_REPOSITORY = "/content/testcontent.xml";

    @Bean
    @Scope(SCOPE_SINGLETON)
    public XmlUapiConfig xmlUapiConfig() {
      return new XmlUapiConfig(CONTENT_REPOSITORY);
    }
  }

  @Inject
  private StoreContextProviderImpl testling;

  @Inject
  private SitesService sitesService;

  @Test
  public void testFindContextBySiteNameAvailable() {
    StoreContext context = testling.findContextBySiteName("Helios");
    assertNotNull(context);
    assertEquals("PerfectChefESite", StoreContextHelper.getStoreName(context));
    assertEquals("10202", StoreContextHelper.getStoreId(context));
    assertEquals("10051", StoreContextHelper.getCatalogId(context));
    assertEquals(new Locale("en"), StoreContextHelper.getLocale(context));
    assertEquals(Currency.getInstance("USD"), StoreContextHelper.getCurrency(context));
  }

  @Test
  public void testFindContextBySite() {
    Site currentSite = getSite("Helios");
    StoreContext context = testling.findContextBySite(currentSite);
    assertNotNull(context);
    assertEquals("PerfectChefESite", StoreContextHelper.getStoreName(context));
    assertEquals("10202", StoreContextHelper.getStoreId(context));
    assertEquals("10051", StoreContextHelper.getCatalogId(context));
    assertEquals(new Locale("en"), StoreContextHelper.getLocale(context));
    assertEquals(Currency.getInstance("USD"), StoreContextHelper.getCurrency(context));
  }

  @Test
  public void testFindContextBySiteAlternatively() {
    Site currentSite = getSite("Alternative");
    StoreContext context = testling.findContextBySite(currentSite);
    assertNotNull(context);
    assertEquals("springsite", StoreContextHelper.getStoreName(context));
    assertEquals("12345", StoreContextHelper.getStoreId(context));
    assertEquals("67890", StoreContextHelper.getCatalogId(context));
    assertEquals(new Locale("de"), StoreContextHelper.getLocale(context));
    assertEquals(Currency.getInstance("EUR"), StoreContextHelper.getCurrency(context));
    assertEquals("spring.only.setting", context.getReplacements().get("spring.only.setting"));
  }

  @Test
  public void testFindContextBySiteNoStoreConfig() throws Exception {
    Site currentSite = getSite("Media");
    assertNotNull("Expected site Media in test content was not found.", currentSite);

    StoreContext context = testling.findContextBySite(currentSite);
    assertNull(context);
  }

  @Test
  public void testFindContextBySiteWrongSite() {
    StoreContext context = testling.findContextBySiteName("not available");
    assertNull(context);
  }

  @Test(expected = InvalidContextException.class)
  public void testFindContextBySiteIncompleteStoreConfig() throws Exception {
    Site currentSite = getSite("Helios-incomplete");
    assertNotNull("Expected site Helios-incomplete in test content was not found.", currentSite);

    StoreContext contextBySite = testling.findContextBySite(currentSite);// should throw an InvalidContextException
    assertNotNull(contextBySite);

    StoreContextHelper.validateContext(contextBySite);
  }

  @Test
  public void testParseReplacementsFromStruct() {
    Site currentSite = getSite("Helios");
    StoreContext context = testling.findContextBySite(currentSite);

    Map<String, String> replacements = context.getReplacements();
    assertEquals("shop-ref.ecommerce.coremedia.com", replacements.get("livecontext.ibm.wcs.host"));
    assertEquals("shop-helios.blueprint-box.vagrant", replacements.get("livecontext.apache.wcs.host"));
    assertEquals("spring.only.setting", replacements.get("spring.only.setting"));
  }

  @Nullable
  private Site getSite(final String siteName) {
    return FluentIterable.from(sitesService.getSites()).firstMatch(new Predicate<Site>() {
      @Override
      public boolean apply(@Nullable Site site) {
        return site != null && site.getName().equals(siteName);
      }
    }).orNull();
  }
}
