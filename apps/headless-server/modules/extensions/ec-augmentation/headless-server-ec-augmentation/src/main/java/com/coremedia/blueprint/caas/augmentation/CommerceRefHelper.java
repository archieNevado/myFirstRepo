package com.coremedia.blueprint.caas.augmentation;

import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdBuilder;
import com.coremedia.blueprint.caas.augmentation.model.CommerceRef;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.Vendor;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.invoke.MethodHandles.lookup;

@DefaultAnnotation(NonNull.class)
public class CommerceRefHelper {

  private static final Logger LOG = LoggerFactory.getLogger(lookup().lookupClass());
  private static final String CATALOG = "catalog";

  private final SitesService sitesService;
  private final CommerceSettingsHelper commerceSettingsHelper;

  public CommerceRefHelper(SitesService sitesService, CommerceSettingsHelper commerceSettingsHelper) {
    this.sitesService = sitesService;
    this.commerceSettingsHelper = commerceSettingsHelper;
  }

  public CommerceId getCommerceId(CommerceRef commerceRef) {
    Site site = sitesService.getSite(commerceRef.getSiteId());
    String erroMsg = "Could not create commerce id from ";
    if (site == null) {
      throw new IllegalArgumentException(erroMsg + commerceRef);
    }

    return CommerceIdBuilder.builder(Vendor.of(commerceSettingsHelper.getVendor(site)), CATALOG, commerceRef.getType())
            .withExternalId(commerceRef.getExternalId())
            .withCatalogAlias(CatalogAlias.of(commerceRef.getCatalogAlias()))
            .build();
  }
}
