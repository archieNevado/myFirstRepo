package com.coremedia.blueprint.boot.cae.live;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {
        FreeMarkerAutoConfiguration.class,
        MongoAutoConfiguration.class,
}, excludeName = {
        "net.devh.boot.grpc.client.autoconfigure.GrpcClientAutoConfiguration",
        "net.devh.boot.grpc.client.autoconfigure.GrpcClientHealthAutoConfiguration",
        "net.devh.boot.grpc.client.autoconfigure.GrpcClientMetricAutoConfiguration",
})
@EnableScheduling
public class CaeLiveApp {

  // ... Bean definitions
  public static void main(String[] args) {
    SpringApplication.run(CaeLiveApp.class, args);
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
