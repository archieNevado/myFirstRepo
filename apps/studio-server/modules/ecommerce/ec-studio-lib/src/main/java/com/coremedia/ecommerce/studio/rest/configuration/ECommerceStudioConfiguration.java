package com.coremedia.ecommerce.studio.rest.configuration;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceServicesConfiguration;
import com.coremedia.ecommerce.studio.preview.CommerceHeadlessPreviewProvider;
import com.coremedia.ecommerce.studio.rest.filter.EcStudioFilters;
import com.coremedia.rest.cap.CapRestServiceConfiguration;
import com.coremedia.rest.invalidations.SimpleInvalidationSource;
import com.coremedia.service.previewurl.PreviewProvider;
import com.coremedia.service.previewurl.impl.PreviewUrlServiceConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration(proxyBeanMethods = false)
@Import({
        EcStudioFilters.class,
        CapRestServiceConfiguration.class,
        BaseCommerceServicesConfiguration.class
})
@ComponentScan(basePackages = "com.coremedia.ecommerce.studio.rest")
public class ECommerceStudioConfiguration {

  /**
   * @deprecated This class is part of the "push" implementation that is not supported by the
   * Commerce Hub architecture. It will be removed or changed in the future.
   */
  @SuppressWarnings("MethodMayBeStatic")
  @Bean
  SimpleInvalidationSource pushStateInvalidationSource() {
    SimpleInvalidationSource simpleInvalidationSource = new SimpleInvalidationSource();
    simpleInvalidationSource.setId("pushStateInvalidationSource");
    simpleInvalidationSource.setCapacity(1000);
    return simpleInvalidationSource;
  }

  @Bean
  public PreviewProvider commerceHeadlessPreviewProvider(PreviewUrlServiceConfigurationProperties previewUrlServiceConfigurationProperties) {
    return new CommerceHeadlessPreviewProvider(previewUrlServiceConfigurationProperties);
  }

}
