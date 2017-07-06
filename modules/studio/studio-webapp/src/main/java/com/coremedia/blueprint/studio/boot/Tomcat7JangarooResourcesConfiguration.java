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

/**
 * Spring Boot replacement for tomcat-context.xml - configure extra resource paths with -Djangaroo.output on command line.
 */
@Configuration
@ConditionalOnClass({Context.class, VirtualDirContext.class})
public class Tomcat7JangarooResourcesConfiguration {

  static final String JANGAROO_OUTPUT = "jangaroo.output";

  @Value("${server.docbase:#{systemProperties['java.io.tmpdir']}}")
  private String docBase;

  /**
   * Provide property 'jangaroo.output' in development mode so that the compiled jangaroo
   * resources can be served immediately
   */
  @Bean
  @ConditionalOnProperty(name = Tomcat7JangarooResourcesConfiguration.JANGAROO_OUTPUT)
  EmbeddedServletContainerCustomizer jangarooOutputConfigurer(@Value("${" + JANGAROO_OUTPUT + '}') final String jangarooOutput) {
    return container -> {
      if (!isEmpty(jangarooOutput) && container instanceof TomcatEmbeddedServletContainerFactory) {
        TomcatEmbeddedServletContainerFactory containerFactory = (TomcatEmbeddedServletContainerFactory) container;
        containerFactory.addContextCustomizers(cofigureExtraResources(jangarooOutput));
      }
    };
  }

  /*
   * Configure index.html when running embedded tomcat
   */
  @Bean
  EmbeddedServletContainerCustomizer indexHtmlConfigurer() {
    return container -> {
      if (container instanceof TomcatEmbeddedServletContainerFactory) {
        TomcatEmbeddedServletContainerFactory containerFactory = (TomcatEmbeddedServletContainerFactory) container;
        containerFactory.addContextCustomizers(context -> {context.addWelcomeFile("index.html");});
      }
    };
  }

  private TomcatContextCustomizer cofigureExtraResources(final String extraResourcePath) {
    return context -> {
      VirtualDirContext dirContext = new VirtualDirContext();
      dirContext.setExtraResourcePaths(extraResourcePath);
      context.setDocBase(docBase);
      context.setResources(dirContext);
    };
  }

}
