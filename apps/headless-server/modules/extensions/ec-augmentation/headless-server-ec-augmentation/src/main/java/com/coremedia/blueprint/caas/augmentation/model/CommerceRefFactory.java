package com.coremedia.blueprint.caas.augmentation.model;

import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanType;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@DefaultAnnotation(NonNull.class)
public class CommerceRefFactory {

  private static final String INTERNAL_LINK_DEFAULT = "#";
  public static final String CATALOG = "catalog";

  private final String externalId;
  private final CommerceBeanType type;
  private final String catalogId;
  private final String storeId;
  private final String locale;
  private final String siteId;
  private final String internalLink;
  private final List<String> breadcrumb;
  private final String catalogAlias;

  private CommerceRefFactory(String externalId, CommerceBeanType type, String catalogId, String catalogAlias, String storeId, String locale, String siteId, @Nullable String internalLink, List<String> breadcrumb) {
    this.externalId = externalId;
    this.type = type;
    this.catalogId = catalogId;
    this.storeId = storeId;
    this.locale = locale;
    this.siteId = siteId;
    this.internalLink = internalLink != null ? internalLink : INTERNAL_LINK_DEFAULT;
    this.breadcrumb = breadcrumb;
    this.catalogAlias = catalogAlias;
  }

  public static CommerceRef from(String externalId, CommerceBeanType commerceBeanType, StoreContext storeContext) {
    return new CommerceRefFactory(
            externalId,
            commerceBeanType,
            storeContext.getCatalogId().map(CatalogId::value).orElse(CATALOG),
            storeContext.getCatalogAlias().value(),
            storeContext.getStoreId(),
            storeContext.getLocale().toLanguageTag(),
            storeContext.getSiteId(),
            INTERNAL_LINK_DEFAULT,
            List.of()
    ).build();
  }

  public static CommerceRef from(CommerceId commerceId,
                                 CatalogId catalogId, String storeId, Locale locale, String siteId, List<String> breadcrumb){
    return new CommerceRefFactory(
            commerceId.getExternalId().orElseThrow(),
            commerceId.getCommerceBeanType(),
            catalogId.value(),
            commerceId.getCatalogAlias().value(),
            storeId,
            locale.toLanguageTag(),
            siteId,
            INTERNAL_LINK_DEFAULT,
            breadcrumb
    ).build();
  }

  public static CommerceRef from(CommerceId commerceId, CatalogId catalogId, String storeId, Locale locale, String siteId) {
    return from(commerceId, catalogId, storeId, locale, siteId, List.of());
  }

  public static Optional<CommerceRef> from(CommerceId commerceId, CatalogId catalogId, String storeId, Site site, List<String> breadcrumb) {
    Optional<String> externalId = commerceId.getExternalId();
    if (externalId.isEmpty()) {
      return Optional.empty();
    }

    return Optional.of(
            new CommerceRefFactory(
                    externalId.get(),
                    commerceId.getCommerceBeanType(),
                    catalogId.value(),
                    commerceId.getCatalogAlias().value(),
                    storeId,
                    site.getLocale().toLanguageTag(),
                    site.getId(),
                    INTERNAL_LINK_DEFAULT,
                    breadcrumb
            ).build());
  }

  public static Optional<CommerceRef> from(CommerceBean commerceBean, Site site) {
    StoreContext context = commerceBean.getContext();
    CatalogId catalogId = context.getCatalogId().orElse(CatalogId.of("catalog"));
    return from(commerceBean.getId(), catalogId, context.getStoreId(), site, List.of());
  }

  private CommerceRef build(){
    return new CommerceRef(externalId, type, catalogId, storeId, locale, siteId, internalLink, breadcrumb, catalogAlias);
  }
}
