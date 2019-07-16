package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.AbstractOCDocument;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Document representing a customer group
 */
public class CustomerGroupDocument extends AbstractOCDocument {

  /**
   * Returns the value of attribute 'creationDate'.
   */
  @JsonProperty("creation_date")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATA_API_DATE_PATTERN)
  private Date creationDate;

  /**
   * The description for the customer group. This property is read-only for system groups.
   */
  @JsonProperty("description")
  private String description;
  /**
   * maxLength=256, minLength=1	The user specific identifier for the customer group, which must be unique across the organization. Property is read-only.
   */
  @JsonProperty("id")
  private String id;

  /**
   * The deletion status of this customer group.
   */
  @JsonProperty("in_deletion")
  private Boolean inDeletion;

  /**
   * Returns the value of attribute 'lastModified'.
   */
  @JsonProperty("last_modified")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATA_API_DATE_PATTERN)
  private Date lastModified;

  /**
   * URL that is used to get this instance. This property is computed and cannot be modified.
   */
  @JsonProperty("link")
  private String link;

  /**
   * The number of members in this customer group.
   */
  @JsonProperty("member_count")
  private Integer memberCount;

  /**
   * The rule of this customer group. Only available for dynamic customer groups.
   */
  @JsonProperty("rule")
  private Object rule;

  /**
   * The type of the customer group. This property is read-only.
   */
  @JsonProperty("type")
  private String type;

  public Date getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public void setId(String id) {
    this.id = id;
  }

  public Boolean getInDeletion() {
    return inDeletion;
  }

  public void setInDeletion(Boolean inDeletion) {
    this.inDeletion = inDeletion;
  }

  public Date getLastModified() {
    return lastModified;
  }

  public void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }

  public String getLink() {
    return link;
  }

  public void setLink(String link) {
    this.link = link;
  }

  public Integer getMemberCount() {
    return memberCount;
  }

  public void setMemberCount(Integer memberCount) {
    this.memberCount = memberCount;
  }

  public Object getRule() {
    return rule;
  }

  public void setRule(Object rule) {
    this.rule = rule;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
