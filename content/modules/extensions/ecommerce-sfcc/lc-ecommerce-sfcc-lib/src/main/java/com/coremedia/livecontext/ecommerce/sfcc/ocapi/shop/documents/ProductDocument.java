package com.coremedia.livecontext.ecommerce.sfcc.ocapi.shop.documents;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.AbstractOCDocument;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Document representing a product.
 */
public class ProductDocument extends AbstractOCDocument {

  /**
   * The product's brand.
   */
  @JsonProperty("brand")
  private String brand;

  /**
   * The array of all bundled products of this product.
   */
  //@JsonProperty("bundled_products")
  //private List<BundledProductDocument> bundledProducts;

  /**
   * The ISO 4217 mnemonic code of the currency.
   */
  @JsonProperty("currency")
  private String currency;

  /**
   * The European Article Number.
   */
  @JsonProperty("ean")
  private String ean;

  /**
   * The array of product image groups.
   */
  @JsonProperty("image_groups")
  private List<ImageGroupDocument> imageGroups;

  /**
   * The array of product inventories explicitly requested via 'inventory_ids' query parameter.
   * This property is private String only returned in context of the 'availability' expansion.
   */
  //@JsonProperty("inventories")
  //private List<InventoryDocument> inventories;

  /**
   * The site default inventory information. This property is only returned in context of the 'availability' expansion.
   */
  //@JsonProperty("inventory")
  //private InventoryDocument inventory;

  /**
   * The localized product long description.
   */
  @JsonProperty("long_description")
  private String longDescription;

  /**
   * The products manufacturer name.
   */
  @JsonProperty("manufacturer_name")
  private String manufacturerName;

  /**
   * The products manufacturer sku.
   */
  @JsonProperty("manufacturer_sku")
  private String manufacturerSku;

  /**
   * The master product information. Only for types master, variation group and variant.
   */
  //@JsonProperty("master")
  //private MasterDocument master;

  /**
   * The minimum order quantity for this product.
   */
  @JsonProperty("min_order_quantity")
  private double minOrderQuantity;

  /**
   * The localized product name.
   */
  @JsonProperty("name")
  private String name;

  /**
   * The array of product options. This array can be empty. Only for type option.
   */
  //@JsonProperty("options")
  //private List<OptionDocument> options;

  /**
   * The localized products page description.
   */
  @JsonProperty("page_description")
  private String pageDescription;

  /**
   * The localized products page description.
   */
  @JsonProperty("page_keywords")
  private String pageKeywords;

  /**
   * The localized products page title.
   */
  @JsonProperty("page_title")
  private String pageTitle;

  /**
   * The sales price of the product.
   * In case of complex products like master or set this is the minimum price of related child private String products.
   */
  @JsonProperty("price")
  private double price;

  /**
   * The maximum sales of related child products in case of complex products like master or set.
   */
  @JsonProperty("price_max")
  private double priceMax;

  /**
   * The prices map with price book ids and their values.
   */
  @JsonProperty("prices")
  private Map<String, Double> prices;

  /**
   * The id of the products primary category.
   */
  @JsonProperty("primary_category_id")
  private String primaryCategoryId;

  /**
   * The array of source and target products links information.
   */
  //@JsonProperty("product_links")
  //private List<ProductLinkDocument> productLinks;

  /**
   * The array of active customer product promotions for this product. This array can be private String empty. Coupon promotions are not returned in this array.
    */
  //@JsonProperty("product_promotions")
  //private List<ProductPromotionDocument> productPromotions;

  /**
   * List of recommendations.
   */
  //@JsonProperty("recommendations")
  //private List<RecommendationDocument> recommendations;

  /**
   * The array of set products of this product.
   */
  @JsonProperty("set_products")
  private List<ProductDocument> setProducts;

  /**
   * The localized product short description.
   */
  @JsonProperty("short_description")
  private String shortDescription;

