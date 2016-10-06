package com.coremedia.blueprint.viewtype.configuration;

import com.coremedia.blueprint.base.tree.TreeRelation;
import com.coremedia.blueprint.theme.ThemeService;
import com.coremedia.blueprint.viewtype.ViewtypeService;
import com.coremedia.cap.content.Content;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import javax.annotation.Resource;

@Configuration
public class ViewtypeServiceConfiguration {
  @Bean(name="viewtypeService")
  public ViewtypeService viewtypeService() {
    return new ViewtypeService();
  }
}
