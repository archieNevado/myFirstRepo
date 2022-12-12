package com.coremedia.ecommerce.studio.rest.cache;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceServicesAutoConfiguration;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.rest.cap.config.StudioConfigurationProperties;
import com.coremedia.rest.linking.Linker;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import static java.lang.invoke.MethodHandles.lookup;

@Configuration(proxyBeanMethods = false)
@Import({
        BaseCommerceServicesAutoConfiguration.class
})
@EnableConfigurationProperties({
        StudioConfigurationProperties.class
})
public class CommerceCacheConfiguration implements DisposableBean {

  private static final Logger LOG = LoggerFactory.getLogger(lookup().lookupClass());

  @Nullable
  private ThreadPoolTaskScheduler threadPoolTaskScheduler;

  public void destroy() {
    if (threadPoolTaskScheduler != null) {
      threadPoolTaskScheduler.destroy();
    }
  }

  @Bean
  CommerceCacheInvalidationSource commerceCacheInvalidationSource(StudioConfigurationProperties studioConfigurationProperties,
                                                                  Linker linker,
                                                                  SettingsService settingsService,
                                                                  ObjectProvider<TaskScheduler> taskSchedulerProvider) {
    var taskScheduler = taskSchedulerProvider.getIfAvailable(this::createLocalTaskScheduler);
    CommerceCacheInvalidationSource commerceCacheInvalidationSource = new CommerceCacheInvalidationSource();
    commerceCacheInvalidationSource.setId("commerceInvalidationSource");
    commerceCacheInvalidationSource.setCapacity(studioConfigurationProperties.getRest().getCommerceCache().getCapacity());
    commerceCacheInvalidationSource.setLinker(linker);
    commerceCacheInvalidationSource.setSettingsService(settingsService);
    commerceCacheInvalidationSource.setTaskScheduler(taskScheduler);
    return commerceCacheInvalidationSource;
  }

  private TaskScheduler createLocalTaskScheduler() {
    threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
    threadPoolTaskScheduler.setBeanName("commerce-cache");
    threadPoolTaskScheduler.initialize();
    LOG.warn("No TaskScheduler bean available, using local thread pool task scheduler instead.");
    return threadPoolTaskScheduler;
  }

}
