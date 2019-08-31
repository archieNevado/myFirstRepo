package com.coremedia.livecontext.ecommerce.sfcc.common;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionFinder;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.cache.Cache;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.struct.Struct;
import com.coremedia.livecontext.ecommerce.common.CommerceConfigKeys;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.sfcc.configuration.SfccSiteGenesisStoreContextProperties;
import com.google.common.collect.ImmutableMap;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static com.google.common.collect.Maps.immutableEntry;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
        SfccSiteGenesisStoreContextProperties.class
})
@TestPropertySource("classpath:framework/spring/SfccStoreContextProviderTest.properties")
@EnableConfigurationProperties
public class SfccStoreContextProviderTest {

  private SfccStoreContextProvider testling;

  @Inject
  private SfccSiteGenesisStoreContextProperties sfccSiteGenesisStoreContextProperties;

  @Before
  public void setup() {
    CommerceConnection commerceConnection = mock(CommerceConnection.class);
    CommerceConnectionFinder commerceConnectionFinder = mock(CommerceConnectionFinder.class);
    when(commerceConnectionFinder.findConnection(any(Site.class))).thenReturn(Optional.of(commerceConnection));

    testling = new SfccStoreContextProvider(commerceConnectionFinder);
    testling.setStoreContextConfigurations(singletonList(sfccSiteGenesisStoreContextProperties));
    testling.initialize();
  }

  @Test
  public void readStoreConfigFromSpring() {
    assertThat(testling.readStoreConfigFromSpring("sitegenesis"))
            .isEqualTo(ImmutableMap.of(
                    CommerceConfigKeys.STORE_ID, "aStoreId",
                    CommerceConfigKeys.CURRENCY, "aCurrency",
                    CommerceConfigKeys.STORE_NAME, "SiteGenesisGlobal",
                    CommerceConfigKeys.CATALOG_ID, "aCatalogId"
            ));
  }

  @Test
  public void readStoreConfigFromSpringNoMatchingConfig() {
    assertThat(testling.readStoreConfigFromSpring("nonMatching")).isEmpty();
  }

  @Test
  public void localeFromSiteEndsUpInReplacements() {
    Site site = mockSite("site-id-314", Locale.ITALY);

    Cache cache = mock(Cache.class);
    when(cache.get(any())).thenReturn(ImmutableMap.of("store-id-42", "catalog-id-23"));
    testling.setCache(cache);

    mockStoreConfigFromSettingsService(site, ImmutableMap.of(
            "store.id", "store-id-42",
            "store.name", "Superstore",
            "currency", "EUR"
    ));

    StoreContext storeContext = testling.internalCreateContext(site).get();

    assertThat(storeContext.getReplacements()).contains(immutableEntry("locale", "it_IT"));
  }

  @NonNull
  private static Site mockSite(@NonNull String id, @NonNull Locale locale) {
    Site site = mock(Site.class);
    when(site.getId()).thenReturn(id);
    when(site.getLocale()).thenReturn(locale);
    return site;
  }

  private void mockStoreConfigFromSettingsService(@NonNull Site site, @NonNull Map<String, Object> config) {
    Struct storeConfigStruct = mock(Struct.class);
    when(storeConfigStruct.getProperties()).thenReturn(config);

    SettingsService settingsService = mock(SettingsService.class);
    when(settingsService.getSetting("livecontext.store.config", Struct.class, site.getSiteRootDocument()))
            .thenReturn(Optional.of(storeConfigStruct));
    testling.setSettingsService(settingsService);
  }
}
