package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.AbstractOCDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.JSONRepresentation;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.json.JSONObject;

import java.util.Optional;

/**
 * Document representing a content folder.
 */
public class ContentFolderDocument extends AbstractOCDocument implements JSONRepresentation {

  @JsonProperty("id")
  private String id;

  @JsonProperty("name")
  private Optional<String> name;

  @JsonProperty("parent_folder_id")
  private String parentFolderId;

  @Override
  public String getId() {
    return id;
  }

  @Override
  public void setId(String id) {
    this.id = id;
  }

  public Optional<String> getName() {
    return name;
  }

  public void setName(Optional<String> name) {
    this.name = name;
  }

  public String getParentFolderId() {
    return parentFolderId;
  }

  public void setParentFolderId(String parentFolderId) {
    this.parentFolderId = parentFolderId;
  }

  @Override
  public JSONObject asJSON() {
    JSONObject json = new JSONObject();
    json.put("id", id);
    if (name.isPresent()) {
      json.put("name", name.orElse(null));
    }
    json.put("parent_folder_id", parentFolderId);
    return json;
  }

  @Override
  public String toJSONString() {
    return asJSON().toString();
  }
}
