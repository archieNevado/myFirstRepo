package com.coremedia.blueprint.caas.p13n;

import com.coremedia.cache.Cache;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.blueprint.base.caas.p13n.adapter.PersonalizationRulesAdapterFactory;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration(proxyBeanMethods = false)
@ImportResource(value = {
        "classpath:/com/coremedia/cap/common/uapi-services.xml",
        "classpath:/com/coremedia/cache/cache-services.xml",
}, reader = ResourceAwareXmlBeanDefinitionReader.class)
public class P13nConfig {

  @Bean
  public PersonalizationRulesAdapterFactory p13nRulesAdapter(ContentRepository contentRepository, Cache cache) {
    return new PersonalizationRulesAdapterFactory(contentRepository, cache);
  }
}
