package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.AbstractOCDocument;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class CatalogResultDocument extends AbstractOCDocument {

  @JsonProperty("count")
  private int count;

  @JsonProperty("data")
  private List<CatalogDocument> data;

  @JsonProperty("start")
  private int start;

  @JsonProperty("total")
  private int total;

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public List<CatalogDocument> getData() {
    return data;
  }

  public void setData(List<CatalogDocument> data) {
    this.data = data;
  }

  public int getStart() {
    return start;
  }

  public void setStart(int start) {
    this.start = start;
  }

  public int getTotal() {
    return total;
  }

  public void setTotal(int total) {
    this.total = total;
  }
}
