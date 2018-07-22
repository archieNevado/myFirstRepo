package com.coremedia.livecontext.ecommerce.sfcc.asset;

import com.coremedia.livecontext.ecommerce.asset.AssetUrlProvider;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * {@link AssetUrlProvider} for Salesforce Commerce Cloud Digital assets.
 */
public class AssetUrlProviderImpl implements AssetUrlProvider {

  @Nullable
  @Override
  public String getImageUrl(@NonNull String imageSegment) {
    return imageSegment;
  }

  @Nullable
  @Override
  public String getImageUrl(@NonNull String imageSegment, boolean prependCatalogPath) {
    return getImageUrl(imageSegment);
  }

  @Override
  public void setCommercePreviewUrl(@NonNull String commercePreviewHost) {
    // unused
  }

  @Override
  public void setCommerceProductionUrl(@NonNull String commerceProductionHost) {
    // unused
  }

  @Override
  public void setCatalogPathPrefix(String catalogPathPrefix) {
    // unused
  }

}
