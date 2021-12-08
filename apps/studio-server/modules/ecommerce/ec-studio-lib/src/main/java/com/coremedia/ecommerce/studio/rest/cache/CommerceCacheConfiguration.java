package com.coremedia.ecommerce.studio.rest.cache;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceServicesConfiguration;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.rest.cap.config.StudioConfigurationProperties;
import com.coremedia.rest.linking.Linker;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.TaskScheduler;

@Configuration(proxyBeanMethods = false)
@Import({
        BaseCommerceServicesConfiguration.class
})
@EnableConfigurationProperties({
        StudioConfigurationProperties.class
})
public class CommerceCacheConfiguration {

  @Bean
  CommerceCacheInvalidationSource commerceCacheInvalidationSource(ObjectProvider<TaskScheduler> taskScheduler,
                                                                  Linker linker,
                                                                  SettingsService settingsService,
                                                                  CommerceBeanClassResolver commerceBeanClassResolver,
                                                                  StudioConfigurationProperties studioConfigurationProperties) {
    CommerceCacheInvalidationSource commerceCacheInvalidationSource =
            new CommerceCacheInvalidationSource(taskScheduler, linker, settingsService, commerceBeanClassResolver);
    commerceCacheInvalidationSource.setId("commerceInvalidationSource");
    commerceCacheInvalidationSource.setCapacity(studioConfigurationProperties.getRest().getCommerceCache().getCapacity());
    return commerceCacheInvalidationSource;
  }

  @Bean
  CommerceBeanClassResolver commerceBeanClassResolver() {
    return new CommerceBeanClassResolver();
  }
}
