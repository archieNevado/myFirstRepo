package com.coremedia.blueprint.elastic.base;

import com.coremedia.elastic.core.api.tenant.TenantService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration(proxyBeanMethods = false)
@EnableScheduling
class TenantInitializerConfiguration {
  @Bean
  TenantInitializer tenantInitializer(TenantService tenantService, TenantHelper tenantHelper){
    return new TenantInitializer(tenantService, tenantHelper);
  }
}
