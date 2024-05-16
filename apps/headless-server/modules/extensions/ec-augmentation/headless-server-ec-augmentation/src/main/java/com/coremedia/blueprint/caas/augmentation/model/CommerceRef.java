package com.coremedia.blueprint.caas.augmentation.model;

import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdParserHelper;
import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanType;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.List;

@DefaultAnnotation(NonNull.class)
public class CommerceRef {

  private final String externalId;
  private final CommerceBeanType type;
  private final String catalogId;
  private final String storeId;
  private final String locale;
  private final String siteId;
  private final String internalLink;
  private final List<String> breadcrumb;
  private final CatalogAlias catalogAlias;

  CommerceRef(String externalId, CommerceBeanType type, String catalogId, String storeId, String locale, String siteId, String internalLink, List<String> breadcrumb, CatalogAlias catalogAlias) {
    validateExternalId(externalId);
    this.externalId = externalId;
    this.type = type;
    this.catalogId = catalogId;
    this.storeId = storeId;
    this.locale = locale;
    this.siteId = siteId;
    this.internalLink = internalLink;
    this.breadcrumb = breadcrumb;
    this.catalogAlias = catalogAlias;
  }

  private static void validateExternalId(String externalId) {
    if (CommerceIdParserHelper.parseCommerceId(externalId).isPresent()) {
      // only accept real externalIds - reject commerce IDs.
      throw new IllegalArgumentException("The given externalId must not be a fully formatted commerce ID: " + externalId);
    }
  }

  public String getExternalId() {
    return externalId;
  }

  public String getCatalogId() {
    return catalogId;
  }

  public String getStoreId() {
    return storeId;
  }

  public String getLocale() {
    return locale;
  }

  public String getSiteId() {
    return siteId;
  }

  public String getInternalLink() {
    return internalLink;
  }

  public CommerceBeanType getType() {
    return type;
  }

  public String getId(){
    return getSiteId() + ":" + getExternalId();
  }

  public List<String> getBreadcrumb() {
    return breadcrumb;
  }

  public CatalogAlias getCatalogAlias() {
    return catalogAlias;
  }
}
