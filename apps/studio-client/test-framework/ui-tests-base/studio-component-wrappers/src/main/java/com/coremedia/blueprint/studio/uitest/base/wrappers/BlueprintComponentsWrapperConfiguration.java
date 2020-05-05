package com.coremedia.blueprint.studio.uitest.base.wrappers;

import com.coremedia.blueprint.studio.uitest.base.wrappers.desktop.BlueprintFavoritesToolbar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@SuppressWarnings("SpringFacetCodeInspection")
@Configuration
@ComponentScan(
        basePackages = {"com.coremedia.uitesting", "com.coremedia.blueprint.studio.uitest"},
        lazyInit = true
)
public class BlueprintComponentsWrapperConfiguration {

  @Bean
  static BlueprintFavoritesToolbar favoritesToolbar() {
    return new BlueprintFavoritesToolbar();
  }
}
