package com.coremedia.blueprint.caas.augmentation.model;

import com.coremedia.blueprint.caas.augmentation.CommerceRefHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.livecontext.ecommerce.asset.AssetService;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.util.Collections;
import java.util.List;

@DefaultAnnotation(NonNull.class)
public class AssetFacade {

  private final AssetService assetService;
  private final CommerceRefHelper commerceRefHelper;

  public AssetFacade(AssetService assetService, CommerceRefHelper commerceRefHelper) {
    this.assetService = assetService;
    this.commerceRefHelper = commerceRefHelper;
  }

  @Nullable
  public Content getPicture(CommerceRef commerceRef) {
    return getPictures(commerceRef).stream().findFirst().orElse(null);
  }

  @NonNull
  public List<Content> getPictures(CommerceRef commerceRef) {
    return commerceRefHelper.getCommerceId(commerceRef)
            .map(commerceId -> assetService.findPictures(commerceId, false, commerceRef.getSiteId()))
            .orElse(Collections.emptyList());
  }

  @NonNull
  public List<Content> getVisuals(CommerceRef commerceRef) {
    return commerceRefHelper.getCommerceId(commerceRef)
            .map(commerceId -> assetService.findVisuals(commerceId, false, commerceRef.getSiteId()))
            .orElse(Collections.emptyList());
  }

  @NonNull
  public List<Content> getDownloads(CommerceRef commerceRef) {
    return commerceRefHelper.getCommerceId(commerceRef)
            .map(commereId -> assetService.findDownloads(commereId, commerceRef.getSiteId()))
            .orElse(Collections.emptyList());
  }

}
