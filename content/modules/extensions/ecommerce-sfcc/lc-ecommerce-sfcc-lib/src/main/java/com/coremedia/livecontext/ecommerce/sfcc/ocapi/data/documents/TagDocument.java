package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.AbstractOCDocument;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Document representing a tag.
 */
public class TagDocument extends AbstractOCDocument {

  /**
   * The id of the tag.
   */
  @JsonProperty("tag_id")
  private String tagId;

  public String getTagId() {
    return tagId;
  }

  public void setTagId(String tagId) {
    this.tagId = tagId;
  }
}
