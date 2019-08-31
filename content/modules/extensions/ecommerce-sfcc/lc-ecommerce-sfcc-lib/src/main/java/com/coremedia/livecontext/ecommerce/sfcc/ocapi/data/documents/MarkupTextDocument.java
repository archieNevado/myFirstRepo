package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.AbstractOCDocument;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 17.1
 */
public class MarkupTextDocument extends AbstractOCDocument {

  public static final String MARKUP = "markup";
  public static final String SOURCE = "source";

  /**
   * The rendered HTML
   */
  @JsonProperty(MARKUP)
  private String markup;

  /**
   * The raw markup text
   */
  @JsonProperty(SOURCE)
  private String source;

  public String getMarkup() {
    return markup;
  }

  public void setMarkup(String markup) {
    this.markup = markup;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }
}
