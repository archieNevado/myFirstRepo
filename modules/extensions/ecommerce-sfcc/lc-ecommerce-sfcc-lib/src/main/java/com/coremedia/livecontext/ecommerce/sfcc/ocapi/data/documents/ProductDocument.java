package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.AbstractOCDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.LocalizedBooleanDeserializer;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.LocalizedMarkupTextDeserializer;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.LocalizedStringDeserializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Document representing a product.
 */
public class ProductDocument extends AbstractOCDocument {

  /**
   * The ATS(Available To Sell) inventory value of the product.
   * This is a calculated value.
   */
  @JsonProperty("ats")
  private BigDecimal ats;

  /**
   * The brand of the product.
   */
  @JsonProperty("brand")
  private String brand;

  /**
   * Returns the value of attribute 'creationDate'.
   */
  @JsonProperty("creation_date")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATA_API_DATE_PATTERN)
  private Date creationDate;

  /**
   * The ID of the product's default variant.
   */
  @JsonProperty("default_variant_id")
  private String defaultVariantId;

  /**
   * The European Article Number of the product.
   */
  @JsonProperty("ean")
  private String ean;

  /**
   * The ID (SKU) of the product.
   */
  @JsonProperty("id")
  private String id;

  /**
   * The image(s) assigned to the product.
   */
  @JsonProperty("image")
  private MediaFileDocument image;

  /**
   * The flag that indicates if the product is in stock, or not.
   * This is a calculated value.
   */
  @JsonProperty("in_stock")
  private boolean inStock;

  /**
   * Returns the value of attribute 'lastModified'.
   */
  @JsonProperty("last_modified")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATA_API_DATE_PATTERN)
  private Date lastModified;

  /**
   * A link to the product.
   */
  @JsonProperty("link")
  private String link;

  /**
   * The localized long description of the product.
   */
  @JsonProperty("long_description")
  @JsonDeserialize(using = LocalizedMarkupTextDeserializer.class)
  private LocalizedProperty<MarkupTextDocument> longDescription;

  /**
   * The name of the product's manufacturer.
   */
  @JsonProperty("manufacturer_name")
  private String manufacturerName;

  /**
   * The SKU of the product's manufacturer.
   */
  @JsonProperty("manufacturer_sku")
  private String manufacturerSku;

  /**
   * The master of the product.
   * This is applicable for product types "variation_group" and "variant" only.
   */
  @JsonProperty("master")
  private MasterDocument master;

  /**
   * The localized name of the product.
   */
  @JsonProperty("name")
  @JsonDeserialize(using = LocalizedStringDeserializer.class)
  private LocalizedProperty<String> name;


  /**
   * The flag that indicates if the product is online, or not.
   * This is a calculated value.
   */
  @JsonProperty("online")
  @JsonDeserialize(using = LocalizedBooleanDeserializer.class)
  private LocalizedProperty<Boolean>/*SiteSpecificProperty<Boolean>*/ online;

