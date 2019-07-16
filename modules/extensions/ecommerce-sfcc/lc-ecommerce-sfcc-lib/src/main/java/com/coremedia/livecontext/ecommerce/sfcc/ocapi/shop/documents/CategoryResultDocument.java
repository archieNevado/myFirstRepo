package com.coremedia.livecontext.ecommerce.sfcc.ocapi.shop.documents;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.AbstractOCDocument;

import java.util.List;

/**
 * Result document containing an array of categories.
 */
public class CategoryResultDocument extends AbstractOCDocument {

  /**
   * The number of returned documents.
   */
  private int count;

  /**
   * The array of category documents.
   */
  private List<CategoryDocument> data;

  /**
   * The total number of documents.
   */
  private int total;



  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public List<CategoryDocument> getData() {
    return data;
  }

  public void setData(List<CategoryDocument> data) {
    this.data = data;
  }

  public int getTotal() {
    return total;
  }

  public void setTotal(int total) {
    this.total = total;
  }
}
