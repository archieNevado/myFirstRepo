package com.coremedia.blueprint.caas.augmentation.model;

import com.coremedia.livecontext.ecommerce.common.CommerceBeanType;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.List;
import java.util.Locale;

import static com.coremedia.blueprint.base.livecontext.ecommerce.common.CatalogAliasTranslationService.DEFAULT_CATALOG_ALIAS;

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
  private final String catalogAlias;

  CommerceRef(String externalId, CommerceBeanType type, String catalogId, String storeId, String locale, String siteId, String internalLink, List<String> breadcrumb, String catalogAlias) {
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

  public CommerceRef(String externalId, String type, String storeId, Locale locale, String siteId, List<String> breadcrumb) {
    this(externalId, CommerceBeanType.of(type), "catalog", storeId, locale.toLanguageTag(), siteId, "dummy", breadcrumb, DEFAULT_CATALOG_ALIAS.value());
  }

  public CommerceRef(String externalId, String type, String storeId, Locale locale, String siteId) {
    this(externalId, CommerceBeanType.of(type), "catalog", storeId, locale.toLanguageTag(), siteId, "dummy", List.of(), DEFAULT_CATALOG_ALIAS.value());
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

  public String getCatalogAlias() {
    return catalogAlias;
  }
}
