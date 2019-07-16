package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.AbstractOCDocument;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Map;

/**
 * Document representing a product variation.
 */
public class VariantDocument extends AbstractOCDocument {

  /**
   * Inventory "Available to Sell" of the product.
   */
  @JsonProperty("ats")
  private BigDecimal ats;

  @JsonProperty("default_product_variation")
  private Boolean defaultProductVariation;

  /**
   * URL to the preview image.
   */
  @JsonProperty("image")
  private MediaFileDocument image;

  /**
   * <code>true</code> if the product is in stock, or <code>false</code> if not.
   */
  @JsonProperty("in_stock")
  private Boolean inStock;

  /**
   * The URL addressing the product.
   */
  @JsonProperty("link")
  private String link;

  /**
   * If the product is currently online.
   * <code>true</code> if online <code>false</code> if not.
   */
  @JsonProperty("online")
  private Boolean online;

  /**
   * A flag indicating whether the variant is orderable.
   */
  @JsonProperty("orderable")
  private Boolean orderable;

  /**
   * The sales price of the variant.
   */
  @JsonProperty("price")
  private BigDecimal price;

  /**
   * code for the price of the product.
   */
  @JsonProperty("price_currency")
  private Currency priceCurrency;

  /**
   * The id (SKU) of the variant.
   */
  @JsonProperty("product_id")
  private String productId;

  @JsonProperty("searchable")
  private Object/*SiteSpecific<Boolean>*/ searchable;

  /**
   * The actual variation attribute id - value pairs.
   */
  @JsonProperty("variation_values")
  private Map<String, String> variationValues;


  public BigDecimal getAts() {
    return ats;
  }

  public void setAts(BigDecimal ats) {
    this.ats = ats;
  }

  public Boolean getDefaultProductVariation() {
    return defaultProductVariation;
  }

  public void setDefaultProductVariation(Boolean defaultProductVariation) {
    this.defaultProductVariation = defaultProductVariation;
  }

  public MediaFileDocument getImage() {
    return image;
  }

  public void setImage(MediaFileDocument image) {
    this.image = image;
  }

  public Boolean getInStock() {
    return inStock;
  }

  public void setInStock(Boolean inStock) {
    this.inStock = inStock;
  }

  public String getLink() {
    return link;
  }

  public void setLink(String link) {
    this.link = link;
  }

  public Boolean getOnline() {
    return online;
  }

  public void setOnline(Boolean online) {
    this.online = online;
  }

  public Boolean getOrderable() {
    return orderable;
  }

  public void setOrderable(Boolean orderable) {
    this.orderable = orderable;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public Currency getPriceCurrency() {
    return priceCurrency;
  }

  public void setPriceCurrency(Currency priceCurrency) {
    this.priceCurrency = priceCurrency;
  }

  public String getProductId() {
    return productId;
  }

  public void setProductId(String productId) {
    this.productId = productId;
  }

  public Object getSearchable() {
    return searchable;
  }

  public void setSearchable(Object searchable) {
    this.searchable = searchable;
  }

  public Map<String, String> getVariationValues() {
    return variationValues;
  }

  public void setVariationValues(Map<String, String> variationValues) {
    this.variationValues = variationValues;
  }
}
