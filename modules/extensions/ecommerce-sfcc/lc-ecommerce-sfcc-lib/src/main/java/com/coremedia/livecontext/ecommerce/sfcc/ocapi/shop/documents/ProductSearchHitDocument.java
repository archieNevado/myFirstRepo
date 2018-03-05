package com.coremedia.livecontext.ecommerce.sfcc.ocapi.shop.documents;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.AbstractOCDocument;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * Document representing a product search hit.
 */
public class ProductSearchHitDocument extends AbstractOCDocument {

  /**
   * The ISO 4217 mnemonic code of the currency.
   */
  @JsonProperty("currency")
  private String currency;

  /**
   * The first image of the product hit for the configured viewtype.
   */
  @JsonProperty("image")
  private ImageDocument image;

  /**
   * The URL addressing the product.
   */
  @JsonProperty("link")
  private String link;

  /**
   * A flag indicating whether the product is orderable.
   */
  @JsonProperty("orderable")
  private boolean orderable;

  /**
   * The sales price of the product.
   * In case of complex products like master
   * or set this is the minimum price of related private String child products.
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
   * The id (SKU) of the product.
   */
  @JsonProperty("product_id")
  private String productId;

  /**
   * The localized name of the product.
   */
  @JsonProperty("product_name")
  private String productName;

  /**
   * The type information for the product.
   */
  //@JsonProperty("product_type")
  //private ProductTypeDocument productType;

  /**
   * The array of represented variation attributes (for the master product only).
   * This private String array can be empty.
   */
  //@JsonProperty("variation_attributes")
  //private List<VariationAttributeDocument> variationAttributes;


  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public String getLink() {
    return link;
  }

  public void setLink(String link) {
    this.link = link;
  }

  public boolean isOrderable() {
    return orderable;
  }

  public void setOrderable(boolean orderable) {
    this.orderable = orderable;
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

  public String getProductId() {
    return productId;
  }

  public void setProductId(String productId) {
    this.productId = productId;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public ImageDocument getImage() {
    return image;
  }

  public void setImage(ImageDocument image) {
    this.image = image;
  }
}
