package com.coremedia.livecontext.ecommerce.sfcc.ocapi.shop;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommercePropertyHelper;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.AbstractOCAPIConnector;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.SfccOcapiConfigurationProperties;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

  @NonNull
  @Override
  protected ListMultimap<String, String> getDefaultQueryParams(StoreContext storeContext) {
    return ImmutableListMultimap.of(CLIENT_ID_PARAM, getClientId(storeContext));
  }

  /**
   * resolve required client id
   */
  @NonNull
  private String getClientId(StoreContext storeContext) {
    return CommercePropertyHelper.replaceTokens(clientId, storeContext);
  }

  @Value("${livecontext.sfcc.oauth.clientId}")
  public void setClientId(String clientId) {
    this.clientId = clientId;
  }
}
