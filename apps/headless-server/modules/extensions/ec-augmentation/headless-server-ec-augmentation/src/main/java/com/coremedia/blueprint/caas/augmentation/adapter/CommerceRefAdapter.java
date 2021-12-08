package com.coremedia.blueprint.caas.augmentation.adapter;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CatalogAliasTranslationService;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdParserHelper;
import com.coremedia.blueprint.caas.augmentation.CommerceEntityHelper;
import com.coremedia.blueprint.caas.augmentation.model.CommerceRef;
import com.coremedia.blueprint.caas.augmentation.model.CommerceRefFactory;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static java.lang.invoke.MethodHandles.lookup;

@DefaultAnnotation(NonNull.class)
public class CommerceRefAdapter {
  private static final Logger LOG = LoggerFactory.getLogger(lookup().lookupClass());

  private final SitesService sitesService;
  private final CommerceEntityHelper commerceEntityHelper;
  private final CatalogAliasTranslationService catalogAliasTranslationService;

  public CommerceRefAdapter(SitesService sitesService, CommerceEntityHelper commerceEntityHelper, CatalogAliasTranslationService catalogAliasTranslationService) {
    this.sitesService = sitesService;
    this.commerceEntityHelper = commerceEntityHelper;
    this.catalogAliasTranslationService = catalogAliasTranslationService;
  }

  @SuppressWarnings("unused")
  // it is being used by within commerce-reference-schema.graphql as @fetch(from: "@commerceRefAdapter.getCommerceRef(#this)")
  @Nullable
  public CommerceRef getCommerceRef(Content content, String externalReferencePropertyName){
    LOG.debug("Loading commerce reference data from commerce adapter for content with id {}", content.getId());
    String commerceIdStr = content.getString(externalReferencePropertyName);

    CommerceId commerceId = CommerceIdParserHelper.parseCommerceId(commerceIdStr).orElse(null);
    if (commerceId == null || commerceId.getExternalId().isEmpty()){
      LOG.debug("externalId is null for {}", content.getId());
      return null;
    }

    Site site = sitesService.getContentSiteAspect(content).getSite();
    if (site == null) {
      LOG.debug("no site for {} {}", content.getId(), commerceIdStr);
      return null;
    }

    CommerceConnection commerceConnection = commerceEntityHelper.getCommerceConnection(site.getId());
    if (commerceConnection == null){
      LOG.debug("commerceConnection is null for {} {}", content.getId(), commerceIdStr);
      return null;
    }

    StoreContext storeContext = commerceConnection.getInitialStoreContext();
    CatalogId catalogId = catalogAliasTranslationService.getCatalogIdForAlias(commerceId.getCatalogAlias(), storeContext)
            .orElse(null);

    return CommerceRefFactory.from(commerceId, catalogId, storeContext.getStoreId(), site, List.of())
            .orElse(null);
  }
}
