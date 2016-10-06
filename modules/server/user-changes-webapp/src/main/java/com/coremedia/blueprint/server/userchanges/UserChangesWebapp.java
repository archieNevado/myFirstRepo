package com.coremedia.blueprint.server.userchanges;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ServerPropertiesAutoConfiguration;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ServerPropertiesAutoConfiguration.class, EmbeddedServletContainerAutoConfiguration.class})
public class UserChangesWebapp extends SpringBootServletInitializer {

  /**
   * Use spring-boot-maven plugin to run to avoid https://youtrack.jetbrains.com/issue/IDEA-107048
   */
  public static void main(String[] args) {
    new SpringApplication(UserChangesWebapp.class).run(args);
  }

}