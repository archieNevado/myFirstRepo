package com.coremedia.blueprint.caas.augmentation;

import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdBuilder;
import com.coremedia.blueprint.caas.augmentation.model.CommerceRef;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.Vendor;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.Optional;

@DefaultAnnotation(NonNull.class)
public class CommerceRefHelper {

  private static final String CATALOG = "catalog";

  private final SitesService sitesService;
  private final CommerceSettingsHelper commerceSettingsHelper;

  public CommerceRefHelper(SitesService sitesService, CommerceSettingsHelper commerceSettingsHelper) {
    this.sitesService = sitesService;
    this.commerceSettingsHelper = commerceSettingsHelper;
  }

  public Optional<CommerceId> getCommerceId(CommerceRef commerceRef) {
    return sitesService.findSite(commerceRef.getSiteId())
            .map(commerceSettingsHelper::getVendor)
            .map(vendor -> toCommerceId(commerceRef, vendor));
  }

  public static CommerceId toCommerceId(CommerceRef commerceRef, Vendor vendor) {
    return CommerceIdBuilder.builder(vendor, CATALOG, commerceRef.getType())
            .withExternalId(commerceRef.getExternalId())
            .withCatalogAlias(commerceRef.getCatalogAlias())
            .build();
  }
}
