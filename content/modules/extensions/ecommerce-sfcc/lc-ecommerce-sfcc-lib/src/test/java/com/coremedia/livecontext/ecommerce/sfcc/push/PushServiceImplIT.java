package com.coremedia.livecontext.ecommerce.sfcc.push;

import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.sfcc.configuration.SfccStoreContextProperties;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.SfccTestConfig;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.SiteGenesisGlobalTestConfig;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.shop.ShopApiResourceTestBase;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Map;

import static com.coremedia.blueprint.lc.test.BetamaxTestHelper.useBetamaxTapes;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = PushServiceImplIT.LocalConfig.class)
@TestPropertySource(properties = {
        "livecontext.cache.invalidation.enabled=false"
})
public class PushServiceImplIT extends ShopApiResourceTestBase {

  @Inject
  private PushServiceImpl pushService;

  @Inject
  SfccContentHelper sfccContentHelper;


  @Test
  void testPushAndDeleteContentAsset(){
    if (useBetamaxTapes()) {
      return;
    }

    CommerceConnection connection = commerce.findConnection(testConfig.getConnectionId())
            .orElseThrow(() -> new IllegalStateException("Could not obtain commerce connection."));
    StoreContext storeContext = testConfig.getStoreContext(connection);

    String pageKey = "testPushContentAsset" + System.currentTimeMillis();

    JSONObject jsonToBePushed = new JSONObject();
    jsonToBePushed.put("simpleString", "aString");
    jsonToBePushed.put("simpleMap", Collections.singletonMap("pommes", "majo"));
    pushService.pushContentAsset(pageKey, "name", "title", jsonToBePushed, storeContext);

    String jsonAsString = sfccContentHelper.getStoredJsonByPageKey(pageKey, storeContext);
    Gson g = new Gson();
    Map storedJson = g.fromJson(jsonAsString, Map.class);

    assertThat(storedJson.get("simpleString")).isEqualTo("aString");
    Map simpleMap = (Map) storedJson.get("simpleMap");
    assertThat(simpleMap.get("pommes")).isEqualTo("majo");

    //clean up
    pushService.deleteByPageKey(pageKey, storeContext);
    assertThat(sfccContentHelper.getStoredJsonByPageKey(pageKey, storeContext)).isNull();
  }

  @Configuration
  @ComponentScan(basePackageClasses = SfccStoreContextProperties.class)
  public static class LocalConfig {

    @Bean
    @Primary
    public SfccTestConfig testConfig() {
      return new SiteGenesisGlobalTestConfig();
    }
  }
}
