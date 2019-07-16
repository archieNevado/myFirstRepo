package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.AbstractOCDocument;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Result document containing an array of code versions.
 */
public class CodeVersionResultDocument extends AbstractOCDocument {

  /**
   * The number of returned documents.
   */
  @JsonProperty("count")
  private int count;

  /**
   * The list of code versions.
   */
  @JsonProperty("data")
  private List<CodeVersionDocument> data;

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

  public List<CodeVersionDocument> getData() {
    return data;
  }

  public void setData(List<CodeVersionDocument> data) {
    this.data = data;
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
