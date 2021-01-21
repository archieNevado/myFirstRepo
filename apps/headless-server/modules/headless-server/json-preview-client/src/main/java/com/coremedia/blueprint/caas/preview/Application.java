package com.coremedia.blueprint.caas.preview;

import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@DefaultAnnotation(NonNull.class)
@Configuration(proxyBeanMethods = false)
public class Application {

  @Bean
  public CloseableHttpClient httpClient() {
    return HttpClients.createDefault();
  }
}
