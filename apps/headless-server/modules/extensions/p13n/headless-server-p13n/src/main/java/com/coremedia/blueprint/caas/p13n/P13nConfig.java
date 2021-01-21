package com.coremedia.blueprint.caas.p13n;

import com.coremedia.cache.Cache;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.blueprint.base.caas.p13n.adapter.PersonalizationRulesAdapterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class P13nConfig {

  @Bean
  public PersonalizationRulesAdapterFactory p13nRulesAdapter(ContentRepository contentRepository, Cache cache) {
    return new PersonalizationRulesAdapterFactory(contentRepository, cache);
  }

}
