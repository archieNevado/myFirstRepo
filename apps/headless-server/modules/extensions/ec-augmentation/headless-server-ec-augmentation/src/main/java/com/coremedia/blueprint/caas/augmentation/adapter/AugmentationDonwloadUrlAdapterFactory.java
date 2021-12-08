package com.coremedia.blueprint.caas.augmentation.adapter;

import com.coremedia.blueprint.caas.augmentation.CommerceEntityHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.SitesService;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;

@DefaultAnnotation(NonNull.class)
public class AugmentationDonwloadUrlAdapterFactory {

  private final CommerceEntityHelper commerceEntityHelper;
  private final SitesService sitesService;

  public AugmentationDonwloadUrlAdapterFactory(CommerceEntityHelper commerceEntityHelper, SitesService sitesService) {
    this.commerceEntityHelper = commerceEntityHelper;
    this.sitesService = sitesService;
  }

  public AugmentationDonwloadUrlAdapter to(Content content){
    return new AugmentationDonwloadUrlAdapter(content);
  }
}
