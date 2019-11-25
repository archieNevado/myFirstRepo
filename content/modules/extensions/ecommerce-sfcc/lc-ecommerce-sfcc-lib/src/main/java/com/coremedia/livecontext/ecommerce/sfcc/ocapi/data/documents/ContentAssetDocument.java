package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.AbstractOCDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.JSONRepresentation;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.LocalizedBooleanDeserializer;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.LocalizedMarkupTextDeserializer;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.LocalizedStringDeserializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.json.JSONObject;

import java.util.Date;
import java.util.Map;

/**
 * Document representing a content asset
 */
public class ContentAssetDocument extends AbstractOCDocument implements JSONRepresentation {

  private static final String DEFAULT_LOCALE_IDENTIFIER = "default";

  /**
   * maxLength=256, minLength=1	The user specific identifier for the customer group, which must be unique across the organization. Property is read-only.
   */
  @JsonProperty("id")
  private String id;

  /**
   * Returns the value of attribute 'creationDate'.
   */
  @JsonProperty("creation_date")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATA_API_DATE_PATTERN)
  private Date creationDate;

  /**
   * Returns the value of attribute 'lastModified'.
   */
  @JsonProperty("last_modified")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATA_API_DATE_PATTERN)
  private Date lastModified;

  @JsonProperty("name")
  @JsonDeserialize(using = LocalizedStringDeserializer.class)
  private LocalizedProperty<String> name;

  @JsonProperty("description")
  @JsonDeserialize(using = LocalizedStringDeserializer.class)
  private LocalizedProperty<String> description;

  @JsonProperty("online")
  @JsonDeserialize(using = LocalizedBooleanDeserializer.class)
  private LocalizedProperty<Boolean> online;

  @JsonProperty("c_body")
  @JsonDeserialize(using = LocalizedMarkupTextDeserializer.class)
  private LocalizedProperty<MarkupTextDocument> body;

  public Date getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public void setId(String id) {
    this.id = id;
  }

  public Date getLastModified() {
    return lastModified;
  }

  public void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }

  public LocalizedProperty<String> getName() {
    return name;
  }

  public void setName(LocalizedProperty<String> name) {
    this.name = name;
  }

  public LocalizedProperty<String> getDescription() {
    return description;
  }

  public void setDescription(LocalizedProperty<String> description) {
    this.description = description;
  }

  public LocalizedProperty<Boolean> getOnline() {
    return online;
  }

  public void setOnline(LocalizedProperty<Boolean> online) {
    this.online = online;
  }

  public LocalizedProperty<MarkupTextDocument> getBody() {
    return body;
  }

  public void setBody(LocalizedProperty<MarkupTextDocument> body) {
    this.body = body;
  }

  @Override
  public JSONObject asJSON() {
    JSONObject json = new JSONObject();

    json.put("id", id);

    JSONObject nameJson = new JSONObject();
    nameJson.put(DEFAULT_LOCALE_IDENTIFIER, name.getValue(DEFAULT_LOCALE_IDENTIFIER));
    json.put("name", nameJson);

    JSONObject descriptionJson = new JSONObject();
    descriptionJson.put(DEFAULT_LOCALE_IDENTIFIER, description.getValue(DEFAULT_LOCALE_IDENTIFIER));
    json.put("description", descriptionJson);

    JSONObject markupJson = new JSONObject();
    Map<String, MarkupTextDocument> bodyValues = body.getValues();
    for (Map.Entry<String, MarkupTextDocument> entry : bodyValues.entrySet()) {
      String locale = entry.getKey();
      MarkupTextDocument markupDocument = entry.getValue();
      markupJson.put("source", markupDocument.getMarkup());
      JSONObject markupJsonParent = new JSONObject();
      markupJsonParent.put(locale, markupJson);
      json.put("c_body", markupJsonParent);
    }

    return json;
  }

  @Override
  public String toJSONString() {
    return asJSON().toString();
  }
}
