package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.AbstractOCAPIConnector;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.AccessToken;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.OAuthConnector;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.SfccOcapiConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Connector for Salesforce Commerce Cloud Open Commerce Data API.
 */
@Service("sfccDataApiConnector")
public class OCDataApiConnector extends AbstractOCAPIConnector {

  private static final String AUTHORIZATION_HEADER = "Authorization";

  private OAuthConnector oAuthConnector;

  OCDataApiConnector(@NonNull SfccOcapiConfigurationProperties properties) {
    super(properties, properties.getDataBasePath(), properties.getVersion());
  }

  @NonNull
  @Override
  protected HttpHeaders buildHttpHeaders() {
    HttpHeaders headers = super.buildHttpHeaders();

    AccessToken token = oAuthConnector.getOrRequestAccessToken()
            .orElseThrow(() -> new IllegalStateException("No access token available."));

    headers.add(AUTHORIZATION_HEADER, token.toHttpHeaderValue());

    return headers;
  }

  @Autowired
  public void setOAuthConnector(OAuthConnector oAuthConnector) {
    this.oAuthConnector = oAuthConnector;
  }

}
