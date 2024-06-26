package com.coremedia.blueprint.boot.contentserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.solr.SolrAutoConfiguration;

/**
 * we need to exclude some autoconfigurations:
 * - the datasource autoconfiguration
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, SolrAutoConfiguration.class})
public class ContentServerApp {

  // ... Bean definitions
  public static void main(String[] args) throws Exception {
    SpringApplication.run(ContentServerApp.class, args);
  }
}
