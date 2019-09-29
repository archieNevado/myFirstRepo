package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.AbstractOCDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.JSONRepresentation;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.json.JSONObject;

import java.math.BigDecimal;

/**
 * Document representing a content folder assignment.
 */
public class ContentFolderAssignmentDocument extends AbstractOCDocument implements JSONRepresentation {

  /**
   * the content id, maxLength=256
   */
  @JsonProperty("content_id")
  private String contentId;

  /**
   * The content link.
   */
  @JsonProperty("content_link")
  private String contentLink;

  /**
   * A flag indicating whether the assignment is the default one.
   */
  @JsonProperty("default")
  private boolean defaultAssignment;

  /**
   * the folder id, maxLength=256
   */
  @JsonProperty("folder_id")
  private String folderId;

  /**
   * The folder link.
   */
  @JsonProperty("folder_link")
  private String folderLink;

  /**
   * The position of the content asset in the folder. minNumberValue=0.0
   */
  @JsonProperty("position")
  private BigDecimal position;

  public String getContentId() {
    return contentId;
  }

  public String getContentLink() {
    return contentLink;
  }

  public boolean isDefaultAssignment() {
    return defaultAssignment;
  }

  public void setDefault(boolean defaultAssignment) {
    this.defaultAssignment = defaultAssignment;
  }

  public String getFolderId() {
    return folderId;
  }

  public String getFolderLink() {
    return folderLink;
  }

  public BigDecimal getPosition() {
    return position;
  }

  @Override
  public JSONObject asJSON() {
    JSONObject json = new JSONObject();

    json.put("default", defaultAssignment);
    return json;
  }

  @Override
  public String toJSONString() {
    return asJSON().toString();
  }
}
