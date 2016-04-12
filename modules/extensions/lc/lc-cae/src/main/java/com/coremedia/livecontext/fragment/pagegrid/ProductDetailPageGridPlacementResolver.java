package com.coremedia.livecontext.fragment.pagegrid;

import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.blueprint.common.layout.PageGridPlacement;
import com.coremedia.livecontext.contentbeans.CMExternalChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Resolve Placements for Product Detail Pages
 */
public class ProductDetailPageGridPlacementResolver implements PageGridPlacementResolver {
  private static final Logger LOG = LoggerFactory.getLogger(ProductDetailPageGridPlacementResolver.class);

  @Nullable
  @Override
  public PageGridPlacement resolvePageGridPlacement(@Nonnull CMChannel context, @Nonnull String placementName) {
    if (context instanceof CMExternalChannel){
      return resolvePlacement((CMExternalChannel) context, placementName);
    }

    return null;//not applicable
  }


  @Nullable
  protected PageGridPlacement resolvePlacement(CMExternalChannel externalChannel, String placementName) {
    try {
      return externalChannel.getPdpPagegrid().getPlacementForName(placementName);
    }
    catch (Exception e) {
      LOG.error("Error when resolving placement '" + placementName + "' of page grid", e);
      return null;
    }
  }

}
