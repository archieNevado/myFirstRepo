package com.coremedia.livecontext.ecommerce.sfcc.catalog;

import com.coremedia.blueprint.lc.test.TestConfig;
import com.coremedia.cap.test.xmlrepo.XmlRepoConfiguration;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.sfcc.configuration.SfccStoreContextProperties;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.SfccTestConfig;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.SiteGenesisGlobalTestConfig;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource({
        "classpath:com/coremedia/livecontext/ecommerce/sfcc/sfcc-api-catalog-test.properties"
})
@Import(XmlRepoConfiguration.class)
@ComponentScan(basePackageClasses = SfccStoreContextProperties.class)
public class ServiceTestBaseConfiguration implements TestConfig {

  @Bean
  public SfccTestConfig testConfig() {
    return new SiteGenesisGlobalTestConfig();
  }

  @Override
  public StoreContext getStoreContext(@NonNull CommerceConnection connection) {
    return testConfig().getStoreContext(connection);
  }

  @Override
  public StoreContext getGermanStoreContext(@NonNull CommerceConnection connection) {
    return testConfig().getGermanStoreContext(connection);
  }

  @Override
  public String getConnectionId() {
    return testConfig().getConnectionId();
  }

  @Override
  public String getCatalogName() {
    return testConfig().getCatalogName();
  }
}