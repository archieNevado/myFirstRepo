package com.coremedia.livecontext.ecommerce.ibm.event;

import com.coremedia.cache.Cache;
import com.coremedia.livecontext.ecommerce.event.CommerceCacheInvalidationPropagator;
import com.coremedia.livecontext.ecommerce.ibm.common.WcRestConnector;
import com.coremedia.springframework.context.support.RequiredPropertySourcesPlaceholderConfigurerConfiguration;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;

@Configuration
@Import(RequiredPropertySourcesPlaceholderConfigurerConfiguration.class)
@ImportResource(value = {
        "classpath:/com/coremedia/cache/cache-services.xml",
        "classpath:/com/coremedia/blueprint/base/settings/impl/bpbase-settings-services.xml",
        "classpath:/com/coremedia/blueprint/base/multisite/bpbase-multisite-services.xml"
}, reader = ResourceAwareXmlBeanDefinitionReader.class)
@ComponentScan
@EnableScheduling
class IbmCommerceEventConfiguration {

  private static final Logger LOGGER = LoggerFactory.getLogger(IbmCommerceEventConfiguration.class);
  private static final String ENABLED_PLACEHOLDER = "${livecontext.ibm.wcs.cache.invalidation.enabled:true}";
  private static final String INTERVAL_PLACEHOLDER = "${livecontext.ibm.wcs.cache.invalidation.interval:500}";
  private static final String ERROR_INTERVAL_PLACEHOLDER = "${livecontext.ibm.wcs.cache.invalidation.error_interval:30000}";

  @Value(ENABLED_PLACEHOLDER)
  private boolean enabled;

  @Value(INTERVAL_PLACEHOLDER)
  private long interval;
  @Value(ERROR_INTERVAL_PLACEHOLDER)
  private long errorInterval;

  @Autowired(required = false)
  private TaskScheduler taskScheduler;

  @Bean
  @Autowired
  CommerceCacheInvalidationLifecycle commerceCacheInvalidationLifecycle(IbmCommerceConnections commerceConnections, CommerceCacheInvalidationListener commerceCacheInvalidationListener, Cache cache) {
    if (!enabled) {
      LOGGER.info("IBM commerce cache invalidations is disabled.");
      return null;
    }
    if (null == taskScheduler) {
      LOGGER.warn("No task scheduler available, disabling IBM commerce cache invalidations.");
      return null;
    }
    return new CommerceCacheInvalidationLifecycle(taskScheduler, commerceConnections, commerceCacheInvalidationListener, cache, interval, errorInterval);
  }

  @Bean
  @Autowired
  CommerceCacheInvalidationListener commerceCacheInvalidationListener(List<CommerceCacheInvalidationPropagator> propagators, WcRestConnector wcRestConnector) {
    AnnotationAwareOrderComparator.sort(propagators);
    return new CommerceCacheInvalidationListener(propagators, cacheWrapperService(wcRestConnector));
  }

  @Bean
  @Autowired
  WcCacheWrapperService cacheWrapperService(WcRestConnector wcRestConnector) {
    WcCacheWrapperService wcCacheWrapperService = new WcCacheWrapperService();
    wcCacheWrapperService.setRestConnector(wcRestConnector);
    return wcCacheWrapperService;
  }

  @Bean
  @Autowired
  @Order(0) // must be informed very early
  CommerceCacheInvalidationPropagator commerceCacheInvalidationPropagator(Cache cache) {
    return new CommerceCacheInvalidationPropagatorImpl(cache);
  }

}
