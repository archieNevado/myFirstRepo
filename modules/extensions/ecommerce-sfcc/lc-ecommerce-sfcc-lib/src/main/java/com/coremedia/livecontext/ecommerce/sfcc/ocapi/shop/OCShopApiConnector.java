package com.coremedia.livecontext.ecommerce.sfcc.ocapi.shop;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommercePropertyHelper;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.AbstractOCAPIConnector;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.SfccOcapiConfigurationProperties;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Connector for Salesforce Commerce Cloud Open Commerce Shop API.
 */
@Service("sfccShopApiConnector")
public class OCShopApiConnector extends AbstractOCAPIConnector {

  public static final String STORE_ID_PARAM = "storeId";
  private static final String CLIENT_ID_PARAM = "client_id";

  private String clientId;

  OCShopApiConnector(@NonNull SfccOcapiConfigurationProperties properties) {
    super(properties, properties.getShopBasePath(), properties.getVersion());
  }

  /**
   * resolve required client id
   */
  private String getClientId() {
    return CommercePropertyHelper.replaceTokens(clientId,
            CurrentCommerceConnection.find().map(CommerceConnection::getStoreContext)
                    .orElseThrow(() -> new IllegalStateException("Client ID missing. Is 'livecontext.sfcc.oauth.clientId'" +
                            " configured?")));
  }

  @Value("${livecontext.sfcc.oauth.clientId}")
  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  @NonNull
  @Override
  protected ListMultimap<String, String> getDefaultQueryParams() {
    return ImmutableListMultimap.of(CLIENT_ID_PARAM, getClientId());
  }
}