  /**
   * The steps in which the order amount of the product can be increased.
   */
  @JsonProperty("step_quantity")
  private double stepQuantity;

  /**
   * The product type information.
   * Can be one or multiple of the following values: item,master,variation_group,variant,private String bundle,set.
   */
  //@JsonProperty("type")
  //private ProductTypeDocument type;

  /**
   * The sales unit of the product.
   */
  @JsonProperty("unit")
  private String unit;

  /**
   * The Universal Product Code.
   */
  @JsonProperty("upc")
  private String upc;

  /**
   * The array of actual variants.
   * This array can be empty.
   * Only for types master, variation group and variant.
   */
  //@JsonProperty("variants")
  //private List<VariantDocument> variants;

  /**
   * Sorted array of variation attributes information.
   * This array can be empty.
   * Only for private String types master, variation group and variant.
    */
  @JsonProperty("variation_attributes")
  private List<VariationAttribute> variationAttributes = new ArrayList<>();

  /**
   * The array of actual variation groups.
   * This array can be empty.
   * Only for types master, private String variation group and variant.
   */
  //@JsonProperty("variation_groups")
  //private List<VariationGroupDocument> variationGroups;

  /**
   * The actual variation attribute id - value pairs.
   * Only for type variant and variation group.
   */
  @JsonProperty("variation_values")
  private Map<String, String> variationValues;


  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public String getEan() {
    return ean;
  }

  public void setEan(String ean) {
    this.ean = ean;
  }

  public String getLongDescription() {
    return longDescription;
  }

  public void setLongDescription(String longDescription) {
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

  public double getMinOrderQuantity() {
    return minOrderQuantity;
  }

  public void setMinOrderQuantity(double minOrderQuantity) {
    this.minOrderQuantity = minOrderQuantity;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPageDescription() {
    return pageDescription;
  }

  public void setPageDescription(String pageDescription) {
    this.pageDescription = pageDescription;
  }

  public String getPageKeywords() {
    return pageKeywords;
  }

  public void setPageKeywords(String pageKeywords) {
    this.pageKeywords = pageKeywords;
  }

  public String getPageTitle() {
    return pageTitle;
  }

  public void setPageTitle(String pageTitle) {
    this.pageTitle = pageTitle;
  }

  public double getPrice() {
    return price;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  public double getPriceMax() {
    return priceMax;
  }

  public void setPriceMax(double priceMax) {
    this.priceMax = priceMax;
  }

  public Map<String, Double> getPrices() {
    return prices;
  }

  public void setPrices(Map<String, Double> prices) {
    this.prices = prices;
  }

  public String getPrimaryCategoryId() {
    return primaryCategoryId;
  }

  public void setPrimaryCategoryId(String primaryCategoryId) {
    this.primaryCategoryId = primaryCategoryId;
  }

  public List<ProductDocument> getSetProducts() {
    return setProducts;
  }

  public void setSetProducts(List<ProductDocument> setProducts) {
    this.setProducts = setProducts;
  }

  public String getShortDescription() {
    return shortDescription;
  }

  public void setShortDescription(String shortDescription) {
    this.shortDescription = shortDescription;
  }

  public double getStepQuantity() {
    return stepQuantity;
  }

  public void setStepQuantity(double stepQuantity) {
    this.stepQuantity = stepQuantity;
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

  public Map<String, String> getVariationValues() {
    return variationValues;
  }

  public void setVariationValues(Map<String, String> variationValues) {
    this.variationValues = variationValues;
  }

  public List<ImageGroupDocument> getImageGroups() {
    return imageGroups;
  }

  public void setImageGroups(List<ImageGroupDocument> imageGroups) {
    this.imageGroups = imageGroups;
  }

  public List<VariationAttribute> getVariationAttributes() {
    return variationAttributes;
  }

  public void setVariationAttributes(List<VariationAttribute> variationAttributes) {
    this.variationAttributes = variationAttributes;
  }
}
