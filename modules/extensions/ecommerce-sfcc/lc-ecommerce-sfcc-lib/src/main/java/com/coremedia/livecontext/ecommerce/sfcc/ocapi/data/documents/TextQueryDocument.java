package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

/**
 * A text query is used to match some text
 * (i.e. a search phrase possibly consisting of multiple terms) against one or multiple fields.
 * <p>
 * In case multiple fields are provided, the phrase conceptually forms a logical OR over the fields.
 * In this case, the terms of the phrase basically have to match within the text,
 * that would result in concatenating all given fields.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TextQueryDocument implements QueryDocument {

  public static final String FIELDS = "fields";
  public static final String SEARCH_PHRASE = "search_phrase";

  public TextQueryDocument() {
  }

  public TextQueryDocument(String field, String searchPhrase) {
    this(Arrays.asList(field), searchPhrase);
  }

  public TextQueryDocument(List<String> fields, String searchPhrase) {
    this.fields = fields;
    this.searchPhrase = searchPhrase;
  }

  /**
   * The document fields the search phrase has to match against.
   */
  @JsonProperty(FIELDS)
  private List<String> fields;

  /**
   * A search phrase, which may consist of multiple terms.
   */
  @JsonProperty(SEARCH_PHRASE)
  private String searchPhrase;


  public List<String> getFields() {
    return fields;
  }

  public void setFields(List<String> fields) {
    this.fields = fields;
  }

  public String getSearchPhrase() {
    return searchPhrase;
  }

  public void setSearchPhrase(String searchPhrase) {
    this.searchPhrase = searchPhrase;
  }

  @Override
  public JSONObject asJSON() {
    JSONObject queryJSON = new JSONObject();

    JSONObject textQueryJson = new JSONObject();
    for (String field : fields) {
      textQueryJson.append("fields", field);
    }
    textQueryJson.put("search_phrase", searchPhrase);
    queryJSON.put(TEXT_QUERY, textQueryJson);

    return queryJSON;
  }

  @Override
  public String toJSONString() {
    return asJSON().toString();
  }
}
