package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.AbstractOCDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.LocalizedStringDeserializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Document representing a link to a customer list.
 */
public class CustomerListLinkDocument extends AbstractOCDocument {

  /**
   * The customerlist id.
   */
  @JsonProperty("customer_list_id")
  private String customer_list_id;

  /**
   * The target of the link.
   */
  @JsonProperty("link")
  private String link;

  /**
   * The link title.
   */
  @JsonProperty("title")
  @JsonDeserialize(using = LocalizedStringDeserializer.class)
  private LocalizedProperty<String> title;

  public String getCustomer_list_id() {
    return customer_list_id;
  }

  public void setCustomer_list_id(String customer_list_id) {
    this.customer_list_id = customer_list_id;
  }

  public String getLink() {
    return link;
  }

  public void setLink(String link) {
    this.link = link;
  }

  public LocalizedProperty<String> getTitle() {
    return title;
  }

  public void setTitle(LocalizedProperty<String> title) {
    this.title = title;
  }
}
