package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.AbstractOCDocument;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class CustomerGroupsDocument extends AbstractOCDocument {
  /**
   * The number of returned documents.
   */
  @JsonProperty("count")
  private int count;

  @JsonProperty("data")
  private List<CustomerGroupDocument> data;

  /**
   * The list of expands set for the search request. Expands are optional.
   */
  @JsonProperty("expand")
  private List<String> expand;

  /**
   * The URL of the next result page.
   */
  @JsonProperty("next")
  private String next;

  /**
   * The URL of the previous result page.
   */
  @JsonProperty("previous")
  private String previous;

  /**
   * The fields that you want to select.
   */
  @JsonProperty("select")
  private String select;

  /**
   * The zero-based index of the first search hit to include in the result.
   */
  @JsonProperty("start")
  private int start;

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

  public List<CustomerGroupDocument> getData() {
    return data;
  }

  public void setData(List<CustomerGroupDocument> data) {
    this.data = data;
  }

  public List<String> getExpand() {
    return expand;
  }

  public void setExpand(List<String> expand) {
    this.expand = expand;
  }

  public String getNext() {
    return next;
  }

  public void setNext(String next) {
    this.next = next;
  }

  public String getPrevious() {
    return previous;
  }

  public void setPrevious(String previous) {
    this.previous = previous;
  }

  public String getSelect() {
    return select;
  }

  public void setSelect(String select) {
    this.select = select;
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
