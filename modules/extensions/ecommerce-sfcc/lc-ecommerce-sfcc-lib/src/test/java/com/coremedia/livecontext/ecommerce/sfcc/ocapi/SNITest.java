package com.coremedia.livecontext.ecommerce.sfcc.ocapi;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.HttpClientFactory;
import org.apache.http.client.HttpClient;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;


public class SNITest {

  @Test
  @Ignore("manual test run to verify SSL SNI with our HttpClient and Spring RestTemplate")
  public void testSNI() {
    HttpClient client = HttpClientFactory.createHttpClient(true);

    HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(client);
    RestTemplate restTemplate = new RestTemplate(requestFactory);
    ResponseEntity<HashMap> result = restTemplate.exchange("https://check-tls.akamaized.net/v1/tlssni.json", HttpMethod.GET, null, HashMap.class);
    Object tls_sni_status = result.getBody().get("tls_sni_status");
    assertThat(tls_sni_status).isEqualTo("present");

    Object tlsVersion = result.getBody().get("tls_version");
    assertThat(tlsVersion).isEqualTo("tls1.2");
  }

}
