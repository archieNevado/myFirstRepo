package com.coremedia.livecontext.ecommerce.sfcc.common;

import com.coremedia.livecontext.ecommerce.sfcc.configuration.SfccSiteGenesisStoreContextProperties;
import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractStoreContextProvider.CONFIG_KEY_CATALOG_ID;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractStoreContextProvider.CONFIG_KEY_CURRENCY;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractStoreContextProvider.CONFIG_KEY_STORE_ID;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractStoreContextProvider.CONFIG_KEY_STORE_NAME;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
        SfccSiteGenesisStoreContextProperties.class
})
@TestPropertySource("classpath:framework/spring/SfccStoreContextProviderTest.properties")
@EnableConfigurationProperties
public class SfccStoreContextProviderTest {

  SfccStoreContextProvider testling;

  @Inject
  SfccSiteGenesisStoreContextProperties sfccSiteGenesisStoreContextProperties;

  @Before
  public void setup() {
    testling = new SfccStoreContextProvider();
    testling.setStoreContextConfigurations(Collections.singletonList(sfccSiteGenesisStoreContextProperties));
    testling.initialize();
  }

  @Test
  public void readStoreConfigFromSpring() throws Exception {
    Map<String, Object> configurationMap = new HashMap<>();
    configurationMap.put(CONFIG_KEY_STORE_ID, "oldStoreId");
    configurationMap.put(CONFIG_KEY_STORE_NAME, "oldStoreName");
    configurationMap.put(CONFIG_KEY_CURRENCY, "USD");
    testling.readStoreConfigFromSpring("sitegenesis", configurationMap);

    assertThat(configurationMap).isEqualTo(
            ImmutableMap.of(
                    CONFIG_KEY_STORE_ID, "aStoreId",
                    CONFIG_KEY_CURRENCY, "aCurrency",
                    CONFIG_KEY_STORE_NAME, "SiteGenesisGlobal",
                    CONFIG_KEY_CATALOG_ID, "aCatalogId"
            )
    );
  }

  @Test
  public void readStoreConfigFromSpringNoMatchingConfig() throws Exception {
    Map<String, Object> configurationMap = new HashMap<>();
    configurationMap.put(CONFIG_KEY_STORE_ID, "oldStoreId");
    testling.readStoreConfigFromSpring("nonMatching", configurationMap);
    assertThat(configurationMap.get(CONFIG_KEY_STORE_ID)).isEqualTo("oldStoreId");
  }
}
