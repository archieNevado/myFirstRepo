package com.coremedia.livecontext.ecommerce.sfcc.cae;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionSupplier;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.livecontext.ecommerce.sfcc.configuration.SfccStoreContextProperties;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.shop.resources.ProductsResource;
import com.coremedia.livecontext.ecommerce.sfcc.pricing.PriceServiceImpl;
import com.coremedia.livecontext.fragment.links.transformers.resolvers.seo.ExternalSeoSegmentBuilder;
import com.coremedia.livecontext.logictypes.CommerceLedLinkBuilderHelper;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import edu.umd.cs.findbugs.annotations.NonNull;

@Configuration
@ImportResource(reader = ResourceAwareXmlBeanDefinitionReader.class,
        value = {
                "classpath:/com/coremedia/blueprint/base/settings/impl/bpbase-settings-services.xml",
                "classpath:/META-INF/coremedia/livecontext-handlers.xml"
        }
)
@ComponentScan(basePackageClasses = SfccStoreContextProperties.class)
public class SfccCaeConfiguration {

  @Bean
  PriceServiceImpl sfccPriceService(@NonNull ProductsResource productsResource) {
    return new PriceServiceImpl(productsResource);
  }

  @Bean
  SfccLinkScheme sfccLinkScheme(@NonNull SfccCommerceUrlProvider urlProvider,
                                @NonNull CommerceConnectionSupplier commerceConnectionSupplier,
                                @NonNull CommerceLedLinkBuilderHelper commerceLedPageExtension,
                                @NonNull SettingsService settingsService) {
    return new SfccLinkScheme(urlProvider, commerceConnectionSupplier, commerceLedPageExtension, settingsService);
  }

  @Bean
  SfccLinkResolver sfccLinkResolver(@NonNull ExternalSeoSegmentBuilder seoSegmentBuilder) {
    return new SfccLinkResolver(seoSegmentBuilder);
  }

  @Bean
  SfccCommerceUrlProvider sfccPageHandlerUrlProvider(@NonNull @Value("${livecontext.sfcc.storefront.url:https://${livecontext.sfcc.host}/on/demandware.store}") String storefrontUrl) {
    return new SfccCommerceUrlProvider(storefrontUrl);
  }
}
