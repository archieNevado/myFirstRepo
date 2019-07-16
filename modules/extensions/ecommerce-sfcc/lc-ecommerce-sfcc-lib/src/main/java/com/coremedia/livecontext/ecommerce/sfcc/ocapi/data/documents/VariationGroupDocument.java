package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.AbstractOCDocument;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Document representing a variation group.
 */
public class VariationGroupDocument extends AbstractOCDocument {

  /**
   * The URL addressing the product.
   */
  @JsonProperty("link")
  private String link;

  /**
   * A flag indicating whether the variation group is orderable.
   */
  @JsonProperty("orderable")
  private Boolean orderable;

  /**
   * The sales price of the variation group.
   */
  @JsonProperty("price")
  private BigDecimal price;

  /**
   * The id (SKU) of the variation group.
   */
  @JsonProperty("product_id")
  private String productId;

  /**
   * The actual variation attribute id - value pairs.
   */
  @JsonProperty("variation_values")
  private Map<String, String> variationValues;


  public String getLink() {
    return link;
  }

  public void setLink(String link) {
    this.link = link;
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

  public String getProductId() {
    return productId;
  }

  public void setProductId(String productId) {
    this.productId = productId;
  }

  public Map<String, String> getVariationValues() {
    return variationValues;
  }

  public void setVariationValues(Map<String, String> variationValues) {
    this.variationValues = variationValues;
  }
}
