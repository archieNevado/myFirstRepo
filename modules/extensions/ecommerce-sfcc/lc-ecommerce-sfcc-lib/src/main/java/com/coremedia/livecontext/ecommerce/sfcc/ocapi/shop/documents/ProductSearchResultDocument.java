package com.coremedia.livecontext.ecommerce.sfcc.ocapi.shop.documents;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.AbstractOCDocument;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * Document representing a product search result.
 */
public class ProductSearchResultDocument extends AbstractOCDocument {

  /**
   * The number of returned documents.
   */
  @JsonProperty("count")
  private int count;

  /**
   * The sorted array of search hits.
   * This array can be empty.
   */
  @JsonProperty("hits")
  private List<ProductSearchHitDocument> hits;

  /**
   * The URL of the next result page.
   */
  @JsonProperty("next")
  private String nextUrl;

  /**
   * The URL of the previous result page.
   */
  @JsonProperty("previous")
  private String previousUrl;

  /**
   * The query String that was searched for.
   */
  @JsonProperty("query")
  private String query;

  /**
   * The sorted array of search refinements.
   * This array can be empty.
   */
  //@JsonProperty("refinements")
  //private List<ProductSearchRefinementDocument> refinements;

  /**
   * The fields that you want to select.
   */
  @JsonProperty("select")
  private String select;

  /**
   * A map of selected refinement attribute id/value(s) pairs.
   * The sorting order is the same as in request URL.
   */
  @JsonProperty("selected_refinements")
  private Map<String, String> selectedRefinements;

  /**
   * The id of the applied sorting option.
   */
  @JsonProperty("selected_sorting_option")
  private String selectedSortingOption;

  /**
   * The sorted array of search sorting options.
   * This array can be empty.
   */
  //@JsonProperty("sorting_options")
  //private List<ProductSearchSortingOptionDocument> sortingOptions;

  /**
   * The zero-based index of the first search hit to include in the result.
   */
  @JsonProperty("start")
  private int startIndex;

  /**
   * The suggestion given by the system if no result was found for the submitted search phrase.
   */
  @JsonProperty("suggested_search_phrase")
  private String suggestedSearchPhrase;

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

  public List<ProductSearchHitDocument> getHits() {
    return hits;
  }

  public void setHits(List<ProductSearchHitDocument> hits) {
    this.hits = hits;
  }

  public String getNextUrl() {
    return nextUrl;
  }

  public void setNextUrl(String nextUrl) {
    this.nextUrl = nextUrl;
  }

  public String getPreviousUrl() {
    return previousUrl;
  }

  public void setPreviousUrl(String previousUrl) {
    this.previousUrl = previousUrl;
  }

  public String getQuery() {
    return query;
  }

  public void setQuery(String query) {
    this.query = query;
  }

  public String getSelect() {
    return select;
  }

  public void setSelect(String select) {
    this.select = select;
  }

  public Map<String, String> getSelectedRefinements() {
    return selectedRefinements;
  }

  public void setSelectedRefinements(Map<String, String> selectedRefinements) {
    this.selectedRefinements = selectedRefinements;
  }

  public String getSelectedSortingOption() {
    return selectedSortingOption;
  }

  public void setSelectedSortingOption(String selectedSortingOption) {
    this.selectedSortingOption = selectedSortingOption;
  }

  public int getStartIndex() {
    return startIndex;
  }

  public void setStartIndex(int startIndex) {
    this.startIndex = startIndex;
  }

  public String getSuggestedSearchPhrase() {
    return suggestedSearchPhrase;
  }

  public void setSuggestedSearchPhrase(String suggestedSearchPhrase) {
    this.suggestedSearchPhrase = suggestedSearchPhrase;
  }

  public int getTotal() {
    return total;
  }

  public void setTotal(int total) {
    this.total = total;
  }
}
