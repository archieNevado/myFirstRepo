package com.coremedia.livecontext.ecommerce.sfcc.ocapi;

import org.json.JSONObject;

/**
 * Interface for classes that support JSON a representation.
 */
public interface JSONRepresentation {

  /**
   * Returns a {@link JSONObject} representation of this document.
   *
   * @return JSON representation
   */
  JSONObject asJSON();

  /**
   * Returns a JSON string representation of this document.
   *
   * @return JSON string representation
   */
  String toJSONString();
}
