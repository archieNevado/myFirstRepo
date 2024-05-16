package com.coremedia.blueprint.coderesources;

import com.coremedia.blueprint.base.tree.TreeRelation;
import com.coremedia.blueprint.base.tree.TreeRelationServicesConfiguration;
import com.coremedia.cap.content.Content;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration(proxyBeanMethods = false)
@Import({
        TreeRelationServicesConfiguration.class,
})
public class ThemeServiceConfiguration {

  @Bean
  public ThemeService themeService(TreeRelation<Content> navigationTreeRelation) {
    return new ThemeService(navigationTreeRelation);
  }

}
