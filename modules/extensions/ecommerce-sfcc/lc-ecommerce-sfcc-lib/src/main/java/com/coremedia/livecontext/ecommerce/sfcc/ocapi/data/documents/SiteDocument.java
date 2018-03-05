package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.AbstractOCDocument;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Document representing a site.
 */
public class SiteDocument extends AbstractOCDocument {

  /**
   * The link to the customer list.
   */
  @JsonProperty("customer_list_link")
  private Object/*CustomerListLink*/ customerListLink;

}
