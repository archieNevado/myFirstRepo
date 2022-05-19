package com.coremedia.blueprint.segments;

import com.coremedia.blueprint.base.links.ContentSegmentStrategy;
import com.coremedia.blueprint.base.links.impl.BlueprintUrlPathFormattingConfiguration;
import com.coremedia.springframework.customizer.Customize;
import com.coremedia.springframework.customizer.CustomizerConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Map;

@Configuration(proxyBeanMethods = false)
@Import({
        BlueprintUrlPathFormattingConfiguration.class,
        CustomizerConfiguration.class,
})
public class SegmentsConfiguration {

  @Bean
  CMLinkableSegmentStrategy cmlinkableSegmentStrategy() {
    return new CMLinkableSegmentStrategy();
  }

  @Bean
  CMTaxonomySegmentStrategy cmtaxonomySegmentStrategy() {
    return new CMTaxonomySegmentStrategy();
  }

  @Bean
  CMPersonSegmentStrategy cmpersonSegmentStrategy() {
    return new CMPersonSegmentStrategy();
  }

  @Customize("contentSegmentStrategyMap")
  @Bean
  Map<String, ContentSegmentStrategy> contentSegmentStrategies(ContentSegmentStrategy cmlinkableSegmentStrategy,
                                                               ContentSegmentStrategy cmtaxonomySegmentStrategy,
                                                               ContentSegmentStrategy cmpersonSegmentStrategy) {
    return Map.of(
            "CMLinkable", cmlinkableSegmentStrategy,
            "CMTaxonomy", cmtaxonomySegmentStrategy,
            "CMPerson", cmpersonSegmentStrategy
    );
  }
}
