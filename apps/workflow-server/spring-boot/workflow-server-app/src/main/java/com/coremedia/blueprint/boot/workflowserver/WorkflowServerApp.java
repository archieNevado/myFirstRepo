package com.coremedia.blueprint.boot.workflowserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * we need to exclude some autoconfigurations:
 * - the datasource autoconfiguration
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, MongoAutoConfiguration.class})
@EnableScheduling
public class WorkflowServerApp {

  // ... Bean definitions
  public static void main(String[] args) {
    SpringApplication.run(WorkflowServerApp.class, args);
  }
}
