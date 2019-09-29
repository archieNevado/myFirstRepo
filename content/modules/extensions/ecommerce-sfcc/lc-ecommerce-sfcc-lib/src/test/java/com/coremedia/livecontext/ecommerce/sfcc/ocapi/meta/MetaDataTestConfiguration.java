package com.coremedia.livecontext.ecommerce.sfcc.ocapi.meta;

import com.coremedia.cap.test.xmlrepo.XmlRepoConfiguration;
import com.coremedia.livecontext.ecommerce.sfcc.configuration.SfccStoreContextProperties;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.SfccTestConfig;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.SiteGenesisGlobalTestConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

@Configuration
@Import(XmlRepoConfiguration.class)
@ComponentScan(basePackageClasses = {
        SfccStoreContextProperties.class,
        OCMetaApiConnector.class
})
public class MetaDataTestConfiguration {

  @Bean
  @Primary
  public SfccTestConfig testConfig(){
    return new SiteGenesisGlobalTestConfig();
  }
}
