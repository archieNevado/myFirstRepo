package com.coremedia.livecontext.ecommerce.sfcc.cae;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommercePropertyHelper;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.objectserver.web.links.TokenResolverHelper;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 * Provider of formatted not encoded Salesforce Commerce Cloud URLs.
 */
class SfccCommerceUrlProvider {

  private final String storeFrontBaseUrl;

  SfccCommerceUrlProvider(@Nonnull String storeFrontBaseUrl) {
    this.storeFrontBaseUrl = storeFrontBaseUrl;
  }

  @Nonnull
  UriComponentsBuilder provideValue(@Nonnull String urlTemplate, @Nonnull Map<String, Object> parameters, @Nullable StoreContext storeContext) {
    String resultUrl = storeFrontBaseUrl + urlTemplate;

    // Apply StoreContext values
    resultUrl = CommercePropertyHelper.replaceTokens(resultUrl, storeContext);

    // Apply other values
    resultUrl = TokenResolverHelper.replaceTokens(resultUrl, parameters, false, false);

    return UriComponentsBuilder.fromUriString(resultUrl);
  }

}
