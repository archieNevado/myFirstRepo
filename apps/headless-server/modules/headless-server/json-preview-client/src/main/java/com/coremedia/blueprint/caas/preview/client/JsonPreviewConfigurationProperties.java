package com.coremedia.blueprint.caas.preview.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "previewclient")
public class JsonPreviewConfigurationProperties {
  /**
   * Caas server endpoint for the JSON Preview Client
   */
  @Value("${caasserver.endpoint:http://localhost:41180/graphql}")
  private String caasserverEndpoint = "http://localhost:41180/graphql";

  /**
   * URL to the JSON Preview Client
   */
  private String url = "http://localhost:41180";

  private List<String> forwardHeaderNames = new ArrayList<>();
  private boolean forwardCookies;

  public String getCaasserverEndpoint() {
    return caasserverEndpoint;
  }

  public void setCaasserverEndpoint(String caasserverEndpoint) {
    this.caasserverEndpoint = caasserverEndpoint;
  }

  public List<String> getForwardHeaderNames() {
    return forwardHeaderNames;
  }

  public void setForwardHeaderNames(List<String> forwardHeaderNames) {
    this.forwardHeaderNames = forwardHeaderNames;
  }

  public boolean isForwardCookies() {
    return forwardCookies;
  }

  public void setForwardCookies(boolean forwardCookies) {
    this.forwardCookies = forwardCookies;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }
}
