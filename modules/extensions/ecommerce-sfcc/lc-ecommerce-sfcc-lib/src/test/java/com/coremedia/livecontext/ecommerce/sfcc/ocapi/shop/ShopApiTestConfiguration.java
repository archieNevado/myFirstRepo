package com.coremedia.livecontext.ecommerce.sfcc.ocapi.shop;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CatalogAliasTranslationService;
import com.coremedia.cap.test.xmlrepo.XmlRepoConfiguration;
import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.sfcc.configuration.SfccStoreContextProperties;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.SfccTestConfig;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.SiteGenesisGlobalTestConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
@Import(XmlRepoConfiguration.class)
@ComponentScan(basePackageClasses = SfccStoreContextProperties.class)
public class ShopApiTestConfiguration {

  public static class LocalConfig {
    @Bean
    @Primary
    CatalogAliasTranslationService theCatalogAliasTranslationService() {
      CatalogAliasTranslationService catalogAliasTranslationService = mock(CatalogAliasTranslationService.class);
      when(catalogAliasTranslationService.getCatalogIdForAlias(any(), any())).thenReturn(Optional.of(CatalogId.of("sitegenisis")));
      return catalogAliasTranslationService;
    }

    @Bean
    @Primary
    public SfccTestConfig testConfig(){
      return new SiteGenesisGlobalTestConfig();
    }
  }
}
