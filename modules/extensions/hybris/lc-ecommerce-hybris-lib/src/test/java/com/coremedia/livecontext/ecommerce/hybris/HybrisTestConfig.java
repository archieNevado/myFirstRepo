package com.coremedia.livecontext.ecommerce.hybris;

import com.coremedia.blueprint.lc.test.TestConfig;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.hybris.common.StoreContextHelper;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;

import java.util.Locale;

@Configuration
@PropertySource(
        value = {
                "classpath:/com/coremedia/livecontext/ecommerce/hybris/test-hybris-services.properties",
                "classpath:/com/coremedia/livecontext/ecommerce/hybris/hybris-example-catalog.properties"
        }
)
@ImportResource(
        value = {
                "classpath:/com/coremedia/livecontext/ecommerce/hybris/test-hybris-services.xml",
                "classpath:/framework/spring/livecontext-hybris-commercebeans.xml"
        },
        reader = ResourceAwareXmlBeanDefinitionReader.class
)
public class HybrisTestConfig implements TestConfig {

  @Override
  public StoreContext getStoreContext() {
    return StoreContextHelper.createContext("configid", "apparel-uk", "Apparel-Catalog", "apparelProductCatalog",
            Locale.ENGLISH, "GBP", "Staged");
  }

  @Override
  public String getConnectionId() {
    return "hybris1";
  }

  @Override
  public String getCatalogName() {
    return null;
  }

  @Override
  public String getStoreName() {
    return null;
  }
}
