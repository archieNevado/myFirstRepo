package com.coremedia.blueprint.boot.studio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

/**
 * we need to exclude the autoconfiguration for the default springSecurityFilterChain to enable ours
 */
@SpringBootApplication(exclude = {
        FreeMarkerAutoConfiguration.class,
        MongoAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class,
        //Used to disable editorial comments feature.
        //EditorialCommentsAutoConfiguration.class, //part of module com.coremedia.cms:editorial-comments-rest
        //DataSourceAutoConfiguration.class,
}, excludeName = {
        "net.devh.boot.grpc.client.autoconfigure.GrpcClientAutoConfiguration",
        "net.devh.boot.grpc.client.autoconfigure.GrpcClientHealthAutoConfiguration",
        "net.devh.boot.grpc.client.autoconfigure.GrpcClientMetricAutoConfiguration",
})
@EnableScheduling
@EnableWebSecurity
public class StudioServerApp {

  // ... Bean definitions
  public static void main(String[] args) {
    SpringApplication.run(StudioServerApp.class, args);
  }
}
