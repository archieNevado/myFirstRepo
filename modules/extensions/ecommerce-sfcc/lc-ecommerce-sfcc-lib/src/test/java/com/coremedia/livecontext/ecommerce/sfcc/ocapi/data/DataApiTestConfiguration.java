package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.cap.test.xmlrepo.XmlRepoConfiguration;
import com.coremedia.livecontext.ecommerce.sfcc.configuration.SfccStoreContextProperties;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.SfccTestConfig;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.SiteGenesisGlobalTestConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(XmlRepoConfiguration.class)
@ComponentScan(basePackageClasses = SfccStoreContextProperties.class)
public class DataApiTestConfiguration {

  public static class LocalConfig {

    @Bean
    public Commerce commerce() {
      return new Commerce();
    }

    @Bean
    public SfccTestConfig testConfig() {
      return new SiteGenesisGlobalTestConfig();
    }
  }
}
