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

  private static CommerceRef from(String externalId, CommerceBeanType commerceBeanType, StoreContext storeContext) {
    var catalogId = storeContext.getCatalogId().map(CatalogId::value).orElse(CATALOG);
    var storeId = storeContext.getStoreId();
    var locale = storeContext.getLocale().toLanguageTag();
    var siteId = storeContext.getSiteId();
    return new CommerceRef(externalId, commerceBeanType, catalogId, storeId, locale, siteId, INTERNAL_LINK_DEFAULT,
            List.of(), storeContext.getCatalogAlias());
  }

  public static CommerceRef from(CommerceId commerceId,
                                 CatalogId catalogId, String storeId, Locale locale, String siteId, List<String> breadcrumb){
    var externalId = commerceId.getExternalId().orElseThrow();
    return new CommerceRef(externalId, commerceId.getCommerceBeanType(), catalogId.value(), storeId, locale.toLanguageTag(),
            siteId, INTERNAL_LINK_DEFAULT, breadcrumb, commerceId.getCatalogAlias());
  }

  public static Optional<CommerceRef> from(CommerceId commerceId, @Nullable CatalogId catalogId, @Nullable String storeId,
                                           Site site, List<String> breadcrumb) {
    return commerceId.getExternalId().map(externalId -> {
      var commerceBeanType = commerceId.getCommerceBeanType();
      var locale = site.getLocale().toLanguageTag();
      var siteId = site.getId();
      var catalogIdValue = catalogId != null ? catalogId.value() : "";
      var storeIdNonNull = storeId != null ? storeId : "";
      return new CommerceRef(externalId, commerceBeanType, catalogIdValue, storeIdNonNull, locale, siteId,
              INTERNAL_LINK_DEFAULT, breadcrumb, commerceId.getCatalogAlias());
    });
  }

  public static Optional<CommerceRef> from(CommerceId commerceId, StoreContext storeContext) {
    return commerceId.getExternalId().map(externalId -> {
      var commerceBeanType = commerceId.getCommerceBeanType();
      return from(externalId, commerceBeanType, storeContext);
    });
  }

  public static CommerceRef from(CommerceBean commerceBean) {
    return from(commerceBean.getExternalId(), commerceBean.getId().getCommerceBeanType(), commerceBean.getContext());
  }

}
