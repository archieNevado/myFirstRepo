package com.coremedia.blueprint.caas.augmentation.tree;

import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.ecommerce.augmentation.AugmentationService;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.List;

@DefaultAnnotation(NonNull.class)
public class ExternalBreadcrumbContentTreeRelationFactory {
  private final AugmentationService augmentationService;
  private final SitesService sitesService;

  public ExternalBreadcrumbContentTreeRelationFactory(AugmentationService augmentationService,
                                                      SitesService sitesService) {
    this.augmentationService = augmentationService;
    this.sitesService = sitesService;
  }

  public ExternalBreadcrumbContentTreeRelation create(List<CommerceId> breadcrumb) {
    var treeRelation = new ExternalBreadcrumbTreeRelation(breadcrumb);
    return new ExternalBreadcrumbContentTreeRelation(augmentationService, treeRelation, sitesService);
  }

}
