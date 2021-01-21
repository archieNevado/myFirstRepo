package com.coremedia.blueprint.boot.cae.preview;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

/**
 * we need to exclude some autoconfigurations:
 * - WMAC to disable the handler listening on /**
 * - FMAC because otherwise we have conflicting FreemarkerConfiguration beans
 *  for CAE and studio for server also exclude JDBC
 */
@SpringBootApplication(exclude = {
        WebMvcAutoConfiguration.class,
        FreeMarkerAutoConfiguration.class,
        MongoAutoConfiguration.class,
}, excludeName = {
        "net.devh.boot.grpc.client.autoconfigure.GrpcClientAutoConfiguration",
        "net.devh.boot.grpc.client.autoconfigure.GrpcClientHealthAutoConfiguration",
        "net.devh.boot.grpc.client.autoconfigure.GrpcClientMetricAutoConfiguration",
})
public class CaePreviewApp {

  // ... Bean definitions
  public static void main(String[] args) {
    SpringApplication.run(CaePreviewApp.class, args);
  }

  /*
   * Needed for 'redirect:*' view resolving. Usually created in WebMvcAutoConfiguration
   */
  @Bean
  public InternalResourceViewResolver defaultViewResolver() {
    return new InternalResourceViewResolver();
  }

  /*
   * Configure index.html when running embedded tomcat
   *
   * we need to do this as a workaround because we cannot use WebMvcAutoConfiguration
   */
  @Bean
  WebServerFactoryCustomizer<TomcatServletWebServerFactory> indexHtmlConfigurer() {
    return container -> container.addContextCustomizers(context -> context.addWelcomeFile("index.html"));
  }
}
