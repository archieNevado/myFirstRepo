package com.coremedia.blueprint.studio.boot;

import org.apache.catalina.Context;
import org.apache.naming.resources.VirtualDirContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.springframework.util.StringUtils.isEmpty;

@Configuration
@ConditionalOnClass({Context.class, VirtualDirContext.class})
@ConditionalOnProperty(name = JangarooResourcesConfiguration.JANGAROO_OUTPUT)
class JangarooResourcesConfiguration {

  static final String JANGAROO_OUTPUT = "jangaroo.output";

  /**
   * Provide property 'jangaroo.output' in delevelopment mode so that the compiled jangaroo
   * resources can be served immediately
   */
  @Bean
  EmbeddedServletContainerCustomizer containerCustomizer(@Value("${" + JANGAROO_OUTPUT + '}') final String jangarooOutput) {
    return container -> {
      if (!isEmpty(jangarooOutput) && container instanceof TomcatEmbeddedServletContainerFactory) {
        TomcatEmbeddedServletContainerFactory containerFactory = (TomcatEmbeddedServletContainerFactory) container;
        containerFactory.addContextCustomizers(getTomcatContextCustomizer(jangarooOutput));
      }
    };
  }

  private static TomcatContextCustomizer getTomcatContextCustomizer(final String extraResourcePath) {
    return context -> {
      VirtualDirContext dirContext = new VirtualDirContext();
      dirContext.setExtraResourcePaths(extraResourcePath);
      context.setResources(dirContext);

      context.addWelcomeFile("index.html");
    };
  }

}
