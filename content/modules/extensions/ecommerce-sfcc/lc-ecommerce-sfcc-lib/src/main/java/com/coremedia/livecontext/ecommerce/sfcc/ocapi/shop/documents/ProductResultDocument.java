package com.coremedia.livecontext.ecommerce.sfcc.ocapi.shop.documents;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.AbstractOCDocument;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Result document containing an array of products.
 */
public class ProductResultDocument extends AbstractOCDocument {

  /**
   * The number of returned documents.
   */
  @JsonProperty("count")
  private int count;

  /**
   * The array of product documents.
   */
  @JsonProperty("data")
  private List<ProductDocument> data;

  /**
   * The total number of documents.
   */
  @JsonProperty("total")
  private int total;


  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public List<ProductDocument> getData() {
    return data;
  }

  public void setData(List<ProductDocument> data) {
    this.data = data;
  }

  public int getTotal() {
    return total;
  }

  public void setTotal(int total) {
    this.total = total;
  }
}
