package com.coremedia.livecontext.ecommerce.sfcc.ocapi.meta;

import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.AbstractOCAPIConnector;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.AccessToken;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.OAuthConnector;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.SfccOcapiConfigurationProperties;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

/**
 * Connector for Salesforce Commerce Cloud Open Commerce Metadata API.
 */
@Service("sfccMetaApiConnector")
public class OCMetaApiConnector extends AbstractOCAPIConnector {

  private static final String AUTHORIZATION_HEADER = "Authorization";

  private OAuthConnector oAuthConnector;

  OCMetaApiConnector(@NonNull SfccOcapiConfigurationProperties properties) {
    // META API does not use api version
    super(properties, properties.getMetaBasePath(), null);
  }

  @NonNull
  @Override
  protected HttpHeaders buildHttpHeaders(StoreContext storeContext) {
    HttpHeaders headers = super.buildHttpHeaders(storeContext);

    AccessToken token = oAuthConnector.getOrRequestAccessToken(storeContext)
            .orElseThrow(() -> new IllegalStateException("No access token available."));

    headers.add(AUTHORIZATION_HEADER, token.toHttpHeaderValue());

    return headers;
  }

  @Autowired
  public void setOAuthConnector(OAuthConnector oAuthConnector) {
    this.oAuthConnector = oAuthConnector;
  }
}
