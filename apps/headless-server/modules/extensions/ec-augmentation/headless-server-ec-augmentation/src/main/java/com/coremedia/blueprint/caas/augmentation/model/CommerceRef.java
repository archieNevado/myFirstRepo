package com.coremedia.blueprint.caas.augmentation.model;

import com.coremedia.livecontext.ecommerce.common.CommerceBeanType;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanTypeRegistry;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.Locale;

@DefaultAnnotation(NonNull.class)
public class CommerceRef {

  private final String externalId;
  private final CommerceBeanType type;
  private final String catalogId;
  private final String storeId;
  private final String locale;
  private final String siteId;
  private final String internalLink;

  public CommerceRef(String externalId, CommerceBeanType type, String catalogId, String storeId, String locale, String siteId, String internalLink) {
    this.externalId = externalId;
    this.type = type;
    this.catalogId = catalogId;
    this.storeId = storeId;
    this.locale = locale;
    this.siteId = siteId;
    this.internalLink = internalLink;
  }

  public CommerceRef(String externalId, String type, String storeId, Locale locale, String siteId) {
    this(externalId, CommerceBeanTypeRegistry.valueOf(type), "catalog", storeId, locale.toLanguageTag(), siteId, "dummy");
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
}
