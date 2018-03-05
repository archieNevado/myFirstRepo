package com.coremedia.livecontext.ecommerce.sfcc.asset;

import com.coremedia.livecontext.ecommerce.asset.AssetUrlProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * {@link AssetUrlProvider} for Salesforce Commerce Cloud Digital assets.
 */
public class AssetUrlProviderImpl implements AssetUrlProvider {

  @Nullable
  @Override
  public String getImageUrl(@Nonnull String imageSegment) {
    return imageSegment;
  }

  @Nullable
  @Override
  public String getImageUrl(@Nonnull String imageSegment, boolean prependCatalogPath) {
    return getImageUrl(imageSegment);
  }

  @Override
  public void setCommercePreviewUrl(@Nonnull String commercePreviewHost) {
    // unused
  }

  @Override
  public void setCommerceProductionUrl(@Nonnull String commerceProductionHost) {
    // unused
  }

  @Override
  public void setCatalogPathPrefix(String catalogPathPrefix) {
    // unused
  }

}
