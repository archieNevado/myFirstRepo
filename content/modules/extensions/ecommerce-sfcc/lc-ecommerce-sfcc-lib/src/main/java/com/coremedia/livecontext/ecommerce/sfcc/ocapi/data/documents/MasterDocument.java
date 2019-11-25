package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.AbstractOCDocument;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * Document representing a variation master.
 */
public class MasterDocument extends AbstractOCDocument {


  /**
   * The URL addressing the master product.
   */
  @JsonProperty("link")
  private String link;
  /**
   * The id (SKU) of the master product.
   */
  @JsonProperty("master_id")
  private String masterId;
  /**
   * A flag indicating whether at least one of the variants is orderable.
   */
  @JsonProperty("orderable")
  private boolean orderable;
  /**
   * The minimum sales price of the related variants.
   */
  @JsonProperty("price")
  private BigDecimal price;
  /**
   * The maximum sales of related variants.
   */
  @JsonProperty("price_max")
  private BigDecimal priceMax;


  public String getLink() {
    return link;
  }

  public void setLink(String link) {
    this.link = link;
  }

  public String getMasterId() {
    return masterId;
  }

  public void setMasterId(String masterId) {
    this.masterId = masterId;
  }

  public boolean isOrderable() {
    return orderable;
  }

  public void setOrderable(boolean orderable) {
    this.orderable = orderable;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public BigDecimal getPriceMax() {
    return priceMax;
  }

  public void setPriceMax(BigDecimal priceMax) {
    this.priceMax = priceMax;
  }
}