//  /**
//   * The site specific online status of the product.
//   */
//  private SiteSpecificProperty<Boolean> online_flag;

  /**
   * The ID of the catalog that owns the product.
   */
  @JsonProperty("owning_catalog_id")
  private String owningCatalogId;

  /**
   * The localized name of the catalog that owns the product.
   */
  private LocalizedProperty<String> owning_catalog_name;

  /**
   * The localized page description of the product.
   */
  @JsonProperty("page_description")
  @JsonDeserialize(using = LocalizedStringDeserializer.class)
  private LocalizedProperty<String> pageDescription;

  /**
   * The localized page keywords of the product.
   */
  @JsonProperty("page_keywords")
  @JsonDeserialize(using = LocalizedStringDeserializer.class)
  private LocalizedProperty<String> pageKeywords;

  /**
   * The localized page title of the product.
   */
  @JsonProperty("page_title")
  @JsonDeserialize(using = LocalizedStringDeserializer.class)
  private LocalizedProperty<String> pageTitle;

  /**
   * The price of the product.
   */
  @JsonProperty("price")
  private BigDecimal price;

  /**
   * The currency code for product's price.
   */
  @JsonProperty("price_currency")
  private Currency currency;

  /**
   * The id of the products primary category.
   */
  @JsonProperty("primary_category_id")
  private String primaryCategoryId;

  /**
   * The site specific searchable status of the product.
   */
  @JsonProperty("searchable")
  @JsonDeserialize(using = LocalizedBooleanDeserializer.class)
  private LocalizedProperty<Boolean> searchable;

  /**
   * The localized short description of the product.
   */
  @JsonProperty("short_description")
  @JsonDeserialize(using = LocalizedMarkupTextDeserializer.class)
  private LocalizedProperty<MarkupTextDocument> shortDescription;

  /**
   * The type of the product.
   * It can have one or more of the values:
   * <ul>
   * <li>item</li>
   * <li>master</li>
   * <li>variation_group</li>
   * <li>variant</li>
   * <li>bundle</li>
   * <li>set</li>
   * </ul>
   */
  @JsonProperty("type")
  private ProductTypeDocument type;

  /**
   * The sales unit of the product.
   */
  @JsonProperty("unit")
  private String unit;

  /**
   * The Universal Product Code of the product.
   */
  @JsonProperty("upc")
  private String upc;

  /**
   * The array of variants of the product.
   * This is applicable for product types "master" and "variation_group" only.
   */
  @JsonProperty("variants")
  private List<VariantDocument> variants;

  /**
   * The sorted array of variation attributes assigned to the product.
   * This is applicable for product types "master", "variation_group" and "variant" only.
   */
  @JsonProperty("variation_attributes")
  private List<VariationAttributeDocument> variationAttributes;

  /**
   * The array of variation groups in the product.
   * This is applicable for product type "master" only.
   */
  @JsonProperty("variation_groups")
  private List<VariationGroupDocument> variationGroups;

  /**
   * The variation values selected for the product in variation attribute id and value pairs.
   * This is applicable for product types "variant" and "variation_group" only.
   */
  @JsonProperty("variation_values")
  private Map<String, String> variationValues;

  public BigDecimal getAts() {
    return ats;
  }

  public void setAts(BigDecimal ats) {
    this.ats = ats;
  }

  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  public Date getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  public String getDefaultVariantId() {
    return defaultVariantId;
  }

  public void setDefaultVariantId(String defaultVariantId) {
    this.defaultVariantId = defaultVariantId;
  }

  public String getEan() {
    return ean;
  }

  public void setEan(String ean) {
    this.ean = ean;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public void setId(String id) {
    this.id = id;
  }

  public MediaFileDocument getImage() {
    return image;
  }

  public void setImage(MediaFileDocument image) {
    this.image = image;
  }

  public boolean isInStock() {
    return inStock;
  }

  public void setInStock(boolean inStock) {
    this.inStock = inStock;
  }

  public Date getLastModified() {
    return lastModified;
  }

  public void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }

  public String getLink() {
    return link;
  }

  public void setLink(String link) {
    this.link = link;
  }

  public LocalizedProperty<MarkupTextDocument> getLongDescription() {
    return longDescription;
  }

  public void setLongDescription(LocalizedProperty<MarkupTextDocument> longDescription) {
    this.longDescription = longDescription;
  }

  public String getManufacturerName() {
    return manufacturerName;
  }

  public void setManufacturerName(String manufacturerName) {
    this.manufacturerName = manufacturerName;
  }

  public String getManufacturerSku() {
    return manufacturerSku;
  }

  public void setManufacturerSku(String manufacturerSku) {
    this.manufacturerSku = manufacturerSku;
  }

  public MasterDocument getMaster() {
    return master;
  }

  public void setMaster(MasterDocument master) {
    this.master = master;
  }

  public LocalizedProperty<String> getName() {
    return name;
  }

  public void setName(LocalizedProperty<String> name) {
    this.name = name;
  }

  public LocalizedProperty<Boolean> getOnline() {
    return online;
  }

  public void setOnline(LocalizedProperty<Boolean> online) {
    this.online = online;
  }

  public String getOwningCatalogId() {
    return owningCatalogId;
  }

  public void setOwningCatalogId(String owningCatalogId) {
    this.owningCatalogId = owningCatalogId;
  }

  public LocalizedProperty<String> getOwning_catalog_name() {
    return owning_catalog_name;
  }

  public void setOwning_catalog_name(LocalizedProperty<String> owning_catalog_name) {
    this.owning_catalog_name = owning_catalog_name;
  }

  public LocalizedProperty<String> getPageDescription() {
    return pageDescription;
  }

  public void setPageDescription(LocalizedProperty<String> pageDescription) {
    this.pageDescription = pageDescription;
  }

  public LocalizedProperty<String> getPageKeywords() {
    return pageKeywords;
  }

  public void setPageKeywords(LocalizedProperty<String> pageKeywords) {
    this.pageKeywords = pageKeywords;
  }

  public LocalizedProperty<String> getPageTitle() {
    return pageTitle;
  }

  public void setPageTitle(LocalizedProperty<String> pageTitle) {
    this.pageTitle = pageTitle;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public Currency getCurrency() {
    return currency;
  }

  public void setCurrency(Currency currency) {
    this.currency = currency;
  }

  public String getPrimaryCategoryId() {
    return primaryCategoryId;
  }

  public void setPrimaryCategoryId(String primaryCategoryId) {
    this.primaryCategoryId = primaryCategoryId;
  }

  public LocalizedProperty<Boolean> getSearchable() {
    return searchable;
  }

  public void setSearchable(LocalizedProperty<Boolean> searchable) {
    this.searchable = searchable;
  }

  public LocalizedProperty<MarkupTextDocument> getShortDescription() {
    return shortDescription;
  }

  public void setShortDescription(LocalizedProperty<MarkupTextDocument> shortDescription) {
    this.shortDescription = shortDescription;
  }

  public ProductTypeDocument getType() {
    return type;
  }

  public void setType(ProductTypeDocument type) {
    this.type = type;
  }

  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public String getUpc() {
    return upc;
  }

  public void setUpc(String upc) {
    this.upc = upc;
  }

  public List<VariantDocument> getVariants() {
    return variants;
  }

  public void setVariants(List<VariantDocument> variants) {
    this.variants = variants;
  }

  public List<VariationAttributeDocument> getVariationAttributes() {
    return variationAttributes;
  }

  public void setVariationAttributes(List<VariationAttributeDocument> variationAttributes) {
    this.variationAttributes = variationAttributes;
  }

  public List<VariationGroupDocument> getVariationGroups() {
    return variationGroups;
  }

  public void setVariationGroups(List<VariationGroupDocument> variationGroups) {
    this.variationGroups = variationGroups;
  }

  public Map<String, String> getVariationValues() {
    return variationValues;
  }

  public void setVariationValues(Map<String, String> variationValues) {
    this.variationValues = variationValues;
  }
}
