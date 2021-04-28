package com.coremedia.blueprint.caas.augmentation.model;

import com.coremedia.blueprint.caas.augmentation.CommerceEntityHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.livecontext.ecommerce.asset.AssetService;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@DefaultAnnotation(NonNull.class)
public class AssetFacade {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final AssetService assetService;
  private final CommerceEntityHelper commerceEntityHelper;

  public AssetFacade(AssetService assetService, CommerceEntityHelper commerceEntityHelper) {
    this.assetService = assetService;
    this.commerceEntityHelper = commerceEntityHelper;
  }

  @Nullable
  public Content getPicture(CommerceRef commerceRef) {
    return getPictures(commerceRef).stream().findFirst().orElse(null);
  }

  @NonNull
  public List<Content> getPictures(CommerceRef commerceRef) {
    return createCommerceId(commerceRef)
            .map(commerceId -> assetService.findPictures(commerceId, false, commerceRef.getSiteId()))
            .orElse(Collections.emptyList());
  }

  @NonNull
  public List<Content> getVisuals(CommerceRef commerceRef) {
    return createCommerceId(commerceRef)
            .map(commerceId -> assetService.findVisuals(commerceId, false, commerceRef.getSiteId()))
            .orElse(Collections.emptyList());
  }

  @NonNull
  public List<Content> getDownloads(CommerceRef commerceRef) {
    return createCommerceId(commerceRef)
            .map(commereId -> assetService.findDownloads(commereId, commerceRef.getSiteId()))
            .orElse(Collections.emptyList());
  }

  private Optional<CommerceId> createCommerceId(CommerceRef commerceRef) {
    try {
      return Optional.of(commerceEntityHelper.getCommerceId(commerceRef));
    } catch (Exception e) {
      LOG.warn("Cannot create commerce id from {} ", commerceRef);
      return Optional.empty();
    }
  }
}
